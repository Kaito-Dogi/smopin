package app.kaito_dogi.smopin

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import app.kaito_dogi.smopin.di.ViewModelFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.android.ActivityKey

@ActivityKey(value = MainActivity::class)
@ContributesIntoMap(scope = AppScope::class, binding<Activity>())
@Inject
internal class MainActivity(
  private val viewModelFactory: ViewModelFactory,
) : ComponentActivity() {

  override val defaultViewModelProviderFactory: ViewModelProvider.Factory
    get() = viewModelFactory

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    setContent {
      App(
        viewModelFactory = viewModelFactory,
      )
    }
  }
}
