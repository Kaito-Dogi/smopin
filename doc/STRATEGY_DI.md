# DI 戦略

## 原則

```text
メモ：DI ライブラリに依存しない原則のため、最上部に記載する
```

- Domain Layer のコンポーネントを Android, iOS 共通で使用する
- 依存グラフは各 OS のアプリモジュールで構築する
- UseCase, Repository, DataSource を interface で抽象化し、テスト時に実装を差し替えられるようにする
- UseCase, Repository, DataSource の具体の実装はモジュールに internal で定義し、隠蔽する

## ライブラリ

```text
メモ：DI ライブラリの選定理由も記載する
```

- DI ライブラリとして [ZacSweers/metro](https://github.com/ZacSweers/metro) を使用する

### 理由

```text
メモ：KMP、Android エンジニアにとって適切な用語を使用して、文章を整える
```

- KMP 対応
- 静的でコンパイル時に依存を解決できる
- Hilt の使い心地に近い

## TBD

```text
メモ：適切なセクション分けをして、文章も整える
```

- @\Provides よりも @\Binds を優先する
- GraphExtension ではなく BindingContainer を採用する
- UseCase, Repository, DataSource は Binds する
  - BindingContainer 内で具体の実装クラスの拡張プロパティとして interface 型のプロパティを定義する
  - プロパティは internal にする
  - そのため、BindingContainer を abstract class にする
  - 依存先でのインスタンス生成を防止するため、internal constructor とする
- インスタンスを Provides する場合は、意味的に分割する
  - Provides を使用するため、Binding Container を object にする
