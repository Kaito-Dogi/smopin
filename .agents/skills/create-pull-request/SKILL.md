---
name: create-pull-request
description: このリポジトリの Git 運用に沿って GitHub プルリクエストを作成する。ユーザーが PR 作成、プルリクエスト作成、作業完了後の PR 化、またはブランチ作成・コミット・push・PR 作成の一連の作業を依頼したときに使用する。
---

# Create Pull Request

このスキルは、リポジトリ上の作業をレビュー可能な GitHub プルリクエストとして完了するために使用する。

## 必ず読むドキュメント

ファイル変更または Git 運用コマンドを実行する前に、次を読む。

1. `AGENTS.md`
2. `docs/strategy-branch.md`
3. `docs/convention-commit.md`
4. 変更対象に関連する `docs/` 配下のドキュメント

ブランチ名、コミットメッセージ、プルリクエストタイトル、プルリクエスト本文は、上記ドキュメントを正とする。

## 作業手順

1. `git status --short --branch` で現在の状態を確認する
2. 現在のブランチが `feature/ISSUE-{issue-number}_{subject}` の形式に一致するか確認する
3. 新しいブランチが必要な場合は、`docs/strategy-branch.md` に従って `git switch` で作成する
4. 依頼された変更を行い、無関係なローカル変更は保持する
5. 自然に分割できる変更では、プルリクエストの差分を200行前後に収める
6. 最小限で有効な検証を先に実行し、変更範囲に応じてより広い検証を実行する
7. `docs/convention-commit.md` に従って Conventional Commits 形式でコミットする
8. ブランチを push する
9. `mkdir -p /tmp/pr-body` を実行する
10. プルリクエスト本文を作成し、`/tmp/pr-body/ISSUE-{issue-number}.md` に保存する
11. ユーザーが明示的に止めていない限り、追加の指示を待たずにプルリクエストを作成する

## プルリクエスト作成

プルリクエストは `gh pr create` で作成する。

このリポジトリでは、開発者が Fine-grained personal access tokens で `gh` 認証済みであることを前提とする。GitHub connector や MCP tool でプルリクエストを作成しない。

### ベースブランチ

- 原則として `main` を使用する
- `docs/strategy-branch.md` が許可する場合のみ、別の `feature` ブランチを使用してよい
- ベースブランチが `main` ではない場合は、その理由をプルリクエスト本文に記載する

### タイトル

- Conventional Commits 形式にする
- 「PR を作成したこと」ではなく、差分の概要を端的に書く

### 本文

- `.github/pull_request_template.md` が存在する場合は、そのテンプレートに従う
- テンプレートが存在しない場合は、下記のフォールバック本文を使用する
- 実行した検証コマンドと結果を記載する
- 環境制約で検証できない場合は、黙って省略せず制約を記載する

フォールバック本文：

```markdown
## 関連チケット

- close: #TBD

## 背景

## 対応したこと

## 対応していないこと

## UI 差分

|            Before            |            After             |
|:----------------------------:|:----------------------------:|
| <img src="" width="300px" /> | <img src="" width="300px" /> |

## 動作確認手順

- [ ] TBD

## 参照資料

- TBD
```

作成例：

```bash
gh pr create --base main --head feature/ISSUE-89_create-pull-request-skill --title "docs: add git workflow documents" --body-file /tmp/pr-body/ISSUE-89.md
```

## コミットメッセージ

`summary` だけでは変更意図が伝わらない場合は、次の形式で body を書く。

```text
<type>: <subject>

<なぜこの変更が必要なのか>
<なぜ代替案ではなくこの設計を選んだのか>
```

## 差分量

プルリクエストはレビューしやすい大きさに保つ。次のように分割すると理解しづらくなる変更では、200行前後の目安を超えてよい。

- ドキュメント変更
- 生成コード
- 大規模リネーム
- 機械的リファクタリング
- 意味的に1つのまとまりである変更

## 禁止事項

- `main` に直接コミットしない
- 未リリース段階では `release` ブランチや `hotfix` ブランチを作成しない
- ブランチ名に `codex` や `claude` などの AI エージェント名を含めない
- `git checkout -b` ではなく `git switch -c` を使用する
- コミットを単なる差分ログとして扱わない。設計意図が重要な変更では body に意図を残す
