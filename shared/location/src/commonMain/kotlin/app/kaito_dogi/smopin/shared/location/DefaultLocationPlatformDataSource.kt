package app.kaito_dogi.smopin.shared.location

import app.kaito_dogi.smopin.shared.common.AppDispatcher
import app.kaito_dogi.smopin.shared.common.AppDispatchers
import app.kaito_dogi.smopin.shared.data.location.LocationDataModel
import app.kaito_dogi.smopin.shared.data.location.LocationPlatformDataSource
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlin.time.Duration

@Inject
internal class DefaultLocationPlatformDataSource(
  private val platformLocationClient: PlatformLocationClient,
  @param:AppDispatcher(dispatcher = AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : LocationPlatformDataSource {
  override fun getCurrentLocationStream(
    isPrecise: Boolean,
    intervalDuration: Duration,
  ): Flow<LocationDataModel> = platformLocationClient.getCurrentLocationStream(
    isPrecise = isPrecise,
    intervalDuration = intervalDuration,
  ).flowOn(
    context = ioDispatcher,
  )
}
