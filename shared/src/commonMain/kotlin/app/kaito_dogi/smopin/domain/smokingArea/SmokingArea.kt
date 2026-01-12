package app.kaito_dogi.smopin.domain.smokingArea

import kotlinx.serialization.Serializable

@Serializable
data class SmokingArea(
  val name: String,
  val location: Location,
)
