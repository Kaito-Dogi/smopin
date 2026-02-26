# Kotlin Coroutines レビュー・チェックリスト

## スコープ
- [ ] `GlobalScope` を使用していない
- [ ] 画面ライフサイクル処理は `viewModelScope` を使っている
- [ ] 完遂が必要な処理は外部スコープを明示している

## Dispatcher
- [ ] DataSource で `withContext(ioDispatcher)` を使用している
- [ ] `Dispatchers.IO/Default/Main` の直書きがない（DI 例外を除く）
- [ ] CPU バウンド処理と I/O バウンド処理を分離している

## Structured Concurrency
- [ ] 並列処理で `coroutineScope` / `supervisorScope` を意図的に使い分けている
- [ ] `async` の `await` 漏れがない
- [ ] 子コルーチン失敗時の期待挙動が定義されている

## キャンセル
- [ ] `CancellationException` を握りつぶしていない
- [ ] `runCatching` 内でキャンセルを再 throw している
- [ ] 長時間処理がキャンセル協調している

## 例外処理
- [ ] `launch` と `async` の例外伝播差を理解した実装になっている
- [ ] UI で表示すべきエラーとログ送信のみのエラーを区別している
- [ ] リトライ方針（回数、間隔、打ち切り条件）が明示されている

## Flow
- [ ] ワンショット取得を Flow にしていない（不要な場合）
- [ ] `stateIn/shareIn` の started 戦略が妥当
- [ ] `flatMapLatest/combine/debounce/distinctUntilChanged` の選定意図が説明可能

## テスト
- [ ] `runTest` を使用している
- [ ] Main Dispatcher をテストで差し替えている
- [ ] 時間依存処理を仮想時間で検証している
- [ ] 成功・失敗・キャンセルの 3 パスを検証している
