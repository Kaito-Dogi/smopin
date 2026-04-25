package app.kaito_dogi.smopin.shared.data.location

import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Latitude
import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Location
import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Longitude
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal class LocationRepositoryTest {
  @Test
  fun getCurrentLocationStreamSuccess() = runTest {
    val locationRepository = DefaultLocationRepository(
      locationDataSource = FakeLocationDataSource(),
    )

    val expectedLocation = fakeLocation.let {
      Location(
        latitude = Latitude(value = it.latitude),
        longitude = Longitude(value = it.longitude),
      )
    }

    val actualLocation = locationRepository.getCurrentLocationStream(
      isPreciseEnabled = IS_PRECISE_ENABLED,
      intervalDuration = INTERVAL_DURATION,
    ).first()

    assertEquals(expected = expectedLocation, actual = actualLocation)
  }

  @Test
  fun getCurrentLocationStreamError() = runTest {
    val locationRepository = DefaultLocationRepository(
      locationDataSource = FakeLocationDataSource(
        shouldFailGetCurrentLocationStream = true,
      ),
    )

    assertFails {
      locationRepository.getCurrentLocationStream(
        isPreciseEnabled = IS_PRECISE_ENABLED,
        intervalDuration = INTERVAL_DURATION,
      ).first()
    }
  }

  companion object {
    private const val IS_PRECISE_ENABLED = false
    private val INTERVAL_DURATION = 5.seconds
  }
}

private class FakeLocationDataSource(
  private val location: LocationDataModel = fakeLocation,
  private val shouldFailGetCurrentLocationStream: Boolean = false,
) : LocationDataSource {
  override fun getCurrentLocationStream(
    isPreciseEnabled: Boolean,
    intervalDuration: Duration,
  ): Flow<LocationDataModel?> = flow {
    if (!shouldFailGetCurrentLocationStream) {
      emit(value = location)
    } else {
      throw Exception()
    }
  }
}

private val fakeLocation: LocationDataModel = LocationDataModel(
  latitude = 0.0,
  longitude = 0.0,
)
