package app.kaito_dogi.smopin.shared.database.firestore.smokingArea

import app.kaito_dogi.smopin.shared.data.smokingArea.SmokingAreaDataModel
import dev.gitlive.firebase.firestore.DocumentSnapshot

internal object SmokingAreaMapper {
  fun toDataModel(documentSnapshot: DocumentSnapshot) = SmokingAreaDataModel(
    name = documentSnapshot.get(field = "name"),
    latitude = documentSnapshot.get(field = "latitude"),
    longitude = documentSnapshot.get(field = "longitude"),
  )
}
