package app.kaito_dogi.smopin.feature.map

import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Latitude
import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Location
import app.kaito_dogi.smopin.shared.domain.smokingArea.location.LocationRepository
import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Longitude
import app.kaito_dogi.smopin.shared.domain.smokingArea.smokingArea.SmokingArea
import app.kaito_dogi.smopin.shared.domain.smokingArea.smokingArea.SmokingAreaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.time.Duration

@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModelTest {
  @BeforeTest
  fun setUp() {
    Dispatchers.setMain(dispatcher = UnconfinedTestDispatcher())
  }

  @AfterTest
  fun tearDown() {
    Dispatchers.resetMain()
  }

  @Test
  fun `onCreate success then uiState contains smoking area list`() = runTest {
    val expectedSmokingAreaList = listOf(
      SmokingArea(name = "A", location = LOCATION_A),
      SmokingArea(name = "B", location = LOCATION_B),
    )
    val mapViewModel = MapViewModel(
      locationRepository = FakeLocationRepository(),
      smokingAreaRepository = FakeSmokingAreaRepository(smokingAreaList = expectedSmokingAreaList),
    )

    mapViewModel.onCreate()

    val uiState = mapViewModel.uiState.first { !it.isSmokingAreaListLoading }
    assertEquals(expected = expectedSmokingAreaList, actual = uiState.smokingAreaList)
  }

  @Test
  fun `onCreate failure then uiState loading is false`() = runTest {
    val mapViewModel = MapViewModel(
      locationRepository = FakeLocationRepository(),
      smokingAreaRepository = FakeSmokingAreaRepository(exception = IllegalStateException("error")),
    )

    mapViewModel.onCreate()

    val uiState = mapViewModel.uiState.first { !it.isSmokingAreaListLoading }
    assertEquals(expected = false, actual = uiState.isSmokingAreaListLoading)
    assertEquals(expected = emptyList(), actual = uiState.smokingAreaList)
  }

  @Test
  fun `onMapLoad then uiState map loaded is true`() = runTest {
    val mapViewModel = MapViewModel(
      locationRepository = FakeLocationRepository(),
      smokingAreaRepository = FakeSmokingAreaRepository(),
    )

    mapViewModel.onMapLoad()

    val uiState = mapViewModel.uiState.first()
    assertEquals(expected = true, actual = uiState.isMapLoaded)
  }

  @Test
  fun `onLocationPermissionDenied then uiState is PermissionDenied`() = runTest {
    val mapViewModel = MapViewModel(
      locationRepository = FakeLocationRepository(),
      smokingAreaRepository = FakeSmokingAreaRepository(),
    )

    mapViewModel.onLocationPermissionDenied()

    assertIs<MapUiState.PermissionDenied>(mapViewModel.uiState.first())
  }

  @Test
  fun `onLocationPermissionGranted without location then uiState is LocationLoading`() = runTest {
    val mapViewModel = MapViewModel(
      locationRepository = FakeLocationRepository(),
      smokingAreaRepository = FakeSmokingAreaRepository(),
    )

    mapViewModel.onLocationPermissionGranted(isPrecise = true)

    assertIs<MapUiState.PermissionGranted.LocationLoading>(mapViewModel.uiState.first())
  }

  @Test
  fun `onLocationPermissionGranted with location then uiState is LocationSuccess`() = runTest {
    val locationRepository = FakeLocationRepository()
    val mapViewModel = MapViewModel(
      locationRepository = locationRepository,
      smokingAreaRepository = FakeSmokingAreaRepository(),
    )

    mapViewModel.onLocationPermissionGranted(isPrecise = false)
    locationRepository.emitCurrentLocation(LOCATION_A)

    val uiState = mapViewModel.uiState.first { it is MapUiState.PermissionGranted.LocationSuccess }
    assertEquals(expected = LOCATION_A, actual = (uiState as MapUiState.PermissionGranted.LocationSuccess).currentLocation)
  }

  @Test
  fun `onCameraPositionAdjust after location resolved then location success is adjusted`() = runTest {
    val locationRepository = FakeLocationRepository()
    val mapViewModel = MapViewModel(
      locationRepository = locationRepository,
      smokingAreaRepository = FakeSmokingAreaRepository(),
    )

    mapViewModel.onLocationPermissionGranted(isPrecise = true)
    locationRepository.emitCurrentLocation(LOCATION_B)
    mapViewModel.onCameraPositionAdjust()

    val uiState = mapViewModel.uiState.first { it is MapUiState.PermissionGranted.LocationSuccess }
    assertEquals(expected = true, actual = (uiState as MapUiState.PermissionGranted.LocationSuccess).isCameraPositionAdjusted)
  }

  private class FakeLocationRepository : LocationRepository {
    private val currentLocationState = MutableStateFlow<Location?>(null)

    fun emitCurrentLocation(location: Location) {
      currentLocationState.value = location
    }

    override fun getCurrentLocationStream(
      isPrecise: Boolean,
      intervalDuration: Duration,
    ): Flow<Location> = currentLocationState.filterNotNull()
  }

  private class FakeSmokingAreaRepository(
    private val smokingAreaList: List<SmokingArea> = emptyList(),
    private val exception: Exception? = null,
  ) : SmokingAreaRepository {
    override suspend fun getSmokingAreaList(): List<SmokingArea> {
      exception?.let { throw it }
      return smokingAreaList
    }
  }

  private companion object {
    val LOCATION_A = Location(latitude = Latitude(35.0), longitude = Longitude(139.0))
    val LOCATION_B = Location(latitude = Latitude(34.0), longitude = Longitude(135.0))
  }
}
