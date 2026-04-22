package app.kaito_dogi.smopin.shared.location

import app.kaito_dogi.smopin.shared.domain.smokingArea.location.LocationPreferencesDataSource
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds

@BindingContainer
abstract class LocationPreferencesBindingContainer private constructor() {

  @Binds
  internal abstract val DefaultLocationPreferencesDataSource.bind: LocationPreferencesDataSource
}
