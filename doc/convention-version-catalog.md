# Version Catalog 規約

## 目的

本ドキュメントは、`gradle/libs.versions.toml` にライブラリを追加する際の手順と命名規約を統一し、モジュール間の依存管理をシンプルに保つことを目的とする。

## 追加手順

1. `[versions]` にバージョンを追加する
2. `[libraries]` にライブラリエイリアスを追加する
3. モジュールの `build.gradle.kts` では `libs.xxx` を参照する

## 命名規約

- バージョンキーは lowerCamelCase で定義する
  - 例：`firebaseKotlinSdk = "2.4.0"`
- ライブラリエイリアスは「用途 + ライブラリ名」で定義する
  - 例：`firebaseKotlinSdkFirestore`
- 同一ファミリー内で公式 SDK とラッパー SDK が混在する場合は、接頭辞で区別する
  - 例：`firebaseFirestore`（公式） / `firebaseKotlinSdkFirestore`（GitLive）

## 実装時の注意

- マルチモジュール構成では、具象モジュールにのみ外部 SDK を追加する
  - 例：`shared:database:firestore` に `firebase-kotlin-sdk` を追加し、`shared:data` には抽象インターフェースのみを配置する
- 依存は必要最小限に限定し、未使用ライブラリを追加しない
