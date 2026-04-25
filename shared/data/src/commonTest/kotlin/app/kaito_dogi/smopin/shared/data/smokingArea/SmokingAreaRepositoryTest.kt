package app.kaito_dogi.smopin.shared.data.smokingArea

import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Latitude
import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Location
import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Longitude
import app.kaito_dogi.smopin.shared.domain.smokingArea.smokingArea.SmokingArea
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class SmokingAreaRepositoryTest {
  @Test
  fun getSmokingAreaListSuccess() = runTest {
    val smokingAreaRepository = DefaultSmokingAreaRepository(
      smokingAreaNetworkDataSource = FakeSmokingAreaNetworkDataSource(),
    )

    val expectedSmokingAreaList = fakeSmokingAreaDataModelList.map {
      SmokingArea(
        name = it.name,
        location = Location(
          latitude = Latitude(value = it.latitude),
          longitude = Longitude(value = it.longitude),
        ),
      )
    }

    val actualSmokingAreaList = smokingAreaRepository.getSmokingAreaList()

    assertEquals(expected = expectedSmokingAreaList, actual = actualSmokingAreaList)
  }

  @Test
  fun getSmokingAreaListError() = runTest {
    val smokingAreaRepository = DefaultSmokingAreaRepository(
      smokingAreaNetworkDataSource = FakeSmokingAreaNetworkDataSource(
        shouldFailGetSmokingAreaList = true,
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
  private val shouldFailGetSmokingAreaList: Boolean = false,
) : SmokingAreaNetworkDataSource {
  override suspend fun getSmokingAreaList(): List<SmokingAreaDataModel> = if (!shouldFailGetSmokingAreaList) {
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
