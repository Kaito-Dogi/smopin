package app.kaito_dogi.smopin.shared.location

import android.app.Application
import com.google.android.gms.location.LocationServices
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Provides

@BindingContainer
object PlatformLocationClientBindingContainer {

  @Provides
  private fun providePlatformLocationClient(
    application: Application,
  ): PlatformLocationClient = PlatformLocationClient(
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(application),
    application = application,
  )
}
