package app.kaito_dogi.smopin.shared.database.firestore

import app.kaito_dogi.smopin.shared.data.smokingArea.SmokingAreaNetworkDataSource
import app.kaito_dogi.smopin.shared.data.smokingArea.SmokingAreaFirestoreWrapper
import app.kaito_dogi.smopin.shared.database.firestore.smokingArea.DefaultSmokingAreaFirestoreWrapper
import app.kaito_dogi.smopin.shared.database.firestore.smokingArea.DefaultSmokingAreaNetworkDataSource
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds

@BindingContainer
abstract class DatabaseFirestoreBindingContainer private constructor() {

  @Binds
  internal abstract val DefaultSmokingAreaNetworkDataSource.binds: SmokingAreaNetworkDataSource

  @Binds
  internal abstract val DefaultSmokingAreaFirestoreWrapper.bind: SmokingAreaFirestoreWrapper
}
