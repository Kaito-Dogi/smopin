# GitHub Connector リファレンス

Codex アプリで GitHub へ操作するときの基本リファレンスとして参照する。`gh` が不安定な場合の代替手段に限定せず、GitHub connector を `gh` と同格のデフォルト選択肢として扱う。

## 使いどころ

- Codex アプリから GitHub へ直接操作したい
- `gh` が不安定、または認証や権限で失敗する
- プルリクエスト作成、Issue 作成、コメント返信、レビュー会話の解決をツール経由で行いたい
- Codex 環境で GitHub connector が利用できる

## 主な操作

### プルリクエストを作成する

```text
mcp__codex_apps__github._create_pull_request
```

- `repository_full_name`：`owner/name`
- `base`：ベースブランチ
- `head`：プルリクエストブランチ
- `title`：プルリクエストタイトル
- `body`：プルリクエスト本文

### Issue を作成する

```text
mcp__codex_apps__github._create_issue
```

- `repository_full_name`：`owner/name`
- `title`：Issue タイトル
- `body`：Issue 本文

### インラインレビューコメントに返信する

```text
mcp__codex_apps__github._reply_to_review_comment
```

- `repo_full_name`：`owner/name`
- `pr_number`：プルリクエスト番号
- `comment_id`：スレッド先頭のレビューコメント ID
- `comment`：返信本文

### プルリクエスト会話へコメントする

```text
mcp__codex_apps__github._add_comment_to_issue
```

- `repo_full_name`：`owner/name`
- `pr_number`：プルリクエスト番号
- `comment`：コメント本文

### レビュー会話を解決済みにする

```text
mcp__codex_apps__github._resolve_review_thread
```

- `thread_id`：`PRRT_...` 形式の review thread node ID

## 注意

- 返信を先に投稿してから解決する
- パラメータ名は実際の connector schema に合わせる
- `mcp__codex_apps__github._create_pull_request` と `mcp__codex_apps__github._create_issue` では `repository_full_name` を使う
- `mcp__codex_apps__github._reply_to_review_comment` と `mcp__codex_apps__github._add_comment_to_issue` では `repo_full_name` を使う
- `mcp__codex_apps__github._add_comment_to_issue` はプルリクエスト会話への投稿でも schema 上の `pr_number` を使う
- GitHub connector に存在しない操作や、現在のセッションで tool が公開されていない操作だけ `gh` を使う
- GitHub connector が使えない場合は、GitHub 上で手動対応するか、`gh` の認証スコープを見直す
