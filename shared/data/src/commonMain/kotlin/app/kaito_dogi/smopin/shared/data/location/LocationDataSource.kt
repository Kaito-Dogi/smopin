package app.kaito_dogi.smopin.shared.data.location

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

// TODO: interface にデフォルト引数を渡せるかどうか確認する
interface LocationDataSource {
  fun getCurrentLocation(
    isPreciseEnabled: Boolean,
    intervalDuration: Duration = 5.seconds,
  ): Flow<LocationDataModel>
}
