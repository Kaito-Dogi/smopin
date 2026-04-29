package app.kaito_dogi.smopin.feature.map

import app.kaito_dogi.smopin.shared.domain.smokingArea.location.Location

internal object MapUiStateMapper {
  fun toUiState(
    viewModelState: MapViewModelState,
    currentLocation: Location?,
  ): MapUiState = when (viewModelState.locationPermission) {
    MapViewModelState.LocationPermission.NotRequested -> MapUiState.PermissionNotRequested(
      smokingAreaList = viewModelState.smokingAreaList,
      isSmokingAreaListLoading = viewModelState.isSmokingAreaListLoading,
      isMapLoaded = viewModelState.isMapLoaded,
    )

    is MapViewModelState.LocationPermission.Granted -> {
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
          isCameraPositionAdjusted = viewModelState.isCameraPositionAdjusted,
        )
      }
    }

    MapViewModelState.LocationPermission.Denied -> MapUiState.PermissionDenied(
      smokingAreaList = viewModelState.smokingAreaList,
      isSmokingAreaListLoading = viewModelState.isSmokingAreaListLoading,
      isMapLoaded = viewModelState.isMapLoaded,
    )
  }
}
