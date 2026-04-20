package app.kaito_dogi.smopin.shared.data.smokingArea

import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Latitude
import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Location
import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Longitude
import app.kaito_dogi.smopin.shared.domain.smokingArea.smokingArea.SmokingArea

internal object SmokingAreaMapper {
  fun toDomainModel(smokingAreaDataModel: SmokingAreaDataModel) = SmokingArea(
    name = smokingAreaDataModel.name,
    location = Location(
      latitude = Latitude(value = smokingAreaDataModel.latitude),
      longitude = Longitude(value = smokingAreaDataModel.longitude),
    ),
  )
}
