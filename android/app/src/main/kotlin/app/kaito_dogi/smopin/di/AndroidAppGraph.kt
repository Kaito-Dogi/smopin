package app.kaito_dogi.smopin.di

import app.kaito_dogi.smopin.shared.data.smokingArea.DefaultSmokingAreaRepository
import app.kaito_dogi.smopin.shared.data.smokingArea.SmokingAreaNetworkDataSource
import app.kaito_dogi.smopin.shared.database.firestore.smokingArea.DefaultSmokingAreaNetworkDataSource
import app.kaito_dogi.smopin.shared.di.AppGraph
import app.kaito_dogi.smopin.shared.domain.smokingArea.SmokingAreaRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.createGraph
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

// FIXME: :shared:di モジュールに移動する
@DependencyGraph(scope = AppScope::class)
internal interface AndroidAppGraph : AppGraph, MetroAppComponentProviders, ViewModelGraph {
  // TODO: Multi provides する
  @Provides
  fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

  @Binds
  val DefaultSmokingAreaRepository.bind: SmokingAreaRepository

  @Binds
  val DefaultSmokingAreaNetworkDataSource.bind: SmokingAreaNetworkDataSource
}

internal fun createAppGraph(): AndroidAppGraph = createGraph<AndroidAppGraph>()
