package ir.kitgroup.saleindemo.ui.logins;

import static android.os.Looper.getMainLooper;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.cedarstudios.cedarmapssdk.CedarMapsStyle;
import com.cedarstudios.cedarmapssdk.CedarMapsStyleConfigurator;
import com.cedarstudios.cedarmapssdk.listeners.OnStyleConfigurationListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;

import ir.kitgroup.saleindemo.Connect.MyViewModel;
import ir.kitgroup.saleindemo.DataBase.Salein;
import ir.kitgroup.saleindemo.DataBase.Users;
import ir.kitgroup.saleindemo.classes.Util;
import ir.kitgroup.saleindemo.databinding.RegisterFragmentBinding;
import ir.kitgroup.saleindemo.ui.companies.CompanyFragment;
import ir.kitgroup.saleindemo.DataBase.Company;
import ir.kitgroup.saleindemo.R;
import ir.kitgroup.saleindemo.models.Setting;


@AndroidEntryPoint
public class RegisterFragment extends Fragment implements PermissionsListener {

    //region Variable
    private MyViewModel myViewModel;
    private RegisterFragmentBinding binding;
    private Company company;

    private final List<Users> accountsList = new ArrayList<>();
    private int gender = -1;
    private boolean ACCSTP = true;
    private String from;//this Variable Show From Which Fragment Did We Enter This Fragment?
    private String mobile;

    private MapboxMap mapboxMap;


    private LocationEngine locationEngine = null;
    private final MapFragmentLocationCallback callback = new MapFragmentLocationCallback(this);
    private PermissionsManager permissionsManager;
    //endregion Variable


    //region Override Method

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = RegisterFragmentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //region Config Map

        try {
            binding.mapView.onCreate(savedInstanceState);
        } catch (Exception ignore) {
        }

        binding.mapView.onCreate(savedInstanceState);


