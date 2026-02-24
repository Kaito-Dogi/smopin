package app.kaito_dogi.smopin.shared.data.firestore

import app.kaito_dogi.smopin.shared.data.smokingArea.SmokingAreaDataModel

interface SmokingAreaFirestoreSdkWrapper {
  suspend fun getSmokingAreaList(): List<SmokingAreaDataModel>
}
