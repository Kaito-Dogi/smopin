# Version Catalog の運用規約

## 目的

本ドキュメントは、`gradle/libs.versions.toml` に依存定義を追加する際の判断基準を整理し、依存更新時の影響範囲を最小化することを目的とする。

## 追加手順

1. `versions` セクションにバージョンキーを追加する
2. `libraries` セクションにライブラリキーを追加する
3. モジュール側の `build.gradle.kts` では、原則 `libs.<key>` 参照で依存を定義する

## 命名規約

- バージョンキーは `kebab-case` で定義する
  - 例：`gitlive-firebase = "2.3.0"`
- ライブラリキーは既存規約に合わせて `camelCase` で定義する
  - 例：`gitliveFirebaseFirestore`

## Firebase Kotlin SDK の追加方針

- KMP で Firebase を利用する場合は、`dev.gitlive` 系ライブラリを追加する
- Android 専用 Firebase SDK（`com.google.firebase:*`）を KMP の shared モジュールから直接参照しない
- Firestore を追加する場合は、`firebase-app` と `firebase-firestore` を同時に追加する

## レビュー観点

- 依存追加の目的が Issue / PR に明記されているか
- 追加先モジュールが責務に一致しているか
- 既存依存で代替できないことが説明されているか
