package ir.kitgroup.order.Fragments.Client.Register;

import android.Manifest;
import android.app.AlertDialog;

import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.BitmapFactory;


import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.CedarMapsStyle;
import com.cedarstudios.cedarmapssdk.CedarMapsStyleConfigurator;
import com.cedarstudios.cedarmapssdk.listeners.OnStyleConfigurationListener;

import com.cedarstudios.cedarmapssdk.listeners.ReverseGeocodeResultListener;
import com.cedarstudios.cedarmapssdk.model.geocoder.reverse.ReverseGeocode;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


import java.util.Objects;
import java.util.UUID;

import ir.kitgroup.order.Util.Util;
import ir.kitgroup.order.classes.App;
import ir.kitgroup.order.DataBase.Account;

import ir.kitgroup.order.DataBase.User;
import ir.kitgroup.order.Fragments.MobileView.MainOrderMobileFragment;

import ir.kitgroup.order.models.ModelLog;
import ir.kitgroup.order.R;
import ir.kitgroup.order.databinding.FragmentRegisterBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;
import static android.os.Looper.getMainLooper;


public class RegisterFragment extends Fragment implements PermissionsListener {
    //region  Parameter


    private FragmentRegisterBinding binding;
    private final List<Account> accountsList = new ArrayList<>();
    private User user;
    private int gender = 0;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 99;
    private LocationManager locationManager;
    private Boolean registerGps = false;
    private MapboxMap mMapboxMap;
    private PermissionsManager permissionsManager;
    private static final String DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID";


    private static final String MARKERS_SOURCE = "markers-source";
    private static final String MARKERS_LAYER = "markers-layer";
    private static final String MARKER_ICON_ID = "marker-icon-id";


    private LocationEngine locationEngine = null;
    private MapFragmentLocationCallback callback = new MapFragmentLocationCallback(this);


    private double longitude = 0.0;
    private double latitude = 0.0;


    //endregion Parameter
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(getLayoutInflater());

