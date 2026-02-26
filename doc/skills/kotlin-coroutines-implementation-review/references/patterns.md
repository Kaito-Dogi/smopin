# Coroutines 典型パターン

## A. DataSource: Dispatcher 注入 + withContext

```kotlin
internal class DefaultSmokingAreaNetworkDataSource(
  @param:AppDispatcher(dispatcher = AppDispatchers.IO)
  private val ioDispatcher: CoroutineDispatcher,
  private val api: SmokingAreaApi,
) : SmokingAreaNetworkDataSource {
  override suspend fun getSmokingAreaDataModelList(): List<SmokingAreaDataModel> =
    withContext(context = ioDispatcher) {
      api.getSmokingAreaResponseList().map(transform = SmokingAreaMapper::toDataModel)
    }
}
```

## B. ViewModel: UI 指向の収集

```kotlin
class HogeViewModel(
  private val getSmokingAreaListUseCase: GetSmokingAreaListUseCase,
) : ViewModel() {
  private val _uiState = MutableStateFlow(HogeUiState())
  val uiState: StateFlow<HogeUiState> = _uiState

  fun onStart() {
    viewModelScope.launch {
      getSmokingAreaListUseCase()
        .onSuccess { smokingAreaList ->
          _uiState.update { state -> state.copy(smokingAreaList = smokingAreaList) }
        }
        .onFailure { throwable ->
          if (throwable is CancellationException) throw throwable
          _uiState.update { state -> state.copy(errorMessage = throwable.message) }
        }
    }
  }
}
```

## C. Repository: アプリケーション指向処理

```kotlin
class RefreshSmokingAreaRepository(
  private val networkDataSource: SmokingAreaNetworkDataSource,
  private val appScope: CoroutineScope,
) {
  suspend fun refresh(): Unit =
    appScope.launch {
      networkDataSource.getSmokingAreaDataModelList()
      // 保存処理
    }.join()
}
```

- 呼び出し元の画面が破棄されても継続すべき処理のみ適用する。
- 値を返す場合は `async/await` を使う。

## D. テスト: runTest + TestDispatcher

```kotlin
@Test
fun `refresh success updates cache`() = runTest {
  val testDispatcher = StandardTestDispatcher(testScheduler)
  val repository = RefreshSmokingAreaRepository(
    networkDataSource = FakeSmokingAreaNetworkDataSource(),
    appScope = CoroutineScope(testDispatcher + SupervisorJob()),
  )

  repository.refresh()
  advanceUntilIdle()

  assertTrue(repository.hasCache())
}
```

## E. 例外戦略の使い分け

- `coroutineScope`: 子の失敗で兄弟を全キャンセル（整合性重視）
- `supervisorScope`: 子の失敗を局所化（部分成功許容）
- `CoroutineExceptionHandler`: root coroutine の最終フォールバック
