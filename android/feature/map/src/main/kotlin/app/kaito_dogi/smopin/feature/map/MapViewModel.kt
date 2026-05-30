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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
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

  private val currentLocation: Flow<Location?> = viewModelState.map { it.locationPermission }
    .distinctUntilChanged()
    .flatMapLatest { locationPermission ->
      when (locationPermission) {
        is MapViewModelState.LocationPermission.Granted -> locationRepository.getCurrentLocationStream(
          isPrecise = locationPermission.isPrecise,
          intervalDuration = 1.seconds,
        ).onStart<Location?> { emit(value = null) }
          .retryWhen { cause, _ ->
            viewModelState.update {
              it.copy(error = AppException.Unknown(cause = cause))
            }
            true
          }
          .catch { cause ->
            viewModelState.update {
              it.copy(error = AppException.Unknown(cause = cause))
            }
            emit(value = null)
          }

        MapViewModelState.LocationPermission.Denied, MapViewModelState.LocationPermission.NotRequested -> flowOf(value = null)
      }
    }

  val uiState: StateFlow<MapUiState> = combine(
    flow = viewModelState,
    flow2 = currentLocation,
    transform = MapUiStateMapper::toUiState,
  ).stateIn(
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

  fun onCameraPositionAdjust() {
    viewModelState.update {
      it.copy(isCameraPositionAdjusted = true)
    }
  }

  fun onMapLoad() {
    viewModelState.update {
      it.copy(isMapLoaded = true)
    }
  }

  fun onLocationPermissionGranted(isPrecise: Boolean) {
    viewModelState.update {
      it.copy(locationPermission = MapViewModelState.LocationPermission.Granted(isPrecise = isPrecise))
    }
  }

  fun onLocationPermissionDenied() {
    viewModelState.update {
      it.copy(locationPermission = MapViewModelState.LocationPermission.Denied)
    }
  }
}
