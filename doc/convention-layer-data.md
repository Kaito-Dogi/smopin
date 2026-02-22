# Data Layer

## 目的

このドキュメントは、`shared:data` / `shared:database:*` を中心とした Data Layer の設計・実装方針を定義する。`doc/strategy-modularization.md` の優先順位（再利用性 > シンプルさ > その他マルチモジュール利点）および依存性逆転の方針を前提とする。

## 原則

| 原則 | ルール | ねらい |
|:--|:--|:--|
| SSOT | Repository を Data Layer の SSOT とする | UI から見たデータ取得経路を 1 つに統一し、差し替え容易性を高める |
| 依存性逆転 | Domain Layer で定義された Repository インターフェースを Data Layer で実装する | 実装詳細を `shared:data` 側に閉じ込め、UI / UseCase が実装詳細に依存しないようにする |
| 公開境界 | UI Layer へは Domain Layer のインターフェース経由でのみデータ公開する | モジュール境界を保ち、実装差し替え・テスト容易性を高める |

## コンポーネント

### Repository

#### 役割

| 観点 | ルール |
|:--|:--|
| UI への公開 | Android ではワンショットは `suspend function`、継続監視は `Flow` で公開する |
| 抽象化 | 複数 DataSource の競合を解決し、ドメインモデル単位で API を整理する |
| ビジネスロジック | 重複排除・再利用性向上の観点で必要な処理のみ保持可。複雑化する場合は UseCase に切り出す |
| 命名 | Repository が他 Repository に依存する場合は `Manager` と命名する |

#### 実装例（Repository が Domain インターフェースを実装）

```kt
@Inject
internal class DefaultSmokingAreaRepository(
  private val smokingAreaNetworkDataSource: SmokingAreaNetworkDataSource,
) : SmokingAreaRepository {
  override suspend fun getSmokingAreaList(): List<SmokingArea> {
    return smokingAreaNetworkDataSource.getSmokingAreaList()
      .map(transform = SmokingAreaDataModel::toDomainModel)
  }
}
```

### DataSource

#### 役割

| 観点 | ルール |
|:--|:--|
| 単一責務 | 1 つのデータソースのみを処理する（Network / Database / Kvs / File / Cache） |
| スレッド | `Dispatchers.IO` はコンストラクタインジェクションし、実装時点で `withContext` を適用する |
| 責務分離 | 外部 API / SDK の呼び出し詳細を閉じ込め、Repository へ生データを返す |

#### 実装例（Firestore 側の NetworkDataSource）

```kt
@Inject
internal class DefaultSmokingAreaNetworkDataSource(
  @param:AppDispatcher(dispatcher = AppDispatchers.IO)
  private val ioDispatcher: CoroutineDispatcher,
) : SmokingAreaNetworkDataSource {
  override suspend fun getSmokingAreaList(): List<SmokingAreaDataModel> =
    withContext(context = ioDispatcher) {
      List(size = 3) {
        SmokingAreaDataModel(
          name = "Mock Smoking Area $it",
          latitude = 35.6889544 + it * 0.1,
          longitude = 139.6992443 + it * 0.1,
        )
      }
    }
}
```

### Model

| 観点 | ルール |
|:--|:--|
| 分離 | Domain のドメインモデルと Data のモデルを分離する |
| 変換責務 | Domain 変換は Repository（または Data モジュール内の mapper）で実施する |
| 影響範囲 | DataSource スキーマ変更の影響を UI Layer に波及させない |

#### 実装例（DataModel -> DomainModel）

```kt
data class SmokingAreaDataModel(
  val name: String,
  val latitude: Double,
  val longitude: Double,
)

fun SmokingAreaDataModel.toDomainModel() = SmokingArea(
  name = name,
  location = Location(
    latitude = Latitude(value = latitude),
    longitude = Longitude(value = longitude),
  ),
)
```

## 処理の種類

