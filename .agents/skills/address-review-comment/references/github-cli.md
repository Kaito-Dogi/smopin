# レビューコメント対応のための GitHub CLI リファレンス

補助スクリプトだけでは足りない場合、またはより細かく GitHub API を操作したい場合に参照する。

## プルリクエスト番号または URL を解決する

```bash
gh pr view <pull-request-number-or-url> --json number,url,headRefName,baseRefName,title,state
```

引数を省略すると、`gh pr view --json number,url,headRefName,baseRefName,title,state` は現在のブランチに紐づくプルリクエストを探す。

## review thread とコメントを取得する

GitHub の review conversation を resolve するには GraphQL の node ID が必要になる。review thread は次のように取得する。

```bash
gh api graphql \
  -f owner='<owner>' \
  -f name='<repo>' \
  -F number=<pull-request-number> \
  -f query='\
query($owner: String!, $name: String!, $number: Int!) {\
  repository(owner: $owner, name: $name) {\
    pullRequest(number: $number) {\
      number\
      url\
      reviewThreads(first: 100) {\
        nodes {\
          id\
          isResolved\
          isOutdated\
          path\
          line\
          startLine\
          comments(first: 50) {\
            nodes {\
              id\
              databaseId\
              author { login }\
              body\
              createdAt\
              url\
            }\
          }\
        }\
      }\
      comments(first: 100) {\
        nodes {\
          id\
          author { login }\
          body\
          createdAt\
          url\
        }\
      }\
      reviews(first: 100) {\
        nodes {\
          id\
          author { login }\
          body\
          state\
          submittedAt\
          url\
        }\
      }\
    }\
  }\
}'
```

## review comment に返信する

inline review thread には、対象 thread の最新の関連 review comment ID に返信する。

```bash
gh api repos/<owner>/<repo>/pulls/comments/<review-comment-database-id>/replies \
  -f body='<reply body>'
```

GraphQL node ID しか手元にない場合は、返信前に GraphQL で REST API 用の `databaseId` を取得する。

通常のプルリクエストコメントへ返信する場合は、次を使う。

```bash
gh pr comment <pull-request-number-or-url> --body '<reply body>'
```

## review thread を解決する

必ず返信してから resolve する。

```bash
gh api graphql \
  -F threadId='<review-thread-node-id>' \
  -f query='mutation($threadId: ID!) { resolveReviewThread(input: { threadId: $threadId }) { thread { id isResolved } } }'
```

## Gemini Code Assist に再レビューを依頼する

```bash
gh pr comment <pull-request-number-or-url> --body "/gemini review"
```

## ポーリング方針

Gemini Code Assist に再レビューを依頼した後は、1 分間隔で review state を確認する。次のいずれかに到達したら停止する。

- 対応可能な Gemini コメントが残っていない。
- 低優先度のコメントだけが残り、それぞれに延期またはコード変更なしの理由を返信済みである。
- レビュー対応サイクルを 5 回完了した。
