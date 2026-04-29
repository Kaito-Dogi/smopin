package app.kaito_dogi.smopin.feature.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.kaito_dogi.smopin.shared.common.AppException
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
import kotlinx.coroutines.flow.catch
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
    when (locationPermissionState) {
      LocationPermissionState.NotRequested -> MapUiState.PermissionNotRequested(
        smokingAreaList = viewModelState.smokingAreaList,
        isSmokingAreaListLoading = viewModelState.isSmokingAreaListLoading,
        isMapLoaded = viewModelState.isMapLoaded,
      )

      is LocationPermissionState.Granted -> {
        requireNotNull(value = currentLocation) {
          "currentLocation must not be null if access to location is permitted"
        }

        MapUiState.PermissionGranted(
          smokingAreaList = viewModelState.smokingAreaList,
          isSmokingAreaListLoading = viewModelState.isSmokingAreaListLoading,
          isMapLoaded = viewModelState.isMapLoaded,
          currentLocation = currentLocation,
          hasCameraPositionAdjustedToCurrentLocation = viewModelState.hasCameraPositionAdjustedToCurrentLocation,
        )
      }

      LocationPermissionState.Denied -> MapUiState.PermissionDenied(
        smokingAreaList = viewModelState.smokingAreaList,
        isSmokingAreaListLoading = viewModelState.isSmokingAreaListLoading,
        isMapLoaded = viewModelState.isMapLoaded,
      )
    }
  }.catch { cause ->
    viewModelState.update {
      it.copy(error = AppException.Unknown(cause = cause))
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
    initialValue = MapUiState.createInitial(),
  )

  fun onCreate() {
    viewModelScope.launch {
      viewModelState.update {
        it.copy(isSmokingAreaListLoading = true)
      }

      runCatching {
        smokingAreaRepository.getSmokingAreaList()
      }.onSuccess { smokingAreaList ->
        viewModelState.update {
          it.copy(
            smokingAreaList = smokingAreaList,
            isSmokingAreaListLoading = false,
          )
        }
      }.onFailure { exception ->
        // TODO: エラーハンドリング
        viewModelState.update {
          it.copy(
            isSmokingAreaListLoading = false,
            error = AppException.Unknown(cause = exception),
          )
        }
      }
    }
  }

  fun onCameraPositionAdjustedToCurrentLocation() {
    viewModelState.update {
      it.copy(hasCameraPositionAdjustedToCurrentLocation = true)
    }
  }

  fun onMapLoaded() {
    viewModelState.update {
      it.copy(isMapLoaded = true)
    }
  }

  fun onLocationPermissionGranted(isPrecise: Boolean) {
    locationPermissionState.update {
      LocationPermissionState.Granted(isPrecise = isPrecise)
    }
  }

  fun onLocationPermissionDenied() {
    locationPermissionState.update {
      LocationPermissionState.Denied
    }
  }
}
