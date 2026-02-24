package app.kaito_dogi.smopin.shared.database.firestore

import app.kaito_dogi.smopin.shared.data.smokingArea.SmokingAreaNetworkDataSource
import app.kaito_dogi.smopin.shared.database.firestore.smokingArea.DefaultSmokingAreaNetworkDataSource
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.Provides

@BindingContainer
abstract class DatabaseFirestoreBindingContainer private constructor() {

  @Provides
  fun provideFirestore(): FirebaseFirestore = Firebase.firestore

  @Binds
  internal abstract val DefaultSmokingAreaNetworkDataSource.binds: SmokingAreaNetworkDataSource
}
