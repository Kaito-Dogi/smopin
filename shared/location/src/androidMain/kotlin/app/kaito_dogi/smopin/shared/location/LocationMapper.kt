package app.kaito_dogi.smopin.shared.location

import android.location.Location
import app.kaito_dogi.smopin.shared.data.location.LocationDataModel

internal object LocationMapper {
  fun toDataModel(location: Location) = LocationDataModel(
    latitude = location.latitude,
    longitude = location.longitude,
  )
}
