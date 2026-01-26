package app.kaito_dogi.smopin.shared.database.firestore

import app.kaito_dogi.smopin.shared.data.DataScope
import app.kaito_dogi.smopin.shared.data.smokingArea.SmokingAreaNetworkDataSource
import app.kaito_dogi.smopin.shared.database.firestore.smokingArea.DefaultSmokingAreaNetworkDataSource
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo

@ContributesTo(scope = DataScope::class)
@BindingContainer
abstract class DatabaseFirestoreBindingContainer internal constructor() {

  @Binds
  internal abstract val DefaultSmokingAreaNetworkDataSource.binds: SmokingAreaNetworkDataSource
}
