package app.kaito_dogi.smopin.feature.hoge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kaito_dogi.smopin.shared.domain.smokingArea.SmokingAreaRepository
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
class HogeViewModel(
  private val smokingAreaRepository: SmokingAreaRepository,
) : ViewModel() {
  private val _uiState: MutableStateFlow<HogeUiState> =
    MutableStateFlow(value = HogeUiState.createInitial())
  val uiState: StateFlow<HogeUiState> = _uiState.asStateFlow()

  fun onResume() {
    viewModelScope.launch {
      runCatching {
        smokingAreaRepository.getSmokingAreaList()
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

