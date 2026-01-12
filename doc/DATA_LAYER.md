## 原則

- Repository を Data Layer の SSOT とする
- Domain Layer で定義されたインターフェースを実装する
- Domain Layer のインターフェースを通じて、UI Layer にデータを公開する

## コンポーネント

### Repository

- UI Layer にデータを公開する
  - Android
    - ワンショット：suspend function
    - 時間経過に伴う：Flow
- 複数の DataSource の競合を解決し、DataSource をドメインオブジェクト単位で抽象化する
- ビジネスロジックは格納しない 🚨
  - ビジネスロジックは UseCase に切り出す
  - UseCase が単に Repository で定義したメソッドのプロキシとなる場合を許容する
- Repository が他の Repository に依存する場合、Manager と命名する
  - 例：UserRepository が LoginRepository や RegistrationRepository に依存する場合、UserManager と命名する

### DataSource

- 1つのデータソースのみを処理する
  - Network, Database, Kvs, File, Cache など

### Model

- DataSource でやり取りするデータモデル
- Domain Layer で扱うドメインオブジェクトと、Data Layer で扱う Model を分離して、Data Layer の変更が UI Layer に影響しないようにする
- Repository で Model をドメインオブジェクトに変換する

## 処理の種類

### UI 指向

- ユーザーが特定の画面にいる間にのみ実行される
- ユーザーがその画面から移動するとキャンセルされる
- UI Layer によってトリガーされ、呼び出し元のライフサイクルに従う
  - ViewModel など

### アプリケーション指向

- アプリが開いている間にのみ実行される
- アプリやプロセスが終了されるとキャンセルされる
- アプリや Data Layer のライフサイクルに従う
  - Application クラスなど

### ビジネス指向

- キャンセルされず、プロセス終了後も存続させる
- バックグラウンドタスクとして実行する
  - WorkManager など

## エラーハンドリング

- Data Layer ではエラーをキャッチせず、Domain Layer にハンドリングを委譲する 🚨

## 処理の実装方法

### Android

以下のドキュメントに従って実装する

https://developer.android.com/topic/architecture/data-layer#common-tasks

#### Worker の依存関係 🚨

- Worker
  - UI Layer のコンポーネントとして扱う
  - DataSource に依存させず、Repository に依存させる
  - ViewModel と同様
- WorkManager
  - Data Layer のコンポーネントとして扱う
  - DataSource で抽象化する

#### Worker を配置する Layer 🚨

- Data Layer に配置する
  - WorkManager を抽象化した DataSource と同じパッケージ

### iOS

✍️

## テスト

### 単体テスト

✍️

### 結合テスト

✍️

## 参考リンク

- [Data layer | App architecture | Android Developers](https://developer.android.com/topic/architecture/data-layer)
