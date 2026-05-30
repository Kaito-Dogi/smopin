# ブランチ戦略

## 目的

本ドキュメントは、複数セッションが同時に進行する AI エージェントを活用した開発において、作業のコンフリクトを削減し、開発効率と品質を高めることを目的とする。

## 基本方針

GitHub Flow をベースとする。

未リリース段階のため、`hotfix` ブランチや `release` ブランチは運用しない。短命な `feature` ブランチを作成し、プルリクエストでレビューした後にベースブランチにマージする。

## ベースブランチ

原則として `main` ブランチをベースとする。

ただし、作業に必要な差分が `main` ブランチに含まれていない場合にのみ、その差分を含む `feature` ブランチをベースとしてよい。この場合、`main` ブランチではなく `feature` ブランチをベースにした理由をプルリクエスト本文に記載する。

## ブランチ作成

ブランチの作成と切り替えには `git switch` コマンドを使用する。

```bash
git switch main
git pull --ff-only
git switch -c feature/ISSUE-89_create-pull-request-skill
```

## ブランチ命名規則

```text
feature/ISSUE-{issue-number}_{subject}
```

- `{issue-number}` には GitHub Issue 番号を入れる
- `{subject}` には Issue の概要をローワーケバブケースで端的に書く
- AI エージェント名をブランチ名に含めてはならない
  - NG: `codex/review-issue-#89-on-github`

## 禁止事項

- `main` ブランチに直接コミットすること
