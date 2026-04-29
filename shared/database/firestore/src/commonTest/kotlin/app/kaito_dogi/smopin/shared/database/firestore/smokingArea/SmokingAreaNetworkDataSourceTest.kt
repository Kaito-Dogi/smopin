package app.kaito_dogi.smopin.shared.database.firestore.smokingArea

import app.kaito_dogi.smopin.shared.data.smokingArea.SmokingAreaDataModel
import kotlin.test.Test
import kotlin.test.assertEquals

internal class SmokingAreaNetworkDataSourceTest {
  @Test
  fun toDataModelSuccess() {
    val rawDocument = SmokingAreaRawDocument(
      name = "name",
      latitude = 1.0,
      longitude = 2.0,
    )

    val actual = SmokingAreaRawDocumentMapper.toDataModel(smokingAreaRawDocument = rawDocument)

    assertEquals(
      expected = SmokingAreaDataModel(
        name = "name",
        latitude = 1.0,
        longitude = 2.0,
      ),
      actual = actual,
    )
  }
}
