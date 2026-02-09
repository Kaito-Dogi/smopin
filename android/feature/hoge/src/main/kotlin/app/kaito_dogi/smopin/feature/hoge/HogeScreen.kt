package app.kaito_dogi.smopin.feature.hoge

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.zacsweers.metrox.viewmodel.metroViewModel

@Composable
fun HogeScreen(
  modifier: Modifier = Modifier,
  viewModel: HogeViewModel = metroViewModel(),
) {
  val uiState: HogeUiState by viewModel.uiState.collectAsStateWithLifecycle()

  LifecycleResumeEffect(key1 = Unit) {
    viewModel.onResume()

    onPauseOrDispose {
      // do nothing
    }
  }

  HogeScreenContent(
    modifier = modifier,
    uiState = uiState,
  )
}

@Composable
internal fun HogeScreenContent(
  uiState: HogeUiState,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier,
  ) {
    uiState.smokingAreaList.forEach { smokingArea ->
      Text(text = smokingArea.name)
    }
  }
}

@Preview
@Composable
internal fun HogeScreenPreview() {
  HogeScreenContent(
    uiState = HogeUiState.createPreview(),
  )
}
