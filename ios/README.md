# iOS

## モジュール構成

`Package.swift` で以下のマルチモジュールを定義しています。

- `AppFeature`
- `HogeFeature`
- `SmopinDI`

## Firestore 接続

`SmopinDI/SmokingAreaClient` は以下の順で Firestore から喫煙所を取得します。

1. `FirebaseApp.configure()` を実行
2. KMP の `createSmokingAreaRepositoryForIos()` で `SmokingAreaRepository` を取得
3. `getSmokingAreaList()` の結果を `HogeView` でテキスト表示

> `SharedDatabaseFirestore` フレームワークを iOS ターゲットにリンクしていない場合、暫定的に `"KMP Framework not linked"` を表示します。
