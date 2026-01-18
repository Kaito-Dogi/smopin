package app.kaito_dogi.smopin.shared.database.firestore.smokingArea

import app.kaito_dogi.smopin.shared.data.smokingArea.SmokingAreaDataModel
import app.kaito_dogi.smopin.shared.data.smokingArea.SmokingAreaNetworkDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class DefaultSmokingAreaNetworkDataSource(
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
