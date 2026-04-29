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
    override val isMapLoaded: Boolean,
  ) : MapUiState

  @Serializable
  sealed interface PermissionGranted : MapUiState {
    override val smokingAreaList: List<SmokingArea>
    override val isSmokingAreaListLoading: Boolean
    override val isMapLoaded: Boolean

    data class LocationLoading(
      override val smokingAreaList: List<SmokingArea>,
      override val isSmokingAreaListLoading: Boolean,
      override val isMapLoaded: Boolean,
    ) : PermissionGranted

    data class LocationSuccess(
      override val smokingAreaList: List<SmokingArea>,
      override val isSmokingAreaListLoading: Boolean,
      override val isMapLoaded: Boolean,
      val currentLocation: Location,
      val hasCameraPositionAdjustedToCurrentLocation: Boolean,
    ) : PermissionGranted
  }

  @Serializable
  data class PermissionDenied(
    override val smokingAreaList: List<SmokingArea>,
    override val isSmokingAreaListLoading: Boolean,
    override val isMapLoaded: Boolean,
  ) : MapUiState

  companion object {
    fun createInitial(): MapUiState = PermissionNotRequested(
      smokingAreaList = emptyList(),
      isSmokingAreaListLoading = false,
      isMapLoaded = false,
    )
  }
}
