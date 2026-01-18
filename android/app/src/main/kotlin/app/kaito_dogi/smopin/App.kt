package app.kaito_dogi.smopin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import app.kaito_dogi.smopin.feature.hoge.HogeScreen
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory

@Composable
internal fun App(
  viewModelFactory: MetroViewModelFactory,
) {
  CompositionLocalProvider(value = LocalMetroViewModelFactory provides viewModelFactory) {
    HogeScreen()
  }
}
