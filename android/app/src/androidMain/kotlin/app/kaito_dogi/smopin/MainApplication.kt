package app.kaito_dogi.smopin

import android.app.Application
import app.kaito_dogi.smopin.di.createAppGraph
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication

internal class MainApplication : Application(), MetroApplication {
  override val appComponentProviders: MetroAppComponentProviders by lazy { createAppGraph() }
}
