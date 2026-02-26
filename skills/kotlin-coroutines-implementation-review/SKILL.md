---
name: kotlin-coroutines-implementation-review
description: Android/Kotlin Multiplatform で Kotlin Coroutines を実装・レビューするための実践スキル。Repository/DataSource/ViewModel/UseCase の責務ごとの Dispatcher 設計、Structured Concurrency、キャンセル、例外処理、Flow 運用、テスト戦略を一貫して適用したいときに使う。
---

# Kotlin Coroutines 実装・レビュー手順

以下を順番に実施する。

## 1) 変更対象を責務ごとに分類する

- UI 層（Compose/SwiftUI と接続する StateHolder）
- Domain 層（UseCase, Repository interface）
- Data 層（Repository 実装, DataSource 実装）

分類時点で次を明確化する。

- 呼び出し元が「一回取得（suspend）」か「継続監視（Flow）」か
- キャンセル可能であるべき処理か、完遂が必要な処理か
- 例外を UI へ伝播するか、Result 型に変換するか

## 2) スコープ境界を固定する

- `ViewModel` では `viewModelScope` を使う
- ライフサイクル境界を超える処理は `applicationScope` 相当を明示的に使う
- `GlobalScope` は使わない

設計判断:

- 画面離脱で止めるべき処理: `viewModelScope`
- 送信・保存など完遂優先処理: 依存注入した外部スコープ + `supervisorScope`

## 3) Dispatcher を注入する

- DataSource で `withContext(ioDispatcher)` を使う
- CPU バウンド処理は `defaultDispatcher`
- Main 固定処理以外で `Dispatchers.IO` などを直書きしない

## 4) Structured Concurrency を守る

- 並列実行は `coroutineScope { async { ... } }` で親子関係を保持する
- 子失敗時の方針を明示する
  - 全体失敗: `coroutineScope`
  - 部分成功許容: `supervisorScope` + 個別ハンドリング

## 5) Flow 契約を明示する

- Repository API で「状態監視」は `Flow<T>`
- 使う演算子の意図をコメントまたは命名で残す
  - 入力追従: `flatMapLatest`
  - 同時収集: `combine`
  - 重複削減: `distinctUntilChanged`
  - 高頻度入力抑制: `debounce`
- `flowOn` は「上流の重い処理」だけに適用する

## 6) キャンセル協調を入れる

- 長いループや I/O 直前で `ensureActive()` / `isActive` を使う
- `CancellationException` を握りつぶさない
- `runCatching` 利用時は `CancellationException` を再 throw する

## 7) 例外処理ポリシーを統一する

- `launch` と `async` の例外伝播差を意識する
- 末端 UI イベントは `CoroutineExceptionHandler` か ViewModel 内のエラーステートで吸収する
- Domain/Data で recover できない例外は握りつぶさず上位に伝える

## 8) テストを先に固定する

- `runTest` + `TestDispatcher` を使う
- `MainDispatcherRule` 相当で Main を差し替える
- Flow 検証は `first`, `toList`, `Turbine` 等で期待値を明示する
- 時間依存演算子は仮想時間（`advanceTimeBy`, `advanceUntilIdle`）で検証する

## 9) レビュー時の最終チェック

`references/review-checklist.md` を使用し、全項目を確認する。

## 出力フォーマット（レビューコメントテンプレート）

- `重大`: アプリクラッシュ、データ欠損、キャンセル不整合
- `重要`: スコープ誤り、Dispatcher 直書き、例外ポリシー不一致
- `改善`: 命名、演算子選定、テスト不足

コメント形式:

1. 事象（何が起きるか）
2. 原因（Coroutine の原則上なぜ問題か）
3. 修正案（最小変更での提案コード）
4. 影響範囲（UI/Domain/Data, Android/iOS/KMP）

## 代表スニペット

具体例は `references/snippets.md` を参照する。
