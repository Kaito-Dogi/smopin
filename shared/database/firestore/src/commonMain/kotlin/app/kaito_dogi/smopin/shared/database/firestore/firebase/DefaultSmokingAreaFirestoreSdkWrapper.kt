package app.kaito_dogi.smopin.shared.database.firestore.firebase

import app.kaito_dogi.smopin.shared.data.firestore.SmokingAreaFirestoreSdkWrapper
import app.kaito_dogi.smopin.shared.data.smokingArea.SmokingAreaDataModel
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.zacsweers.metro.Inject

@Inject
internal class DefaultSmokingAreaFirestoreSdkWrapper : SmokingAreaFirestoreSdkWrapper {
  override suspend fun getSmokingAreaList(): List<SmokingAreaDataModel> {
    return Firebase.firestore.collection(path = "smoking_area")
      .get()
      .documents
      .map { documentSnapshot ->
        SmokingAreaDataModel(
          name = documentSnapshot.get(field = "name"),
          latitude = documentSnapshot.get(field = "latitude"),
          longitude = documentSnapshot.get(field = "longitude"),
        )
      }
  }
}
