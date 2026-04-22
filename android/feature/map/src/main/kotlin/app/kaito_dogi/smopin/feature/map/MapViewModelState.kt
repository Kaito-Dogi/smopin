package app.kaito_dogi.smopin.feature.map

import app.kaito_dogi.smopin.shared.domain.smokingArea.smokingArea.SmokingArea
import kotlinx.serialization.Serializable

@Serializable
data class MapViewModelState(
  val isMapLoading: Boolean,
  val uiState: MapUiState.UiState,
  val shouldRequestPermission: Boolean,
  val smokingAreaList: List<SmokingArea>,
) {
  companion object {
    fun createInitial(): MapViewModelState = MapViewModelState(
      isMapLoading = true,
      uiState = MapUiState.UiState.PermissionRequested,
      shouldRequestPermission = true,
      smokingAreaList = emptyList(),
    )
  }
}
