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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Inject
@ViewModelKey(value = MapViewModel::class)
@ContributesIntoMap(scope = AppScope::class)
class MapViewModel(
  locationRepository: LocationRepository,
  private val smokingAreaRepository: SmokingAreaRepository,
) : ViewModel() {
  private val viewModelState: MutableStateFlow<MapViewModelState> = MutableStateFlow(value = MapViewModelState.createInitial())

  // TODO: isPreciseEnabled を正確な位置情報のパーミッションが付与されているかどうかで切り替える
  private val currentLocation: Flow<Location?> = locationRepository.getCurrentLocation(
    isPreciseEnabled = false,
    intervalDuration = 5.seconds,
  )

  val uiState: StateFlow<MapUiState> = viewModelState.combine(
    flow = currentLocation,
  ) { viewModelState, currentLocation ->
    MapUiState(
      isMapLoading = viewModelState.isMapLoading,
      currentLocation = currentLocation,
      smokingAreaList = viewModelState.smokingAreaList,
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
  }

  fun onMapLoaded() {
    viewModelState.update {
      it.copy(
        isMapLoading = false,
      )
    }
  }
}
