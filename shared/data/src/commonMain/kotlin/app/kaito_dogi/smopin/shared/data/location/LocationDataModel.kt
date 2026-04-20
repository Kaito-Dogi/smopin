package app.kaito_dogi.smopin.shared.data.location

import kotlinx.serialization.Serializable

/**
 * 緯度経度のデータモデル
 *
 * @param latitude 緯度
 * @param longitude 経度
 */
@Serializable
data class LocationDataModel(
  val latitude: Double,
  val longitude: Double,
)
