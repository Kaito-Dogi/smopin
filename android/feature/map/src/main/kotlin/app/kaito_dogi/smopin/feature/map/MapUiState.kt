package app.kaito_dogi.smopin.feature.map

import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Location
import app.kaito_dogi.smopin.shared.domain.smokingArea.smokingArea.SmokingArea
import kotlinx.serialization.Serializable

@Serializable
data class MapUiState(
  val isMapLoading: Boolean,
  val uiState: UiState,
  val shouldRequestPermission: Boolean,
  val currentLocation: Location?,
  val smokingAreaList: List<SmokingArea>,
) {
  @Serializable
  sealed interface UiState {
    val isMapShown: Boolean

    @Serializable
    data object PermissionRequested : UiState {
      override val isMapShown: Boolean = false
    }

    @Serializable
    data class PermissionGranted(
      val isPrecise: Boolean,
    ) : UiState {
      override val isMapShown: Boolean = true
    }

    @Serializable
    data object PermissionDenied : UiState {
      override val isMapShown: Boolean = true
    }
  }

  companion object {
    fun createInitial(): MapUiState = MapViewModelState.createInitial().run {
      MapUiState(
        isMapLoading = isMapLoading,
        uiState = uiState,
        shouldRequestPermission = shouldRequestPermission,
        currentLocation = null,
        smokingAreaList = smokingAreaList,
      )
    }
  }
}
