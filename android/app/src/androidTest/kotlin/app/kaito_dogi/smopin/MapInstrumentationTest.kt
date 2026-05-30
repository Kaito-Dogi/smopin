package app.kaito_dogi.smopin

import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import app.kaito_dogi.smopin.feature.map.TEST_TAG_LOADING
import app.kaito_dogi.smopin.feature.map.TEST_TAG_MAP
import org.junit.Rule
import org.junit.Test

class MapInstrumentationTest {

  @get:Rule
  val composeRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun 地図画面_位置情報パーミッションを許可したら地図とピン表示まで進める() {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    val device = UiDevice.getInstance(instrumentation)

    val allowWhileUsingAppButton = device.wait(
      Until.findObject(By.res("com.android.permissioncontroller:id/permission_allow_foreground_only_button")),
      5_000L,
    )
    allowWhileUsingAppButton?.click()

    composeRule.waitUntil(timeoutMillis = 15_000L) {
      composeRule.onAllNodesWithTag(testTag = TEST_TAG_MAP).fetchSemanticsNodes().isNotEmpty()
    }

    composeRule.onNodeWithTag(testTag = TEST_TAG_MAP).assertExists()
    composeRule.onNodeWithTag(testTag = TEST_TAG_LOADING).assertDoesNotExist()
  }
}
