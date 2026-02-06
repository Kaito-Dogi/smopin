package app.kaito_dogi.smopin.shared.data

import app.kaito_dogi.smopin.shared.data.smokingArea.DefaultSmokingAreaRepository
import app.kaito_dogi.smopin.shared.domain.smokingArea.SmokingAreaRepository
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds

@BindingContainer
abstract class DataBindingContainer private constructor() {

  @Binds
  internal abstract val DefaultSmokingAreaRepository.bind: SmokingAreaRepository
}
