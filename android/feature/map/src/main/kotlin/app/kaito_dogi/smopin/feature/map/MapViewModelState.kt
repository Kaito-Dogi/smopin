package app.kaito_dogi.smopin.feature.map

import app.kaito_dogi.smopin.shared.domain.smokingArea.smokingArea.SmokingArea
import kotlinx.serialization.Serializable

@Serializable
data class MapViewModelState(
  val isMapLoaded: Boolean,
  val isCameraPositionInitialized: Boolean,
  val smokingAreaList: List<SmokingArea>,
  val error: Exception?,
) {
  companion object {
    fun createInitial(): MapViewModelState = MapViewModelState(
      isMapLoaded = false,
      isCameraPositionInitialized = false,
      smokingAreaList = emptyList(),
      error = null,
    )
  }
}
