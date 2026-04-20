package app.kaito_dogi.smopin.shared.data.location

import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Latitude
import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Location
import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Longitude

internal object LocationMapper {
  fun toDomainModel(locationDataModel: LocationDataModel) = Location(
    latitude = Latitude(value = locationDataModel.latitude),
    longitude = Longitude(value = locationDataModel.longitude),
  )
}
