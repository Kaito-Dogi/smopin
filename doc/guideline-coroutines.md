# Coroutines ガイドライン（Android / KMP）

## 目的

本ドキュメントは、Smopin の Android / Kotlin Multiplatform（KMP）において、Kotlin Coroutines を **再利用可能・拡張可能・テスト容易** に実装するための共通指針を定義する。

`doc/strategy-modularization.md` の「再利用性を重視する」方針、および `doc/architecture-layer-data.md` の「Repository / DataSource の責務分離」を前提に、coroutine の責務とスレッド制御を明確化する。

## 適用範囲

- `shared` 配下（domain / data / database 実装）
- Android UI（ViewModel, state holder）
- 将来の iOS 側連携時に shared の suspend / Flow を利用する箇所

## 設計原則

## 1. 構造化並行性を守る

- `GlobalScope` は使用しない。
- 親子関係が明確な `CoroutineScope` を使う。
- 並列実行は `coroutineScope` を第一選択とし、失敗独立性が必要な場合のみ `supervisorScope` を使う。

**なぜか**
- 画面破棄時の自動キャンセル（例: `viewModelScope`）により、不要な通信・リークを防げる。
- KMP shared のユースケースを Android/iOS 両方から呼ぶ際も、親スコープの寿命で動作を統一できる。

## 2. Dispatcher は注入する

- `Dispatchers.IO` などを実装に直書きしない。
- DataSource / UseCase / ViewModel へ `CoroutineDispatcher`（または `DispatcherProvider`）を DI する。

**なぜか**
- `doc/CONVENTION_CODING.md` の「DataSource に Dispatchers.IO をコンストラクタ注入する」規約に一致する。
- テスト時に `TestDispatcher` へ差し替え可能となり、`runTest` で決定的に検証できる。

## 3. レイヤ責務ごとに coroutine の役割を固定する

### UI（Android ViewModel）

- `viewModelScope.launch` を起点にする。
- 画面状態は `StateFlow`、単発イベントは `SharedFlow` を使い分ける。

### Domain（shared）

- ビジネスロジックを suspend / Flow で表現する。
- プラットフォーム依存の API を直接使わない（Dispatcher 注入経由）。

### Data（Repository / DataSource）

- Repository はデータ変換と集約に専念し、原則スレッド切替をしない。
- DataSource で `withContext(ioDispatcher)` を行い I/O 境界を閉じる。

**なぜか**
- `doc/architecture-layer-data.md` の責務分離に整合。
- レイヤごとの変更影響を局所化でき、機能追加時のリグレッション範囲を狭められる。

## 実装指針

## 1. API 設計

- ワンショット取得: `suspend fun`
- 継続監視: `Flow<T>`
- 画面状態: `StateFlow<UiState>`

命名は `doc/CONVENTION_CODING.md` に従い、List を返す場合は `xxxList` を明示する。

## 2. Cancellation

- `CancellationException` を握りつぶさない（再スローする）。
- 長時間ループは `isActive` / `ensureActive` で協調キャンセルを維持する。
- 中断できない後処理が必要な場合のみ `withContext(NonCancellable)` を使う。

## 3. Exception

- `launch`: 即時伝播（親へ）
- `async`: `await()` 時に再送出
- `CoroutineExceptionHandler`: 最上位でのロギング用途に限定する

## 4. Flow

- `flowOn` は上流責務として限定利用する。
- `stateIn/shareIn` は購読戦略（`SharingStarted`）を明示する。
- UI で不要な再描画が多い場合のみ `distinctUntilChanged` を付与する。

## 5. テスト

- `kotlinx-coroutines-test` の `runTest` を使う。
- `StandardTestDispatcher` を基本にし、`advanceUntilIdle` で進行制御する。
- 最低限、以下をテスト対象に含める。
  - 正常系
  - キャンセル
  - 例外伝播
  - タイムアウト / リトライ

## Android 画面・ユースケース想定の実践例

## 例1: 喫煙所一覧画面（Map/List）

- 画面表示時、ViewModel が `viewModelScope` で `ObserveSmokingAreaListUseCase` を購読。
- UseCase は shared domain で `Flow<List<SmokingArea>>` を返却。
- Repository は DataSource の Flow を domain model に変換するのみ。

**効果**
- Android では Compose state 更新へ直結でき、iOS でも shared の Flow を同じ契約で再利用できる。

## 例2: 喫煙所投稿（ワンショット）

- ViewModel から `CreateSmokingAreaUseCase` を `suspend` で実行。
- DataSource が `withContext(ioDispatcher)` で Firestore 書き込み。
- 失敗時は domain エラーに変換し UI に反映。

**効果**
- 失敗ポリシーを UI へ一貫して返せる。
- DataSource 以外の層に I/O 詳細が漏れない。

## モジュール分割が coroutines 運用に与える利点

`doc/strategy-modularization.md` の方針に基づき、次の効果を狙う。

- **再利用性**: shared domain/data の suspend/Flow 契約を Android/iOS で共有できる。
- **シンプルさを犠牲にして得るメリット**: Dispatcher 注入やインターフェース分離でコード量は増えるが、テスト差し替えと責務境界が明確化される。
- **拡張性**: Firestore 以外の DataSource 追加時も UseCase 契約を保ったまま差し替え可能。
- **テスト容易性**: レイヤ単位で TestDispatcher と Fake 実装を適用しやすい。
- **ビルド速度**: 影響範囲がモジュール境界で限定され、変更時の再ビルドを抑制しやすい。

## 採用しなかった代替案

## 代替案A: Repository で `withContext(Dispatchers.IO)` を直接実施

### 概要
Repository が DataSource 呼び出し前後で I/O 切替を担う設計。

### 採用しなかった理由
- `doc/architecture-layer-data.md` の「I/O 切り替えは DataSource 責務」に反する。
- Repository が変換・集約以外の責務を持ち、肥大化しやすい。
- テストでスレッド制御点が分散する。

### 有効になり得る条件
- 単一モジュールの短期 PoC で、DataSource 層を分けない設計に限定する場合。

## 代替案B: Dispatcher をハードコード（`Dispatchers.IO` を直書き）

### 概要
各実装クラスが直接 `Dispatchers.*` を参照する設計。

### 採用しなかった理由
- テストで差し替え困難となり、`runTest` の決定性を損なう。
- Android 固有実装に寄り、KMP shared の移植性が低下する。

### 有効になり得る条件
- チーム規模が極小で、短期間の実験実装を最速で作る場合。

## 参照資料

- https://developer.android.com/kotlin/coroutines
- https://developer.android.com/kotlin/coroutines/coroutines-adv
- https://medium.com/androiddevelopers/easy-coroutines-in-android-viewmodelscope-25bffb605471
- https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-coroutine-exception-handler/
- https://developer.android.com/kotlin/coroutines/test
- https://developer.android.com/kotlin/coroutines/coroutines-best-practices
- https://medium.com/androiddevelopers/coroutines-first-things-first-e6187bf3bb21
- https://medium.com/androiddevelopers/cancellation-in-coroutines-aa6b90163629
- https://medium.com/androiddevelopers/exceptions-in-coroutines-ce8da1ec060c
- https://medium.com/androiddevelopers/coroutines-patterns-for-work-that-shouldnt-be-cancelled-e26c40f142ad
- https://kotlinlang.org/docs/exception-handling.html
