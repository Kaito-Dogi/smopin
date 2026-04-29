package app.kaito_dogi.smopin.shared.location

import app.kaito_dogi.smopin.shared.data.location.LocationDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal class LocationPlatformDataSourceTest {
  @Test
  fun getCurrentLocationStreamSuccess() = runTest {
    val fakePlatformLocationClient = FakePlatformLocationClient()
    val locationPlatformDataSource = DefaultLocationPlatformDataSource(
      platformLocationClient = fakePlatformLocationClient,
      ioDispatcher = Dispatchers.Default,
    )

    val actual = locationPlatformDataSource.getCurrentLocationStream(
      isPrecise = IS_PRECISE,
      intervalDuration = INTERVAL_DURATION,
    ).first()

    assertEquals(expected = fakeLocationDataModel, actual = actual)
    assertEquals(expected = IS_PRECISE, actual = fakePlatformLocationClient.actualIsPrecise)
    assertEquals(expected = INTERVAL_DURATION, actual = fakePlatformLocationClient.actualIntervalDuration)
  }

  companion object {
    private const val IS_PRECISE = true
    private val INTERVAL_DURATION = 3.seconds
    private val fakeLocationDataModel = LocationDataModel(
      latitude = 35.0,
      longitude = 139.0,
    )
  }

  private class FakePlatformLocationClient : PlatformLocationClient {
    var actualIsPrecise: Boolean? = null
    var actualIntervalDuration: Duration? = null

    override fun getCurrentLocationStream(
      isPrecise: Boolean,
      intervalDuration: Duration,
    ): Flow<LocationDataModel> {
      actualIsPrecise = isPrecise
      actualIntervalDuration = intervalDuration
      return flowOf(fakeLocationDataModel)
    }
  }
}
