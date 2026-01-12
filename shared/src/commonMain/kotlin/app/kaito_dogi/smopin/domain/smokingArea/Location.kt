package app.kaito_dogi.smopin.domain.smokingArea

import kotlinx.serialization.Serializable

@Serializable
data class Location(
  val latitude: Latitude,
  val longitude: Longitude,
)
