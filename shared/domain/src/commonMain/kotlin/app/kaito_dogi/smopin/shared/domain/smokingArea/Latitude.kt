package app.kaito_dogi.smopin.shared.domain.smokingArea

import kotlinx.serialization.Serializable

/**
 * 緯度
 *
 * @param value 緯度の値
 */
@Serializable
data class Latitude(
  val value: Double,
)
