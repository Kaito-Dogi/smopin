package app.kaito_dogi.smopin.shared.database.firestore.smokingArea

import app.kaito_dogi.smopin.shared.common.AppDispatcher
import app.kaito_dogi.smopin.shared.common.AppDispatchers
import app.kaito_dogi.smopin.shared.data.firestore.SmokingAreaFirestoreSdkWrapper
import app.kaito_dogi.smopin.shared.data.smokingArea.SmokingAreaDataModel
import app.kaito_dogi.smopin.shared.data.smokingArea.SmokingAreaNetworkDataSource
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Inject
internal class DefaultSmokingAreaNetworkDataSource(
  @param:AppDispatcher(dispatcher = AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
  private val smokingAreaFirestoreSdkWrapper: SmokingAreaFirestoreSdkWrapper,
) : SmokingAreaNetworkDataSource {
  override suspend fun getSmokingAreaList(): List<SmokingAreaDataModel> =
    withContext(context = ioDispatcher) {
      smokingAreaFirestoreSdkWrapper.getSmokingAreaList()
    }
}
