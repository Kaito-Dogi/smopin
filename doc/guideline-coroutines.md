# Kotlin Coroutines ガイドライン

## 目的

本ドキュメントは、Android / Kotlin Multiplatform（KMP）を前提としたアプリ開発で、Kotlin Coroutines を安全かつ一貫して実装・レビューするための判断基準を定義する。

> 注記: 本ドキュメントは汎用ガイドとして記述する。プロジェクト固有のレイヤ設計・モジュール設計へ適用するときは `doc/strategy-modularization.md`、`doc/architecture-layer-data.md`、`doc/strategy-dependency-injection.md` の規約を優先してマッピングする。

---

## 1. 設計原則（Why）

### 1-1. Structured Concurrency を優先する理由

- 親子関係のあるジョブ管理により、キャンセルと例外伝播が予測可能になる。
- Android の画面ライフサイクル（`ViewModel` の生存期間）と自然に整合し、メモリリークや孤立ジョブを避けられる。
- KMP shared ロジックでも同じルールで運用でき、Android/iOS で挙動差が出にくい。

### 1-2. Dispatcher 注入を優先する理由

- テスト時に `StandardTestDispatcher` へ置換し、並行処理を決定論的に検証できる。
- I/O 境界を DataSource 側に閉じ込めることで責務を明確化できる。
- Android 端末依存のスケジューリング差を吸収しやすい。

### 1-3. Flow と suspend の使い分けを明確化する理由

- API 契約が明確になり、呼び出し側が「一回取得」か「継続監視」かを誤解しない。
- UI 層で `stateIn` / `shareIn` を選定しやすく、再購読や画面回転時の挙動が安定する。

---

## 2. レイヤ別ガイド

## 2-1. UI 層（Android 画面 / StateHolder）

### 方針

- 画面イベント起点の処理は `viewModelScope.launch` で実行する。
- `Flow` を `StateFlow` に変換する際、`SharingStarted.WhileSubscribed(timeout)` を基本とする。
- UI 表示に必要な状態は単一の `UiState` に集約し、ロード中・成功・失敗を表現する。

### 実装イメージ

- 例: 検索画面で、キーワード入力を `debounce` し、`flatMapLatest` で最新検索のみ有効化。
- 例: 地図画面で現在地更新を購読し、画面非表示時は収集停止。

### 禁止事項

- `GlobalScope` 利用
- `Dispatchers.IO` の ViewModel 直書き
- `launch` 内で例外を握りつぶして UI 状態を更新しない実装

## 2-2. Domain 層（UseCase / Repository interface）

### 方針

- UseCase はユースケース単位で `suspend` または `Flow` を返し、スレッド戦略を隠蔽する。
- 「複数 Repository 呼び出しを束ねる」責務は UseCase に置く。
- 失敗時の方針（即失敗/部分成功許容）を `coroutineScope` / `supervisorScope` で明示する。

### 実装イメージ

- 例: ユーザー詳細画面で `User` と `Badge` を並列取得。`User` は必須、`Badge` は任意。
- 例: 保存操作後に分析イベントを送る場合、保存成功を優先し分析イベント失敗はログ化。

## 2-3. Data 層（Repository 実装 / DataSource 実装）

### 方針

- DataSource で `withContext(ioDispatcher)` を用いて I/O 切り替えを行う。
- Repository はモデル変換とデータ統合に集中し、ビジネスルールは持たない。
- `CancellationException` を握りつぶさない。

### 実装イメージ

- 例: `UserRepository.getUserList()` は NetworkDataSource の結果をドメインモデルへ変換。
- 例: `UserRepository.observeUser(userId)` は Disk/Remote の Flow を統合。

---

## 3. 例外処理・キャンセル

## 3-1. 基本原則

- `launch` の例外は親へ伝播する。
- `async` の例外は `await()` 時に表面化する。
- `CoroutineExceptionHandler` は最終ハンドラであり、復旧ロジックは別途明示する。

