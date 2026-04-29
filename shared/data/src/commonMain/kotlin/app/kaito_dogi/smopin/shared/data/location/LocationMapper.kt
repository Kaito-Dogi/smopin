package app.kaito_dogi.smopin.shared.data.location

import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Latitude
import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Location
import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Longitude

internal object LocationMapper {
  fun toDomainModel(dataModel: LocationDataModel) = Location(
    latitude = Latitude(value = dataModel.latitude),
    longitude = Longitude(value = dataModel.longitude),
  )
}
