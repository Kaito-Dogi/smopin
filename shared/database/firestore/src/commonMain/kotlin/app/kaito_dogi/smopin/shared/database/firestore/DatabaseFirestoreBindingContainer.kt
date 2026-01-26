package app.kaito_dogi.smopin.shared.database.firestore

import app.kaito_dogi.smopin.shared.data.smokingArea.SmokingAreaNetworkDataSource
import app.kaito_dogi.smopin.shared.database.firestore.smokingArea.DefaultSmokingAreaNetworkDataSource
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo

@ContributesTo(scope = AppScope::class)
@BindingContainer
abstract class DatabaseFirestoreBindingContainer internal constructor() {

  @Binds
  internal abstract val DefaultSmokingAreaNetworkDataSource.binds: SmokingAreaNetworkDataSource
}
