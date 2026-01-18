package app.kaito_dogi.smopin.feature.hoge

import app.kaito_dogi.smopin.shared.domain.smokingArea.SmokingArea

data class HogeUiState(
  val smokingAreaList: List<SmokingArea>,
) {
  companion object {
    fun createInitial(): HogeUiState = HogeUiState(smokingAreaList = emptyList())
  }
}
