package app.kaito_dogi.smopin.shared.location

import app.kaito_dogi.smopin.shared.common.AppDispatcher
import app.kaito_dogi.smopin.shared.common.AppDispatchers
import app.kaito_dogi.smopin.shared.data.location.LocationDataModel
import app.kaito_dogi.smopin.shared.data.location.LocationDataSource
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlin.time.Duration

@Inject
internal class DefaultLocationDataSource(
  private val platformLocationClient: PlatformLocationClient,
  @param:AppDispatcher(dispatcher = AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : LocationDataSource {
  override fun getCurrentLocationStream(
    isPrecise: Boolean,
    intervalDuration: Duration,
  ): Flow<LocationDataModel?> = platformLocationClient.getCurrentLocationStream(
    isPrecise = isPrecise,
    intervalDuration = intervalDuration,
  ).flowOn(
    context = ioDispatcher,
  )
}
