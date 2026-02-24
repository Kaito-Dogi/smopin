package app.kaito_dogi.smopin

import android.app.Application
import app.kaito_dogi.smopin.di.createAppGraph
import com.google.firebase.firestore.FirebaseFirestoreSettings
import dev.gitlive.firebase.firestore.android
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.initialize
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication
import dev.gitlive.firebase.Firebase as GitLiveFirebase

internal class MainApplication : Application(), MetroApplication {
  override val appComponentProviders: MetroAppComponentProviders by lazy { createAppGraph() }

  override fun onCreate() {
    super.onCreate()

    // FIXME: App Startup を使用する（参考：https://developer.android.com/topic/libraries/app-startup）
    GitLiveFirebase.initialize(context = this)
    GitLiveFirebase.firestore.android.firestoreSettings = FirebaseFirestoreSettings.Builder(GitLiveFirebase.firestore.android.firestoreSettings).build()
  }
}
