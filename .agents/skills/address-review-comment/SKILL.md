---
name: address-review-comment
description: プルリクエストのレビューコメント対応を行う Skill。GitHub の review thread とプルリクエストコメントを取得し、指摘を修正または説明し、commit・push して、Gemini Code Assist などへ再レビューを依頼する。ユーザーが「レビューコメントに対応して」「Gemini Code Assist の指摘を直して」「プルリクエストの未解決レビューを処理して」「修正後に /gemini review して」などを依頼したときに使用する。
---

# Address Review Comment

## 概要

プルリクエストのレビューコメント対応を、次のループとして実行する。

1. 対象プルリクエストを特定する。
2. 未解決の review thread とプルリクエストコメントを取得する。
3. コメントを確認し、修正するか説明で返すか判断する。
4. 必要なファイルを修正し、検証する。
5. 変更を commit して push する。
6. コメントへ返信し、解決できる thread を resolve する。
7. `/gemini review` で再レビューを依頼し、追加コメントを確認する。

## リソース

- `scripts/fetch-review-state.sh`: プルリクエストのメタデータ、review thread、プルリクエストコメント、review summary を JSON で取得する。
- `references/github-cli.md`: スクリプトだけでは足りない場合の `gh` コマンド例を確認する。

まずはスクリプトを使い、失敗した場合や thread 返信・resolve などの個別操作が必要な場合だけリファレンスを読む。

## ワークフロー

### 1. 対象プルリクエストを特定する

ユーザーがプルリクエスト番号または URL を指定している場合は、それを使う。指定がない場合は現在のブランチに紐づくプルリクエストを確認する。

```bash
gh pr view --json number,url,headRefName,baseRefName,title,state
```

プルリクエストを 1 つに特定できない場合は、作業を進める前にユーザーへプルリクエスト番号または URL を確認する。

### 2. レビュー状態を取得する

補助スクリプトでレビュー状態を取得する。

```bash
.agents/skills/address-review-comment/scripts/fetch-review-state.sh <pr-number-or-url>
```

確認対象は次の順に優先する。

1. 未解決の review thread
2. 具体的な修正要求を含むプルリクエストコメント
3. review summary に含まれる具体的な修正要求

解決済み thread、単なる要約、対応不要な通知は作業対象にしない。

### 3. コメントを分類する

各コメントを次のどれかに分類する。

- **修正する**: 指摘が妥当で、今回のプルリクエストで直せる。
- **説明する**: 現在の内容が正しい、または指摘の前提が誤っている。
- **延期する**: 指摘は妥当だが、今回のプルリクエスト範囲外または対応規模が大きい。

延期する場合は、Issue を作成してから、その Issue URL と今回扱わない理由をコメントへ返信する。

### 4. 修正して検証する

修正するコメントでは、指摘されたファイルと周辺を読んでから最小限の変更を入れる。

修正後は、変更内容に合う検証を実行する。例:

```bash
bash -n <script>
git diff --check
```

プロジェクト固有のテストやビルドが必要な場合は、それも実行する。検証できなかった場合は、理由を返信や最終報告に明記する。

### 5. commit して push する

変更を commit し、プルリクエストブランチへ push する。push 前に再レビューを依頼してはならない。

```bash
git status --short
git add <changed-files>
git commit -m "<type>: <summary>"
git push origin HEAD
```

コミットメッセージは対象リポジトリの規約に従う。

### 6. コメントへ返信して resolve する

review thread を resolve する前に必ず返信する。

- 修正した場合: 何を直したか、どの検証を実行したかを書く。
- 説明する場合: なぜ変更しないかを書く。
- 延期する場合: 作成した Issue URL と、なぜ今回扱わないかを書く。

返信後、解決できる review thread を resolve する。詳細な `gh` 操作が必要な場合は `references/github-cli.md` を読む。

### 7. 延期用 Issue を作成する

延期するコメントがある場合は、対象リポジトリの Issue テンプレートに従って Issue を作成する。まず `.github/ISSUE_TEMPLATE/` を確認し、レビュー指摘の follow-up には task 系テンプレートを優先する。

テンプレートがない場合は、次のフォールバック本文を使う。

```markdown
## 背景

- <レビューコメントの内容と、今回のプルリクエストで扱わない理由>

## 完了条件

- [ ] <完了条件>

## 参照資料

- <レビューコメント URL またはプルリクエスト URL>
```

Issue 作成例:

```bash
gh issue create \
  --title "<follow-up title>" \
  --body-file <issue-body-file>
```

作成後、延期するレビューコメントへ Issue URL を返信する。

### 8. 再レビューを依頼する

push 後に Gemini Code Assist へ再レビューを依頼する。

```bash
gh pr comment <pr-number-or-url> --body "/gemini review"
```

### 9. 追加コメントを確認する

再レビュー依頼後は 1 分程度待ってからレビュー状態を再取得する。

```bash
.agents/skills/address-review-comment/scripts/fetch-review-state.sh <pr-number-or-url>
```

新しい未解決コメントがあれば、同じ手順で対応する。次のいずれかに到達したら停止する。

- 未解決かつ対応可能なコメントがなくなった。
- 残ったコメントすべてに「説明する」理由、または延期用 Issue URL を返信済みである。
- レビュー対応サイクルを 5 回完了した。

## ガードレール

- コメントへ返信せずに review thread を resolve しない。
- commit と push を行う前に `/gemini review` を投稿しない。
- `/gemini review` をローカル検証の代替にしない。
- ユーザーが明示的に依頼しない限り、5 回を超えてレビュー対応サイクルを続けない。
