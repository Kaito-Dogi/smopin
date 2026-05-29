---
name: create-pull-request
description: Create a GitHub pull request for this repository by following the documented Git workflow. Use when the user asks to create a PR, open a pull request, finish work with a PR, or perform the branch, commit, and PR sequence for repository changes.
---

# Create Pull Request

Use this skill to finish repository work with the project-defined Git workflow.

## Required reading

Before changing files or running Git workflow commands, read:

1. `AGENTS.md`
2. `doc/strategy-branch.md`
3. `doc/convention-commit.md`
4. Any `doc/` files relevant to the code or documentation being changed

These documents are the source of truth for branch names, commit messages, pull request titles, and pull request body expectations.

## Workflow

1. Inspect the current state.
   - Run `git status --short --branch`.
   - Confirm whether the current branch already matches `feature/ISSUE-{Issue Number}_{Subject}`.
2. Create a branch when needed.
   - Use `git switch`.
   - Default branch name: `feature/ISSUE-{Issue Number}_{Subject}`.
   - Do not include AI agent names such as `codex` or `claude` in the branch name.
3. Make the requested change.
   - Keep the pull request near 200 changed lines when the change can be split naturally.
   - It is acceptable to exceed 200 lines for documentation, generated code, large rename, mechanical refactor, or changes that would become semantically unnatural if split.
4. Validate the change.
   - Run the smallest useful command first, then broader checks if practical.
   - Document any environment limitation instead of silently skipping validation.
5. Commit the change.
   - Use Conventional Commits.
   - Put the summary in the subject.
   - Put the intent and design reason in the body when the subject alone is insufficient.
6. Create the pull request without waiting for another human instruction.
   - Use `gh pr create`.
   - Use `.github/pull_request_template.md` if it exists.
   - Use a Conventional Commit style title.
   - Set the base branch to `main` unless `doc/strategy-branch.md` allows a feature branch base for the current work.

## Commit template

```text
<type>: <subject>

<why this change is needed>
<why this design was selected over alternatives>
```

## Pull request body fallback

If `.github/pull_request_template.md` does not exist, use this structure:

```markdown
## Summary

- [change summary]

## Testing

- [test command and result]

## Related Issue

- Closes #<Issue Number>
```

## Gotchas

- Do not create `release` or `hotfix` branches while the product is unreleased.
- Do not use `git checkout -b`; use `git switch -c`.
- Do not omit the pull request step after committing unless the user explicitly says not to create a pull request.
- Do not treat commits as only a diff log; record intent in the commit body when design context matters.
