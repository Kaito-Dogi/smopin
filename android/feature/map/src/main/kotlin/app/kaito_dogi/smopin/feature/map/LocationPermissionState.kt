package app.kaito_dogi.smopin.feature.map

import kotlinx.serialization.Serializable

@Serializable
internal sealed interface LocationPermissionState {
  @Serializable
  data object NotRequested : LocationPermissionState

  @Serializable
  data class Granted(
    val isPrecise: Boolean,
  ) : LocationPermissionState

  @Serializable
  data object Denied : LocationPermissionState
}
