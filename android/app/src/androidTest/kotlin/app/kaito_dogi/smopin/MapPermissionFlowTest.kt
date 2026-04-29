package app.kaito_dogi.smopin

import android.os.Build
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import app.kaito_dogi.smopin.feature.map.MAP_LOADING_INDICATOR_TEST_TAG
import org.junit.Rule
import org.junit.Test

class MapPermissionFlowTest {

  @get:Rule
  val composeRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun locationPermissionGranted_shouldHideLoadingIndicator() {
    allowLocationPermissionIfNeeded()

    composeRule.waitUntil(timeoutMillis = 15_000) {
      composeRule.onAllNodesWithTag(MAP_LOADING_INDICATOR_TEST_TAG).fetchSemanticsNodes().isEmpty()
    }

    composeRule.onNodeWithTag(MAP_LOADING_INDICATOR_TEST_TAG).assertDoesNotExist()
  }

  private fun allowLocationPermissionIfNeeded() {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    val device = UiDevice.getInstance(instrumentation)

    val permissionPackage = if (Build.VERSION.SDK_INT >= 30) {
      "com.android.permissioncontroller"
    } else {
      "com.android.packageinstaller"
    }

    if (!device.wait(Until.hasObject(By.pkg(permissionPackage).depth(0)), 5_000)) return

    val candidates = listOf(
      By.res(permissionPackage, "permission_allow_foreground_only_button"),
      By.res(permissionPackage, "permission_allow_one_time_button"),
      By.res(permissionPackage, "permission_allow_button"),
      By.textContains("While using"),
      By.textContains("使用中のみ"),
      By.textContains("許可"),
    )

    candidates.firstNotNullOfOrNull { selector -> device.findObject(selector) }?.click()
  }
}
