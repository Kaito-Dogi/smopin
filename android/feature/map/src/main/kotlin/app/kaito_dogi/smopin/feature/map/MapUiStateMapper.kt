package app.kaito_dogi.smopin.feature.map

import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Location

internal object MapUiStateMapper {
  fun toUiState(
    viewModelState: MapViewModelState,
    locationPermissionState: LocationPermissionState,
    currentLocation: Location?,
  ): MapUiState = when (locationPermissionState) {
    LocationPermissionState.NotRequested -> MapUiState.PermissionNotRequested(
      smokingAreaList = viewModelState.smokingAreaList,
      isSmokingAreaListLoading = viewModelState.isSmokingAreaListLoading,
      isMapLoaded = viewModelState.isMapLoaded,
    )

    is LocationPermissionState.Granted -> {
      if (currentLocation == null) {
        MapUiState.PermissionGranted.LocationLoading(
          smokingAreaList = viewModelState.smokingAreaList,
          isSmokingAreaListLoading = viewModelState.isSmokingAreaListLoading,
          isMapLoaded = viewModelState.isMapLoaded,
        )
      } else {
        MapUiState.PermissionGranted.LocationSuccess(
          smokingAreaList = viewModelState.smokingAreaList,
          isSmokingAreaListLoading = viewModelState.isSmokingAreaListLoading,
          isMapLoaded = viewModelState.isMapLoaded,
          currentLocation = currentLocation,
          hasCameraPositionAdjustedToCurrentLocation = viewModelState.hasCameraPositionAdjustedToCurrentLocation,
        )
      }
    }

    LocationPermissionState.Denied -> MapUiState.PermissionDenied(
      smokingAreaList = viewModelState.smokingAreaList,
      isSmokingAreaListLoading = viewModelState.isSmokingAreaListLoading,
      isMapLoaded = viewModelState.isMapLoaded,
    )
  }
}
