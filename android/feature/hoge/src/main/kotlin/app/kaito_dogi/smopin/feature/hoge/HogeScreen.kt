package app.kaito_dogi.smopin.feature.hoge

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
fun HogeScreen(
  modifier: Modifier = Modifier,
  viewModel: HogeViewModel = metroViewModel(),
) {
  val uiState: HogeUiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(key1 = Unit) {
    viewModel.onCreate()
  }

  Column(
    modifier = modifier,
  ) {
    uiState.smokingAreaList.forEach { smokingArea ->
      Text(text = smokingArea.name)
      Text(text = "latitude: ${smokingArea.location.latitude.value}")
      Text(text = "longitude: ${smokingArea.location.longitude.value}")
    }
  }
}
