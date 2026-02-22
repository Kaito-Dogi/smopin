# Android UI レイヤ規約: Feature / StateHolder / TextField

## 1. 目的

本ドキュメントは Android Feature モジュールにおける UI レイヤ実装規約を定義する。`doc/strategy-modularization.md` と `doc/strategy-dependency-injection.md` の方針に従い、Feature 実装が Domain 抽象を利用して UI 状態を生成する構造を標準とする。

## 2. Feature モジュールの実装責務

### 2.1 実装責務マトリクス

| 層 | 主要責務 | 依存可能範囲 |
|:--|:--|:--|
| `Screen`（Composable） | UI 描画、UI ロジックの最小処理 | `UiState`, コールバック |
| `ViewModel` | ビジネスロジックの実行、`UiState` 生成 | `shared/domain` の抽象 |
| `UiState` | 画面描画に必要な最終状態の表現 | Domain model（読み取り用途） |

### 2.2 実装例（既存コードベース準拠）

`HogeScreen` / `HogeViewModel` のように、ViewModel で Repository を呼び出して状態を生成し、Composable は状態を描画する。

```kt
@Composable
fun HogeScreen(
  modifier: Modifier = Modifier,
  viewModel: HogeViewModel = metroViewModel(),
) {
  val uiState: HogeUiState by viewModel.uiState.collectAsStateWithLifecycle()

  LifecycleResumeEffect(key1 = Unit) {
    viewModel.onResume()
    onPauseOrDispose { }
  }

  Column(modifier = modifier) {
    uiState.smokingAreaList.forEach { smokingArea ->
      Text(text = smokingArea.name)
    }
  }
}
```

## 3. ViewModel イベント規約

### 3.1 命名

イベントハンドラは処理ベースの動詞で命名する。

| 例 | 判定 |
|:--|:--|
| `addBookmark(id)` | 推奨 |
| `logIn(username, password)` | 推奨 |
| `onButtonClicked()` | 非推奨（意図が不明瞭） |

### 3.2 決定木

| 発火元 | ロジック種別 | 実行場所 |
|:--|:--|:--|
| UI | ビジネスロジック | ViewModel |
| UI | UI ロジック | Composable / UI Logic State Holder |
| ViewModel | UI state 更新 | ViewModel 内で完結 |

## 4. ViewModel から UI への通知規約

#### 4.1 one-off event を直接流さない

`SharedFlow` 等でナビゲーションイベントを直接流す方式は原則採用しない。UI が解釈すべき情報は `UiState` として公開する。

```kt
data class LoginUiState(
  val isLoading: Boolean = false,
  val errorMessage: String? = null,
  val isUserLoggedIn: Boolean = false,
)
```

#### 4.2 メッセージ消費

一時メッセージを表示する場合も `UiState` に保持し、消費後に ViewModel へ通知して `null` に戻す。

```kt
data class LatestNewsUiState(
  val isLoading: Boolean = false,
  val userMessage: String? = null,
)
```

## 5. TextFieldState 規約

### 5.1 参照資料

- https://developer.android.com/develop/ui/compose/text/user-input?hl=ja&textfield=state-based
- https://developer.android.com/develop/ui/compose/text/migrate-state-based?hl=ja
- https://medium.com/androiddevelopers/effective-state-management-for-textfield-in-compose-d6e5b070fbe5
- https://star-zero.medium.com/textfieldstate%E3%81%AE%E4%BD%BF%E3%81%84%E6%96%B9-5ac430ddda88

### 5.2 基本方針

ViewModel で `TextFieldState` を保持してよい。状態ベースの TextField を標準とする。

```kt
class LoginViewModel : ViewModel() {
  val usernameState = TextFieldState()
  val passwordState = TextFieldState()
}
```

### 5.3 使い分け

| API | 用途 |
|:--|:--|
| `InputTransformation` | 入力中フィルタ |
| `OutputTransformation` | 表示前フォーマット（value-based の `VisualTransformation` 相当） |
| `SecureTextField` | パスワード入力 |

### 5.4 バリデーション戦略

入力検証を非同期で行う場合は `snapshotFlow` と `mapLatest` を利用し、古い検証処理をキャンセルする。

```kt
class SignUpViewModel : ViewModel() {
  var username by mutableStateOf("")
    private set

  private val _userNameHasError = MutableStateFlow(false)
  val userNameHasError: StateFlow<Boolean> = _userNameHasError.asStateFlow()

  fun updateUsername(newUsername: String) {
    username = newUsername
  }

  init {
    viewModelScope.launch {
      snapshotFlow { username }
        .mapLatest { latestUsername ->
          latestUsername.length < 3
        }
        .collect { hasError ->
          _userNameHasError.value = hasError
        }
    }
  }
}
```

## 6. State Holder 規約

### 6.1 分割方針

| 種類 | 役割 | 再利用性 |
|:--|:--|:--|
| ビジネスロジック State Holder（ViewModel） | Data Layer/Domain から UI State を生成 | 画面単位で再利用しやすい |
| UI ロジック State Holder | 展開・選択・スクロールなど UI 要素状態を管理 | 複数画面・複数要素で再利用しやすい |

### 6.2 依存ルール

State Holder は同一寿命またはより長寿命の State Holder にのみ依存する。ViewModel が Activity/Composable を参照する依存は禁止する。

### 6.3 Composable と ViewModel の結合制約

原則として、下位 Composable に ViewModel インスタンスを直接渡さない。`UiState` とイベントコールバックを渡す。

## 7. UI State 生成規約

### 7.1 ストリーム合成

複数ソースを合成する際は `combine` を優先し、`stateIn` の `SharingStarted` は要件で選択する。

| 設定 | 適用条件 |
|:--|:--|
| `SharingStarted.WhileSubscribed()` | UI が可視の間だけ更新したい |
| `SharingStarted.Lazily` | バックスタック/タブ復帰を見越して維持したい |

### 7.2 初期化

`init` で重い非同期処理を直接起動しない。`initialize()` を MainThread で呼び出す構造を推奨する。

## 8. 代替案との比較

### 8.1 代替案 A: すべての UI 操作を ViewModel メソッド経由にする

| 観点 | 評価 |
|:--|:--|
| 概要 | 展開状態やローカル UI 状態も全て ViewModel 管理 |
| 不採用理由 | UI 細部の変更で ViewModel が肥大化し、プレビュー容易性が低下 |
| 有効条件 | XML View ベースで UI 状態量が少ない画面 |

### 8.2 代替案 B: `MutableStateFlow<String>` ベースで TextField を管理

| 観点 | 評価 |
|:--|:--|
| 概要 | テキスト値だけを Flow で保持 |
| 不採用理由 | 入力中状態の表現・カーソル制御・フォーマット対応が難しくなる |
| 有効条件 | 入力項目が最小で、編集体験要件が厳しくない短期実装 |

## 9. マルチモジュール観点での意義

本規約を採用することで、`android/feature` は UI 仕様変更に集中し、`shared/domain` は業務仕様に集中できる。結果として、再利用性の向上に加え、変更影響の局所化、テスト設計の単純化、モジュール単位ビルドの効率化が期待できる。
