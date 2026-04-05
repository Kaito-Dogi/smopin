# Version Catalog 運用規約

## versions, libraries, plugins のいずれも、ローワーキャメルケースで命名する

- 理由：Android Studio の補完機能で名前の終わりを分かりやすくするため
- OK：kotlinxCoroutinesCore：補完機能で `kotlinxCoroutinesCore` と表示されるため、間違えようがない
- NG：kotlinx-coroutines-core：補完機能で `kotlinx`, `coroutines`, `core` と順に表示されるため、誤って `kotlinx.coroutines` と入力した状態で Sync して、エラーになる可能性がある

## アルファベット順で定義する

- 理由：ライブラリを重複して追加してしまうことを防ぐため

## libraries で `module` を使用する

- 理由： `group`, `name` を使用して書くよりも簡潔に書けるため
- OK： `kotlinxCoroutinesCore = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "kotlinxCoroutines" }`
- NG： `kotlinxCoroutinesCore = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version.ref = "kotlinxCoroutines" }`
