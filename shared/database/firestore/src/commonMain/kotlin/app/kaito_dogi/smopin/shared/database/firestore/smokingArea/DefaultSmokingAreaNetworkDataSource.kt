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
      .map { documentSnapshot ->
        SmokingAreaRawDocument(
          name = documentSnapshot.get(field = "name"),
          latitude = documentSnapshot.get(field = "latitude"),
          longitude = documentSnapshot.get(field = "longitude"),
        )
      }
      .map(transform = SmokingAreaRawDocumentMapper::toDataModel)
  }

  companion object {
    private const val SMOKING_AREA_COLLECTION = "smoking_area"
  }
}

internal data class SmokingAreaRawDocument(
  val name: String,
  val latitude: Double,
  val longitude: Double,
)

internal object SmokingAreaRawDocumentMapper {
  fun toDataModel(smokingAreaRawDocument: SmokingAreaRawDocument) = SmokingAreaDataModel(
    name = smokingAreaRawDocument.name,
    latitude = smokingAreaRawDocument.latitude,
    longitude = smokingAreaRawDocument.longitude,
  )
}
