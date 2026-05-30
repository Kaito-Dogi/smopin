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
  override suspend fun getSmokingAreaList(): List<SmokingAreaDataModel> = withContext(context = ioDispatcher) {
    Firebase.firestore.collection(collectionPath = SMOKING_AREA_COLLECTION).get()
      .documents
      .mapNotNull { documentSnapshot ->
        runCatching {
          val rawDocument = SmokingAreaRawDocument(
            name = documentSnapshot.get<String?>(field = "name"),
            latitude = documentSnapshot.get<Double?>(field = "latitude"),
            longitude = documentSnapshot.get<Double?>(field = "longitude"),
          )
          rawDocument.toDataModel()
        }.getOrNull()
      }
  }

  companion object {
    private const val SMOKING_AREA_COLLECTION = "smoking_area"
  }
}

internal data class SmokingAreaRawDocument(
  val name: String?,
  val latitude: Double?,
  val longitude: Double?,
)

internal fun SmokingAreaRawDocument.toDataModel(): SmokingAreaDataModel? {
  val name = name ?: return null
  val latitude = latitude ?: return null
  val longitude = longitude ?: return null
  return SmokingAreaDataModel(
    name = name,
    latitude = latitude,
    longitude = longitude,
  )
}
