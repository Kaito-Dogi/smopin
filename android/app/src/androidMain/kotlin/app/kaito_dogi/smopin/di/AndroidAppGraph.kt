package app.kaito_dogi.smopin.di

import app.kaito_dogi.smopin.shared.di.AppGraph
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraph
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@DependencyGraph(scope = AppScope::class)
internal interface AndroidAppGraph : AppGraph, MetroAppComponentProviders, ViewModelGraph {
  @Provides
  fun provideIsSuccess(): Boolean = true
}

internal fun createAppGraph(): AndroidAppGraph = createGraph<AndroidAppGraph>()
