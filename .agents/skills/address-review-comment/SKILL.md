---
name: address-review-comment
description: Pull Request のレビューコメント対応を支援する Skill。gh で PR の review thread を取得し、必要なコード修正、会話の解決、Gemini Code Assist への再レビュー依頼、追加コメントのポーリングまでを行う。ユーザーが「レビューコメントの修正」「レビューコメントへの対応」「Gemini Code Assist のコメント対応」「レビュー後に /gemini review を再実行」などを依頼したときに使用する。
---

# Address Review Comment

## 概要

この Skill は、Pull Request のレビュー対応を「未解決コメントの取得 → トリアージ → 修正または説明 → 会話の解決 → `/gemini review` 依頼 → 追加コメントのポーリング」という一連のループとして扱う。

目的は、人間のレビュー担当者に戻す前に、AI やツールからの指摘を一貫した品質ゲートで処理すること。単にコメントを消化するのではなく、プロジェクトの `docs/` に書かれた Android / KMP / iOS の設計方針を守りながら対応する。

## なぜこの設計なのか

本リポジトリは、`docs/strategy-modularization.md` で再利用性を最優先し、シンプルさを少し犠牲にしてでもマルチモジュール化や依存性逆転を採用している。レビューコメント対応でも同じ判断基準を維持する必要がある。

そのため、この Skill は次の設計原則に従う。

- **単一責任**: レビューコメント対応の手順を Skill に集約し、Android 画面・shared ロジック・iOS 側の実装判断とは分離する。
- **依存性逆転**: UI / feature は domain 抽象に依存し、data 実装や Firestore の詳細を漏らさない。
- **情報隠蔽**: DataSource 実装や DI の具象は、`docs/strategy-dependency-injection.md` に従って必要最小限の公開範囲に留める。
- **再利用性優先**: レビュー取得や解決の GitHub 操作を毎回手作業で組み立てず、補助スクリプトと参照ドキュメントに分離する。

たとえば Android の地図画面で喫煙所一覧を表示するレビューコメントを処理するとき、`android:feature:map` が Firestore 実装へ直接依存する修正は避ける。画面は `shared:domain` の Repository インターフェースに依存し、Firestore からの取得は `shared:database:firestore` の DataSource 実装に閉じ込める。この分割はコード量を増やすが、Android / iOS の再利用性、差し替えテスト、影響範囲を限定したビルドに寄与する。

## 必ず参照するプロジェクト文脈

コードを編集する前に、対象に応じて次の `docs/` を読む。

- `docs/strategy-modularization.md`: KMP のモジュール境界、依存方向、shared / android / ios の責務。
- `docs/architecture-layer-data.md`: Repository、DataSource、DataModel、Mapper の責務。
- `docs/strategy-dependency-injection.md`: Metro の BindingContainer、DependencyGraph、可視性、DI の所有者。
- `docs/convention-coding.md`: 命名、Expression body、モデル KDoc、`Default` 実装名、`internal` 可視性、DataSource の dispatcher 規約。
- `docs/convention-version-catalog.md`: 依存関係や Gradle Version Catalog を変更する場合の規約。

レビューコメントと `docs/` の方針が衝突する場合は、`docs/` を優先する。そのうえで、会話を解決する前に「なぜコード変更しないのか」を返信する。

## ワークフロー

### 1. 対象 PR を特定する

ユーザーが PR を指定している場合は、その PR を使う。指定がない場合は、このセッションで作成した PR を使う。どちらも不明な場合は、現在のブランチから PR を特定する。

```bash
gh pr view --json number,url,headRefName,baseRefName,title
```

PR を 1 つに特定できない場合は、ユーザーに PR URL または PR 番号を確認する。

### 2. 未解決レビューコメントを取得する

まず補助スクリプトを使う。PR メタデータ、review thread、PR コメント、review summary を 1 つの JSON として取得できる。

```bash
.agents/skills/address-review-comment/scripts/fetch-review-state.sh <pr-number-or-url>
```

スクリプトが失敗する、またはより細かい操作が必要な場合は、`references/github-cli.md` を読んで、同等の `gh pr view`、`gh api graphql`、`gh pr comment` を直接実行する。

優先順位は未解決の review thread が最も高い。Gemini Code Assist、他の AI エージェント、人間のレビュー担当者による PR コメントや review summary も、具体的な修正要求であれば対象に含める。

### 3. コメントをトリアージする

各未解決会話を次のいずれかに分類する。

- **今すぐ修正**: 正しさ、アーキテクチャ、保守性、テスト、命名、ドキュメントに関する妥当な指摘。
- **コード変更なし**: 現在のコードが正しい、指摘が `docs/` と衝突する、または適用できない指摘。
- **延期**: 指摘は妥当だが、優先度が低い、今回の PR 範囲外、ブロック要因がある、または対応規模が大きい。

