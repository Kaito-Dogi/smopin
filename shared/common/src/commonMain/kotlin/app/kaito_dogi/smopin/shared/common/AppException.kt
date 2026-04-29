package app.kaito_dogi.smopin.shared.common

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * アプリ固有のエラー
 */
@Serializable
sealed class AppException : Exception() {
  abstract override val message: String?

  @Transient
  abstract override val cause: Throwable?

  /**
   * その他のエラー
   */
  @Serializable
  data class Unknown(
    override val message: String? = null,
    @Transient override val cause: Throwable? = null,
  ) : AppException()
}
