package app.kaito_dogi.smopin.shared.data.smokingArea

import app.kaito_dogi.smopin.shared.domain.smokingArea.Latitude
import app.kaito_dogi.smopin.shared.domain.smokingArea.Location
import app.kaito_dogi.smopin.shared.domain.smokingArea.Longitude
import app.kaito_dogi.smopin.shared.domain.smokingArea.SmokingArea
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class SmokingAreaRepositoryTest {
  @Test
  fun `getSmokingAreaList_Success`() = runTest {
    val smokingAreaRepository = DefaultSmokingAreaRepository(
      smokingAreaNetworkDataSource = FakeSmokingAreaNetworkDataSource(),
    )

    val expect = fakeSmokingAreaDataModelList.map {
      SmokingArea(
        name = it.name,
        location = Location(
          latitude = Latitude(value = it.latitude),
          longitude = Longitude(value = it.longitude),
        ),
      )
    }

    val actual = smokingAreaRepository.getSmokingAreaList()

    assertEquals(expected = expect, actual = actual)
  }

  @Test
  fun `getSmokingAreaList_Error`() = runTest {
    val smokingAreaRepository = DefaultSmokingAreaRepository(
      smokingAreaNetworkDataSource = FakeSmokingAreaNetworkDataSource(
        shouldThrow = true,
      ),
    )

    // TODO: Exception をテストできるようにする
    try {
      smokingAreaRepository.getSmokingAreaList()
    } catch (e: Exception) {
      assertTrue { true }
    }
  }
}

private class FakeSmokingAreaNetworkDataSource(
  private val smokingAreaList: List<SmokingAreaDataModel> = fakeSmokingAreaDataModelList,
  private val shouldThrow: Boolean = false,
) : SmokingAreaNetworkDataSource {
  override suspend fun getSmokingAreaList(): List<SmokingAreaDataModel> = if (!shouldThrow) {
    smokingAreaList
  } else {
    throw Exception()
  }
}

private val fakeSmokingAreaDataModelList: List<SmokingAreaDataModel> = List(size = 3) {
  SmokingAreaDataModel(
    name = it.toString(),
    latitude = it.toDouble(),
    longitude = it.toDouble(),
  )
}
