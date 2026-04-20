package app.kaito_dogi.smopin.feature.map

import app.kaito_dogi.smopin.shared.domain.smokingArea.smokingArea.SmokingArea
import kotlinx.serialization.Serializable

@Serializable
data class MapViewModelState(
  val isMapLoading: Boolean,
  val smokingAreaList: List<SmokingArea>,
) {
  companion object {
    fun createInitial(): MapViewModelState = MapViewModelState(
      isMapLoading = true,
      smokingAreaList = emptyList(),
    )
  }
}
