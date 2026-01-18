package app.kaito_dogi.smopin.shared.data.smokingArea

interface SmokingAreaNetworkDataSource {
  suspend fun getSmokingAreaList(): List<SmokingAreaDataModel>
}
