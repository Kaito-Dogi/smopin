package app.kaito_dogi.smopin.shared.domain.smokingArea.location

import kotlinx.serialization.Serializable

/**
 * 経度
 *
 * @param value 経度の値
 */
@Serializable
data class Longitude(
  val value: Double,
)
