package app.kaito_dogi.smopin.shared.domain.smokingArea.smokingArea

interface SmokingAreaRepository {
  suspend fun getSmokingAreaList(): List<SmokingArea>
}