## 3-2. キャンセル

- キャンセルは正常系制御の一部として扱う。
- `runCatching` 使用時は `CancellationException` を再 throw する。
- 長時間ループは `ensureActive()` で協調キャンセルを実装する。

## 3-3. 完遂が必要な処理

- 投稿・送信など「途中中断が不正」な処理は、画面スコープでなくアプリスコープで実行する。
- ただし無制限再試行は避け、上限回数・指数バックオフ・打ち切り条件を定義する。

---

## 4. Flow 運用

### 演算子選定ルール

- `flatMapLatest`: 最新入力のみ有効（検索、フィルタ）
- `combine`: 複数状態の合成（設定 + データ）
- `debounce`: 高頻度入力の抑制（テキスト入力）
- `distinctUntilChanged`: 重複再描画防止

### Hot Flow 化

- 画面表示状態を持つ場合は `stateIn` を使う。
- イベント配信（1回消費）には `SharedFlow` を使う。
- バッファサイズと `replay` は最小限にする。

---

## 5. テスト戦略

### 必須セット

- `runTest`
- `MainDispatcher` 差し替えルール
- 仮想時間 API（`advanceTimeBy`, `advanceUntilIdle`）

### テスト観点

- 成功パス
- 失敗パス
- キャンセルパス
- 時間依存演算子（`debounce`, `timeout`）

### 具体例（Android 画面）

- 検索画面: 連続入力時に最後のキーワードだけで API 呼び出しされること。
- 詳細画面: 画面離脱時に収集ジョブが確実に停止すること。

### 具体例（KMP shared ロジック）

- 共有 UseCase の並列取得ロジックで、片方失敗時の挙動が仕様通りであること。
- iOS 連携時に Flow 変換後の状態更新順序が変わらないこと。

---

## 6. レビュー観点（短縮版）

- スコープ境界: `viewModelScope` / アプリスコープの使い分けが妥当か
- Dispatcher: 直書きがなく注入されているか
- 例外: `CancellationException` が正しく伝播するか
- Flow: 演算子選定に意図があるか
- テスト: 時間依存・キャンセルを含めて検証しているか

---

## 7. 代替案との比較

## 代替案 A: 単純なコールバック + Executor ベース

### 概要

Coroutines/Flow を使わず、コールバックで非同期処理を接続する。

### 採用しなかった理由

- 例外伝播とキャンセル伝播が分断され、UI・Domain・Data を跨ぐ制御が煩雑になる。
- KMP shared で Android/iOS 間の非同期契約を揃えにくい。

### 有効になり得る条件

- 短期の PoC で非同期パターンがごく少ない場合。
- 既存ライブラリ都合で Coroutines 導入コストが過大な場合。

## 代替案 B: すべて suspend に統一し Flow を使わない

### 概要

継続監視用途も含めて都度 `suspend` 再取得で実装する。

### 採用しなかった理由

- 状態監視の表現力が落ち、再購読や差分更新の効率が悪化する。
- 画面回転・復帰時の再取得戦略が分散し、State 管理が複雑化する。

### 有効になり得る条件

- 更新頻度が非常に低く、単発 API 取得のみで成立する管理画面。
- MVP 初期で状態監視要件が存在しない場合。

---

## 8. プロジェクト文脈への適用メモ

以下の既存方針と整合させる。

- DataSource で I/O を切り替える（`doc/CONVENTION_CODING.md`）
- Repository を SSOT とし、Repository にビジネスロジックを置かない（`doc/architecture-layer-data.md`）
- モジュール境界を維持し、依存性逆転を崩さない（`doc/strategy-modularization.md`）
- Dispatcher やスコープ提供を DI で切り替え可能にする（`doc/strategy-dependency-injection.md`）

この整合を維持することで、再利用性・拡張性・テスト容易性・ビルド最適化の各利点を、Coroutines 実装でも一貫して享受できる。
