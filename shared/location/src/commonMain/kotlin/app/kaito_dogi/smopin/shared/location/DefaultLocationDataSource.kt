package app.kaito_dogi.smopin.shared.location

import app.kaito_dogi.smopin.shared.common.AppDispatcher
import app.kaito_dogi.smopin.shared.common.AppDispatchers
import app.kaito_dogi.smopin.shared.data.location.LocationDataModel
import app.kaito_dogi.smopin.shared.data.location.LocationDataSource
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlin.time.Duration

@Inject
internal class DefaultLocationDataSource(
  private val platformLocationClient: PlatformLocationClient,
  @param:AppDispatcher(dispatcher = AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : LocationDataSource {
  override fun getCurrentLocation(
    isPreciseEnabled: Boolean,
    intervalDuration: Duration,
  ): Flow<LocationDataModel?> = flow {
    while (currentCoroutineContext().isActive) {
      platformLocationClient.getLocation(isPreciseEnabled = isPreciseEnabled)?.let {
        emit(value = it)
      }
      delay(duration = intervalDuration)
    }
  }.flowOn(
    context = ioDispatcher,
  )
}
