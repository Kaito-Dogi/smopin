package app.kaito_dogi.smopin.hoge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kaito_dogi.smopin.shared.domain.smokingArea.SmokingArea
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@ViewModelKey(value = HogeViewModel::class)
@ContributesIntoMap(scope = AppScope::class)
@Inject
internal class HogeViewModel(
  private val isSuccess: Boolean,
  // private val smokingAreaRepository: SmokingAreaRepository,
) : ViewModel() {
  private val _uiState: MutableStateFlow<HogeUiState> =
    MutableStateFlow(value = HogeUiState.createInitial())
  val uiState: StateFlow<HogeUiState> = _uiState.asStateFlow()

  fun onResume() {
    viewModelScope.launch {
      runCatching {
        println("あああ: $isSuccess")
        // smokingAreaRepository.getSmokingAreaList()
        emptyList<SmokingArea>()
      }.onSuccess { smokingAreaList ->
        _uiState.update {
          HogeUiState(smokingAreaList = smokingAreaList)
        }
      }.onFailure {
        _uiState.update {
          HogeUiState(smokingAreaList = emptyList())
        }
      }
    }
  }
}

data class HogeUiState(
  val smokingAreaList: List<SmokingArea>,
) {
  companion object {
    fun createInitial(): HogeUiState = HogeUiState(smokingAreaList = emptyList())
  }
}
