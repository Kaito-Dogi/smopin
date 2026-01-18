package app.kaito_dogi.smopin.shared.domain.smokingArea

import kotlinx.serialization.Serializable

/**
 * 緯度経度
 *
 * @param latitude 緯度
 * @param longitude 経度
 */
@Serializable
data class Location(
  val latitude: Latitude,
  val longitude: Longitude,
)
