package app.kaito_dogi.smopin.di

import app.kaito_dogi.smopin.shared.common.AppDispatcherBindingContainer
import app.kaito_dogi.smopin.shared.data.DataBindingContainer
import app.kaito_dogi.smopin.shared.database.firestore.DatabaseFirestoreBindingContainer
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.createGraph
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@DependencyGraph(
  scope = AppScope::class,
  bindingContainers = [
    AppDispatcherBindingContainer::class,
    DataBindingContainer::class,
    DatabaseFirestoreBindingContainer::class,
  ],
)
internal interface AndroidAppGraph : MetroAppComponentProviders, ViewModelGraph

internal fun createAppGraph(): AndroidAppGraph = createGraph<AndroidAppGraph>()
