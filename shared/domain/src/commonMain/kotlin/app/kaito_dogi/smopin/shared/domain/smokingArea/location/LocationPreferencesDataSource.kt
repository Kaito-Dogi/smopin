package app.kaito_dogi.smopin.shared.domain.smokingArea.location

import kotlinx.coroutines.flow.Flow

interface LocationPreferencesDataSource {
  fun getShouldRequestPermission(): Flow<Boolean>

  suspend fun updateShouldRequestPermission(shouldRequestPermission: Boolean)
}