正しさやアーキテクチャに関わる指摘は原則として修正する。延期は、理由を具体的に説明できる場合だけ選ぶ。

### 4. 修正して検証する

**今すぐ修正** に分類したコメントでは、次の順で対応する。

1. 指摘されたファイルと周辺コードを読む。
2. `docs/` の設計方針を守る最小の修正を入れる。
3. まず対象範囲の狭いチェックを実行し、必要に応じて広いチェックを実行する。
4. チェックが成功するか、環境制約が明確になるまで繰り返す。

KMP プロジェクトの修正では、次の境界を守る。

- Android の feature / UI は domain 抽象に依存し、data 実装を直接参照しない。
- `shared:data` は Repository 実装、DataModel、DataSource インターフェースを持つ。
- `shared:database:firestore` などの具象モジュールは外部 SDK 依存を閉じ込める。
- DataSource 実装は I/O dispatcher の注入と `withContext` に責務を持つ。
- DI の組み立ては app / platform graph または BindingContainer に閉じ込める。

この分割は、単一モジュール構成より理解する要素が増える。しかし、喫煙所一覧取得ロジックを Android 画面と iOS 画面で再利用できること、Repository / DataSource を fake に差し替えてテストできること、feature 単位で変更の影響を狭められること、Gradle の差分ビルドを活かしやすいことがメリットになる。

### 5. 返信して会話を解決する

会話を解決する前に必ず返信する。

- **今すぐ修正**: どのように修正したか、どの検証を実行したかを説明する。
- **コード変更なし**: なぜ変更しないかを説明し、必要に応じて根拠となる `docs/` を示す。
- **延期**: なぜ今回対応しないかを説明し、必要であれば follow-up issue を参照または作成する。

返信後、`references/github-cli.md` の GraphQL mutation を使って review thread を resolve する。

### 6. コミットして Gemini Code Assist に再レビューを依頼する

コードや Skill ファイルを変更した場合は、リポジトリの通常の手順で現在のブランチにコミットする。その後、PR に `/gemini review` を投稿する。

```bash
gh pr comment <pr-number-or-url> --body "/gemini review"
```

### 7. 追加コメントをポーリングする

`/gemini review` 投稿後は、1 分ごとにレビュー状態を確認する。

```bash
.agents/skills/address-review-comment/scripts/fetch-review-state.sh <pr-number-or-url>
```

新しい対応可能コメントがあれば、同じトリアージ、修正、返信、resolve のループで処理する。

次のいずれかに到達したら停止する。

- Gemini Code Assist の未解決かつ対応可能なコメントがなくなった。
- 低優先度のコメントだけが残り、それぞれに延期またはコード変更なしの理由を返信済みである。
- レビュー対応サイクルを 5 回完了した。

停止時はユーザーに通知し、最終メッセージに `@Kaito-Dogi` を含める。

## 採用しなかった代替案

### 代替案 1: `SKILL.md` に GitHub 操作をすべて直書きする

概要: GraphQL クエリ、返信、resolve、ポーリングの詳細をすべて `SKILL.md` に書く。

採用しなかった理由: 人間が読むべき判断フローと、機械的な GitHub 操作が混ざり、レビュー対応時の認知負荷が上がるため。Skill 本体は「何をどう判断するか」に集中させ、詳細コマンドは `references/` と `scripts/` に分離した。

有効になり得る条件: 1 回限りの検証、短期開発、GitHub 操作のバリエーションがほぼない小規模チーム。

### 代替案 2: GitHub Actions などで完全自動化する

概要: コメント取得、修正、resolve、Gemini 再レビュー依頼を CI 上で自動実行する。

採用しなかった理由: レビューコメントが `docs/` の設計方針と衝突する場合や、Android feature と KMP shared のどちらを修正すべきか判断する場合に、文脈を読んだ設計判断が必要になるため。完全自動化すると、誤ったレイヤに修正を入れるリスクが高い。

有効になり得る条件: 指摘内容がフォーマット修正や機械的な lint 修正に限定される場合、またはチーム規模が大きく、レビュー対応ポリシーが十分に定型化されている場合。

## ガードレール

- 会話を無言で resolve しない。必ず理由を返信してから resolve する。
- 実質的な未解決コメントは、修正をコミットするか、明確な「コード変更なし / 延期」の理由を返信するまで resolve しない。
- `/gemini review` をローカル検証の代替にしない。再レビュー依頼前に関連するテストやチェックを実行する。
- 速さとアーキテクチャが衝突する場合は、アーキテクチャを優先し、トレードオフを説明する。
- ユーザーが明示的に依頼しない限り、5 回を超えてレビュー対応サイクルを続けない。
