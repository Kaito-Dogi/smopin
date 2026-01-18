package app.kaito_dogi.smopin.shared.domain.smokingArea

import kotlinx.serialization.Serializable

/**
 * 喫煙所
 *
 * @param name 名前
 * @param location 緯度経度
 */
@Serializable
data class SmokingArea(
  val name: String,
  val location: Location,
)
