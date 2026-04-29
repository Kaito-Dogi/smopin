package app.kaito_dogi.smopin

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
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
    val topLevelBackStack = remember { TopLevelBackStack<ScreenRoute>(ScreenRoute.Map) }

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
    ) { _ ->
      NavDisplay(
        backStack = topLevelBackStack.backStack,
        onBack = { topLevelBackStack.removeLast() },
        entryProvider = entryProvider {
          entry<ScreenRoute.Map> { MapEntry() }
          entry<ScreenRoute.Counter> { CounterEntry() }
        },
      )
    }
  }
}

private enum class ScreenRoute(val label: String) {
  Map(label = "map"),
  Counter(label = "counter"),
}

private class TopLevelBackStack<T : Any>(startKey: T) {
  private val topLevelStackMap: LinkedHashMap<T, SnapshotStateList<T>> = linkedMapOf(startKey to mutableStateListOf(startKey))

  var topLevelKey by mutableStateOf(startKey)
    private set

  val backStack = mutableStateListOf(startKey)

  fun addTopLevel(key: T) {
    if (topLevelStackMap[key] == null) {
      topLevelStackMap[key] = mutableStateListOf(key)
    } else {
      topLevelStackMap.remove(key)?.let { topLevelStack ->
        topLevelStackMap[key] = topLevelStack
      }
    }
    topLevelKey = key
    updateBackStack()
  }

  fun removeLast() {
    val removedKey = topLevelStackMap[topLevelKey]?.removeLastOrNull() ?: return
    if (topLevelStackMap.size == 1 && removedKey == topLevelKey) return
    topLevelStackMap.remove(removedKey)
    topLevelKey = topLevelStackMap.keys.last()
    updateBackStack()
  }

  private fun updateBackStack() {
    backStack.clear()
    backStack.addAll(topLevelStackMap.values.flatten())
  }
}
