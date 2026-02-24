# データレイヤ

## 目的

本ドキュメントは、開発者が本プロジェクトのアーキテクチャのうち、データレイヤの設計を理解し、ソースコードに反映できることを目的とする。

## 基本方針

- Repository と DataSource の2種類のコンポーネントで構成する
- Repository をデータレイヤの SSOT（Single Source of Truth）とする
- ドメインレイヤで Repository のインターフェースを定義し、UI レイヤにデータを公開する
- データレイヤの具体実装モジュールで、Repository のインターフェースを実装する

## コンポーネント

### データモデル

データレイヤで使用するモデル定義を、データモデルと呼ぶ。

#### 責務

- ドメインレイヤで扱うモデルとデータレイヤで扱うモデルを分離し、データレイヤの変更がドメインレイヤに直接影響しないようにする

#### 規約

- データモデルは Repository でドメインモデルに変換される
- ドメインレイヤ、UI レイヤではデータモデルを直接参照してはならない

#### 定義例

```kt
@Serializable
data class UserDataModel(
  val userId: String,
  val name: String,
)
```

- モデル定義を簡略化するため、`UserId` など、バリューオブジェクトに当たるモデル定義はしない
- 具体実装のモジュールで参照するため、可視性を `public` にする

### Repository

#### 責務

1. 複数の DataSource をまとめ、ドメインモデル単位で抽象化する
2. データモデルをドメインモデルに変換する
3. UI レイヤにデータを公開する

#### 規約

- ドメインレイヤで Repository のインターフェースを定義する
- `shared:data` モジュールで Repository のインターフェースを実装する
- ビジネスロジックを保持してはならない
  - 原則として ViewModel がビジネスロジックを保持する
  - 重複するビジネスロジックや複雑なビジネスロジックは UseCase に切り出す
