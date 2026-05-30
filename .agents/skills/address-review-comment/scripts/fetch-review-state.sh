#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat >&2 <<'USAGE'
使い方: fetch-review-state.sh [<pr-number-or-url>]

プルリクエストのメタデータ、review thread、プルリクエストコメント、review summary を
1 つの JSON として出力します。プルリクエスト引数を省略した場合は、gh が現在の
ブランチからプルリクエストを推測します。
USAGE
}

if [[ "${1:-}" == "-h" || "${1:-}" == "--help" ]]; then
  usage
  exit 0
fi

pr_arg="${1:-}"

if [[ -n "$pr_arg" ]]; then
  pr_json=$(gh pr view "$pr_arg" --json number,url,headRefName,baseRefName,title,state)
else
  pr_json=$(gh pr view --json number,url,headRefName,baseRefName,title,state)
fi

owner=$(jq -r 'try (.url | split("/")[3]) catch null' <<<"$pr_json")
repo=$(jq -r 'try (.url | split("/")[4]) catch null' <<<"$pr_json")
number=$(jq -r '.number' <<<"$pr_json")

if [[ -z "$owner" || "$owner" == "null" || -z "$repo" || "$repo" == "null" || ! "$number" =~ ^[0-9]+$ ]]; then
  echo "エラー: プルリクエストのメタデータ（owner, repo, number）を正しく取得できませんでした。" >&2
  exit 1
fi

threads_json=$(gh api graphql \
  -f owner="$owner" \
  -f name="$repo" \
  -F number="$number" \
  -f query='
query($owner: String!, $name: String!, $number: Int!) {
  repository(owner: $owner, name: $name) {
    pullRequest(number: $number) {
      reviewThreads(first: 100) {
        nodes {
          id
          isResolved
          isOutdated
          path
          line
          startLine
          comments(first: 50) {
            nodes {
              id
              databaseId
              author { login }
              body
              createdAt
              url
            }
          }
        }
      }
      comments(first: 100) {
        nodes {
          id
          author { login }
          body
          createdAt
          url
        }
      }
      reviews(first: 100) {
        nodes {
          id
          author { login }
          body
          state
          submittedAt
          url
        }
      }
    }
  }
}')

if jq -e '.errors' <<<"$threads_json" >/dev/null; then
  echo "GraphQL エラー:" >&2
  jq '.errors' <<<"$threads_json" >&2
  exit 1
fi

jq -n \
  --argjson pr "$pr_json" \
  --argjson reviewState "$threads_json" \
  '{ pullRequest: $pr, reviewState: (try $reviewState.data.repository.pullRequest catch null) }'
