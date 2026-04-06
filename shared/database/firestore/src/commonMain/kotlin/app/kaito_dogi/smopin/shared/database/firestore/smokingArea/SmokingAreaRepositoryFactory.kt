package app.kaito_dogi.smopin.shared.database.firestore.smokingArea

import app.kaito_dogi.smopin.shared.common.AppDispatcherBindingContainer
import app.kaito_dogi.smopin.shared.data.DataBindingContainer
import app.kaito_dogi.smopin.shared.database.firestore.DatabaseFirestoreBindingContainer
import app.kaito_dogi.smopin.shared.domain.smokingArea.SmokingAreaRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.createGraph

@DependencyGraph(
  scope = AppScope::class,
  bindingContainers = [
    AppDispatcherBindingContainer::class,
    DataBindingContainer::class,
    DatabaseFirestoreBindingContainer::class,
  ],
)
interface IosSmokingAreaGraph {
  val smokingAreaRepository: SmokingAreaRepository
}

fun createSmokingAreaRepositoryForIos(): SmokingAreaRepository = createGraph<IosSmokingAreaGraph>().smokingAreaRepository
