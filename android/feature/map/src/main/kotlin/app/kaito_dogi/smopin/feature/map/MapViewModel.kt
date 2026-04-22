package app.kaito_dogi.smopin.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kaito_dogi.smopin.shared.domain.smokingArea.location.LocationPreferencesDataSource
import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Location
import app.kaito_dogi.smopin.shared.domain.smokingArea.location.LocationRepository
import app.kaito_dogi.smopin.shared.domain.smokingArea.smokingArea.SmokingAreaRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Inject
@ViewModelKey(value = MapViewModel::class)
@ContributesIntoMap(scope = AppScope::class)
class MapViewModel(
  private val locationRepository: LocationRepository,
  private val locationPreferencesDataSource: LocationPreferencesDataSource,
  private val smokingAreaRepository: SmokingAreaRepository,
) : ViewModel() {
  private val viewModelState: MutableStateFlow<MapViewModelState> = MutableStateFlow(value = MapViewModelState.createInitial())

  private val currentLocation: Flow<Location?> = viewModelState.flatMapLatest { state ->
    when (val uiState = state.uiState) {
      is MapUiState.UiState.PermissionGranted -> locationRepository.getCurrentLocation(isPreciseEnabled = uiState.isPrecise)
      MapUiState.UiState.PermissionDenied,
      MapUiState.UiState.PermissionRequested,
      -> emptyFlow()
    }
  }

  val uiState: StateFlow<MapUiState> = viewModelState.combine(
    flow = currentLocation,
  ) { state, currentLocation ->
    MapUiState(
      isMapLoading = state.isMapLoading,
      uiState = state.uiState,
      shouldRequestPermission = state.shouldRequestPermission,
      currentLocation = currentLocation,
      smokingAreaList = state.smokingAreaList,
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
    initialValue = MapUiState.createInitial(),
  )

  fun onCreate() {
    viewModelScope.launch {
      runCatching {
        smokingAreaRepository.getSmokingAreaList()
      }.onSuccess { smokingAreaList ->
        viewModelState.update {
          it.copy(
            smokingAreaList = smokingAreaList,
          )
        }
      }.onFailure {
        // TODO: エラーハンドリング
      }
    }

    viewModelScope.launch {
      locationPreferencesDataSource.getShouldRequestPermission().collect { shouldRequestPermission ->
        viewModelState.update {
          it.copy(
            shouldRequestPermission = shouldRequestPermission,
          )
        }
      }
    }
  }

  fun onPermissionRequested() {
    viewModelScope.launch {
      locationPreferencesDataSource.updateShouldRequestPermission(shouldRequestPermission = false)
    }
  }

  fun onPermissionResult(isGranted: Boolean, isPrecise: Boolean) {
    viewModelState.update {
      it.copy(
        uiState = if (isGranted) {
          MapUiState.UiState.PermissionGranted(isPrecise = isPrecise)
        } else {
          MapUiState.UiState.PermissionDenied
        },
      )
    }
  }

  fun onMapLoaded() {
    viewModelState.update {
      it.copy(
        isMapLoading = false,
      )
    }
  }
}
