package app.kaito_dogi.smopin.ui.testing

import app.cash.paparazzi.Paparazzi
import app.kaito_dogi.smopin.feature.hoge.HogeScreenPreview
import org.junit.Rule
import org.junit.Test

class HogeScreenPreviewTest {

  @get:Rule
  val paparazzi: Paparazzi = Paparazzi()

  @Test
  fun captureHogeScreenPreview(): Unit = paparazzi.snapshot {
    HogeScreenPreview()
  }
}
