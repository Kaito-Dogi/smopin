package app.kaito_dogi.smopin.shared.location

import app.kaito_dogi.smopin.shared.data.location.LocationPlatformDataSource
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds

@BindingContainer
abstract class LocationBindingContainer private constructor() {

  @Binds
  internal abstract val DefaultLocationPlatformDataSource.binds: LocationPlatformDataSource
}