        Bundle bundle = getArguments();
        assert bundle != null;
        String mobile = bundle.getString("mobile");
        String edit_address = bundle.getString("edit_address");


        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);


        binding.edtNumberPhoneCustomer.setText(mobile);
        user = Select.from(User.class).first();

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


        binding.btnRegisterInformation.setOnClickListener(v -> {
            if (!registerGps) {
                if (edit_address != null && edit_address.equals("1")) {
                    Account account = Select.from(Account.class).first();
                    account.ADR1 = binding.edtAddress.getText().toString();
                    account.update();
                    assert getFragmentManager() != null;
                    getFragmentManager().popBackStack();
                    Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("PaymentFragment");
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    assert frg != null;
                    ft.detach(frg);
                    ft.attach(frg);
                    ft.commit();

                    return;

                }
                if (edit_address != null && edit_address.equals("2")) {
                    Account account = Select.from(Account.class).first();
                    if (mobile.equals("1"))
                    account.ADR = binding.edtAddress.getText().toString()+" "+binding.edtAddressComplete.getText().toString();
                    else
                        account.ADR1 = binding.edtAddress.getText().toString()+" "+binding.edtAddressComplete.getText().toString();
                    account.update();
                    assert getFragmentManager() != null;

                    Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("ProfileFragment");
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    if (frg != null){
                        getFragmentManager().popBackStack();
                        ft.detach(frg);
                        ft.attach(frg);
                        ft.commit();
                    }


                    return;

                }
                binding.layoutMap.setVisibility(View.GONE);
                binding.cardInfoCustomer.setVisibility(View.VISIBLE);
                binding.rlFloating.setVisibility(View.GONE);
                registerGps = true;
                binding.edtAddressCustomerComplete.setText(binding.edtAddressComplete.getText().toString());
                binding.btnRegisterInformation.setEnabled(true);
                binding.btnRegisterInformation.setText("ثبت اطلاعات");
                binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
            } else {


                if (
                        binding.edtFLNameCustomer.getText().toString().isEmpty() ||
                                binding.edtNumberPhoneCustomer.getText().toString().isEmpty() ||
                                binding.edtAddressCustomer.getText().toString().isEmpty() ||
                                binding.edtPlaqueCustomer.getText().toString().isEmpty()
                ) {
                    Toast.makeText(getActivity(), "لطفا تمام فیلد ها را پر کنید", Toast.LENGTH_SHORT).show();
                    return;
                }

                Account account = new Account();
                account.I = UUID.randomUUID().toString();
                account.N = binding.edtFLNameCustomer.getText().toString();
                account.M = binding.edtNumberPhoneCustomer.getText().toString();
                account.ADR = binding.edtAddressCustomer.getText().toString() + " پلاک " + binding.edtPlaqueCustomer.getText().toString();
                account.S = String.valueOf(gender);
                accountsList.clear();
                accountsList.add(account);
                addAccount(user.userName, user.passWord, accountsList);

            }
        });


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(mapboxMap -> {
            this.mMapboxMap = mapboxMap;

            CedarMapsStyleConfigurator.configure(
                    CedarMapsStyle.VECTOR_LIGHT, new OnStyleConfigurationListener() {
                        @Override
                        public void onSuccess(@NonNull Style.Builder styleBuilder) {

                            mapboxMap.setStyle(styleBuilder, style -> {
                                if (getActivity() == null) {
                                    return;
                                }


                                setupCurrentLocationButton();
                                if (!PermissionsManager.areLocationPermissionsGranted(getActivity())) {
                                    enableLocationComponent(style);
                                }
                            });
                        }

                        @Override
                        public void onFailure(@NonNull String errorMessage) {
                            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });

            this.mMapboxMap.setMaxZoomPreference(18);
            this.mMapboxMap.setMinZoomPreference(6);
            this.mMapboxMap.setCameraPosition(
                    new CameraPosition.Builder()
                            .target(Util.VANAK_SQUARE)
                            .zoom(15)
                            .build());


            reverseGeocode(mapboxMap.getCameraPosition());

            mMapboxMap.addOnCameraIdleListener(() -> reverseGeocode(mMapboxMap.getCameraPosition()));

//            this.mMapboxMap.addOnMapClickListener(point -> {
//
//                addMarkerToMapViewAtPosition(point);
//                reverseGeocode(mapboxMap.getCameraPosition());
//
//                return true;
//            });


            binding.floating.setOnClickListener(v -> setupCurrentLocationButton());

        });


    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {

    }


    private static class JsonObjectAccount {

        public List<Account> Account;

    }

    private void addAccount(String userName, String pass, List<Account> accounts) {


        try {
            JsonObjectAccount jsonObjectAcc = new JsonObjectAccount();
            jsonObjectAcc.Account = accounts;


            Gson gson = new Gson();
            Type typeJsonObject = new TypeToken<JsonObjectAccount>() {
            }.getType();

            Call<String> call = App.api.addAccount(userName, pass, gson.toJson(jsonObjectAcc, typeJsonObject), "");
            binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
            binding.btnRegisterInformation.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelLog>() {
                    }.getType();
                    ModelLog iDs = gson.fromJson(response.body(), typeIDs);

                    assert iDs != null;
                    int message = iDs.getLogs().get(0).getMessage();
                    String description = iDs.getLogs().get(0).getDescription();
                    if (message == 1) {
                        Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();

                        Account.deleteAll(Account.class);
                        Account.saveInTx(accountsList);
                        accountsList.clear();
                        Bundle bundle = new Bundle();
                        bundle.putString("Ord_TYPE", "");
                        bundle.putString("Tbl_GUID", "");
                        bundle.putString("Inv_GUID", "");
                        MainOrderMobileFragment mainOrderMobileFragment = new MainOrderMobileFragment();
                        mainOrderMobileFragment.setArguments(bundle);
                        FragmentTransaction replaceFragment = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment");
                        replaceFragment.commit();


                    } else {

                        Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                    }
                    binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
                    binding.btnRegisterInformation.setEnabled(true);
                    binding.progressBar.setVisibility(View.GONE);


                }


                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getContext(), "خطای تایم اوت در ثبت مشتری" + t.toString(), Toast.LENGTH_SHORT).show();

                    binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
                    binding.btnRegisterInformation.setEnabled(true);
                    binding.progressBar.setVisibility(View.GONE);


                }
            });


        } catch (NetworkOnMainThreadException ex) {

            Toast.makeText(getContext(), "خطا در ثبت مشتری" + ex.toString(), Toast.LENGTH_SHORT).show();
            binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.btnRegisterInformation.setEnabled(true);
            binding.progressBar.setVisibility(View.GONE);

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            enableMyLocation();
        } else {

            Toast.makeText(getContext(), "نرم افزار به مکان یاب دستگاه دسترسی ندارد", Toast.LENGTH_SHORT).show();
            Objects.requireNonNull(getActivity()).finish();
        }
    }

    private void enableMyLocation() {

        try {


            if (ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);

            } else if (Objects.requireNonNull(locationManager).getProviders(true).size() < 2) {

                AlertNoGPS();
            } else {

                try {


                } catch (Exception ex) {

                    Toast.makeText(getContext(), "نرم افزار به مکان یاب دستگاه دسترسی ندارد", Toast.LENGTH_SHORT).show();
                    Objects.requireNonNull(getActivity()).finish();
                }
            }
        } catch (Exception ex) {

            Toast.makeText(getContext(), "نرم افزار به مکان یاب دستگاه دسترسی ندارد", Toast.LENGTH_SHORT).show();
        }
    }

    private void AlertNoGPS() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage("برای استفاده مکان یاب دستگاه باید روشن باشد. لطفا با تایید آنرا روشن نمایید.")
                .setCancelable(false)
                .setPositiveButton("تایید", (dialog, id) -> startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 91));
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 91) {

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                if (Objects.requireNonNull(locationManager).getProviders(true).size() < 2) {
                    Toast.makeText(getContext(), "مکان یاب باید روشن باشد", Toast.LENGTH_SHORT).show();

                }
                enableMyLocation();

            }
        }


    }

    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (getActivity() == null) {
            return;
        }

        if (PermissionsManager.areLocationPermissionsGranted(getActivity())) {

            LocationComponent locationComponent = mMapboxMap.getLocationComponent();
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(getActivity(), loadedMapStyle)
                            .useDefaultLocationEngine(true)
                            .build();

            locationComponent.activateLocationComponent(locationComponentActivationOptions);
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                getActivity().finish();
                return;
            }
            locationComponent.setLocationComponentEnabled(true);

            initializeLocationEngine();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

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

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    private void addMarkerToMapViewAtPosition(LatLng coordinate) {
        if (mMapboxMap != null && mMapboxMap.getStyle() != null) {
            Style style = mMapboxMap.getStyle();

            if (style.getImage(MARKER_ICON_ID) == null) {
                style.addImage(MARKER_ICON_ID, BitmapFactory.decodeResource(getResources(), R.drawable.ic_location_marker));
            }

            GeoJsonSource geoJsonSource;
            if (style.getSource(MARKERS_SOURCE) == null) {
                geoJsonSource = new GeoJsonSource(MARKERS_SOURCE);
                style.addSource(geoJsonSource);
            } else {
                geoJsonSource = (GeoJsonSource) style.getSource(MARKERS_SOURCE);
            }
            if (geoJsonSource == null) {
                return;
            }

            Feature feature = Feature.fromGeometry(
                    Point.fromLngLat(coordinate.getLongitude(), coordinate.getLatitude()));


            geoJsonSource.setGeoJson(feature);

            style.removeLayer(MARKERS_LAYER);

            SymbolLayer symbolLayer = new SymbolLayer(MARKERS_LAYER, MARKERS_SOURCE);
            symbolLayer.withProperties(
                    PropertyFactory.iconImage(MARKER_ICON_ID),
                    PropertyFactory.iconAllowOverlap(true)
            );
            style.addLayer(symbolLayer);
        }
    }

    private void setupCurrentLocationButton() {
        if (getView() == null) {
            return;
        }


        if (mMapboxMap.getStyle() != null) {
            enableLocationComponent(mMapboxMap.getStyle());
        }
        toggleCurrentLocationButton();

    }

    private void toggleCurrentLocationButton() {
        if (!mMapboxMap.getLocationComponent().isLocationComponentActivated() || !mMapboxMap.getLocationComponent().isLocationComponentEnabled()) {
            return;
        }
        Location location = mMapboxMap.getLocationComponent().getLastKnownLocation();
        if (location != null) {
            animateToCoordinate(new LatLng(location.getLatitude(), location.getLongitude()));
            //addMarkerToMapViewAtPosition(new LatLng(location.getLatitude(), location.getLongitude()));

        }

        switch (mMapboxMap.getLocationComponent().getRenderMode()) {
            case RenderMode.NORMAL:
                mMapboxMap.getLocationComponent().setRenderMode(RenderMode.COMPASS);
                break;
            case RenderMode.GPS:
            case RenderMode.COMPASS:
                mMapboxMap.getLocationComponent().setRenderMode(RenderMode.NORMAL);
                break;
        }
    }

    private void animateToCoordinate(LatLng coordinate) {
        CameraPosition position = new CameraPosition.Builder()
                .target(coordinate)
                .zoom(16)
                .build();
        mMapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    private static class MapFragmentLocationCallback implements LocationEngineCallback<LocationEngineResult> {

        private WeakReference<RegisterFragment> fragmentWeakReference;

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

                if (fragment.mMapboxMap != null && result.getLastLocation() != null) {
                    fragment.mMapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                }
            }
        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            String message = exception.getLocalizedMessage();
            if (message != null) {

            }
        }
    }

    private void reverseGeocode(CameraPosition position) {

        if (TextUtils.isEmpty(binding.edtAddress.getText())) {
            binding.edtAddress.setVisibility(View.GONE);
        } else {
            binding.edtAddress.setVisibility(View.VISIBLE);
        }
        binding.pro.setVisibility(View.VISIBLE);

        CedarMaps.getInstance().reverseGeocode(
                new LatLng(position.target.getLatitude(), position.target.getLongitude()),
                new ReverseGeocodeResultListener() {
                    @Override
                    public void onSuccess(@NonNull ReverseGeocode result) {

                       // addMarkerToMapViewAtPosition(new LatLng(position.target.getLatitude(), position.target.getLongitude()));
                        latitude = position.target.getLatitude();
                        longitude = position.target.getLongitude();
                        binding.edtAddress.setVisibility(View.VISIBLE);
                        binding.pro.setVisibility(View.GONE);
                        binding.edtAddressCustomer.setText(result.getPlace() + " " + result.getCity() + " " + result.getAddress() + " " + result.getDistrict() + " ");

                        binding.edtAddress.setText(result.getPlace() + " " + result.getCity() + " " + result.getAddress() + " " + result.getDistrict() + " ");
                    }

                    @Override
                    public void onFailure(@NonNull String errorMessage) {
                        binding.edtAddress.setVisibility(View.VISIBLE);
                        binding.pro.setVisibility(View.GONE);
                        binding.edtAddress.setText("PARS ERROR");
                    }
                });

//
//        CedarMaps.getInstance().reverseGeocode(
//                position.target,
//                null,
//                "short",
//                null,
//                false, new ReverseGeocodeResultListener() {
//                    @Override
//                    public void onSuccess(@NonNull ReverseGeocode result) {
//                        binding.progressBar.setVisibility(View.GONE);
//                        binding.tvAddress.setVisibility(View.VISIBLE);
//
//                        String address = result.getFormattedAddress();
//                        if (address != null) {
//                            binding.tvAddress.setText(address);
//                        } else {
//                            binding.tvAddress.setText("R.string.no_address_available");
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(@NonNull String errorMessage) {
//                        binding.progressBar.setVisibility(View.GONE);
//                        binding.tvAddress.setVisibility(View.VISIBLE);
//
//                        binding.tvAddress.setText("getString(R.string.parse_error)");
//                    }
//                });

    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.mapView.onSaveInstanceState(outState);
    }

    @Override
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
        binding.mapView.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


}
