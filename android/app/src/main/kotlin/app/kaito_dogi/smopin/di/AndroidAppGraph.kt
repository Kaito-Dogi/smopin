package app.kaito_dogi.smopin.di

import app.kaito_dogi.smopin.shared.data.DataScope
import app.kaito_dogi.smopin.shared.di.AppDispatcher
import app.kaito_dogi.smopin.shared.di.AppDispatchers
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraph
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

// FIXME: :shared:di モジュールに移動する
@DependencyGraph(
  scope = AppScope::class,
  additionalScopes = [
    DataScope::class,
  ],
)
internal interface AndroidAppGraph : MetroAppComponentProviders, ViewModelGraph {
  @Provides
  @AppDispatcher(dispatcher = AppDispatchers.IO)
  fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

internal fun createAppGraph(): AndroidAppGraph = createGraph<AndroidAppGraph>()
