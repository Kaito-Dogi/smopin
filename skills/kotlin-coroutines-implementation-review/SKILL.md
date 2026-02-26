---
name: kotlin-coroutines-implementation-review
description: Kotlin Coroutines の実装・設計・コードレビューを、Android/Kotlin Multiplatform（KMP）前提で一貫して実施するためのスキル。CoroutineScope設計、Dispatcher注入、Flow運用、キャンセル/例外ハンドリング、テスト（runTest, TestDispatcher）を扱うタスクで使用する。
---

# Kotlin Coroutines Implementation & Review

1. `doc/guideline-coroutines.md` を最初に読み、プロジェクト規約を確認する。
2. レイヤ単位（UI / Domain / DataSource）で coroutine の責務を分離する。
3. 実装時は `Dispatchers` を直接参照せず、注入可能に設計する。
4. レビュー時は `references/review-checklist.md` の順に確認する。
5. テスト時は `kotlinx-coroutines-test` の仮想時刻制御を優先し、実時間待ちを禁止する。

## Workflow

### 1) Analyze

- 対象コードを UI / Domain / Data のどの責務か分類する。
- `suspend` / `Flow` / `StateFlow` / `SharedFlow` のどれが意図に一致するか判定する。
- 呼び出しライフサイクル（画面生存期間、アプリ生存期間、永続化タスク）を明確化する。

### 2) Design

- **UI（Android）**: ViewModel を coroutine 起点にし、`viewModelScope` を使う。
- **Domain（shared）**: `CoroutineDispatcher`/`DispatcherProvider` を受け取り、プラットフォーム依存を避ける。
- **DataSource**: I/O 境界で `withContext(ioDispatcher)` を適用する（Repository では切り替えない）。
- エラー方針（UI 表示、再試行、握りつぶし禁止）を先に定義する。

### 3) Implement

- 並列実行は原則 `coroutineScope` + `async`、失敗独立性が必要な場合だけ `supervisorScope`。
- 長時間処理は協調キャンセル（`isActive`, `ensureActive`）を維持する。
- `Flow` は cold stream を基本とし、`stateIn`/`shareIn` は必要最小限で使う。

### 4) Verify

- `runTest` を使用し、`StandardTestDispatcher` を標準とする。
- `advanceUntilIdle` / `runCurrent` で進行を制御する。
- 「キャンセル」「例外伝播」「リトライ」「タイムアウト」を必ず 1 ケース以上テストする。

## Resource Loading Guide

- 実装前提の詳細原則: `references/implementation-pattern.md`
- レビュー観点チェック: `references/review-checklist.md`
- 迷ったら先に `doc/guideline-coroutines.md` を優先し、差分のみ references を読む。
