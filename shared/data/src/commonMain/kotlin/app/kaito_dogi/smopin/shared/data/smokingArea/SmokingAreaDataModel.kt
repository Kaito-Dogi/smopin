package app.kaito_dogi.smopin.shared.data.smokingArea

/**
 * 喫煙所情報をデータソースの入出力で扱うためのデータモデル。
 */
data class SmokingAreaDataModel(
  val name: String,
  val latitude: Double,
  val longitude: Double,
)
