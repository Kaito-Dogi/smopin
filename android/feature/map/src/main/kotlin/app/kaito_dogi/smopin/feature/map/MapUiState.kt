package app.kaito_dogi.smopin.feature.map

import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Location
import app.kaito_dogi.smopin.shared.domain.smokingArea.smokingArea.SmokingArea
import kotlinx.serialization.Serializable

@Serializable
sealed interface MapUiState {
  val smokingAreaList: List<SmokingArea>
  val isSmokingAreaListLoading: Boolean
  val isMapLoaded: Boolean

  @Serializable
  data class PermissionNotRequested(
    override val smokingAreaList: List<SmokingArea>,
    override val isSmokingAreaListLoading: Boolean,
    override val isMapLoaded: Boolean
  ) : MapUiState

  @Serializable
  data class PermissionGranted(
    override val smokingAreaList: List<SmokingArea>,
    override val isSmokingAreaListLoading: Boolean,
    override val isMapLoaded: Boolean,
    val currentLocation: Location,
    val hasCameraPositionAdjustedToCurrentLocation: Boolean,
  ) : MapUiState

  @Serializable
  data class PermissionDenied(
    override val smokingAreaList: List<SmokingArea>,
    override val isSmokingAreaListLoading: Boolean,
    override val isMapLoaded: Boolean
  ) : MapUiState

  companion object {
    fun createInitial(): MapUiState = PermissionNotRequested(
      smokingAreaList = emptyList(),
      isSmokingAreaListLoading = false,
      isMapLoaded = false,
    )
  }
}
