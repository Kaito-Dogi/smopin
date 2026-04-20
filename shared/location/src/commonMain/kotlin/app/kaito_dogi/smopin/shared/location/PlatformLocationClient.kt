package app.kaito_dogi.smopin.shared.location

import app.kaito_dogi.smopin.shared.data.location.LocationDataModel

internal expect class PlatformLocationClient {
  suspend fun getLocation(isPreciseEnabled: Boolean): LocationDataModel
}
