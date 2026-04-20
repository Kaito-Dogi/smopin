package app.kaito_dogi.smopin.shared.data

import app.kaito_dogi.smopin.shared.data.location.DefaultLocationRepository
import app.kaito_dogi.smopin.shared.data.smokingArea.DefaultSmokingAreaRepository
import app.kaito_dogi.smopin.shared.domain.smokingArea.location.LocationRepository
import app.kaito_dogi.smopin.shared.domain.smokingArea.smokingArea.SmokingAreaRepository
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds

@BindingContainer
abstract class DataBindingContainer private constructor() {

  @Binds
  internal abstract val DefaultLocationRepository.bind: LocationRepository

  @Binds
  internal abstract val DefaultSmokingAreaRepository.bind: SmokingAreaRepository
}