        binding.mapView.getMapAsync(mapboxMap -> {
            this.mapboxMap = mapboxMap;

            CedarMapsStyleConfigurator.configure(
                    CedarMapsStyle.VECTOR_LIGHT, new OnStyleConfigurationListener() {
                        @Override
                        public void onSuccess(@NonNull Style.Builder styleBuilder) {
                            mapboxMap.setStyle(styleBuilder, style -> {
                                if (getActivity() == null) {
                                    return;
                                }

                                //Add marker to map
                                if (mapboxMap.getStyle() != null) {
                                    enableLocationComponent(mapboxMap.getStyle());
                                }
                                toggleCurrentLocationButton();
                                addMarkerToMapViewAtPosition(new LatLng(Util.latitude, Util.longitude));
                            });
                        }

                        @Override
                        public void onFailure(@NonNull String errorMessage) {
                            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });

            this.mapboxMap.setMaxZoomPreference(18);
            this.mapboxMap.setMinZoomPreference(6);
            this.mapboxMap.setCameraPosition(
                    new CameraPosition.Builder()
                            .target(new LatLng(Util.latitude, Util.longitude))
                            .zoom(15)
                            .build());

            //Set a touch event listener on the map
            this.mapboxMap.addOnMapClickListener(point -> {
                Util.nameUser = binding.edtName.getText().toString();
                Util.address = binding.edtAddress.getText().toString();
                NavDirections action = RegisterFragmentDirections.actionGoToMapFragment("RegisterFragment");
                Navigation.findNavController(binding.getRoot()).navigate(action);
                return false;
            });

        });
        //endregion Config Map


        //region Get Bundle And Set Data
        from = RegisterFragmentArgs.fromBundle(getArguments()).getFrom();
        mobile = RegisterFragmentArgs.fromBundle(getArguments()).getMobile();
        //endregion Get Bundle And Set Data

        //region Get User Is Save In Database And Complete Information If User Is Not Null
        Users user = Select.from(Users.class).first();

        if (user != null) {
            binding.edtName.setEnabled(false);
            binding.edtName.setText(user.N);
            Util.latitude = Double.parseDouble(user.LAT);
            Util.latitude = Double.parseDouble(user.LAT);

            if (user.getS().equals("1")) {
                binding.radioWoman.setChecked(true);
                gender = 1;
            }
            else if (user.getS().equals("0")) {
                binding.radioMan.setChecked(true);
                gender = 0;
            }
            if (!Util.address.equals(""))
                binding.edtAddress.setText(user.ADR);
            else
                binding.edtAddress.setText(Util.address);
        }


        else {
            binding.edtAddress.setText(Util.address);
            binding.edtName.setText(Util.nameUser);
        }
        //endregion Get User Is Save In Database And Complete Information If User Is Not Null

        //region Get The Company Is Save In The Database
        company = Select.from(Company.class).first();
        //endregion Get The Company Is Save In The Database

        //region Press RadioButton
        binding.radioMan.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                gender = 0;
            }
        });
        binding.radioWoman.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                gender = 1;
            }
        });


        //endregion Press RadioButton

        //region Action btnRegisterInformation
        binding.btnRegisterInformation.setOnClickListener(v -> {
            if (binding.edtName.getText().toString().isEmpty()
                    || gender == -1
            ) {

                Toasty.error(requireActivity(), "لطفا فیلدهای ستاره دار را پر کنید.", Toast.LENGTH_SHORT, true).show();
                return;
            }
            if (from.equals("VerifyFragment")){
                Users account = new Users();
                account.I = UUID.randomUUID().toString();
                account.N = binding.edtName.getText().toString();
                account.M = mobile;
                account.PSW = mobile;
                account.ADR = binding.edtAddress.getText().toString();
                account.LAT = String.valueOf(Util.latitude);
                account.LNG = String.valueOf(Util.longitude);
                account.S = String.valueOf(gender);
                account.PC = binding.edtCode.getText().toString();
                account.STAPP = ACCSTP;
                accountsList.clear();
                accountsList.add(account);
                myViewModel.getSetting(company.USER, company.PASS);
            }

        });
        //endregion Action btnRegisterInformation*/
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        myViewModel.getResultMessage().setValue(null);

        myViewModel.getResultAddAccount().observe(getViewLifecycleOwner(), result -> {
            binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.btnRegisterInformation.setEnabled(true);

            if (result == null)
                return;
            myViewModel.getResultAddAccount().setValue(null);

            if (result) {
                Users.deleteAll(Users.class);
                Users.saveInTx(accountsList);
                accountsList.clear();


                //region  Go To CompanyFragment Because Account Is Register
                if (Select.from(Salein.class).first() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new CompanyFragment(), "StoriesFragment").commit();
                }
                //endregion  Go To CompanyFragment Because Account Is Register


                //region Go To MainOrderFragment Because Account Is Register
                else {
                    NavDirections action = VerifyFragmentDirections.actionGoToMainFragment("");
                    Navigation.findNavController(binding.getRoot()).navigate(action);
                }
                //endregion Go To MainOrderFragment Because Account Is Register
            }
        });

        myViewModel.getResultSetting().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            myViewModel.getResultSetting().setValue(null);
            List<Setting> settingsList = new ArrayList<>(result);
            if (settingsList.size() > 0) {
                String accStp = settingsList.get(0).ACC_STATUS_APP;
                ACCSTP = !accStp.equals("0");
                accountsList.get(0).STAPP = ACCSTP;
                binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
                binding.btnRegisterInformation.setEnabled(false);
                myViewModel.addAccount(company.USER, company.PASS, accountsList);
            }


        });

        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {
            binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.btnRegisterInformation.setEnabled(true);

            if (result == null)
                return;
            Toasty.warning(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
        });

    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.mapView.onSaveInstanceState(outState);
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onStart() {
        super.onStart();
        binding.mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.mapView.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        binding.mapView.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(getActivity(), "برنامه نیاز به دسترسی دارد", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            if (mapboxMap.getStyle() != null) {
                enableLocationComponent(mapboxMap.getStyle());
                toggleCurrentLocationButton();
            }
        } else {
            Toast.makeText(getActivity(), "برنامه نیاز به دسترسی دارد", Toast.LENGTH_LONG).show();
        }
    }

    //endregion Override Method


    //region Method

    private void addMarkerToMapViewAtPosition(LatLng coordinate) {
        if (mapboxMap != null && mapboxMap.getStyle() != null) {
            Style style = mapboxMap.getStyle();

            if (style.getImage(Util.MARKER_ICON_ID) == null) {
                style.addImage(Util.MARKER_ICON_ID,
                        BitmapFactory.decodeResource(
                                getResources(), R.drawable.ic_location_marker));
            }

            GeoJsonSource geoJsonSource;
            if (style.getSource(Util.MARKERS_SOURCE) == null) {
                geoJsonSource = new GeoJsonSource(Util.MARKERS_SOURCE);
                style.addSource(geoJsonSource);
            } else {
                geoJsonSource = (GeoJsonSource) style.getSource(Util.MARKERS_SOURCE);
            }
            if (geoJsonSource == null) {
                return;
            }

            Feature feature = Feature.fromGeometry(
                    Point.fromLngLat(coordinate.getLongitude(), coordinate.getLatitude()));
            geoJsonSource.setGeoJson(feature);

            style.removeLayer(Util.MARKERS_LAYER);

            SymbolLayer symbolLayer = new SymbolLayer(Util.MARKERS_LAYER, Util.MARKERS_SOURCE);
            symbolLayer.withProperties(
                    PropertyFactory.iconImage(Util.MARKER_ICON_ID),
                    PropertyFactory.iconAllowOverlap(true)
            );
            style.addLayer(symbolLayer);
        }
    }

    private void animateToCoordinate(LatLng coordinate) {
        CameraPosition position = new CameraPosition.Builder()
                .target(coordinate)
                .zoom(16)
                .build();
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    @SuppressLint("MissingPermission")
    private void toggleCurrentLocationButton() {
        if (!mapboxMap.getLocationComponent().isLocationComponentActivated() || !mapboxMap.getLocationComponent().isLocationComponentEnabled()) {
            return;
        }
        Location location = mapboxMap.getLocationComponent().getLastKnownLocation();
        if (location != null) {
            animateToCoordinate(new LatLng(location.getLatitude(), location.getLongitude()));
            Util.latitude = location.getLatitude();
            Util.longitude = location.getLongitude();
        }

        switch (mapboxMap.getLocationComponent().getRenderMode()) {
            case RenderMode.NORMAL:
                mapboxMap.getLocationComponent().setRenderMode(RenderMode.COMPASS);
                break;
            case RenderMode.GPS:
            case RenderMode.COMPASS:
                mapboxMap.getLocationComponent().setRenderMode(RenderMode.NORMAL);
                break;
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (getActivity() == null) {
            return;
        }
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getActivity())) {

            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(getActivity(), loadedMapStyle)
                            .useDefaultLocationEngine(true)
                            .build();

            locationComponent.activateLocationComponent(locationComponentActivationOptions);
            locationComponent.setLocationComponentEnabled(true);

            initializeLocationEngine();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @SuppressWarnings({"MissingPermission"})
    private void initializeLocationEngine() {
        if (getActivity() == null) {
            return;
        }

        locationEngine = LocationEngineProvider.getBestLocationEngine(getActivity());

        long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
        long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    private static class MapFragmentLocationCallback implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<RegisterFragment> fragmentWeakReference;

        MapFragmentLocationCallback(RegisterFragment fragment) {
            fragmentWeakReference = new WeakReference<>(fragment);
        }


        @Override
        public void onSuccess(LocationEngineResult result) {
            RegisterFragment fragment = fragmentWeakReference.get();

            if (fragment != null) {
                Location location = result.getLastLocation();

                if (location == null) {
                    return;
                }

                if (fragment.mapboxMap != null && result.getLastLocation() != null) {
                    fragment.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                }
            }
        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            //String message = exception.getLocalizedMessage();
        }
    }
    //endregion Method


}