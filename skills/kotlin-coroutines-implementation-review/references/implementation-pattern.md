# Kotlin Coroutines 実装パターン（Android / KMP）

## 1. Dispatcher

- `Dispatchers.IO` / `Dispatchers.Default` / `Dispatchers.Main` を直接呼び出し側に書かない。
- `DispatcherProvider` もしくは個別 Dispatcher を DI で注入する。
- DataSource で I/O 切り替えを行い、Repository は変換と集約に集中する。

### 推奨インターフェース例

```kotlin
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
}
```

## 2. Scope ownership

- 画面スコープ: `viewModelScope`
- アプリ横断の常駐スコープ: Application/DI で明示的に管理
- 関数内の子 coroutine: `coroutineScope` を標準採用

## 3. Flow選定

- 一回取得: `suspend fun`
- 監視更新: `Flow<T>`
- UI 状態公開: `StateFlow<UiState>`
- 単発イベント: `SharedFlow<UiEvent>`（`replay=0` 基本）

## 4. Cancellation

- `catch (e: CancellationException)` を握りつぶさない。
- `finally` でのクリーンアップは non-cancellable が必要な場合のみ `withContext(NonCancellable)` を使う。

## 5. Exception

- `launch` は親に例外伝播、`async` は `await()` まで遅延。
- `CoroutineExceptionHandler` は最上位でのログ/監視用。回復ロジックには使わない。

## 6. Test

- `runTest` を使い、`Thread.sleep` を禁止。
- Main dispatcher を使うコードは `Dispatchers.setMain(testDispatcher)` で置換。
- `Flow` テストは収集キャンセル漏れを避ける。
