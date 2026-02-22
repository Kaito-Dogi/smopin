# Navigation（Android / iOS 共通）

## 目的

本ドキュメントは、`doc/strategy-modularization.md` の優先順位（再利用性 > シンプルさ > その他マルチモジュールの利点）を前提として、Navigation に関する設計判断を言語化することを目的とする。  
とくに「どこに画面遷移の知識を置くか」を明確化し、Android Compose / iOS 双方で拡張しやすい構成を定義する。

## 設計原則

### 1. Feature が「遷移要求」を定義し、Platform が「遷移実行」を担う

Feature は「次にどこへ行きたいか」という意図を定義し、実際の遷移 API 呼び出し（NavHostController / UINavigationController など）は platform 側で実行する。

この分離により、次の価値を得る。

| 観点 | 効果 |
|:--|:--|
| 再利用性 | KMP の shared ロジックや画面状態遷移（例：ログイン完了 → 一覧へ）を、Android/iOS 間で同じユースケースとして扱いやすい |
| シンプルさを犠牲にして得るメリット | `Route`/`Navigator` などの抽象を追加する分、初期実装は増えるが、UI API 変更（Compose Navigation 更新、iOS 実装差し替え）を feature から隔離できる |
| 拡張性・テスト容易性・ビルド効率 | Feature 単体テストでは fake navigator を注入し、遷移意図のみ検証可能。将来 Deep Link/認可ガード/ログ計測を Navigator に集約できる |

### 2. 遷移識別子（Route / NavKey）は feature ごとに閉じる

`strategy-modularization.md` が示す「知識単位で分割」の原則に従い、遷移識別子は feature の知識として feature 側に置く。  
Navigation 実装詳細を持つモジュールを別途設ける場合も、公開境界は「feature の遷移識別子」に合わせる。

### 3. UseCase は Navigation API を直接知らない

Domain は UI フレームワーク非依存を維持し、UseCase は「成功時イベント」までを返す。  
そのイベントを受けた UI Layer が Navigator を呼ぶことで、`shared:domain` の独立性を担保する。

## Feature に NavKey / entry を含めるか

## 含める場合（本プロジェクト推奨）

| 項目 | 内容 |
|:--|:--|
| メリット | feature の知識（画面、引数、遷移先）を凝集できる。feature の削除時に遷移知識も同時に削除しやすい |
| デメリット | feature が Navigation ライブラリや route 定義規約に触れるため、追従コストが増える。api/impl 分離時はモジュール数が増える |
| 向いている条件 | 中長期運用、機能追加が継続する、画面遷移が複雑化しやすいプロダクト |

## 含めない場合

| 項目 | 内容 |
|:--|:--|
| メリット | Navigation ライブラリの知識を専用モジュールへ集約できる |
| デメリット | feature 知識が navigation 管理モジュールに分散し、仕様変更時の影響範囲が見えにくくなる |
| 向いている条件 | 画面数が少ない検証用プロジェクト、短期開発 |

## 本プロジェクトでの採用方針

`doc/strategy-modularization.md` の優先順位に合わせ、**feature に遷移識別子を寄せる構成**を基本とする。  
理由は、再利用性と変更局所性を優先するためである。

- 1機能の追加/削除時に、feature モジュール内で完結しやすい
- 将来的に Android / iOS 双方で同等の画面遷移仕様を持たせる際に、仕様の起点を feature 側へ固定できる

## 代替案との比較

### 代替案 A: navigation 専用モジュールに全 Route を集中

- 概要：全 feature の route 定義と entry を `android:navigation`（または iOS 側の同等層）へ集約する
- 採用しない理由：feature 変更のたびに central module へ変更が波及し、知識の局所性が下がる
- 有効な条件：PoC、画面数が少ない、少人数で短期に作る場合

### 代替案 B: 画面遷移をラムダだけで接続する

- 概要：`onClick = { navigateToX() }` を呼び出し側で毎回直接定義し、Navigator インターフェースを設けない
- 採用しない理由：引数契約や遷移ログ、ガード処理が分散しやすく、テストで遷移意図を検証しにくい
- 有効な条件：単一画面フロー、プロトタイプ、遷移要件が非常に単純な場合

## サンプル（KMP を意識した遷移要求）

```kotlin
// shared/domain: ユースケースはナビゲーション API を知らない
sealed interface LoginResult {
    data object Success : LoginResult
    data class Failure(val message: String) : LoginResult
}

interface LoginRepository {
    suspend fun login(mailAddress: String, password: String): LoginResult
}

class LoginUseCase(
    private val loginRepository: LoginRepository,
) {
    suspend operator fun invoke(mailAddress: String, password: String): LoginResult =
        loginRepository.login(mailAddress, password)
}
```

```kotlin
// android/feature/login: UI は結果に応じて Navigator を呼ぶ
interface LoginNavigator {
    fun navigateToSmokingAreaList()
}

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val loginNavigator: LoginNavigator,
) : ViewModel() {
    fun onLoginClick(mailAddress: String, password: String) {
        viewModelScope.launch {
            when (loginUseCase(mailAddress, password)) {
                LoginResult.Success -> loginNavigator.navigateToSmokingAreaList()
                is LoginResult.Failure -> Unit
            }
        }
    }
}
```

## 参考

- https://developer.android.com/guide/navigation/navigation-3
- `doc/strategy-modularization.md`
