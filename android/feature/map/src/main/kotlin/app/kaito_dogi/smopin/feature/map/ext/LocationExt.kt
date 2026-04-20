package app.kaito_dogi.smopin.feature.map.ext

import app.kaito_dogi.smopin.shared.domain.smokingArea.Location
import com.google.android.gms.maps.model.LatLng

internal fun Location.toLatLng() = LatLng(latitude.value, longitude.value)
