package app.kaito_dogi.smopin

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import app.kaito_dogi.smopin.feature.counter.CounterEntry
import app.kaito_dogi.smopin.feature.map.MapEntry
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory

@Composable
internal fun App(
  viewModelFactory: MetroViewModelFactory,
) {
  CompositionLocalProvider(value = LocalMetroViewModelFactory provides viewModelFactory) {
    val topLevelBackStack = rememberSaveable(saver = TopLevelBackStack.Saver) {
      TopLevelBackStack(startRoute = ScreenRoute.Map)
    }
    val screenEntryProvider = remember {
      entryProvider {
        entry<ScreenRoute> { screenRoute ->
          when (screenRoute) {
            ScreenRoute.Map -> MapEntry()
            ScreenRoute.Counter -> CounterEntry()
          }
        }
      }
    }

    Scaffold(
      bottomBar = {
        NavigationBar {
          ScreenRoute.entries.forEach { screenRoute ->
            NavigationBarItem(
              selected = screenRoute == topLevelBackStack.topLevelKey,
              onClick = { topLevelBackStack.addTopLevel(screenRoute) },
              icon = {
                Text(text = screenRoute.label.take(1).uppercase())
              },
            )
          }
        }
      },
    ) { innerPadding ->
      if (topLevelBackStack.backStack.size > 1) {
        NavDisplay(
          modifier = Modifier.padding(innerPadding),
          backStack = topLevelBackStack.backStack,
          onBack = { topLevelBackStack.removeLast() },
          entryProvider = screenEntryProvider,
        )
      } else {
        NavDisplay(
          modifier = Modifier.padding(innerPadding),
          backStack = topLevelBackStack.backStack,
          entryProvider = screenEntryProvider,
        )
      }
    }
  }
}

private enum class ScreenRoute(val label: String) {
  Map(label = "map"),
  Counter(label = "counter"),
}

private class TopLevelBackStack(
  startRoute: ScreenRoute,
  routeHistory: List<ScreenRoute> = listOf(startRoute),
) {
  val backStack: SnapshotStateList<ScreenRoute> = mutableStateListOf<ScreenRoute>()
    .apply {
      addAll(elements = routeHistory.ifEmpty { listOf(startRoute) })
    }

  val topLevelKey: ScreenRoute
    get() = backStack.last()

  fun addTopLevel(key: ScreenRoute) {
    if (key == topLevelKey) return

    backStack.remove(element = key)
    backStack.add(element = key)
  }

  fun removeLast() {
    if (backStack.size > 1) {
      backStack.removeAt(index = backStack.lastIndex)
    }
  }

  companion object {
    val Saver = listSaver<TopLevelBackStack, String>(
      save = { topLevelBackStack ->
        topLevelBackStack.backStack.map(transform = ScreenRoute::name)
      },
      restore = { routeNameList ->
        TopLevelBackStack(
          startRoute = ScreenRoute.Map,
          routeHistory = routeNameList.mapNotNull { routeName ->
            runCatching { ScreenRoute.valueOf(routeName) }.getOrNull()
          },
        )
      },
    )
  }
}
