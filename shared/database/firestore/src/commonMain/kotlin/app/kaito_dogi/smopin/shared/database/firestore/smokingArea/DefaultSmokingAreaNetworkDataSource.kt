package app.kaito_dogi.smopin.shared.database.firestore.smokingArea

import app.kaito_dogi.smopin.shared.data.smokingArea.SmokingAreaDataModel
import app.kaito_dogi.smopin.shared.data.smokingArea.SmokingAreaNetworkDataSource
import app.kaito_dogi.smopin.shared.di.AppDispatcher
import app.kaito_dogi.smopin.shared.di.AppDispatchers
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Inject
class DefaultSmokingAreaNetworkDataSource(
  @param:AppDispatcher(dispatcher = AppDispatchers.IO)
  private val ioDispatcher: CoroutineDispatcher,
) : SmokingAreaNetworkDataSource {
  override suspend fun getSmokingAreaList(): List<SmokingAreaDataModel> =
    withContext(context = ioDispatcher) {
      List(size = 3) {
        SmokingAreaDataModel(
          name = "Mock Smoking Area $it",
          latitude = 35.6889544 + it * 0.1,
          longitude = 139.6992443 + it * 0.1,
        )
      }
    }
}
