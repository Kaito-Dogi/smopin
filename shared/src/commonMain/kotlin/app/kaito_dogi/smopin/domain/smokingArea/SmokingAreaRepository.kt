package app.kaito_dogi.smopin.domain.smokingArea

interface SmokingAreaRepository {
  suspend fun getSmokingArea(): List<SmokingArea>
}
