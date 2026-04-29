package app.kaito_dogi.smopin.shared.domain.smokingArea.location

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration

// FIXME: ワンショットの位置情報取得処理を追加する & 権限取得時にカメラが移動するようにする
interface LocationRepository {
  fun getCurrentLocationStream(
    isPrecise: Boolean,
    intervalDuration: Duration,
  ): Flow<Location>
}
