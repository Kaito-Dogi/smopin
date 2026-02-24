package app.kaito_dogi.smopin.shared.database.firestore

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides

@BindingContainer
object GitLiveFirebaseBindingContainer {

    @Provides
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore
}
