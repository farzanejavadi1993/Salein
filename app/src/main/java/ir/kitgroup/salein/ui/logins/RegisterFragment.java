package ir.kitgroup.salein.ui.logins;

import static android.os.Looper.getMainLooper;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
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

import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;

import ir.kitgroup.salein.Connect.CompanyViewModel;

import ir.kitgroup.salein.Connect.MainViewModel;
import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.DataBase.Salein;

import ir.kitgroup.salein.classes.Util;
import ir.kitgroup.salein.databinding.RegisterFragmentBinding;
import ir.kitgroup.salein.DataBase.Company;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.models.AppDetail;
import ir.kitgroup.salein.models.Setting;
import ir.kitgroup.salein.ui.payment.PaymentFragment;


@AndroidEntryPoint
public class RegisterFragment extends Fragment implements PermissionsListener {

    //region Variable
    private CompanyViewModel companyViewModel;
    private MainViewModel mainViewModel;

    private RegisterFragmentBinding binding;

    private String userName;
    private String passWord;
    private List<Account> accountsList;

    private Account account;
    private int radioValue;
    private boolean ACCSTP = true;
    private String from;
    private String mobile;

    private List<Account> accounts;

    private MapboxMap mapboxMap;
    private LocationEngine locationEngine = null;
    private final MapFragmentLocationCallback callback = new MapFragmentLocationCallback(this);
    private PermissionsManager permissionsManager;
    private String applicationVersion="";
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

        initMap(savedInstanceState);
        getBundle();
        try {
            appVersion();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        setDataBundle();
        init();
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        companyViewModel = new ViewModelProvider(this).get(CompanyViewModel.class);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);


        companyViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {


            if (result == null)
                return;

            binding.btnRegisterInformation.setBackgroundResource(R.drawable.bottom_background);
            binding.btnRegisterInformation.setEnabled(true);
            Toasty.warning(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
        });

        companyViewModel.getResultAddAccount().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;
            companyViewModel.getResultAddAccount().setValue(null);

