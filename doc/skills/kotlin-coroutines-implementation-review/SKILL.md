---
name: kotlin-coroutines-implementation-review
description: Android/KMP プロジェクトで Kotlin Coroutines を実装またはレビューするときに、構造化並行性・キャンセル・例外・テスト・Dispatcher 注入の観点で設計判断を支援する。
---

# Kotlin Coroutines 実装・レビュー Skill

## この Skill を使う場面

- ViewModel / UseCase / Repository / DataSource に `suspend` / `Flow` / `CoroutineScope` を導入する
- 既存実装のレビューで、キャンセル安全性・例外伝播・Dispatcher の責務境界を確認する
- KMP shared で共通化したロジックを Android / iOS 双方で安全に扱う

## 使い方（最小手順）

1. 対象コードが UI 指向 / アプリケーション指向 / ビジネス指向のどれかを判定する。
2. `references/checklist.md` のチェックリストで責務分離を検証する。
3. 実装・修正が必要なら `references/patterns.md` のテンプレートをベースに最小変更で反映する。
4. `kotlinx-coroutines-test` でキャンセル・例外・Dispatcher 差し替えをテストする。

## 判断原則

- **構造化並行性優先**：`launch` / `async` を呼ぶスコープ所有者を明示し、子コルーチンの寿命を親に従属させる。
- **責務境界優先**：
  - UI レイヤは画面ライフサイクルと状態管理を担当
  - data レイヤは I/O と変換のみ担当（例外の握りつぶし禁止）
- **Dispatcher 注入優先**：`Dispatchers.IO` などの直参照を避け、テストで差し替え可能にする。
- **キャンセル協調優先**：`CancellationException` を握りつぶさず再送出し、`finally` でクリーンアップする。

## 禁止事項

- `GlobalScope` の利用
- DataSource/Repository での無根拠な `supervisorScope` 常用
- `flow {}` 内で別スコープに `launch` して emit 競合を作る実装
- `runBlocking` の本番コード利用

## 参照

- 実装・レビュー観点: `references/checklist.md`
- 典型パターン: `references/patterns.md`
