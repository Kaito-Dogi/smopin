package app.kaito_dogi.smopin.shared.location

import android.app.Application
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import app.kaito_dogi.smopin.shared.domain.smokingArea.location.LocationPreferencesDataSource
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val LOCATION_PREFERENCES_DATA_STORE_NAME: String = "location_preferences"
private const val SHOULD_REQUEST_PERMISSION_KEY: String = "should_request_permission"
private val Application.locationPreferencesDataStore by preferencesDataStore(name = LOCATION_PREFERENCES_DATA_STORE_NAME)

@Inject
internal class DefaultLocationPreferencesDataSource(
  private val application: Application,
) : LocationPreferencesDataSource {
  override fun getShouldRequestPermission(): Flow<Boolean> = application.locationPreferencesDataStore.data
    .map { preferences ->
      preferences[shouldRequestPermissionPreferencesKey] ?: true
    }

  override suspend fun updateShouldRequestPermission(shouldRequestPermission: Boolean) {
    application.locationPreferencesDataStore.edit { preferences ->
      preferences[shouldRequestPermissionPreferencesKey] = shouldRequestPermission
    }
  }

  private companion object {
    val shouldRequestPermissionPreferencesKey: Preferences.Key<Boolean> = booleanPreferencesKey(name = SHOULD_REQUEST_PERMISSION_KEY)
  }
}
