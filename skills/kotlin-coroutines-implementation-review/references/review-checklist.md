# Kotlin Coroutines レビュー・チェックリスト

## A. API 設計

- [ ] ワンショット処理が `suspend fun` で定義されている
- [ ] 連続更新が `Flow` / `StateFlow` で定義されている
- [ ] 責務に応じて `suspend` と `Flow` が混同されていない

## B. スレッドと DI

- [ ] Dispatcher が注入されている（ハードコードなし）
- [ ] DataSource で `withContext(ioDispatcher)` を適用している
- [ ] Repository がスレッド切り替え責務を持っていない

## C. 構造化並行性

- [ ] `GlobalScope` を使用していない
- [ ] 並列実行に `coroutineScope` / `supervisorScope` の意図がある
- [ ] キャンセル時のリークがない（子 coroutine の孤児化なし）

## D. 例外・キャンセル

- [ ] `CancellationException` を再スローしている
- [ ] `runCatching` 等でキャンセルを誤って成功扱いしていない
- [ ] 例外のユーザー体験（再試行、表示、ログ）が定義されている

## E. Flow 運用

- [ ] `flowOn` の適用位置が妥当（上流のみ）
- [ ] `stateIn` / `shareIn` の `started` 戦略が妥当
- [ ] 不要な `distinctUntilChanged` や過剰な `buffer` がない

## F. テスト

- [ ] `runTest` を使用している
- [ ] 仮想時刻 API (`advanceUntilIdle` 等) を使っている
- [ ] キャンセル、例外、タイムアウトのケースがある
