# MVP におけるモジュール、パッケージ構成

## モジュール構成

- shared：共通ロジック
- composeApp：Android アプリ
- iosApp：iOS アプリ

## パッケージ構成

### 原則

- Domain 単位でパッケージを切り分ける（例：smokingArea など）
  - model, repository など、役割ごとにパッケージを切らない

### shared

- domain
  - ドメインオブジェクト
  - Repository のインターフェース
- data
  - Repository の実装
  - DataSource のインターフェース
- network
  - NetworkDataSource の実装（Firebase）
  - Android, iOS ごとに実装する

### composeApp, iosApp

- app：アプリのエントリーポイント
- ui：例外的に、役割ごとにパッケージを切る
  - component：共通 Composable
  - designSystem：Color, Typography, Shape, Spacing
- feature：機能

## MVP で採用しないコンポーネント

- UseCase
- インメモリキャッシュ（CacheDataSource）