| 種類 | ライフサイクル | 代表例 | 配置方針 |
|:--|:--|:--|:--|
| UI 指向 | 画面滞在中のみ有効。離脱でキャンセル | ViewModel 起点の読み込み | UI Layer が開始・キャンセル制御 |
| アプリケーション指向 | アプリ起動中のみ有効。プロセス終了で停止 | アプリ全体同期・プリフェッチ | Application / Data Layer が制御 |
| ビジネス指向 | プロセス終了後も継続が必要 | WorkManager などのバックグラウンド処理 | 永続実行基盤を Data Layer で抽象化 |

## エラーハンドリング

Data Layer では原則としてエラーを握りつぶさず、Domain Layer（UseCase）にハンドリングを委譲する。🚨

- 理由 1: Data Layer の責務を I/O と変換に限定できる
- 理由 2: 画面仕様に依存したエラー文言・リトライ戦略を Domain / UI で決定できる
- 理由 3: テスト観点（例外発生時の分岐）を UseCase 側に集約できる

## 処理の実装方法

### Android

Android の実装は以下をベースにする。

- <https://developer.android.com/topic/architecture/data-layer#common-tasks>

#### Worker の依存関係 🚨

| コンポーネント | 扱い | 依存先 |
|:--|:--|:--|
| Worker | UI Layer のコンポーネント（ViewModel 同等） | Repository に依存させる（DataSource へ直接依存しない） |
| WorkManager | Data Layer のコンポーネント | DataSource で抽象化して扱う |

#### Worker を配置する Layer 🚨

- Data Layer に配置する
- 具体的には WorkManager を抽象化した DataSource と同一パッケージに置く

### iOS

KMP Shared を呼び出す iOS 実装では、次を原則とする。

| 観点 | 方針 |
|:--|:--|
| 依存解決 | iOS 側 DI モジュールから Domain インターフェースを注入し、Data 実装を直接参照しない |
| 非同期 | Swift 側からは `suspend` API を `async/await` ブリッジ経由で利用する |
| 役割分離 | iOS 固有 API（位置情報権限など）が必要な場合のみ iOS DataSource を追加し、Shared の Repository 境界を維持する |

## テスト

### 単体テスト

Repository の単体テストでは FakeDataSource を実装し、成功・失敗パスを検証する。

```kt
private class FakeSmokingAreaNetworkDataSource(
  private val smokingAreaList: List<SmokingAreaDataModel> = fakeSmokingAreaDataModelList,
  private val shouldThrow: Boolean = false,
) : SmokingAreaNetworkDataSource {
  override suspend fun getSmokingAreaList(): List<SmokingAreaDataModel> = if (!shouldThrow) {
    smokingAreaList
  } else {
    throw Exception()
  }
}
```

### 結合テスト

- DataSource 実装（例: Firestore）と Repository の接続を確認する
- 本番 SDK を利用する場合はテスト環境（エミュレータ / モックサーバ）を分離し、Domain モデルへの変換結果を検証する

## 採用判断と代替案

### この構成を採用する理由

`doc/strategy-modularization.md` の優先順位に従い、次を重視する。

1. 再利用性: `shared:domain` API を固定し、`shared:data` / `shared:database:*` を差し替え可能にする
2. シンプルさとのトレードオフ: mapper・interface が増えるが、実装境界が明確になり AI 補助開発でも破綻しにくい
3. その他利点: モジュール単位テスト、影響範囲の局所化、ビルドキャッシュ活用による開発効率向上

### 代替案 A: Data/Domain を統合した単一 shared モジュール

- 概要: Repository 実装と Domain モデルを同一モジュールで管理する
- 不採用理由: 実装詳細が UI に漏れやすく、差し替え・テスト戦略が弱くなる
- 有効条件: 小規模チームで短期検証を優先し、保守期間が短い PoC

### 代替案 B: Repository で例外を捕捉し Result 型へ正規化

- 概要: Data Layer で例外を握って `Result` などへ変換して返却
- 不採用理由: Data 層が画面都合の失敗分類を持ち始め、責務が肥大化しやすい
- 有効条件: 複数プラットフォームで完全に同一の失敗分類を厳密保証したい場合

## 参考リンク

- [Data layer | App architecture | Android Developers](https://developer.android.com/topic/architecture/data-layer)
