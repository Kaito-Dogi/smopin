package app.kaito_dogi.smopin.feature.map

import app.kaito_dogi.smopin.shared.domain.smokingArea.Location
import app.kaito_dogi.smopin.shared.domain.smokingArea.SmokingArea

data class MapUiState(
  val isLoading: Boolean,
  val currentLocation: Location?,
  val smokingAreaList: List<SmokingArea>,
) {
  companion object Companion {
    fun createInitial(): MapUiState = MapUiState(
      isLoading = false,
      currentLocation = null,
      smokingAreaList = emptyList(),
    )
  }
}
