# DI 戦略

## 目的

本ドキュメントは、開発者が本プロジェクトの DI 戦略を理解し、ソースコードに反映できることを目的とする

## 対象読者

- 一般的な Android/KMP 開発者
- Android/KMP 初学の iOS 開発者

## 対象とするモジュール

- `shared`, `android` モジュールを対象とする
- `app-for-ios` モジュールで iOS の依存グラフを組み立てる（TBD）

## 基本方針

| 観点                                              | 理由                                                                                                |
|:------------------------------------------------|:--------------------------------------------------------------------------------------------------|
| Domain Layer のコンポーネントを Android/iOS 共通で使えるようにする  | KMP の利点（共有ロジック）を最大限活かすため                                                                          |
| 依存グラフは各 OS のアプリモジュールで構築する                       | OS 固有の依存をアプリモジュールに集約するため\n構築するアプリによって 同じ OS でも必要な依存関係が変わるので、アプリモジュールに依存グラフを構築する責務を与えてシンプルな構成とするため |
| UseCase, Repository, DataSource はインターフェースで抽象化する | テストなどで実装を差し替えやすくするため                                                                              |
| 具体実装はモジュール内で `internal` にする（可視性を最低限に絞る）         | 実装の詳細を隠蔽し、依存先で誤って使用してしまうことを防ぐため                                                                   |

## DI ライブラリ

DI フレームワーク [ZacSweers/metro](https://github.com/ZacSweers/metro) を採用する

### 採用理由

- KMP 対応であるため
- 依存解決が静的で、コンパイル時にエラーを検出でき、デバッグしやすいため
- Hilt に近い使用感で、Android 開発者の学習コストが小さいため

## 依存グラフの構成

- Hilt のアプリケーションレベルのコンポーネントに相当する概念として、Metro の `DependencyGraph` を定義する
- 必要な依存を Binding Container で分割しながら1つの依存グラフを構築する

| 観点                              | 理由                                               |
|:--------------------------------|:-------------------------------------------------|
| `@BindingContainer` で依存グラフを分割する | Hilt モジュールに相当する概念のため\n責務ごとに分割でき、依存グラフの見通しを良くするため |
| `@GraphExtension` を使用しない        | TBD（ `GraphExtension` の使いどころを理解できていない）           |

## スコープ

| 観点                      | 理由                                                                                                                                                                         |
|:------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `AppScope` を使用する        | 現時点では、シングルトンパターンで、アプリケーションレベルで扱う依存のみを注入しているため                                                                                                                              |
| `@ContributesTo` を使用しない | Metro の仕様上、依存グラフを `public` にする必要があり、 `@ContributesTo` を使用しても Binding Container 自体は `internal` にして隠蔽できないため\n依存グラフの定義元で、依存する Binding Container を列挙されておらず、メンテナンス性に欠けると判断できるため 
| 必要に応じて分割する              | TBD（ `ViewModel` など UI Layer で扱うコンポーネントを実装するタイミングで考える）                                                                                                                     |

## インターフェースの注入

`@Binds` を使用する

### 理由

- インターフェースに対して、実装の詳細をシンプルに紐づけられるため
- `@Provides` を使用したメソッド定義では、メソッドの返り値として実装クラスをインスタンス化する必要があり、依存グラフの見通しが悪くなってしまうため

### インターフェース定義例

```kotlin
interface UserRepository {
  suspend fun getUserList(): List<User>
}

interface UserNetworkDataSource {
  suspend fun getUserList(): List<UserDataModel>
}
```

### インターフェース実装例

```kotlin
@Inject
internal class DefaultUserRepository(
  private val userNetworkDataSource: UserNetworkDataSource,
) : UserRepository {
  override suspend fun getUserList(): List<User> {
    return userNetworkDataSource.getUserList()
      .map(UserDataModel::toDomainModel)
  }
}
```

### Binding Container 例

- `abstract class` で定義する
  - 実装クラスを `internal` にして、モジュール内に隠蔽するため
  - `interface` では、プロパティを `internal` にできないため
    - 補足： `@Binds` では、インターフェースを型として、実装クラスの拡張プロパティを定義する必要がある

```kotlin
@ContributesTo(scope = AppScope::class)
@BindingContainer
abstract class DataBindingContainer internal constructor() {

  @Binds
  internal abstract val DefaultUserRepository.bind: UserRepository
}
```

## インスタンスの注入

`@Provides` を使用する

### 制約

CoroutineDispatcher, Clock, Context など、外部ライブラリのインスタンスを注入する場合にのみ使用する

### Binding Container 例

- `object` で定義する
  - Metro の仕様上、`interface` や `abstract class` では `@Provides` を付与したメソッドを定義できないため
    - `companion object` 内に定義すれば解決できるが、ライブラリの仕様に合わせた定義となり、直感的ではない

```kotlin
@ContributesTo(scope = AppScope::class)
@BindingContainer
object AppDispatcherBindingContainer {

  @Provides
  @AppDispatcher(dispatcher = AppDispatchers.Default)
  fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

  @Provides
  @AppDispatcher(dispatcher = AppDispatchers.IO)
  fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
```

### Binding Container の分割基準

- 責務単位で分割する
  - 基本的に、1つのモジュールにつき1つの Binding Container を定義する
- 例外的に、 `shared:common` モジュールではコンポーネントごとに分割する
  - 汎用的な Kotlin 依存のコンポーネントを格納するため
  -
  参考： [Now in Android の
  `:core:common` モジュール](https://github.com/android/nowinandroid/tree/main/core/common/src/main/kotlin/com/google/samples/apps/nowinandroid/core)

## テスト

- 実装クラスを Fake 実装に差し替える
- 具体的な方法・手順は TBD

## 参考資料

- [Metro ドキュメント](https://zacsweers.github.io/metro/latest/)
- [DroidKaigi 2025 official app](https://github.com/DroidKaigi/conference-app-2025)
