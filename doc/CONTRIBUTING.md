# CONTRIBUTING

本ドキュメントは、`smopin` にコントリビュートする開発者向けのガイドです。

## 1. 開発の前提

- 本プロジェクトは Kotlin Multiplatform（KMP）を前提に、`android` / `ios` / `shared` を分離して開発します。
- 設計方針の一次情報は `doc/` 配下の戦略・規約ドキュメントです。
- 実装前に以下を確認してください。
  - `doc/strategy-modularization.md`
  - `doc/strategy-dependency-injection.md`
  - `doc/convention-layer-data.md`
  - `doc/CONVENTION_CODING.md`

## 2. 推奨ワークフロー

1. Issue で目的・完了条件を確認する。
2. 影響範囲のモジュール（`shared/*`, `android/*`, `ios/*`）を特定する。
3. 設計ドキュメントの方針と矛盾しない実装案を作る。
4. 実装し、ローカルでビルド/テストを実行する。
5. 変更内容とテスト結果を明記して Pull Request を作成する。

## 3. 実装ルール（要点）

### 命名・コードスタイル

- 命名に曖昧な複数形を使わず、型名（`List` など）を明示する。
- Kotlin の expression body を積極的に使う。
- モデルには KDoc を記述する。
- `value class` は Swift 互換性のため使用しない。

### レイヤ構成

- Repository は Data Layer の SSOT とする。
- Domain のインターフェースを Data 側で実装し、UI には Domain 抽象を公開する。
- DataSource は単一データソース責務（network / database / preferences など）に限定する。

### DI

- Metro を利用する。
- インターフェースの束縛は `@Binds`、外部インスタンスの提供は `@Provides` を使う。
- 具象実装は原則 `internal` とし、モジュール外に漏らさない。

## 4. モジュール追加・変更時の注意

- モジュールは「必要性が明確になってから」追加する。
- 依存関係は `doc/strategy-modularization.md` の方針（依存性逆転含む）に合わせる。
- 新しい共通ルールを導入した場合は、実装と同時に `doc/` の関連ドキュメントを更新する。

## 5. テスト方針

- まずは変更箇所に近い単体テストを優先する。
- Data Layer は Fake 実装を使ったテストを基本とする。
- 仕様変更を伴う場合、テスト追加とドキュメント更新をセットで行う。

## 6. Pull Request のチェックリスト

- [ ] 変更目的と背景を PR に記載した
- [ ] 影響範囲（モジュール/レイヤ）を説明した
- [ ] ビルドまたはテスト結果を記載した
- [ ] 必要なドキュメント更新を行った
- [ ] 既存規約（coding / layer / DI / modularization）との整合性を確認した
