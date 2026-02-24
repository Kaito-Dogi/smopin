package app.kaito_dogi.smopin.shared.data.smokingArea

import kotlinx.serialization.Serializable

interface SmokingAreaFirestoreWrapper {
  suspend fun getSmokingAreaDocumentList(): List<SmokingAreaDocumentDataModel>
}

/**
 * Firestore の喫煙所ドキュメント
 *
 * @param name 名前
 * @param latitude 緯度
 * @param longitude 経度
 */
@Serializable
data class SmokingAreaDocumentDataModel(
  val name: String,
  val latitude: Double,
  val longitude: Double,
)

fun SmokingAreaDocumentDataModel.toDataModel() = SmokingAreaDataModel(
  name = name,
  latitude = latitude,
  longitude = longitude,
)
