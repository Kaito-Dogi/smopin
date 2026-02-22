# Domain Layer

## 目的

本ドキュメントは、本プロジェクトにおける Domain Layer の責務を明確化し、`shared/domain` を中心とした KMP コードに対して、実装判断の一貫性を持たせることを目的とする。

`strategy-modularization.md` で定義した「再利用性を最優先し、シンプルさとのトレードオフを許容する」方針に従い、Domain Layer は **他プラットフォームで再利用したい業務知識の保管場所**として扱う。

## 前提（Android Developers の Domain Layer との再解釈）

Android Developers の Domain Layer は、実質的に UseCase 層を指す文脈が強い。
一方、本プロジェクトは依存性逆転を導入し、`shared/domain` にドメインモデルと Repository インターフェースを置く設計を採用している。

そのため本プロジェクトの Domain Layer は、以下の 2 つを含む。

| 要素 | 役割 | 例 |
|:--|:--|:--|
| Domain Model | 業務知識を表す不変な型 | `SmokingArea`, `Location`, `Latitude`, `Longitude` |
| Domain Contract | Data Layer 実装を切り替えるための抽象 | `SmokingAreaRepository` |

UseCase は「必須コンポーネント」ではなく、複雑さや再利用性の必要性が高い場合にのみ導入する。

## 設計原則

### 1. 不変性（Immutable）を原則とする

Domain Model は `val` ベースで定義し、状態を保持しない。
これにより UI・Data・iOS/Android 間で同じ意味を保ったまま受け渡しでき、並行実行時の破壊的変更リスクを下げられる。

### 2. 知識単位でパッケージを切る

`strategy-modularization.md` の方針どおり、`model` / `repository` の責務別パッケージは作らず、`smokingArea` のような業務知識単位でまとめる。

### 3. 依存方向を固定する

Domain Layer は下位レイヤ（Data 実装詳細、Platform API、UI フレームワーク）に依存しない。
Data Layer が Domain のインターフェースを実装することで、実装差し替えとテストを容易にする。

### 4. UseCase は「複雑さに対する投資」として導入する

以下の条件を 2 つ以上満たす場合に、UseCase 導入を検討する。

- 複数 Repository の合成が必要
- 画面をまたぐ同一ビジネスルールがある
- スレッド制御やエラーマッピングを共通化したい
- ViewModel に分岐・整形ロジックが増えている

単一 Repository の単純プロキシになるだけなら、UseCase は作らない。

## コンポーネント規約

## Domain Model

| 観点 | 規約 |
|:--|:--|
| 命名 | 業務上の概念名を採用する（例：`SmokingArea`） |
| 可変性 | `data class` + `val` を基本とする |
| バリデーション | 生成時に成立条件を担保できる型を優先する（Value Object） |
| 依存 | Kotlin 標準 + 必要最小限の共通ライブラリに限定する |

## Repository Interface

| 観点 | 規約 |
|:--|:--|
| 配置 | 該当の知識パッケージ配下に置く |
| 抽象度 | Domain Model を返す／受け取る。DataModel を露出しない |
| 非同期 | ワンショットは `suspend`、継続値は `Flow` を使い分ける |
| 例外 | Data Layer で握りつぶさず、Domain/UI 側で方針を決められる形で伝播させる |

## UseCase（任意）

| 観点 | 規約 |
|:--|:--|
| 導入条件 | 前節の「複雑さに対する投資」を満たすときのみ |
| 責務 | 複数データ源の合成、ビジネスルール適用、エラーマッピング |
| 責務外 | 単なる Repository 呼び出し中継 |
| 設計 | 状態を持たないクラス。`operator fun invoke()` を推奨 |

## 実装パターン（KMP / Android / iOS を想定）

### KMP shared における基本例

```kotlin
package app.kaito_dogi.smopin.shared.domain.smokingArea

class GetNearbySmokingAreasUseCase(
  private val smokingAreaRepository: SmokingAreaRepository,
) {
  suspend operator fun invoke(): List<SmokingArea> {
    return smokingAreaRepository
      .getSmokingAreaList()
      .filter { area ->
        // サンプル: 例として緯度経度の存在確認などを行う
        area.location.latitude.value in -90.0..90.0 &&
          area.location.longitude.value in -180.0..180.0
      }
  }
}
```

この形により、Android 画面（ViewModel）と iOS 画面（Presenter / ViewModel 相当）は同一の業務ルールを共有できる。

### Android での呼び出し例

Android Developers のガイドに従い、UI スコープから UseCase を呼び出す。
メインスレッドをブロックする可能性がある場合は、`withContext(Dispatchers.Default)` などで切り替える。

```kotlin
class SmokingAreaViewModel(
  private val getNearbySmokingAreasUseCase: GetNearbySmokingAreasUseCase,
) : ViewModel() {
  fun fetch() {
    viewModelScope.launch {
      val areas = withContext(Dispatchers.Default) {
        getNearbySmokingAreasUseCase()
      }
      // state 更新
    }
  }
}
```

### iOS での利用方針

- iOS 側は KMP の公開 API（Repository/UseCase）を呼び出し、業務ロジックを Swift に重複実装しない
- プラットフォーム固有の表示整形（日時フォーマット、文言）は iOS 側で担当する

## Util の静的メソッドに関する方針

ビジネスルールを `Util` の static 関数へ分散させる方法は、責務の発見性と依存追跡性が下がるため推奨しない。
業務ルールは Domain Model の操作または UseCase に集約し、呼び出し点から意味が辿れる構成を優先する。

## テスト規約

## 単体テスト

| 対象 | テスト方針 |
|:--|:--|
| Domain Model | 生成条件、不変性、同値性 |
| UseCase | FakeRepository を使った正常系・異常系・境界値 |

```kotlin
private class FakeSmokingAreaRepository(
  private val result: Result<List<SmokingArea>>,
) : SmokingAreaRepository {
  override suspend fun getSmokingAreaList(): List<SmokingArea> {
    return result.getOrThrow()
  }
}
```

## 結合テスト

- Data Layer 実装（`Default...Repository`）が Domain Contract を満たすかを確認する
- 例外伝播方針（握りつぶさない）を検証する

## 代替案との比較

## 代替案 A: Domain Layer を UseCase のみに限定する

### 概要

Android のガイド文脈に合わせ、Repository interface や Domain Model を Domain Layer から外し、UseCase だけを置く。

### 今回採用しない理由

- 本プロジェクトは KMP 前提であり、モデル契約まで共有しないと iOS/Android 間で業務知識の重複が起きる
- 依存性逆転の恩恵（Data 実装の差し替え、Fake によるテスト）を弱める

### 有効になり得る条件

- Android 単一プラットフォームで短期開発する場合
- 画面数・機能数が少なく、将来的な再利用を重視しない検証用途

## 代替案 B: UseCase を常に作成する

### 概要

すべての Repository 操作に 1:1 の UseCase を必ず作る。

### 今回採用しない理由

- 単純プロキシが大量発生し、認知負荷と保守コストが増える
- 本プロジェクトの「必要なときだけ複雑さを導入する」方針に反する

### 有効になり得る条件

- チーム規模が大きく、責務境界を強制したい場合
- アプリ全体で監査・ロギング・共通エラー処理を UseCase 層に一律実装したい場合

## 参考リンク

- [Domain layer | App architecture | Android Developers](https://developer.android.com/topic/architecture/domain-layer)
- [Guide to app architecture | App architecture | Android Developers](https://developer.android.com/topic/architecture)
