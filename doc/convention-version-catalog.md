# Version Catalog 運用規約

## 目的

Firebase Kotlin SDK のようにマルチモジュールで利用するライブラリを、依存定義の重複なく管理するための運用ルールを定義する。

## ライブラリ追加手順

1. `gradle/libs.versions.toml` の `[versions]` にバージョンキーを追加する
2. 同ファイルの `[libraries]` にライブラリエントリを追加し、`version.ref` を参照する
3. 利用するモジュールの `build.gradle.kts` の `sourceSets` / `dependencies` に `libs.xxx` を追加する
4. プラグインが必要な場合のみ `[plugins]` に追加する

## コーディング規約

- バージョン文字列を各モジュールに直書きしない
- モジュールごとの事情でライブラリの利用可否だけを切り替え、バージョンは Catalog に一元化する
- KMP 依存は `commonMain` に置けるかを先に確認し、置けない場合のみ platform source set に分離する

## Firebase Kotlin SDK 導入メモ

- Firestore は `dev.gitlive:firebase-firestore` を使用する
- `shared:database:firestore` の `commonMain` で依存させ、`shared:data` の DataSource インターフェース実装として扱う
