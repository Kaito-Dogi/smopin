package app.kaito_dogi.smopin.shared.database.firestore.smokingArea

import app.kaito_dogi.smopin.shared.data.smokingArea.SmokingAreaDocumentDataModel
import app.kaito_dogi.smopin.shared.data.smokingArea.SmokingAreaFirestoreWrapper
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.data
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.get
import dev.zacsweers.metro.Inject

@Inject
internal class DefaultSmokingAreaFirestoreWrapper : SmokingAreaFirestoreWrapper {
  override suspend fun getSmokingAreaDocumentList(): List<SmokingAreaDocumentDataModel> =
    Firebase.firestore.collection(path = SMOKING_AREA_COLLECTION_PATH)
      .get()
      .documents
      .map { document ->
        document.data<SmokingAreaDocumentDataModel>()
      }

  private companion object {
    const val SMOKING_AREA_COLLECTION_PATH: String = "smoking_area"
  }
}
