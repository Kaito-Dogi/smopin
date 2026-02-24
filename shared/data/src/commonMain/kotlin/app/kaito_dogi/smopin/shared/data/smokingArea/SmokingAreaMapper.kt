package app.kaito_dogi.smopin.shared.data.smokingArea

import app.kaito_dogi.smopin.shared.domain.smokingArea.Latitude
import app.kaito_dogi.smopin.shared.domain.smokingArea.Location
import app.kaito_dogi.smopin.shared.domain.smokingArea.Longitude
import app.kaito_dogi.smopin.shared.domain.smokingArea.SmokingArea

internal object SmokingAreaMapper {
  fun toDomainModel(smokingAreaDataModel: SmokingAreaDataModel) = SmokingArea(
    name = smokingAreaDataModel.name,
    location = Location(
      latitude = Latitude(value = smokingAreaDataModel.latitude),
      longitude = Longitude(value = smokingAreaDataModel.longitude),
    ),
  )
}
