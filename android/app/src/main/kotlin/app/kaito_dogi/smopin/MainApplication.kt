package app.kaito_dogi.smopin

import android.app.Application
import app.kaito_dogi.smopin.di.createAppGraph
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication

internal class MainApplication : Application(), MetroApplication {
  override val appComponentProviders: MetroAppComponentProviders by lazy { createAppGraph() }

  override fun onCreate() {
    super.onCreate()
    Firebase.initialize(context = this)
  }
}
