#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat >&2 <<'USAGE'
使い方: fetch-review-state.sh [--unresolved] [<pull-request-number-or-url>]

プルリクエストのメタデータ、レビュー会話、プルリクエストコメント、レビュー要約を
1 つの JSON として出力します。プルリクエスト引数を省略した場合は、gh が現在の
ブランチからプルリクエストを推測します。

--unresolved を指定した場合は、未解決のレビュー会話だけを TSV で出力します。
USAGE
}

if [[ "${1:-}" == "-h" || "${1:-}" == "--help" ]]; then
  usage
  exit 0
fi

for cmd in gh jq; do
  if ! command -v "$cmd" >/dev/null 2>&1; then
    echo "エラー: $cmd がインストールされていないか、PATH に通っていません。" >&2
    exit 1
  fi
done

unresolved_only=false
pr_arg=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    --unresolved)
      unresolved_only=true
      shift
      ;;
    *)
      if [[ -n "$pr_arg" ]]; then
        usage
        exit 1
      fi
      pr_arg="$1"
      shift
      ;;
  esac
done

if [[ -n "$pr_arg" ]]; then
  pr_json=$(gh pr view "$pr_arg" --json number,url,headRefName,baseRefName,title,state)
else
  pr_json=$(gh pr view --json number,url,headRefName,baseRefName,title,state)
fi

if [[ -z "$pr_json" ]] || ! jq -e . <<<"$pr_json" >/dev/null 2>&1; then
  echo "エラー: プルリクエスト情報を取得できませんでした、または無効な JSON が返されました。" >&2
  exit 1
fi

owner=$(jq -r '.url | if type == "string" then split("/")[3] else empty end' <<<"$pr_json")
repo=$(jq -r '.url | if type == "string" then split("/")[4] else empty end' <<<"$pr_json")
number=$(jq -r '.number // empty' <<<"$pr_json")

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

if [[ -z "$threads_json" ]]; then
  echo "エラー: GitHub API から空のレスポンスが返されました。" >&2
  exit 1
fi

if ! jq -e . <<<"$threads_json" >/dev/null 2>&1; then
  echo "エラー: GitHub API から無効な JSON レスポンスが返されました。" >&2
  exit 1
fi

if jq -e '.errors' <<<"$threads_json" >/dev/null; then
  echo "GraphQL エラー:" >&2
  jq '.errors' <<<"$threads_json" >&2
  exit 1
fi

jq -n \
  --argjson pr "$pr_json" \
  --argjson reviewState "$threads_json" \
  '{ pullRequest: $pr, reviewState: ($reviewState | .data?.repository?.pullRequest // null) }' |
  if [[ "$unresolved_only" == true ]]; then
    jq -r '.reviewState.reviewThreads.nodes[]
      | select(.isResolved == false)
      | [
          .id,
          .path,
          ((.comments.nodes[0].databaseId // "") | tostring),
          (.comments.nodes[0].url // "")
        ]
      | @tsv'
  else
    cat
  fi
