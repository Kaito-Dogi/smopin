# Version Catalog の運用規約

## 目的

Firebase Kotlin SDK のようなマルチプラットフォーム依存を導入する際に、依存宣言の重複や命名の揺れを防止する。

## 追加手順

1. `gradle/libs.versions.toml` の `[versions]` にバージョンを追加する
2. `gradle/libs.versions.toml` の `[libraries]` にライブラリ定義を追加する
3. 利用モジュールの `build.gradle.kts` では、文字列リテラルではなく `libs.xxx` で参照する

## 命名規約

- バージョンキー: ベンダー名 + ライブラリ群（例: `gitliveFirebase`）
- ライブラリキー: ベンダー名 + 用途（例: `gitliveFirebaseFirestore`）
- Firebase Android SDK（BOM）と Firebase Kotlin SDK は用途が異なるため、必ず別キーで管理する

## Firebase Kotlin SDK 導入時の注意点

- `shared` の KMP モジュールでは Firebase Kotlin SDK（`dev.gitlive`）を優先する
- Android 固有機能（Analytics など）で Google 公式 SDK が必要な場合は、Android モジュール側で BOM を使う
- 同一機能を `shared` と Android で二重実装しない
