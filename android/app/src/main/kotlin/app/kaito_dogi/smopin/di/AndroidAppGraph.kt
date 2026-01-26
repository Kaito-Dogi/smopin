package app.kaito_dogi.smopin.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.createGraph
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@DependencyGraph(scope = AppScope::class)
internal interface AndroidAppGraph : MetroAppComponentProviders, ViewModelGraph

internal fun createAppGraph(): AndroidAppGraph = createGraph<AndroidAppGraph>()
