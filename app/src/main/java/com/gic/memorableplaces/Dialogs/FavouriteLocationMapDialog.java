package com.gic.memorableplaces.Dialogs;

import androidx.fragment.app.Fragment;

public class FavouriteLocationMapDialog extends Fragment /*implements OnMapReadyCallback*/ {

//
//    private static final String TAG = "FavouriteLocationMapDialog";
//    private static final int PERMISSIONS_REQUEST_CODE = 105;
//
//    private Context mContext;
//    private Activity activity;
//
//    private GoogleMap mMap;
//    private FusedLocationProviderClient fusedLocationProviderClient;
//    private PlacesClient placesClient;
//    private List<AutocompletePrediction> predictionList;
//
//    private Location mLastKnownLocation;
//    private LocationCallback locationCallback;
//
//    private MaterialSearchBar LocationSearchBar;
//    private View mapView;
//    private AppCompatButton ViewSelectedLocations;
//    private AutocompleteSessionToken token;
//
//    private final int DEFAULT_ZOOM = 18;
//
//    private boolean locationPermissionGiven = false;
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.favoruite_places_dialog_map, container, false);
//
//        mContext = getActivity();
////        try {
////            activity = ((HomeActivity) getActivity());
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//        LocationSearchBar = view.findViewById(R.id.searchBar);
//        ViewSelectedLocations = view.findViewById(R.id.ViewSelected);
//
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
//        Places.initialize(mContext, mContext.getString(R.string.The_GOOGLE_MAPS_API_KEY));
//        placesClient = Places.createClient(mContext);
//        token = AutocompleteSessionToken.newInstance();
//
//        getLocationPermissions();
//        PerformSearch();
//
//        return view;
//    }
//private void PerformSearch(){
//        LocationSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
//            @Override
//            public void onSearchStateChanged(boolean enabled) {
//
//            }
//
//            @Override
//            public void onSearchConfirmed(CharSequence text) {
//                requireActivity().startSearch(text.toString(),true,null,true);
//            }
//
//            @Override
//            public void onButtonClicked(int buttonCode) {
//
//                if(buttonCode == MaterialSearchBar.BUTTON_BACK){
//                    LocationSearchBar.clearSuggestions();
//                    LocationSearchBar.closeSearch();
//                }
//            }
//        });
//
//        LocationSearchBar.addTextChangeListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                final FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
//                        .setTypeFilter(TypeFilter.ADDRESS)
//                        .setSessionToken(token)
//                        .setQuery(s.toString())
//                        .build();
//
//                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
//                    @Override
//                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
//                        if(task.isSuccessful()){
//                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
//                            if(predictionsResponse != null){
//                                predictionList = predictionsResponse.getAutocompletePredictions();
//
//                                List<String> suggestionList = new ArrayList<>();
//                                for(int i = 0; i < predictionList.size();i++){
//                                    AutocompletePrediction prediction = predictionList.get(i);
//                                    suggestionList.add(prediction.getFullText(null).toString());
//                                }
//                                LocationSearchBar.setLastSuggestions(suggestionList);
//                                if(!LocationSearchBar.isSuggestionsVisible()){
//                                    LocationSearchBar.showSuggestionsList();
//                                }
//                            }
//                        }
//                        else{
//                            Exception exception = task.getException();
//                            Log.d(TAG, "onComplete: exception " + exception.getMessage());
//                            Toast.makeText(mContext, "Search Unsuccessful! :( ", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//}
//
//    private void GetMap() {
//        SupportMapFragment MapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.FavLocMap);
//        // assert MapFragment != null;
//        MapFragment.getMapAsync(this);
//
//        mapView = MapFragment.getView();
//    }
//
//    @SuppressLint("MissingPermission")
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        Toast.makeText(mContext, "Welcome to Map", Toast.LENGTH_SHORT).show();
//        mMap = googleMap;
//        mMap.setMyLocationEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
//
//        if (mapView != null & mapView.findViewById((Integer.parseInt("1"))) != null) {
//            View locationButton = ((View) mapView.findViewById((Integer.parseInt("1"))).getParent()).findViewById((Integer.parseInt("2")));
//            locationButton.setDrawingCacheBackgroundColor(Color.BLACK);
//            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
//            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//            layoutParams.setMargins(0, 0, 40, 240);
//        }
//
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setInterval(10000);
//        locationRequest.setFastestInterval(5000);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
//
//        SettingsClient settingsClient = LocationServices.getSettingsClient(mContext);
//        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
//
//        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
//            @Override
//            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//                getDeviceLocation();
//            }
//        });
//
//        task.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//                if (e instanceof ResolvableApiException) {
//                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;
//                    try {
//                        resolvableApiException.startResolutionForResult(getActivity(), 123);
//
//                    } catch (IntentSender.SendIntentException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//            }
//        });
//
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//
//
//        if (requestCode == 123) {
//            if (resultCode == RESULT_OK) {
//                getDeviceLocation();
//            }
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    private void getDeviceLocation() {
//        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
//            @Override
//            public void onComplete(@NonNull Task<Location> task) {
//                if(task.isSuccessful()){
//                    mLastKnownLocation = task.getResult();
//
//                    if(mLastKnownLocation != null){
//                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude()
//                                ,mLastKnownLocation.getLongitude()),DEFAULT_ZOOM));
//
//                    }
//                    else{
//                        final LocationRequest locationRequest = LocationRequest.create();
//                        locationRequest.setInterval(10000);
//                        locationRequest.setFastestInterval(5000);
//                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//                        locationCallback = new LocationCallback(){
//                            @Override
//                            public void onLocationResult(LocationResult locationResult) {
//                                super.onLocationResult(locationResult);
//                                if(locationResult == null){
//                                    return;
//                                }
//                                else{
//                                    mLastKnownLocation = locationResult.getLastLocation();
//                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
//                                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
//                                }
//                            }
//                        };
//
//                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null);
//                    }
//                }else{
//                    Toast.makeText(mContext, "Unable to get current Location. Please try again", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
//    }
//
//    private void getLocationPermissions() {
//
//
//        //String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
//        Dexter.withContext(this.getActivity())
//                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
//                        Manifest.permission.ACCESS_FINE_LOCATION)
//                .withListener(new MultiplePermissionsListener() {
//                    @Override
//                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
//                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
//                            GetMap();
//                        } else if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                            builder.setTitle("Location Permissions!")
//                                    .setMessage("Location permission is needed to choose your favourite locations from a map.")
//                                    .setPositiveButton("OK",
//                                            new DialogInterface.OnClickListener() {
//                                                public void onClick(DialogInterface dialog, int whichButton) {
//                                                    Intent intent = new Intent();
//                                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                                    intent.setData(Uri.fromParts("package", mContext.getPackageName(), null));
//                                                    startActivity(intent);
//                                                }
//                                            }
//                                    )
//                                    .setNegativeButton("Cancel",
//                                            new DialogInterface.OnClickListener() {
//                                                public void onClick(DialogInterface dialog, int whichButton) {
//                                                    dialog.dismiss();
//                                                }
//                                            }
//                                    )
//                                    .create();
//                            builder.show();
//                        } else {
//                            Toast.makeText(mContext, "Permission Denied", Toast.LENGTH_SHORT).show();
//                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                            builder.setTitle("Location Permissions!")
//                                    .setMessage("Location permission is needed to choose your favourite locations from the map.")
//                                    .setPositiveButton("OK",
//                                            (dialog, whichButton) -> getLocationPermissions()
//                                    )
//                                    .setNegativeButton("Cancel",
//                                            (dialog, whichButton) -> {
//                                                dialog.dismiss();
//                                                requireFragmentManager().beginTransaction().remove(FavouriteLocationMapDialog.this).commit();
//                                            }
//                                    )
//                                    .create();
//                            builder.show();
//                        }
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
//                        permissionToken.continuePermissionRequest();
//                    }
//                }).check();
//    }
}