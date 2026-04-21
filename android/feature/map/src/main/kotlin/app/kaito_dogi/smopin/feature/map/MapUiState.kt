package app.kaito_dogi.smopin.feature.map

import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Location
import app.kaito_dogi.smopin.shared.domain.smokingArea.smokingArea.SmokingArea
import kotlinx.serialization.Serializable

// TODO: パーミッションを取得しているかどうかの表現を考える
@Serializable
data class MapUiState(
  val isMapLoading: Boolean,
  val currentLocation: Location?,
  val smokingAreaList: List<SmokingArea>,
) {
  companion object {
    fun createInitial(): MapUiState = MapViewModelState.createInitial().run {
      MapUiState(
        isMapLoading = isMapLoading,
        currentLocation = null,
        smokingAreaList = smokingAreaList,
      )
    }
  }
}
