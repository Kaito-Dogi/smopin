package app.kaito_dogi.smopin.feature.map

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

@Inject
@ViewModelKey(value = MapViewModel::class)
@ContributesIntoMap(scope = AppScope::class)
class MapViewModel(
  private val smokingAreaRepository: SmokingAreaRepository,
) : ViewModel() {
  private val _uiState: MutableStateFlow<MapUiState> = MutableStateFlow(value = MapUiState.createInitial())
  val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

  fun onCreate() {
    viewModelScope.launch {
      _uiState.update { it.copy(isMapLoading = true) }

      runCatching {
        smokingAreaRepository.getSmokingAreaList()
      }.onSuccess { smokingAreaList ->
        _uiState.update {
          it.copy(
            smokingAreaList = smokingAreaList,
          )
        }
      }.onFailure {
        // TODO: エラーハンドリング
      }
    }
  }

  fun onMapLoaded() {
    _uiState.update {
      it.copy(
        isMapLoading = false,
      )
    }
  }
}
