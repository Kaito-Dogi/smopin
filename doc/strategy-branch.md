# ブランチ戦略

## 目的

本ドキュメントは、AI エージェントと人間の開発者が同じ Git 運用ルールで作業し、変更の意図とレビュー単位を揃えられることを目的とする。

## 基本方針

本プロジェクトのブランチ戦略は GitHub Flow をベースとする。

未リリース段階のため、`hotfix` ブランチや `release` ブランチは運用しない。常に短命な feature ブランチを作成し、プルリクエストでレビューした後にベースブランチへマージする。

## ベースブランチ

原則として `main` ブランチをベースブランチとする。

ただし、作業に必要な差分が `main` に含まれていない場合のみ、その差分を含む `feature` ブランチをベースブランチとしてよい。この場合、プルリクエスト本文に `main` ではなく feature ブランチをベースにした理由を記載する。

## ブランチ作成

ブランチの作成と切り替えには `git switch` コマンドを使用する。

```bash
git switch main
git pull --ff-only
git switch -c feature/ISSUE-89_create-pull-request-skill
```

## ブランチ命名規則

ブランチ名は次の形式とする。

```text
feature/ISSUE-{Issue Number}_{Subject}
```

- `{Issue Number}` には GitHub Issue 番号を入れる
- `{Subject}` には変更内容をローワーケバブケースで端的に書く
- AI エージェント名をブランチ名に含めてはならない
  - NG: `feature/ISSUE-89_codex-create-pull-request-skill`
  - NG: `feature/ISSUE-89_claude-create-pull-request-skill`

## プルリクエスト

プルリクエストは `gh` コマンドで作成する。

```bash
gh pr create --base main --head feature/ISSUE-89_create-pull-request-skill --title "docs: add git workflow documents" --body-file /tmp/pr-body.md
```

AI エージェントは、人間から明示的に止められていない限り、変更、検証、コミット、プルリクエスト作成まで自律的に行う。

プルリクエスト本文は `.github/pull_request_template.md` が存在する場合、そのテンプレートに従う。テンプレートが存在しない場合でも、少なくとも概要、変更内容、検証内容、関連 Issue を記載する。

プルリクエストのタイトルは Conventional Commits の形式で、差分の概要を端的にまとめる。

## プルリクエストの差分量

プルリクエストの差分は最大 200 行を目安とする。

ただし、次のように人間が意味を把握しやすい差分は例外とする。

- ドキュメントの変更
- コードの自動生成
- 大規模なリネーム
- 機械的なリファクタリング
- 無理に分けることで意味的に不自然になる変更

## なぜこの設計なのか

GitHub Flow を採用する理由は、本プロジェクトが未リリース段階であり、Android 画面、KMP shared ロジック、iOS 実装の変更を小さな feature ブランチ単位で素早くレビューしたいためである。

例えば、Android の地図画面で喫煙所ピンの表示を変更する場合、`android:feature:map` の UI 変更だけでなく、`shared:domain` のモデルや `shared:data` の Repository 実装に変更が波及する可能性がある。短命な feature ブランチで作業することで、画面単位ではなく機能単位で差分を確認できる。

この方針は `doc/strategy-modularization.md` の「再利用性を最優先し、シンプルさを犠牲にしてでも拡張性、テスト容易性、ビルド速度を得る」という考え方と一致する。ブランチ単位を Issue と機能に揃えることで、KMP shared ロジックを他プロジェクトへ転用する際にも、どの変更がどの意図で行われたかを追跡しやすくなる。

## 代替案との比較

### Git Flow

`develop`、`release`、`hotfix` ブランチを使う運用である。

今回は未リリース段階であり、リリース管理よりも小さな機能変更を素早くレビューすることを優先するため採用しない。リリース版を複数系統で保守し、緊急修正を本番へ即時反映する必要が出た場合には有効になり得る。

### main への直接コミット

feature ブランチを作らず、`main` に直接コミットする運用である。

短期検証や 1 人開発では最もシンプルだが、AI エージェントが Android、KMP shared、iOS の複数レイヤにまたがる変更を行う場合、レビュー前の差分が `main` に混入しやすい。再利用性とレビュー容易性を優先する本プロジェクトでは採用しない。
