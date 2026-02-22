# Android Navigation（Compose）

## 目的

本ドキュメントは Android UI Layer における画面遷移の実装規約を定義する。  
`doc/convention-layer-ui-navigation-core.md` の共通方針を Android 実装へ落とし込み、feature モジュールの独立性と保守性を高める。

## 実装原則

### 1. entry は feature ごとに定義する

- `android/feature/*` が、自 feature の entry composable を公開する
- app（または navigation 集約層）は entry を組み合わせて NavHost を構成する

この構成により、feature の UI と遷移引数の知識を feature 内に閉じ込められる。

### 2. 画面遷移は Navigator 経由で実行する

Now in Android と同様、entry に遷移ラムダを直接大量に渡すのではなく、**責務単位の Navigator** を渡す。

- メリット
  - UI コンポーネントの引数が肥大化しにくい
  - 遷移の副作用（ログ計測、A/B フラグ、認可ガード）を集約しやすい
- デメリット
  - インターフェース設計と実装のボイラープレートが増える

### 3. 引数は「型付き Route オブジェクト」で表現する

`String` 連結で route を構築すると不整合を検知しにくいため、feature 側に route 型を置く。  
アプリ側は route 型経由でしか遷移しない。

## 推奨パッケージ構成（例）

| モジュール | パッケージ | 役割 |
|:--|:--|:--|
| `android:feature:login` | `feature.login.navigation` | `LoginRoute`, `loginEntry` |
| `android:feature:login` | `feature.login.ui` | 画面 UI、ViewModel |
| `android:app` | `app.navigation` | `AppNavHost`, navigator 実装 |

## サンプルコード

```kotlin
// android/feature/login/navigation/LoginRoute.kt
sealed interface LoginRoute {
    data object Top : LoginRoute
    data object Registration : LoginRoute
}

interface LoginNavigator {
    fun navigateToRegistration()
    fun navigateToSmokingAreaList(clearBackStack: Boolean)
}
```

```kotlin
// android/feature/login/navigation/LoginEntry.kt
fun NavGraphBuilder.loginEntry(
    loginNavigator: LoginNavigator,
) {
    composable(route = "login") {
        LoginScreen(
            onRegistrationClick = loginNavigator::navigateToRegistration,
            onLoginSuccess = {
                loginNavigator.navigateToSmokingAreaList(clearBackStack = true)
            },
        )
    }
}
```

```kotlin
// android/app/navigation/AppNavHost.kt
class AppLoginNavigator(
    private val navController: NavHostController,
) : LoginNavigator {
    override fun navigateToRegistration() {
        navController.navigate("registration")
    }

    override fun navigateToSmokingAreaList(clearBackStack: Boolean) {
        navController.navigate("smokingAreaList") {
            if (clearBackStack) {
                popUpTo("login") { inclusive = true }
            }
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController) {
    val loginNavigator = remember(navController) { AppLoginNavigator(navController) }

    NavHost(navController = navController, startDestination = "login") {
        loginEntry(loginNavigator = loginNavigator)
    }
}
```

## テスト方針

### Feature 単体テスト

- fake `LoginNavigator` を使い、`ViewModel` が正しい遷移要求を発行するかを検証する
- `NavController` 依存を持ち込まない

### Android 実装テスト

- app 側で `AppLoginNavigator` を検証し、`popUpTo` 設定や route 文字列を確認する

## 代替案との比較

### 代替案 A: entry に遷移ラムダをそのまま注入

- 概要：`onNavigateToX: () -> Unit` を UI に直接渡す
- 不採用理由：画面数増加時に引数が爆発し、遷移仕様変更時の差分追跡が困難
- 有効条件：小規模画面、遷移先が 1〜2 個までの短期開発

### 代替案 B: feature が NavController を直接保持

- 概要：feature 側で `NavController` を直接使って遷移する
- 不採用理由：feature が framework API に強く結合し、テストと再利用性が低下
- 有効条件：Android 専用かつプロトタイプ段階で速度最優先の場合

## 参考

- https://developer.android.com/guide/navigation/navigation-3
- https://github.com/android/nowinandroid
- `doc/strategy-modularization.md`
- `doc/convention-layer-ui-navigation-core.md`
