package app.kaito_dogi.smopin.feature.map

import app.kaito_dogi.smopin.shared.common.AppException
import app.kaito_dogi.smopin.shared.domain.smokingArea.smokingArea.SmokingArea
import kotlinx.serialization.Serializable

@Serializable
internal data class MapViewModelState(
  val smokingAreaList: List<SmokingArea>,
  val isSmokingAreaListLoading: Boolean,
  val isMapLoaded: Boolean,
  val isCameraPositionAdjusted: Boolean,
  val error: AppException?,
) {
  companion object {
    fun createInitial(): MapViewModelState = MapViewModelState(
      smokingAreaList = emptyList(),
      isSmokingAreaListLoading = false,
      isMapLoaded = false,
      isCameraPositionAdjusted = false,
      error = null,
    )
  }
}
