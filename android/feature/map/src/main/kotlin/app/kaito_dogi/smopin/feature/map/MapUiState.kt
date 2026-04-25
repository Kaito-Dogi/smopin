package app.kaito_dogi.smopin.feature.map

import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Location
import app.kaito_dogi.smopin.shared.domain.smokingArea.smokingArea.SmokingArea
import kotlinx.serialization.Serializable

@Serializable
sealed interface MapUiState2 {
  val locationPermissionState: LocationPermissionState

  @Serializable
  data class MapLoading(
    override val locationPermissionState: LocationPermissionState,
  ) : MapUiState2

  @Serializable
  data class MapSuccess(
    override val locationPermissionState: LocationPermissionState,
    val isCameraPositionInitialized: Boolean,
    val smokingAreaList: List<SmokingArea>,
    val currentLocation: Location?
  ) : MapUiState2

  companion object {
    fun createInitial(): MapUiState2 = MapLoading(
      locationPermissionState = LocationPermissionState.NotRequested,
    )
  }
}
