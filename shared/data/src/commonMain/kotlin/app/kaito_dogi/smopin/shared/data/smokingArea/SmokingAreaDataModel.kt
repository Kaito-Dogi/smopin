package app.kaito_dogi.smopin.shared.data.smokingArea

import kotlinx.serialization.Serializable

/**
 * 喫煙所のデータモデル
 *
 * @param name 名前
 * @param latitude 緯度
 * @param longitude 経度
 */
@Serializable
data class SmokingAreaDataModel(
  val name: String,
  val latitude: Double,
  val longitude: Double,
)
