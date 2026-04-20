package app.kaito_dogi.smopin.feature.map

import app.kaito_dogi.smopin.shared.domain.smokingArea.Location
import app.kaito_dogi.smopin.shared.domain.smokingArea.SmokingArea

data class MapUiState(
  val isMapLoading: Boolean,
  val currentLocation: Location?,
  val smokingAreaList: List<SmokingArea>,
) {
  companion object {
    fun createInitial(): MapUiState = MapUiState(
      isMapLoading = false,
      currentLocation = null,
      smokingAreaList = emptyList(),
    )
  }
}
