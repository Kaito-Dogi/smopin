# Kotlin Coroutines 代表スニペット

## 1. Dispatcher 注入

```kotlin
class UserNetworkDataSource(
  private val ioDispatcher: CoroutineDispatcher,
) {
  suspend fun getUserList(): List<UserDataModel> = withContext(ioDispatcher) {
    // network call
    emptyList()
  }
}
```

## 2. キャンセルを壊さない Result 変換

```kotlin
suspend fun <T> safeCall(block: suspend () -> T): Result<T> =
  try {
    Result.success(block())
  } catch (throwable: Throwable) {
    if (throwable is CancellationException) throw throwable
    Result.failure(throwable)
  }
```

## 3. 並列実行と失敗方針の明示

```kotlin
suspend fun loadUserAndBadge(
  userRepository: UserRepository,
  badgeRepository: BadgeRepository,
): Pair<User, Badge?> = supervisorScope {
  val userDeferred = async { userRepository.getUser() }
  val badgeDeferred = async {
    runCatching { badgeRepository.getBadge() }.getOrNull()
  }
  userDeferred.await() to badgeDeferred.await()
}
```

## 4. ViewModel での Flow 変換

```kotlin
val uiState: StateFlow<UserUiState> = query
  .debounce(300)
  .distinctUntilChanged()
  .flatMapLatest { keyword -> repository.observeUserList(keyword) }
  .map { users -> UserUiState(users = users, isLoading = false) }
  .stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = UserUiState(isLoading = true),
  )
```

## 5. runTest で時間検証

```kotlin
@Test
fun debounceの後に最新入力のみが反映される() = runTest {
  val resultList = mutableListOf<String>()
  val flow = MutableSharedFlow<String>(extraBufferCapacity = 10)

  val job = launch {
    flow.debounce(300).collect(resultList::add)
  }

  flow.emit("u")
  flow.emit("us")
  flow.emit("use")
  advanceTimeBy(300)
  advanceUntilIdle()

  assertEquals(listOf("use"), resultList)
  job.cancel()
}
```
