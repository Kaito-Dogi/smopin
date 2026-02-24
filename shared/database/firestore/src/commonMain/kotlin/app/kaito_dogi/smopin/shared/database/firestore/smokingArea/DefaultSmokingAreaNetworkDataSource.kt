package app.kaito_dogi.smopin.shared.database.firestore.smokingArea

import app.kaito_dogi.smopin.shared.common.AppDispatcher
import app.kaito_dogi.smopin.shared.common.AppDispatchers
import app.kaito_dogi.smopin.shared.data.smokingArea.SmokingAreaDataModel
import app.kaito_dogi.smopin.shared.data.smokingArea.SmokingAreaNetworkDataSource
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

@Inject
internal class DefaultSmokingAreaNetworkDataSource(
  @param:AppDispatcher(dispatcher = AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : SmokingAreaNetworkDataSource {
  override suspend fun getSmokingAreaList(): List<SmokingAreaDataModel> =
    withContext(context = ioDispatcher) {
      Firebase.firestore.collection(path = SMOKING_AREA_COLLECTION).get().documents
        .map { document ->
          SmokingAreaDataModel(
            name = document.get(field = NAME_FIELD),
            latitude = document.get(field = LATITUDE_FIELD),
            longitude = document.get(field = LONGITUDE_FIELD),
          )
        }
    }

  private companion object {
    const val SMOKING_AREA_COLLECTION = "smoking_area"
    const val NAME_FIELD = "name"
    const val LATITUDE_FIELD = "latitude"
    const val LONGITUDE_FIELD = "longitude"
  }
}
