package app.kaito_dogi.smopin.shared.data.smokingArea

import app.kaito_dogi.smopin.shared.domain.smokingArea.Latitude
import app.kaito_dogi.smopin.shared.domain.smokingArea.Location
import app.kaito_dogi.smopin.shared.domain.smokingArea.Longitude
import app.kaito_dogi.smopin.shared.domain.smokingArea.SmokingArea

data class SmokingAreaDataModel(
  val name: String,
  val latitude: Double,
  val longitude: Double,
)

fun SmokingAreaDataModel.toDomainModel() = SmokingArea(
  name = name,
  location = Location(
    latitude = Latitude(value = latitude),
    longitude = Longitude(value = longitude),
  ),
)
