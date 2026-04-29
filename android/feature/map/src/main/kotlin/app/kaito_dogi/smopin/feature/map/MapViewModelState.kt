package app.kaito_dogi.smopin.feature.map

import app.kaito_dogi.smopin.shared.common.AppException
import app.kaito_dogi.smopin.shared.domain.smokingArea.smokingArea.SmokingArea
import kotlinx.serialization.Serializable

@Serializable
internal data class MapViewModelState(
  val smokingAreaList: List<SmokingArea>,
  val isSmokingAreaListLoading: Boolean,
  val isMapLoaded: Boolean,
  val locationPermission: LocationPermission,
  val isCameraPositionAdjusted: Boolean,
  val error: AppException?,
) {
  @Serializable
  sealed interface LocationPermission {
    @Serializable
    data object NotRequested : LocationPermission

    @Serializable
    data class Granted(
      val isPrecise: Boolean,
    ) : LocationPermission

    @Serializable
    data object Denied : LocationPermission
  }

  companion object {
    fun createInitial(): MapViewModelState = MapViewModelState(
      smokingAreaList = emptyList(),
      isSmokingAreaListLoading = false,
      isMapLoaded = false,
      locationPermission = LocationPermission.NotRequested,
      isCameraPositionAdjusted = false,
      error = null,
    )
  }
}
