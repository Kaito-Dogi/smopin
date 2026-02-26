# Kotlin Coroutines ガイドライン

## 目的

本ドキュメントは、Smopin の Android / KMP / iOS 連携において Kotlin Coroutines を一貫した設計で実装・レビューするための基準を示す。

合わせて、以下の既存方針を Coroutines 設計に接続する。

- `doc/strategy-modularization.md`
- `doc/strategy-dependency-injection.md`
- `doc/architecture-layer-data.md`
- `doc/CONVENTION_CODING.md`

## なぜこの設計なのか

Smopin ではモジュール分割の優先順位として **再利用性 > シンプルさ > その他利点（拡張性・テスト容易性・ビルド速度）** を採用している。
そのため Coroutines も「短く書けるか」より「責務境界を越えても破綻しないか」を優先する。

具体的には次を重視する。

1. **レイヤごとに Coroutine の責務を固定**し、再利用時の認知負荷を下げる
2. **Dispatcher と Scope の注入**でテスト可能性を確保する
3. **構造化並行性とキャンセル協調**で画面破棄・アプリ遷移時の不整合を防ぐ

この方針は、KMP shared で共通化したデータ取得処理を Android ViewModel と iOS 側購読の双方で安全に扱うために必要。

## レイヤ別ガイドライン

### UI レイヤ（Android 例: `HogeScreen` / `HogeViewModel`）

#### 役割

- 画面イベントを受けて UseCase/Repository を呼ぶ
- `StateFlow<UiState>` を公開し UI は購読に専念する
- 画面ライフサイクルに従って処理を開始・キャンセルする

#### 実装指針

- `viewModelScope` 以外のグローバルスコープは使わない
- 例外は UI 表示に必要な形へ変換するが、`CancellationException` は再送出する
- Flow を `stateIn` する場合、`SharingStarted` を明示する

### ドメインレイヤ（UseCase / ドメインモデル）

#### 役割

- ビジネスルールの合成
- エラー分類（ドメイン観点の意味づけ）

#### 実装指針

- `suspend` / `Flow` の選択理由を API で表現する
- 並列実行が必要な場合は `coroutineScope` を基本とする
- 部分成功を許容する場合のみ `supervisorScope` を使う

### データレイヤ（Repository / DataSource）

#### 役割

- Repository: DataSource の統合、ドメイン変換
- DataSource: 外部 SDK / API の隠蔽、I/O 実行

#### 実装指針

- DataSource 実装は `withContext(ioDispatcher)` を必須とする
- `Dispatchers.IO` を直参照せず、DI で注入する
- データレイヤでエラーを握りつぶさない（`architecture-layer-data.md` 準拠）

## 処理種別ごとの Scope 戦略

### UI 指向

- 例: 喫煙所一覧画面で初回ロード
- Scope: `viewModelScope`
- 理由: 画面離脱時は処理継続価値が低く、キャンセルが正しい

### アプリケーション指向

- 例: バックグラウンド同期間隔で喫煙所キャッシュ更新
- Scope: Repository に注入した `CoroutineScope`
- 理由: 画面に依存しないため、画面破棄で中断しない

### ビジネス指向

- 例: 必達の投稿再送、利用規約同意ログ送信
- Scope: WorkManager など OS 管理のジョブ
- 理由: プロセス死後も継続が必要

## API 設計規約

- ワンショット取得: `suspend fun getXxxList()`
- 監視取得: `fun observeXxxList(): Flow<List<Xxx>>`
- List 返却は命名に `List` を明示（`CONVENTION_CODING.md` 準拠）

## エラーハンドリング規約

1. DataSource: 例外をそのまま伝播（ログ最小限）
2. Repository/UseCase: 失敗の意味をドメインへ寄せる
3. ViewModel: 画面表示可能な `UiState` に落とす

補足:

- `CoroutineExceptionHandler` は root coroutine でのみ有効
- `async` の例外は `await` まで遅延するため、回収漏れを禁止

## キャンセル規約

- `catch` で `CancellationException` を検知したら再送出
- リソース解放は `finally` で実施
- キャンセル不可の後処理が必要な場合のみ `NonCancellable` を使う

## テスト規約

### 基本

- `runTest` と `StandardTestDispatcher` を使用
- 時間依存は `advanceTimeBy` / `advanceUntilIdle` で制御
- 失敗系（例外・キャンセル）を最低 1 ケース入れる

### Android 画面ユースケース例

- ケース: `HogeViewModel` が `onStart` で喫煙所一覧取得
- 観点:
  - 成功時に `UiState` が更新される
  - 失敗時にエラー文言が設定される
  - ViewModel 破棄時に処理がキャンセルされる

### KMP shared ロジック例

- ケース: `shared:data` の Repository が `shared:database:firestore` から取得
- 観点:
  - DataSource の Dispatcher が注入で差し替え可能
  - Mapper 変換が常に通る
  - 例外が握りつぶされずドメインまで到達する

## マルチモジュール分割が Coroutines に与える効果

### 再利用性

- `shared:data` の Coroutine 実装を Android/iOS 双方で使える
- Dispatcher 注入ルールを共通化すると、プラットフォーム差分を実装に漏らさない

### シンプルさを犠牲にしてでも得られるメリット

- Scope 注入・Mapper 分離・Repository/DataSource 分離でコード量は増える
- ただし以下が得られる:
  - 実装の置換容易性（Firestore → 別 backend）
  - AI/人間のレビュー観点の定型化
  - 大規模化時の破綻回避

### その他利点

- **拡張性**: 新しい機能モジュール追加時に Coroutine 責務を転用可能
- **テスト容易性**: 各層で TestDispatcher/Fake を差し込める
- **ビルド速度**: 変更影響範囲をモジュール単位で局所化できる

## 採用しなかった代替案

### 代替案 1: ViewModel から DataSource まで直接呼び出し（Repository 省略）

#### 概要

- 小規模構成として、UI → DataSource へ直結し `suspend` 呼び出しのみで実装する

#### 採用しなかった理由

- モジュール戦略の「依存性逆転」「再利用性」と矛盾する
- 例外方針・変換責務が UI に漏れ、レビュー軸が崩れる

#### 有効になり得る条件

- PoC で寿命が短い
- 機能が 1 画面のみで shared 化しない
- チーム 1 人で短期間検証する

### 代替案 2: DataSource 内で例外吸収し `Result` を返す

#### 概要

- I/O 境界で `runCatching` し、常に `Result<T>` を返す

#### 採用しなかった理由

- データレイヤの責務肥大化（UI 都合のエラー分類が混入）
- 既存方針「データレイヤでエラーをキャッチしない」に反する

#### 有効になり得る条件

- 外部 SDK が壊れやすく、データ層でリトライ戦略を統一したい
- ドメインが極小で、エラー型の厳密設計より実装速度を優先する

## 参照

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
