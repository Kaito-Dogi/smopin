package app.kaito_dogi.smopin.feature.counter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun CounterEntry() = Box(
  modifier = Modifier
    .fillMaxSize()
    .background(color = MaterialTheme.colorScheme.surface),
  contentAlignment = Alignment.Center,
) {
  Text(
    text = ":feature:counter",
    style = MaterialTheme.typography.headlineMedium,
    color = MaterialTheme.colorScheme.onSurface,
  )
}
