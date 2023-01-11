package ir.kitgroup.salein.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;

import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.CedarMapsStyle;
import com.cedarstudios.cedarmapssdk.CedarMapsStyleConfigurator;
import com.cedarstudios.cedarmapssdk.listeners.ForwardGeocodeResultsListener;
import com.cedarstudios.cedarmapssdk.listeners.OnStyleConfigurationListener;
import com.cedarstudios.cedarmapssdk.listeners.ReverseGeocodeResultListener;
import com.cedarstudios.cedarmapssdk.model.geocoder.forward.ForwardGeocode;
import com.cedarstudios.cedarmapssdk.model.geocoder.reverse.ReverseGeocode;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.CircleManager;
import com.mapbox.mapboxsdk.plugins.annotation.CircleOptions;

import com.mapbox.mapboxsdk.utils.ColorUtils;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.DataBase.Locations;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.databinding.FragmentMapBinding;

import static android.content.Context.LOCATION_SERVICE;
import static android.os.Looper.getMainLooper;


@AndroidEntryPoint

public class MapFragment extends Fragment implements PermissionsListener {

    //region Parameter

    private FragmentMapBinding binding;

    private final int LOCATION_PERMISSION_REQUEST_CODE = 99;
    private LocationManager locationManager;
    private MapboxMap mMapboxMap;
    private final MapFragmentLocationCallback callback = new MapFragmentLocationCallback(this);
    private SearchViewAdapter mRecyclerAdapter;
    private State state = State.MAP;
    private CircleManager circleManager;


    private double latitude = 0.0;
    private double longitude = 0.0;
    private String address="";


    private enum State {
        MAP,
        MAP_PIN,
        SEARCHING,
        RESULTS
    }



