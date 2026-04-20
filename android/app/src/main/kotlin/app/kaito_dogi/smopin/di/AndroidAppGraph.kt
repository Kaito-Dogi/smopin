package app.kaito_dogi.smopin.di

import android.app.Application
import app.kaito_dogi.smopin.shared.common.AppDispatcherBindingContainer
import app.kaito_dogi.smopin.shared.data.DataBindingContainer
import app.kaito_dogi.smopin.shared.database.firestore.DatabaseFirestoreBindingContainer
import app.kaito_dogi.smopin.shared.location.LocationBindingContainer
import app.kaito_dogi.smopin.shared.location.PlatformLocationClientBindingContainer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@DependencyGraph(
  scope = AppScope::class,
  bindingContainers = [
    AppDispatcherBindingContainer::class,
    DataBindingContainer::class,
    DatabaseFirestoreBindingContainer::class,
    LocationBindingContainer::class,
    PlatformLocationClientBindingContainer::class,
  ],
)
internal interface AndroidAppGraph : MetroAppComponentProviders, ViewModelGraph {

  @DependencyGraph.Factory
  fun interface Factory {
    fun create(
      @Provides application: Application
    ): AndroidAppGraph
  }
}

internal fun createAppGraph(application: Application): AndroidAppGraph = createGraphFactory<AndroidAppGraph.Factory>().create(application = application)
