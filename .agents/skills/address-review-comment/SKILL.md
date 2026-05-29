---
name: address-review-comment
description: Resolve pull request review comments by fetching PR review threads with gh, applying necessary code changes, resolving conversations, requesting another Gemini Code Assist pass, and polling for follow-up review comments. Use when the user asks for 「レビューコメントの修正」, 「レビューコメントへの対応」, addressing PR review comments, fixing Gemini Code Assist comments, or rerunning Gemini review after review feedback.
---

# Address Review Comment

## Overview

Automate the review-feedback loop for a GitHub pull request: collect unresolved review conversations, decide whether each requires a code change, implement and validate fixes, resolve the conversation with a reason, request `/gemini review`, then poll for follow-up comments.

Keep the human reviewer out of the loop until AI/tool comments are addressed or intentionally deferred.

## Required project context

Before editing code in this repository, read the relevant `doc/` files and apply them as source-of-truth architecture constraints:

- `doc/strategy-modularization.md` for KMP module boundaries, dependency direction, and shared/android/ios responsibilities.
- `doc/architecture-layer-data.md` for Repository, DataSource, data-model, and mapper responsibilities.
- `doc/strategy-dependency-injection.md` for Metro binding, graph, visibility, and DI ownership.
- `doc/convention-coding.md` for naming, Kotlin expression bodies, model KDoc, `Default` implementation names, `internal` implementation visibility, and DataSource dispatcher rules.
- `doc/convention-version-catalog.md` before dependency or Gradle catalog changes.

If a review comment conflicts with these docs, prefer the docs and explain that in the reply before resolving the conversation.

## Workflow

### 1. Identify the target PR

Use the PR specified by the user. If the user did not specify a PR, use the PR created in the current session. If neither is available, inspect the current branch:

```bash
gh pr view --json number,url,headRefName,baseRefName,title
```

If the command cannot identify exactly one PR, ask the user for the PR URL or number.

### 2. Fetch unresolved review conversations

Prefer the bundled helper because it returns PR metadata, review threads, issue comments, and review summaries in one JSON document:

```bash
.agents/skills/address-review-comment/scripts/fetch-review-state.sh <pr-number-or-url>
```

If the helper fails or needs adjustment, read `references/github-cli.md` and run equivalent `gh pr view`, `gh api graphql`, or `gh pr comment` commands directly.

Focus on unresolved review threads first. Also consider issue comments and review summaries from Gemini Code Assist, other AI agents, and users when they request a concrete code change.

### 3. Triage each comment

For each unresolved conversation, classify it as one of:

- **Fix now**: The comment identifies a correctness, architecture, maintainability, test, naming, or documentation issue that should be addressed.
- **No code change**: The current code is correct, the requested change conflicts with `doc/`, or the suggestion is not applicable.
- **Defer**: The comment is valid but low priority, out of scope, blocked, or larger than the current review loop.

Default to fixing correctness and architecture issues. Defer only when the reason is concrete.

### 4. Apply changes and validate

For comments classified as **Fix now**:

1. Inspect the referenced files and nearby code.
2. Apply the smallest change that satisfies the comment and preserves the documented architecture.
3. Run the narrowest useful checks first, then broader checks when feasible.
4. Repeat until validation passes or a clear environment limitation is identified.

When editing this KMP project, preserve module boundaries: UI/feature code should depend on domain abstractions, shared data implementations should not leak into UI, DataSource implementations should own I/O dispatching, and DI wiring should stay in the app/platform graph or binding containers described in `doc/`.

### 5. Reply and resolve each conversation

After addressing a conversation, reply before resolving it:

- For **Fix now**, summarize the concrete fix and mention validation.
- For **No code change**, explain why no change is needed and cite the governing architecture/convention when relevant.
- For **Defer**, explain why it is intentionally left for later and, if appropriate, reference or create a follow-up issue.

Resolve the conversation with the GitHub GraphQL mutation from `references/github-cli.md`.

### 6. Commit and request another Gemini review

When code or skill files changed, commit the changes on the current branch using the repository's normal commit workflow. Then request Gemini Code Assist review on the PR:

```bash
gh pr comment <pr-number-or-url> --body "/gemini review"
```

### 7. Poll for follow-up review comments

After posting `/gemini review`, poll once per minute:

```bash
.agents/skills/address-review-comment/scripts/fetch-review-state.sh <pr-number-or-url>
```

Handle new actionable comments using the same triage/fix/reply/resolve loop.

Stop when one of these conditions is met:

- Gemini Code Assist has no unresolved actionable comments.
- Only low-priority comments remain and each has a written defer/no-change rationale.
- Five review-response cycles have completed.

When stopping, notify the user and include `@Kaito-Dogi` in the final message.

## Guardrails

- Do not resolve a conversation silently; always reply with the reason first.
- Do not mark a substantive unresolved comment as resolved until the fix is committed or a clear no-change/defer rationale is posted.
- Do not let `/gemini review` replace local validation; run relevant tests/checks before requesting review.
- Do not bypass project docs for quick fixes. If speed and architecture conflict, choose architecture and explain the tradeoff.
- Do not continue beyond five response cycles unless the user explicitly asks.