    //endregion Parameter
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            config();
            init();
            onClickBtnRegister();
            initMap(savedInstanceState);
            initSearchOnMap(view);
        } catch (Exception ignored) {}

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
            requireActivity().finish();
        }
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
        binding = null;

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
    }

    @Override
    public void onPermissionResult(boolean granted) {
    }


    //region Method
    private void config(){
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(4);
    }

    private void init(){
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        binding.ivBack.setOnClickListener(view1 -> Navigation.findNavController(binding.getRoot()).popBackStack());
    }





    private void onClickBtnRegister(){
        binding.btnRegisterInformation.setOnClickListener(v -> {
            setLocation();
            Navigation.findNavController(binding.getRoot()).popBackStack();
        });
    }

    private void initMap(Bundle savedInstanceState){

        try {
            binding.mapView.onCreate(savedInstanceState);
        } catch (Exception ignore) {
        }

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
                                circleManager = new CircleManager(binding.mapView, mMapboxMap, style);

                                setupCurrentLocationButton();

                                if (PermissionsManager.areLocationPermissionsGranted(getActivity())) {
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
                            .target(new LatLng(latitude,longitude))
                            .zoom(12)
                            .build());


            reverseGeocode(mapboxMap.getCameraPosition());

            mMapboxMap.addOnCameraIdleListener(() -> reverseGeocode(mMapboxMap.getCameraPosition()));

            binding.floating.setOnClickListener(v -> setupCurrentLocationButton());

        });
    }

    private void setLocation(){
        Locations.deleteAll(Locations.class);
        Locations locations=new Locations();
        locations.setLongitude(longitude);
        locations.setLatitude(latitude);
        locations.setAddress(address);
        locations.save();
    }

    private void enableMyLocation() {

        try {


            if (ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);

            } else if (Objects.requireNonNull(locationManager).getProviders(true).size() < 2) {

                AlertNoGPS();
            } else {

                try {


                } catch (Exception ex) {

                    Toast.makeText(getContext(), "نرم افزار به مکان یاب دستگاه دسترسی ندارد", Toast.LENGTH_SHORT).show();
                    requireActivity().finish();
                }
            }
        } catch (Exception ex) {

            Toast.makeText(getContext(), "نرم افزار به مکان یاب دستگاه دسترسی ندارد", Toast.LENGTH_SHORT).show();
        }
    }

    private void AlertNoGPS() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogTheme);

        builder.setMessage("برای استفاده مکان یاب دستگاه باید روشن باشد. لطفا با تایید آنرا روشن نمایید.")
                .setCancelable(false)
                .setPositiveButton("تایید", (dialog, id) -> startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 91));
        AlertDialog alert = builder.create();
        alert.show();


        TextView textView = alert.findViewById(android.R.id.message);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "iransans.ttf");
        textView.setTypeface(face);
        textView.setTextColor(getActivity().getResources().getColor(R.color.medium_color));
        textView.setTextSize(13);
    }

    public static class JsonObjectAccount {

        public List<Account> Account;

    }

    private void setupCurrentLocationButton() {
        if (getView() == null) {
            return;
        }

        if (mMapboxMap.getStyle() != null)
            enableLocationComponent(mMapboxMap.getStyle());

        toggleCurrentLocationButton();

    }

    @SuppressLint("MissingPermission")
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
            AlertPermission();
        }
    }

    @SuppressLint("MissingPermission")
    private void initializeLocationEngine() {
        if (getActivity() == null) {
            return;
        }

        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(getActivity());

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

    private void AlertPermission() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialogTheme);

        builder.setMessage("برای استفاده از gps ، نرم افزار نیاز به دسترسی دارد.لطفا دسترسی لازم را با فشردن دکمه<<دسترسی دادن>> بدهید.")
                .setCancelable(false)
                .setPositiveButton("دسترسی دادن", (dialog, id) ->
                        {
                            PermissionsManager permissionsManager = new PermissionsManager(this);
                            permissionsManager.requestLocationPermissions(getActivity());
                        }
                ).setNegativeButton("بستن", (dialog, which) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();

        TextView textView = alert.findViewById(android.R.id.message);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "iransans.ttf");
        textView.setTypeface(face);
        textView.setTextColor(getActivity().getResources().getColor(R.color.medium_color));
        textView.setTextSize(13);
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
                .zoom(15)
                .build();
        mMapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    private static class MapFragmentLocationCallback implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MapFragment> fragmentWeakReference;

        MapFragmentLocationCallback(MapFragment fragment) {
            fragmentWeakReference = new WeakReference<>(fragment);
        }


        @Override
        public void onSuccess(LocationEngineResult result) {
            MapFragment fragment = fragmentWeakReference.get();

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

        }
    }

    private void reverseGeocode(CameraPosition position) {

        if (TextUtils.isEmpty(binding.edtAddress.getText().toString())) {
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


                        latitude = position.target.getLatitude();
                        longitude = position.target.getLongitude();
                        address = result.getPlace() + " " + result.getCity() + " " + result.getAddress() + " " + result.getDistrict() + " ";

                        binding.edtAddress.setText(address);
                        binding.edtAddress.setVisibility(View.VISIBLE);
                        binding.pro.setVisibility(View.GONE);
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onFailure(@NonNull String errorMessage) {
                        binding.edtAddress.setVisibility(View.VISIBLE);
                        binding.pro.setVisibility(View.GONE);
                        binding.edtAddress.setText("PARS ERROR");
                    }
                });


    }

    private void  initSearchOnMap(View view){
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(mLinearLayoutManager);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(binding.recyclerView.getContext(), mLinearLayoutManager.getOrientation());
        binding.recyclerView.addItemDecoration(mDividerItemDecoration);


        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && state == State.MAP_PIN) {
                setState(State.RESULTS);
                return true;
            }
            return false;
        });

        binding.searchView.setIconifiedByDefault(false);
        binding.searchView.setQueryHint("جستجو");
        binding.searchView.setMaxWidth(Integer.MAX_VALUE);


        List<ForwardGeocode> resultsLis = new ArrayList<>();
        mRecyclerAdapter = new SearchViewAdapter(resultsLis);

        binding.recyclerView.setAdapter(mRecyclerAdapter);

        try {
            LinearLayout linearLayout1 = (LinearLayout) binding.searchView.getChildAt(0);
            LinearLayout linearLayout2;
            try {
                linearLayout2 = (LinearLayout) linearLayout1.getChildAt(2);
            } catch (Exception ignore) {
                linearLayout2 = (LinearLayout) linearLayout1.getChildAt(0);
            }

            LinearLayout linearLayout3;
            try {
                linearLayout3 = (LinearLayout) linearLayout2.getChildAt(1);
            } catch (Exception ignore) {
                linearLayout3 = (LinearLayout) linearLayout2.getChildAt(0);
            }


            AutoCompleteTextView autoComplete;
            try {
                autoComplete = (AutoCompleteTextView) linearLayout3.getChildAt(0);
            } catch (Exception ignore) {
                autoComplete = (AutoCompleteTextView) linearLayout3.getChildAt(1);
            }
            autoComplete.setTextSize(12);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                autoComplete.setTextColor(getActivity().getColor(R.color.medium_color));
            }
            Typeface iranSansBold = Typeface.createFromAsset(getActivity().getAssets(), "iransans.ttf");
            autoComplete.setTypeface(iranSansBold);
        } catch (Exception ignore) {}
        binding.searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && state == State.MAP_PIN) {
                circleManager.deleteAll();
                if (!TextUtils.isEmpty(binding.searchView.getQuery())) {
                    setState(State.RESULTS);
                } else {
                    setState(State.MAP);
                }
            }
        });
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    binding.searchView.clearFocus();
                } catch (Exception ignored) {
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {

                try {
                    if (TextUtils.isEmpty(newText)) {
                        circleManager.deleteAll();
                        setState(State.MAP);
                    } else {
                        setState(State.SEARCHING);
                        CedarMaps.getInstance().forwardGeocode(newText, new ForwardGeocodeResultsListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onSuccess(@NonNull List<ForwardGeocode> results) {
                                setState(State.RESULTS);
                                if (results.size() > 0 && newText.equals(binding.searchView.getQuery().toString())) {
                                    resultsLis.clear();
                                    resultsLis.addAll(results);
                                    mRecyclerAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull String errorMessage) {
                                setState(State.RESULTS);
                                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (Exception ignored) {
                }


                return false;
            }
        });
        mRecyclerAdapter.setOnClickItemListener(this::showItemOnMap);
    }
    private void setState(State state) {

        this.state = state;
        switch (state) {
            case MAP:

            case MAP_PIN:
                binding.cardAddress.setVisibility(View.VISIBLE);
                binding.floating.setVisibility(View.VISIBLE);
                binding.searchResultsLinearLayout.setVisibility(View.GONE);
                break;
            case SEARCHING:

                binding.cardAddress.setVisibility(View.GONE);
                binding.floating.setVisibility(View.GONE);
                binding.searchResultsLinearLayout.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.INVISIBLE);
                binding.searchProgressBar.setVisibility(View.VISIBLE);
                break;
            case RESULTS:
                binding.cardAddress.setVisibility(View.GONE);
                binding.floating.setVisibility(View.GONE);
                binding.searchResultsLinearLayout.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.searchProgressBar.setVisibility(View.GONE);
                break;
        }
    }

    public void showItemOnMap(final ForwardGeocode item) {
        setState(State.MAP_PIN);
        if (getActivity() == null || item.getLocation().getCenter() == null) {
            return;
        }

        circleManager.deleteAll();
        binding.searchView.clearFocus();

        int color = ContextCompat.getColor(getActivity(), R.color.red);
        int strokeColor = ContextCompat.getColor(getActivity(), R.color.green);
        CircleOptions circleOptions = new CircleOptions()
                .withLatLng(item.getLocation().getCenter())
                .withCircleColor(ColorUtils.colorToRgbaString(color))
                .withCircleStrokeWidth(4f)
                .withCircleStrokeColor(ColorUtils.colorToRgbaString(strokeColor))
                .withCircleBlur(0.5f)
                .withCircleRadius(12f);
        circleManager.create(circleOptions);

        circleManager.addClickListener(circle -> {
            if (!TextUtils.isEmpty(item.getAddress())) {
                Toast.makeText(getContext(), item.getAddress(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), item.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        if (item.getLocation().getCenter() != null) {
            mMapboxMap.easeCamera(CameraUpdateFactory.newLatLng(item.getLocation().getCenter()), 1000);
        }
    }
    //endregion Method


}