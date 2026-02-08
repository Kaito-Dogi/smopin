# Codex テスト: `doc/` 配下ドキュメント要約

Issue #1 の完了条件に基づき、`doc/` 配下の既存 Markdown を確認して要約を整理した。

## `doc/CONVENTION_CODING.md`

- Kotlin 実装のコーディング規約を定義。
- 命名では不規則な複数形を避け、`List` / `Map` / `Set` など型を明示する方針。
- 式で書ける関数は expression body（`=`）を使って簡潔に記述する。
- モデルには KDoc を書き、`value class` は Swift 互換性の理由で使用しない。
- DataSource はコンストラクタインジェクションで `Dispatchers.IO` を受け取り、メソッド内で `withContext` を呼ぶ。

## `doc/CONVENTION_DOC.md`

- ドキュメントの命名規則と記法ルールを定義。
- ファイル名は `{種類名}_{具体名}.md` 形式を推奨。
- 絵文字の意味を定義（🚨: 要議論、✍️: 要追記）。

## `doc/convention-layer-data.md`

- Data Layer の原則、責務分離、実装方針を整理。
- Repository を Data Layer の SSOT とし、Domain のインターフェース実装として UI へデータ公開する。
- DataSource は単一データソースを担当し、Data モデルと Domain モデルを分離する。
- 処理を UI 指向 / アプリケーション指向 / ビジネス指向に分類し、ライフサイクルに応じた実行方針を示す。
- エラーハンドリング、Worker の配置、iOS 実装、結合テストには TBD/議論項目が残る。

## `doc/strategy-dependency-injection.md`

- KMP 前提の DI 戦略を説明。
- Metro を採用し、`DependencyGraph` と `BindingContainer` で依存グラフを構成する方針。
- UseCase / Repository / DataSource をインターフェース化し、具象実装は `internal` で隠蔽する。
- インターフェース束縛は `@Binds`、外部インスタンス注入は `@Provides` を使い分ける。
- スコープ運用やテスト差し替えの基本方針と、今後の TBD 項目を明示。

## `doc/strategy-modularization.md`

- モジュール・パッケージ分割戦略を整理。
- 優先順位は「再利用性 > シンプルさ > その他のマルチモジュール利点」。
- 依存性逆転を採用し、Data Layer が Domain Layer に依存する構成を採る。
- `shared` / `android` / `ios` のモジュール責務と依存関係を定義し、Mermaid で可視化。
- `shared:domain` は知識単位、`android:ui` は責務単位でパッケージ分割する方針。
