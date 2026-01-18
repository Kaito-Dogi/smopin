package app.kaito_dogi.smopin.shared.domain.smokingArea

interface SmokingAreaRepository {
  suspend fun getSmokingAreaList(): List<SmokingArea>
}
