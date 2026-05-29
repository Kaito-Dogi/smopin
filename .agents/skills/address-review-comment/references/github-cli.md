# GitHub CLI reference for review-comment workflows

Use these commands when the helper script is insufficient or when you need finer control.

## Resolve PR number or URL

```bash
gh pr view <pr-number-or-url> --json number,url,headRefName,baseRefName,title,state
```

Without an argument, `gh pr view --json number,url,headRefName,baseRefName,title,state` uses the current branch when possible.

## Fetch review threads and comments

GitHub review conversation resolution requires GraphQL node IDs. Fetch review threads with:

```bash
gh api graphql \
  -F owner='<owner>' \
  -F name='<repo>' \
  -F number=<pr-number> \
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

## Reply to a review comment

For an inline review thread, reply to the latest relevant review comment ID:

```bash
gh api repos/<owner>/<repo>/pulls/<pr-number>/comments \
  -f body='<reply body>' \
  -F in_reply_to='<review-comment-database-id>'
```

If only a GraphQL node ID is available, fetch the REST `databaseId` from GraphQL before replying.

For general PR comments, use:

```bash
gh pr comment <pr-number-or-url> --body '<reply body>'
```

## Resolve a review thread

Resolve only after replying:

```bash
gh api graphql \
  -F threadId='<review-thread-node-id>' \
  -f query='mutation($threadId: ID!) { resolveReviewThread(input: { threadId: $threadId }) { thread { id isResolved } } }'
```

## Request Gemini Code Assist review

```bash
gh pr comment <pr-number-or-url> --body "/gemini review"
```

## Polling policy

Poll at one-minute intervals after requesting Gemini review. Stop after five response cycles, when no actionable Gemini comments remain, or when only explicitly-deferred low-priority comments remain.
