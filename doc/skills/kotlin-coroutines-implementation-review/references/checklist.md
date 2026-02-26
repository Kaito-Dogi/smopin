# Coroutines 実装・レビュー チェックリスト

## 1. レイヤ責務

- [ ] ViewModel では `viewModelScope` を使い、画面離脱時に不要処理がキャンセルされる
- [ ] UseCase はビジネスルールの合成に集中し、UI 状態や SDK 依存を持たない
- [ ] Repository は複数 DataSource の統合とドメイン変換を担当する
- [ ] DataSource は I/O と外部依存の隠蔽に限定され、`withContext(ioDispatcher)` で実行される

## 2. API 形状

- [ ] ワンショット読み取りは `suspend fun`
- [ ] 監視系は `Flow<T>`
- [ ] `Flow` の hot/cold を呼び出し側責務に合わせて明示 (`stateIn`, `shareIn`)
- [ ] `suspend` と `Flow` を混在させるとき、責務理由が KDoc/コメントで説明されている

## 3. キャンセル

- [ ] `catch` で `CancellationException` を再送出している
- [ ] 長時間処理で `isActive` / `ensureActive` を使って協調キャンセルしている
- [ ] リソース解放は `finally` で実施し、必要なら `withContext(NonCancellable)` を使う

## 4. 例外

- [ ] 例外は「発生箇所でログ」「変換は境界で最小限」「UI 文言は UI 側」で分離
- [ ] `CoroutineExceptionHandler` は root coroutine でのみ利用している
- [ ] `async` の例外は `await()` で回収される設計になっている
- [ ] 並列子タスクの失敗戦略（全体失敗 or 部分継続）が `coroutineScope` / `supervisorScope` で明示されている

## 5. Dispatcher / Scope 注入

- [ ] `AppDispatcher` などの qualifier で Dispatcher を注入
- [ ] Repository のアプリケーション指向処理に必要な場合のみ外部 `CoroutineScope` を注入
- [ ] `Dispatchers.Default/IO/Main` の直参照がテスト不可能な箇所に残っていない

## 6. テスト

- [ ] `runTest` + `StandardTestDispatcher` を使用
- [ ] `advanceUntilIdle` / `advanceTimeBy` で時間依存処理を決定的に検証
- [ ] Flow テストで `first`, `toList`, Turbine 等を利用し、キャンセル時の挙動も確認
- [ ] 失敗ケース（例外、タイムアウト、キャンセル）を少なくとも 1 ケース含む

## 7. Android / KMP 実務観点

- [ ] Android: ViewModel が `StateFlow<UiState>` を公開し、UI は collect のみ行う
- [ ] shared: `expect/actual` を使う場合、Dispatcher/時刻/ネットワーク境界を抽象化する
- [ ] iOS 連携: 長寿命 Flow をブリッジする際、購読解除時のキャンセル経路が定義されている
