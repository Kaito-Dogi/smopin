package app.kaito_dogi.smopin.shared.data

import app.kaito_dogi.smopin.shared.data.smokingArea.DefaultSmokingAreaRepository
import app.kaito_dogi.smopin.shared.domain.smokingArea.SmokingAreaRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo

@ContributesTo(scope = AppScope::class)
@BindingContainer
abstract class DataBindingContainer internal constructor() {

  @Binds
  internal abstract val DefaultSmokingAreaRepository.bind: SmokingAreaRepository
}
