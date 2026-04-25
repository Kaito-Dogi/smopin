# コーディング規約

## 命名規則

### 複数形を使用しない

- 理由：イレギュラーな複数形で命名に迷いが生じるため
- 例：非可算名詞、末尾が "~s" 以外に変形する名詞

### 返り値が `List`, `Map`, `Set` の場合、メソッド名に接尾辞 `List`, `Map`, `Set` をつける

- 理由：命名で Collection の型を判断できるようにするため
- OK：`getSmokingAreaList`
- NG：`getSmokingAreas`

### 返り値が `Flow` の場合、メソッド名に接尾辞 `Stream` をつける

- 理由：命名でオブザーバルな値（ストリーム）を判断できるようにするため
- OK：`getSmokingAreaListStream`
- NG：`getSmokingAreaListFlow`
- NG：`getSmokingAreaList`（接尾辞 `Stream` をつけない）

## 文法

### メソッドの返り値を式で表現できる場合は `=` を使用する（Expression body）

- 理由：Kotlin の言語機能を活用し、シンプルに記述するため

## モデル定義

### KDoc を記載する

- 理由：そのモデルの意図や理由を後から確認できるようにするため

### `value class` を使用しない

- 理由：Swift でプリミティブな型に変換されてしまうため
- OK：バリューオブジェクトに相当するモデルは `data class` で定義する

## Repository 実装

TBD

## DataSource 実装

### コンストラクタインジェクションで `Dispatchers.IO` を渡す

- 理由：テストでスレッドを差し替えられるようにするため

### メソッド実装時点で `withContext` を呼び出す

- 理由：スレッドの切り替え忘れを防止するため