            if (result) {
                Account.deleteAll(Account.class);
                Account.saveInTx(accountsList);
                accountsList.clear();
                mainViewModel.getCustomerFromServer(mobile);
            }
        });

        companyViewModel.getResultSetting().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            companyViewModel.getResultSetting().setValue(null);
            List<Setting> settingsList = new ArrayList<>(result);
            if (settingsList.size() > 0) {
                String accStp = settingsList.get(0).ACC_STATUS_APP;
                ACCSTP = !accStp.equals("0");
                accountsList.get(0).STAPP = ACCSTP;
                accountsList.get(0).setVersion(applicationVersion);
                binding.btnRegisterInformation.setBackgroundResource(R.drawable.inactive_bottom);
                binding.btnRegisterInformation.setEnabled(false);
                companyViewModel.addAccount(userName, passWord, accountsList);
            }


        });

        companyViewModel.getResultUpdateAccount().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;

            binding.btnRegisterInformation.setBackgroundResource(R.drawable.bottom_background);
            binding.btnRegisterInformation.setEnabled(true);

            companyViewModel.getResultUpdateAccount().setValue(null);
            if (result != null) {
                int message = result.getLogs().get(0).getMessage();
                String description = result.getLogs().get(0).getDescription();

                Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                if (message == 1) {
                    Account.deleteAll(Account.class);
                    Account.saveInTx(accounts);
                    Util.longitude = 0;
                    Util.latitude = 0;
                    Util.address = "";
                    Util.nameUser = "";
                    Navigation.findNavController(binding.getRoot()).popBackStack();

                } else {
                    Toasty.error(getActivity(), "خطا در بروز رسانی اطلاعات کاربر", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toasty.error(getActivity(), "خطا در دریافت مدل", Toast.LENGTH_SHORT).show();
            }

        });

        mainViewModel.getResultCustomerFromServer().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;

            mainViewModel.getResultCustomerFromServer().setValue(null);

            if (result.size() > 0) {
                String IMEI = Util.getAndroidID(getActivity());
                List<AppDetail> Apps = result.get(0).getApps();
                CollectionUtils.filter(Apps, l -> l.getAppId().equals(Util.APPLICATION_ID) && l.getIemi().equals(IMEI));

                if (Apps.size() > 0)
                    navigate();
                else
                    addCustomerToSerVer();

            } else {
                addCustomerToSerVer();
            }

        });

        mainViewModel.getResultAddAccountToServer().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;
            mainViewModel.getResultAddAccountToServer().setValue(null);
            navigate();
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
                // toggleCurrentLocationButton();
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

    private Company getCompany() {
        return Select.from(Company.class).first();
    }

    private void initMap(Bundle savedInstanceState) {
        try {
            binding.mapView.onCreate(savedInstanceState);
        } catch (Exception ignore) {
        }


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
                                //  toggleCurrentLocationButton();

                                if (Util.latitude != 0 && Util.longitude != 0)
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
                //Navigation.findNavController(binding.getRoot()).popBackStack();
                NavDirections action = RegisterFragmentDirections.actionGoToMapFragment("RegisterFragment");
                Navigation.findNavController(binding.getRoot()).navigate(action);
                return false;
            });

        });
    }

    private void getBundle() {
        from = RegisterFragmentArgs.fromBundle(getArguments()).getFrom();
        mobile = RegisterFragmentArgs.fromBundle(getArguments()).getMobile();
        radioValue = RegisterFragmentArgs.fromBundle(getArguments()).getRadioValue();
    }

    private void setDataBundle() {
        if (from.equals("PaymentFragment")) {
            binding.tvRadioGroup.setText("انتخاب به عنوان");
            binding.radio1.setText("آدرس 1");
            binding.radio2.setText("آدرس 2");
            binding.CodeId.setVisibility(View.GONE);
            binding.nameCard.setVisibility(View.GONE);
        }
        if (from.equals("ProfileFragment")) {
            binding.edtName.setEnabled(false);
            binding.CodeId.setVisibility(View.GONE);
            binding.radioCard.setVisibility(View.GONE);
        }
    }

    private Account getAccount() {
        return Select.from(Account.class).first();
    }

    private void init() {
        accountsList = new ArrayList<>();
        account = getAccount();

        accounts = new ArrayList<>();
        binding.edtAddress.setText(Util.address);
        binding.edtName.setText(Util.nameUser);

        if (account != null)
            binding.edtName.setText(account.getN());


        userName = getCompany().getUser();
        passWord = getCompany().getPass();


        binding.radio1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                radioValue = 1;// Choose As Woman gender Or Choose As Address1
            }
        });


        binding.radio2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (from.equals("PaymentFragment"))
                    radioValue = 2;//Choose As Address2
                else
                    radioValue = 0;//Choose As Man Gender
            }
        });


        binding.btnRegisterInformation.setOnClickListener(v -> {

            if (binding.edtName.getText().toString().isEmpty()
                    ||
                    binding.edtAddress.getText().toString().isEmpty()
                    ||
                    radioValue == -1
            ) {

                Toasty.error(requireActivity(), "لطفا فیلدهای ستاره دار را پر کنید.", Toast.LENGTH_SHORT, true).show();
                return;
            }
            Account acc = new Account();

            //region Add Account
            if (from.equals("VerifyFragment")) {
                acc.setI(UUID.randomUUID().toString());
                acc.setN(binding.edtName.getText().toString());
                acc.setM(mobile);
                acc.PSW = mobile;
                acc.setAdr(binding.edtAddress.getText().toString());
                acc.LAT = String.valueOf(Util.latitude);
                acc.LNG = String.valueOf(Util.longitude);
                acc.S = String.valueOf(radioValue);
                acc.PC = binding.edtCode.getText().toString();
                acc.STAPP = ACCSTP;
                accountsList.clear();
                accountsList.add(acc);
                companyViewModel.getSetting(userName, passWord);
            }
            //endregion Add Account


            //region Update Account
            else {
                acc.setI(account.getI());
                acc.setN(account.getN());
                acc.setM(account.getM());
                acc.setAdr(account.getAdr());
                acc.setAdr2(account.getAdr2());
                acc.CRDT = account.CRDT > 0 ? account.CRDT : 0.0;
                acc.LAT = account.LAT != null && !account.LAT.equals("") && !account.LAT.equals("-") ? account.LAT : "0.0";
                acc.LNG = account.LNG != null && !account.LNG.equals("") && !account.LNG.equals("-") ? account.LNG : "0.0";
                acc.LAT1 = account.LAT1 != null && !account.LAT1.equals("") && !account.LAT1.equals("-") ? account.LAT1 : "0.0";
                acc.LNG1 = account.LNG1 != null && !account.LNG1.equals("") && !account.LNG1.equals("-") ? account.LNG1 : "0.0";

                if (radioValue == 1) {
                    if (from.equals("PaymentFragment"))
                        PaymentFragment.ChooseAddress2 = false;

                    acc.setAdr(binding.edtAddress.getText().toString());
                    acc.LAT = String.valueOf(Util.latitude);
                    acc.LNG = String.valueOf(Util.longitude);
                } else if (radioValue == 2) {
                    if (from.equals("PaymentFragment"))
                        PaymentFragment.ChooseAddress2 = true;

                    acc.setAdr2(binding.edtAddress.getText().toString());
                    acc.LAT1 = String.valueOf(Util.latitude);
                    acc.LNG1 = String.valueOf(Util.longitude);
                }


                accounts.clear();
                accounts.add(acc);

                binding.btnRegisterInformation.setBackgroundResource(R.drawable.inactive_bottom);
                binding.btnRegisterInformation.setEnabled(false);


                companyViewModel.updateAccount(userName, passWord, accounts);


            }
            //endregion Update Account


        });


        binding.ivBack.setOnClickListener(v -> Navigation.findNavController(binding.getRoot()).popBackStack());
    }


    private void addCustomerToSerVer() {
        Account account = Select.from(Account.class).first();

        if (account != null) {
            account.setImei(Util.getAndroidID(getActivity()));
            account.setAppId(Util.APPLICATION_ID);
            Util.JsonObjectAccount jsonObjectAcc = new Util.JsonObjectAccount();
            ArrayList<Account> accounts = new ArrayList<>();
            accounts.add(account);
            jsonObjectAcc.Account = accounts;
            mainViewModel.addAccountToServer(jsonObjectAcc);
        }

    }

    private void navigate() {
        binding.btnRegisterInformation.setBackgroundResource(R.drawable.bottom_background);
        binding.btnRegisterInformation.setEnabled(true);
        if (Select.from(Salein.class).first().getSalein() ){
            NavDirections action = RegisterFragmentDirections.actionGoToCompanyFragment();
            Navigation.findNavController(binding.getRoot()).navigate(action);
        } else {
            Util.address = "";
            Util.latitude = 0;
            Util.longitude = 0;
            Util.nameUser = "";
            NavDirections action = VerifyFragmentDirections.actionGoToHomeFragment("");
            Navigation.findNavController(binding.getRoot()).navigate(action);
        }

    }


    public void appVersion() throws PackageManager.NameNotFoundException {
        applicationVersion= getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
    }
    //endregion Method


}