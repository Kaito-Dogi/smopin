package app.kaito_dogi.smopin.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Location
import app.kaito_dogi.smopin.shared.domain.smokingArea.location.LocationRepository
import app.kaito_dogi.smopin.shared.domain.smokingArea.smokingArea.SmokingAreaRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Inject
@ViewModelKey(value = MapViewModel::class)
@ContributesIntoMap(scope = AppScope::class)
@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModel(
  locationRepository: LocationRepository,
  private val smokingAreaRepository: SmokingAreaRepository,
) : ViewModel() {
  private val viewModelState: MutableStateFlow<MapViewModelState> = MutableStateFlow(value = MapViewModelState.createInitial())
  private val locationPermissionState: MutableStateFlow<LocationPermissionState> = MutableStateFlow(value = LocationPermissionState.NotRequested)

  private val currentLocation: Flow<Location?> = locationPermissionState
    .flatMapLatest { locationPermissionState: LocationPermissionState ->
      when (locationPermissionState) {
        is LocationPermissionState.Granted -> locationRepository.getCurrentLocationStream(
          isPrecise = locationPermissionState.isPrecise,
          intervalDuration = 5.seconds,
        )

        LocationPermissionState.Denied, LocationPermissionState.NotRequested -> flowOf(value = null)
      }
    }

  val uiState: StateFlow<MapUiState> = combine(
    viewModelState,
    locationPermissionState,
    currentLocation,
  ) { viewModelState, locationPermissionState, currentLocation ->
    if (viewModelState.isMapLoaded) {
      MapUiState.MapSuccess(
        locationPermissionState = locationPermissionState,
        isCameraPositionInitialized = viewModelState.isCameraPositionInitialized,
        smokingAreaList = viewModelState.smokingAreaList,
        currentLocation = currentLocation,
      )
    } else {
      MapUiState.MapLoading(
        locationPermissionState = locationPermissionState,
      )
    }
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
  }

  fun onCameraPositionInitialize() {
    viewModelState.update {
      it.copy(
        isCameraPositionInitialized = true,
      )
    }
  }

  fun onMapLoad() {
    viewModelState.update {
      it.copy(
        isMapLoaded = true,
      )
    }
  }

  fun onLocationPermissionStateChange(newLocationPermissionState: LocationPermissionState) {
    locationPermissionState.update {
      newLocationPermissionState
    }
  }
}
