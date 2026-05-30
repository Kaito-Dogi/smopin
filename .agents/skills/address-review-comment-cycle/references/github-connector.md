# GitHub Connector リファレンス

`gh api graphql` でレビュー会話の解決が `Resource not accessible by personal access token` などの権限エラーになる場合に参照する。

## 使いどころ

- `gh api graphql` で `resolveReviewThread` が失敗する
- PAT の権限や SAML 制約で `gh` からは操作できない
- Codex 環境で GitHub connector が利用できる

## 代替手順

GitHub connector の `_resolve_review_thread` を使って、GraphQL の review thread node ID を直接解決する。

```text
mcp__codex_apps__github._resolve_review_thread
```

必要なものは次のとおり。

- `thread_id`：`PRRT_...` 形式の review thread node ID

## 注意

- 返信を先に投稿してから解決する
- GitHub connector が使えない場合は、GitHub 上で手動解決するか、`gh` の認証スコープを見直す