- Repository は他の Repository に依存してはならない
  - 参考：他の Repository への依存を許容し、他の Repository に依存する Repository を、Manager と命名する設計がある（参考：[Multiple levels of repositories](https://developer.android.com/topic/architecture/data-layer#multiple-levels)）

#### インターフェース例

```kt
interface UserRepository {
  suspend fun getUserList(): List<User>
  suspend fun createUser(user: User)
  fun getUser(userId: UserId): Flow<User>
  suspend fun updateUser(user: User)
  suspend fun deleteUser(userId: UserId)
}
```

- CRUD 処理は `create`, `get`, `update`, `delete` と命名する
- ワンショットな読み取り処理は `suspend fun` で定義する
- オブザーバルな読み取り処理は `suspend fun` を使用せず、返り値に `Flow` を使用する
- 返り値が List の場合、メソッド名に接尾辞 `List` をつける

#### 実装例

```kt
@Inject
internal class DefaultUserRepository(
  private val userNetworkDataSource: UserNetworkDataSource,
  private val userDiskDataSource: UserDiskDataSource,
) : UserRepository {
  override suspend fun getUserList(): List<User> = userNetworkDataSource.getUserList()
    .map(transform = UserMapper::toDomainModel)

  override fun getUser(userId: UserId): Flow<User> = userDiskDataSource.getUser(userId = userId)
    .map(transform = UserMapper::toDomainModel)

  // override suspend fun createUser(user: User)
  // override suspend fun updateUser(user: User)
  // override suspend fun deleteUser(userId: UserId)
}
```

- デフォルト実装のクラス名に接頭辞 `Default` をつける
  - 接尾辞 `Impl` をつけてはならない
- デフォルト実装の可視性は `internal` にする
  - Metro などの DI ライブラリを使用し、ライブラリ経由で依存グラフを構築するため
- I/O スレッドに切り替えてはならない
  - I/O スレッドへの切り替えは DataSource の責務のため
- データモデルをドメインモデルに変換する処理は Mapper オブジェクトに定義する
  - 変換ロジックが単純な場合や再利用しない場合も、Repository に直接記述してはならない

### DataSource

#### 責務

- データソースの依存を隠蔽する
- I/O スレッドに切り替える

#### 規約

- `shared:data` モジュールで DataSource のインターフェースを定義する
  - 具体実装のモジュールで DataSource のインターフェースを実装する
- 1つのデータソースのみを隠蔽する
- 具体的なソースの種類を重視するため、命名にソースの種類を含める
  - Network, Database, Preferences, File など
- Firebase などの外部 SDK は、`shared:data` で定義したラッパーインターフェース経由で呼び出す
  - SDK 実装や SDK 型を、DataSource インターフェースに漏らさない

#### インターフェース例

```kt
interface UserNetworkDataSource {
  suspend fun getUserList(): List<UserDataModel>
}
```

- CRUD 処理は `create`, `get`, `update`, `delete` と命名する
- ワンショットな読み取り処理は `suspend fun` で定義する
- オブザーバルな読み取り処理は `suspend fun` を使用せず、返り値に `Flow` を使用する
- 返り値が List の場合、メソッド名に接尾辞 `List` をつける

#### 実装例

```kt
@Inject
internal class DefaultUserNetworkDataSource(
  @param:AppDispatcher(dispatcher = AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
  private val userApi: UserApi,
) : UserNetworkDataSource {
  override suspend fun getUserList(): List<UserDataModel> =
    withContext(context = ioDispatcher) {
      userApi.getUserList()
        .map(transform = UserMapper::toDataModel)
    }
}
```

- デフォルト実装のクラス名に接頭辞 `Default` をつける
  - 接尾辞 `Impl` をつけてはならない
- デフォルト実装の可視性は `internal` にする
  - Metro などの DI ライブラリを使用し、ライブラリ経由で依存グラフを構築するため
- データソースで扱うモデルをデータモデルに変換する処理は Mapper オブジェクトに定義する
  - 変換ロジックが単純な場合や再利用しない場合も、DataSource に直接記述してはならない

### SDK ラッパー

#### 責務

- 外部 SDK の API 仕様差分を吸収する
- DataSource から SDK 依存を切り離し、テスト時に Fake 実装へ差し替えやすくする

#### 規約

- ラッパーインターフェースは `shared:data` に定義する
- ラッパー実装は具体実装モジュール（例：`shared:database:firestore`）に定義する
- ラッパーでは SDK 呼び出しに専念し、Repository の責務（ドメインモデル変換）は持たない

## 処理の種類

| 処理の種類      | ライフサイクル                                                                                    |
|:-----------|:-------------------------------------------------------------------------------------------|
| UI 指向      | ・UI レイヤでトリガーされ、呼び出し元のライフサイクルに従う<br/>・ユーザーが特定の画面にいる間にのみ処理される<br/>・ユーザーがその画面から移動したら、キャンセルされる |
| アプリケーション指向 | ・アプリやデータレイヤのライフサイクルに従う<br/>アプリが開いている間にのみ処理される<br/>・プロセスが終了したら、キャンセルされる                     |
| ビジネス指向     | ・キャンセルされず、プロセス終了後も継続させる<br/>・WorkManger などを使用し、バックグラウンドタスクとして実行する                          |

### アプリケーション指向の処理の実装方法

Repository にアプリケーションレベルの `CoroutineScope` を注入する。<br/>
参考：[Make an operation live longer than the screen](https://developer.android.com/topic/architecture/data-layer#make_an_operation_live_longer_than_the_screen)

```kt
// https://developer.android.com/topic/architecture/data-layer#make_an_operation_live_longer_than_the_screen
class NewsRepository(
  private val newsRemoteDataSource: NewsRemoteDataSource,
  private val externalScope: CoroutineScope
) {
  /* ... */

  suspend fun getLatestNews(refresh: Boolean = false): List<ArticleHeadline> {
    return if (refresh) {
      externalScope.async {
        newsRemoteDataSource.fetchLatestNews().also { networkResult ->
          // Thread-safe write to latestNews.
          latestNewsMutex.withLock {
            latestNews = networkResult
          }
        }
      }.await()
    } else {
      return latestNewsMutex.withLock { this.latestNews }
    }
  }
}
```

値を返すため、`Job` 型を返す `launch` ではなく、`async` を使用する。

### ビジネス指向の処理の実装方法（Android）

WorkManger を使用する。

| コンポーネント     | 該当レイヤ  | 実装方法                                                                                      |
|:------------|:-------|:------------------------------------------------------------------------------------------|
| Worker      | UI レイヤ | ・データレイヤ（WorkManager の具体実装モジュール）に配置する<br/>・DataSource ではなく、Repository に依存する（ViewModel と同様） |
| WorkManager | データレイヤ | ・DataSource で抽象化する（その他データソースと同様）<br/>・`WorkerDataSource` と命名する                            |

## エラーハンドリング

データレイヤではエラーをキャッチせず、ドメインレイヤでハンドリングする。<br/>
具体的な設計は TBD。

### 理由

1. データレイヤの責務をデータの I/O と変換に限定するため
2. 画面仕様に則したエラー文言やリトライ処理を、UI レイヤで実装できるため

## テスト

### 単体テスト

- FakeRepository, FakeDataSource を実装する
- Mock を使用するかは TBD

#### 実装例

```kt
// TBD
```

### 結合テスト

TBD

## 参考リンク

- [Data layer | App architecture | Android Developers](https://developer.android.com/topic/architecture/data-layer)
