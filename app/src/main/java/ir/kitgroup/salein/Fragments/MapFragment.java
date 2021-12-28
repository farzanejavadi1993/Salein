package ir.kitgroup.salein.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;

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
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.salein.Activities.LauncherActivity;
import ir.kitgroup.salein.Adapters.SearchViewAdapter;
import ir.kitgroup.salein.Connect.API;
import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.classes.ConfigRetrofit;

import ir.kitgroup.salein.classes.ServerConfig;
import ir.kitgroup.salein.classes.Util;
import ir.kitgroup.salein.classes.CustomProgress;
import ir.kitgroup.salein.databinding.FragmentMapBinding;
import ir.kitgroup.salein.DataBase.Company;
import ir.kitgroup.salein.models.ModelLog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;
import static android.os.Looper.getMainLooper;

@AndroidEntryPoint

public class MapFragment extends Fragment implements PermissionsListener {


    //region Parameter
    @Inject
    SharedPreferences sharedPreferences;

    private CompositeDisposable compositeDisposable;


    private Company company;
    private API api;


    private FragmentMapBinding binding;
    private CustomProgress customProgress;


    private Boolean setADR1 = false;// If setADR1  Is True Set Address1 In TvAddress(PaymentFragment)
    private Boolean ChooseAddress = false;


    private Dialog dialogEditAddress;


    private String address = "";

    private double longitude = 0.0;
    private double latitude = 0.0;
    private String ADDRESS = "";

    private final int LOCATION_PERMISSION_REQUEST_CODE = 99;

    private LocationManager locationManager;

    private MapboxMap mMapboxMap;


    private final MapFragmentLocationCallback callback = new MapFragmentLocationCallback(this);


    private SearchViewAdapter mRecyclerAdapter;
    private State state = State.MAP;
    private CircleManager circleManager;


    private String edit_address = "";
    private String userName = "";
    private String pasWord = "";
    private String numberPos = "";
    private String IP1 = "";
    private String IP2 = "";


    private enum State {
        MAP,
        MAP_PIN,
        SEARCHING,
        RESULTS
    }

    private ServerConfig serverConfig;

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


        compositeDisposable = new CompositeDisposable();


        customProgress = CustomProgress.getInstance();
        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);


        ((LauncherActivity) getActivity()).getVisibilityBottomBar(false);


        //region Configuration Text Size
        int fontSize;
        if (Util.screenSize >= 7) {

            fontSize = 14;
        } else
            fontSize = 12;
        binding.edtAddress.setTextSize(fontSize);

        binding.btnRegisterInformation.setTextSize(fontSize);

        //endregion Configuration Text Size


        //region Get Bundle
        Bundle bundle = getArguments();
        String mobileNumber = bundle.getString("mobileNumber");
        String type = bundle.getString("type");
        edit_address = bundle.getString("edit_address");
        IP1 = bundle.getString("IP1");
        IP2 = bundle.getString("IP2");
        userName = bundle.getString("userName");
        pasWord = bundle.getString("passWord");
        numberPos = bundle.getString("numberPos");


        company = null;
        api = null;
        company = Select.from(Company.class).first();

        if (IP1!=null) {
            binding.btnRegisterInformation.setText("ثبت موقعیت و ارسال اطلاعات");
            binding.btnRegisterInformation.setVisibility(View.GONE);
        }

        if ((company != null && company.mode == 1) || IP1 != null) {


            new AsyncTask() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    api = ConfigRetrofit.getRetrofit("http://" + serverConfig.URL1 + "/api/REST/", true,30).create(API.class);
                   binding.btnRegisterInformation.setVisibility(View.VISIBLE);

                }

                @Override
                protected Object doInBackground(Object[] params) {
                    if (company == null) {
                        serverConfig = new ServerConfig(IP1, IP2);

                    } else {
                        serverConfig = new ServerConfig(company.IP1, company.IP2);
                    }


                    return 0;
                }
            }.execute(0);

        } else if (IP1 == null) {
            api = ConfigRetrofit.getRetrofit("http://" + company.IP1 + "/api/REST/", false,30).create(API.class);
        }


        //endregion Get Bundle


        //region Cast DialogSetADR
        dialogEditAddress = new Dialog(getActivity());
        dialogEditAddress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogEditAddress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogEditAddress.setContentView(R.layout.dialog_register_address);
        dialogEditAddress.setCancelable(true);

        MaterialButton btnRegisterAddress = dialogEditAddress.findViewById(R.id.btn_register_addressMap);
        LinearLayout linearButtons = dialogEditAddress.findViewById(R.id.linearButtons);
        MaterialButton address1 = dialogEditAddress.findViewById(R.id.btn_1);
        MaterialButton address2 = dialogEditAddress.findViewById(R.id.btn_2);
        EditText edtAddress = dialogEditAddress.findViewById(R.id.edt_address);
        EditText edtUnit = dialogEditAddress.findViewById(R.id.edt_unit);
        EditText edtPlaque = dialogEditAddress.findViewById(R.id.edt_plaque);
        address1.setOnClickListener(v -> {
            ChooseAddress = true;
            setADR1 = false;
            address1.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.green_table)));
            address1.setTextColor(getActivity().getResources().getColor(R.color.green_table));
            address2.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.purple_700)));
            address2.setTextColor(getActivity().getResources().getColor(R.color.purple_700));

        });
        address2.setOnClickListener(v -> {
            ChooseAddress = true;
            setADR1 = true;
            address2.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.green_table)));
            address2.setTextColor(getActivity().getResources().getColor(R.color.green_table));
            address1.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.purple_700)));
            address1.setTextColor(getActivity().getResources().getColor(R.color.purple_700));
        });


        btnRegisterAddress.setOnClickListener(v -> {
            Account accountORG = Select.from(Account.class).first();


            if (edtAddress.getText().toString().equals("") ||
                    edtUnit.getText().toString().equals("") ||
                    edtPlaque.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "تمام فبلد ها را پر کنید.", Toast.LENGTH_SHORT).show();
                return;
            } else if (edit_address.equals("1") && accountORG != null && accountORG.ADR != null && accountORG.ADR2 != null && !accountORG.ADR.equals("") && !accountORG.ADR2.equals("") && !ChooseAddress) {
                Toast.makeText(getActivity(), "لطفا مشخص کنید، کدام آدرس را ویرایش میکنید؟", Toast.LENGTH_SHORT).show();
                return;
            }


            dialogEditAddress.dismiss();
            ADDRESS = edtAddress.getText().toString() + " واحد " + edtUnit.getText().toString() + " پلاک  " + edtPlaque.getText().toString();


            //region Edit Address When Going From PaymentFragment
            if (edit_address != null && edit_address.equals("1")) {
                Account account = new Account();
                account.I = accountORG.I;
                account.N = accountORG.N;
                account.M = accountORG.M;
                account.ADR = accountORG.ADR;
                account.ADR2 = accountORG.ADR2;
                account.CRDT = accountORG.CRDT;


                if (account.ADR == null) {
                    setADR1 = false;
                    account.ADR = ADDRESS;
                    account.LAT = String.valueOf(latitude);
                    account.LNG = String.valueOf(longitude);

                } else if (account.ADR2 == null) {
                    setADR1 = true;
                    account.ADR2 = ADDRESS;
                    account.LAT1 = String.valueOf(latitude);
                    account.LNG1 = String.valueOf(longitude);

                } else {
                    if (setADR1) {
                        account.ADR2 = ADDRESS;
                        account.LAT1 = String.valueOf(latitude);
                        account.LNG1 = String.valueOf(longitude);
                    } else {
                        account.ADR = ADDRESS;
                        account.LAT = String.valueOf(latitude);
                        account.LNG = String.valueOf(longitude);
                    }
                }


                ArrayList<Account> list = new ArrayList<>();
                list.add(account);

                UpdateAccount(list, 1, "");


            }
            //endregion Edit Address When Going From PaymentFragment


            //region Edit Address When Going From ProfileFragment

            else if (edit_address != null && edit_address.equals("2")) {
                Account account = new Account();
                account.I = accountORG.I;
                account.N = accountORG.N;
                account.M = accountORG.M;
                account.CRDT = accountORG.CRDT;

                if (type.equals("1")) {
                    account.ADR2 = accountORG.ADR2;
                    account.ADR = ADDRESS;
                    account.LAT = String.valueOf(latitude);
                    account.LNG = String.valueOf(longitude);

                } else {
                    account.ADR = accountORG.ADR;
                    account.ADR2 = ADDRESS;
                    account.LAT1 = String.valueOf(latitude);
                    account.LNG1 = String.valueOf(longitude);

                }


                ArrayList<Account> list = new ArrayList<>();
                list.add(account);
                UpdateAccount(list, 2, type);
            }


            //endregion Edit Address When Going From ProfileFragment


            //region Edit Address When Going From MainOrderFragment

            else if (edit_address != null && edit_address.equals("3")) {
                Account account = new Account();
                account.I = accountORG.I;
                account.N = accountORG.N;
                account.M = accountORG.M;
                account.ADR = accountORG.ADR;
                account.ADR2 = accountORG.ADR2;
                account.CRDT = accountORG.CRDT;

                if (account.ADR == null) {
                    setADR1 = false;
                    account.ADR =ADDRESS;
                    account.LAT = String.valueOf(latitude);
                    account.LNG = String.valueOf(longitude);
                } else if (account.ADR2 == null) {
                    setADR1 = true;
                    account.ADR2 =  ADDRESS ;
                    account.LAT1 = String.valueOf(latitude);
                    account.LNG1 = String.valueOf(longitude);
                } else {

                    if (setADR1) {
                        account.ADR2 =  ADDRESS ;
                        account.LAT1 = String.valueOf(latitude);
                        account.LNG1 = String.valueOf(longitude);

                    }
                    else
                    {
                        account.ADR =ADDRESS ;
                        account.LAT = String.valueOf(latitude);
                        account.LNG = String.valueOf(longitude);
                    }
                }


                ArrayList<Account> list = new ArrayList<>();
                list.add(account);
                UpdateAccount(list, 3, "");
            }


            //endregion Edit Address When Going From MainOrderFragment


        });
        //endregion Cast DialogSetADR


        //region Action btnRegisterInformation

        binding.btnRegisterInformation.setOnClickListener(v -> {
            Account accountORG = Select.from(Account.class).first();

            if (edit_address != null && (edit_address.equals("10"))) {


                Login(userName, pasWord, numberPos, String.valueOf(latitude), String.valueOf(longitude));
                return;
            }


            if (edit_address != null && (edit_address.equals("1") ||
                    edit_address.equals("2") ||
                    edit_address.equals("3"))) {

                if (accountORG.ADR != null && accountORG.ADR2 != null && !accountORG.ADR.equals("") && !accountORG.ADR2.equals("")) {
                    linearButtons.setVisibility(View.VISIBLE);
                } else
                    linearButtons.setVisibility(View.GONE);


                if (edit_address.equals("2"))
                    linearButtons.setVisibility(View.GONE);


                dialogEditAddress.show();
                return;
            }


            //region Region Gps Information And Go To RegisterFragment


            Bundle bundleRegister = new Bundle();
            bundleRegister.putString("address2", ADDRESS);
            bundleRegister.putDouble("lat", latitude);
            bundleRegister.putDouble("lng", longitude);
            bundleRegister.putString("mobileNumber", mobileNumber);

            RegisterFragment registerFragment = new RegisterFragment();
            registerFragment.setArguments(bundleRegister);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, registerFragment, "RegisterFragment").addToBackStack("RegisterF").commit();


            //endregion Region Gps Information And Go To RegisterFragment
        });
        //endregion Action btnRegisterInformation


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

            binding.floating.setOnClickListener(v -> setupCurrentLocationButton());

        });


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
        } catch (Exception ignore) {

        }


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
                binding.searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {

                if (TextUtils.isEmpty(newText)) {


                    circleManager.deleteAll();
                    setState(State.MAP);
                } else {
                    setState(State.SEARCHING);
                    CedarMaps.getInstance().forwardGeocode(newText, new ForwardGeocodeResultsListener() {
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

                return false;
            }
        });


        mRecyclerAdapter.setOnClickItemListener(this::showItemOnMap);

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
            PermissionsManager permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

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

                        // addMarkerToMapViewAtPosition(new LatLng(position.target.getLatitude(), position.target.getLongitude()));
                        latitude = position.target.getLatitude();
                        longitude = position.target.getLongitude();
                        binding.edtAddress.setVisibility(View.VISIBLE);
                        binding.pro.setVisibility(View.GONE);


                        address = "";
                        address = result.getPlace() + " " + result.getCity() + " " + result.getAddress() + " " + result.getDistrict() + " ";
                        binding.edtAddress.setText(address);
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
        compositeDisposable.clear();

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

        compositeDisposable.dispose();
        binding = null;
        if (edit_address.equals("3"))
            ((LauncherActivity) getActivity()).getVisibilityBottomBar(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();

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

        int color = ContextCompat.getColor(getActivity(), R.color.red_table);
        int strokeColor = ContextCompat.getColor(getActivity(), R.color.green_table);
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

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {

    }


    private static class JsonObjectAccount {

        public List<Account> Account;

    }

    private void UpdateAccount(List<Account> accounts, int flag, String locationAddress) {


        try {
            customProgress.showProgress(getContext(), "در حال ویرایش اطلاعات", false);
            JsonObjectAccount jsonObjectAcc = new JsonObjectAccount();
            jsonObjectAcc.Account = accounts;


            Gson gson = new Gson();
            Type typeJsonObject = new TypeToken<JsonObjectAccount>() {
            }.getType();

            Call<String> call = api.UpdateAccount(company.USER, company.PASS, gson.toJson(jsonObjectAcc, typeJsonObject), "");
            binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
            binding.btnRegisterInformation.setEnabled(false);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {

                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelLog>() {
                    }.getType();
                    ModelLog iDs = gson.fromJson(response.body(), typeIDs);

                    if (iDs != null) {
                        int message = iDs.getLogs().get(0).getMessage();
                        String description = iDs.getLogs().get(0).getDescription();

                        Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                        if (message == 1) {
                            if (customProgress.isShow)
                                customProgress.hideProgress();
                            customProgress.hideProgress();
                            Account.deleteAll(Account.class);
                            Account.saveInTx(accounts);


                            getActivity().getSupportFragmentManager().popBackStack();
                            Fragment frg;


                            if (flag == 1) {
                                frg = getActivity().getSupportFragmentManager().findFragmentByTag("PaymentMobileFragment");

                                if (frg instanceof PaymentMobileFragment) {
                                    PaymentMobileFragment fgf = (PaymentMobileFragment) frg;
                                    Bundle bundle = fgf.getBundle(setADR1);
                                    frg.setArguments(bundle);
                                }

                            } else if (flag == 2) {

                                Bundle bundleProfile = new Bundle();
                                bundleProfile.putString("address", ADDRESS);
                                bundleProfile.putString("type", locationAddress);

                                frg = getActivity().getSupportFragmentManager().findFragmentByTag("ProfileFragment");
                                frg.setArguments(bundleProfile);
                            } else {

                                frg = getActivity().getSupportFragmentManager().findFragmentByTag("MainOrderFragment");

                                if (frg instanceof MainOrderFragment) {
                                    MainOrderFragment fgf = (MainOrderFragment) frg;
                                    Bundle bundle = fgf.getBundle(setADR1);
                                    frg.setArguments(bundle);
                                }

                            }


                            FragmentManager ft = getActivity().getSupportFragmentManager();
                            if (frg != null) {


                                try {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                                        ft.beginTransaction().detach(frg).commitNow();
                                        ft.beginTransaction().attach(frg).commitNow();

                                    } else {
                                        ft.beginTransaction().detach(frg).attach(frg).commit();
                                    }
                                } catch (Exception ignored) {

                                }

                            }

                        }

                    } else {
                        Toast.makeText(getActivity(), "خطا در دریافت مدل", Toast.LENGTH_SHORT).show();
                    }


                    binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
                    binding.btnRegisterInformation.setEnabled(true);
                    customProgress.hideProgress();
                }


                @Override
                public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                    Toast.makeText(getContext(), "خطای تایم اوت در ثبت مشتری" + t.toString(), Toast.LENGTH_SHORT).show();

                    binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
                    binding.btnRegisterInformation.setEnabled(true);
                    customProgress.hideProgress();


                }
            });


        } catch (NetworkOnMainThreadException ex) {

            Toast.makeText(getContext(), "خطا در ثبت مشتری" + ex.toString(), Toast.LENGTH_SHORT).show();
            binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.btnRegisterInformation.setEnabled(true);
            customProgress.hideProgress();

        }


    }


    private void Login(String userName, String passWord, String saleCode, String lat, String lng) {

        binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
        binding.btnRegisterInformation.setEnabled(false);

        if (api==null) {
            Toast.makeText(getActivity(), "لظفا تا دریافت آی پی منتظر بمانبد...", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            compositeDisposable.add(
                    api.Login(userName, passWord)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
                                binding.btnRegisterInformation.setEnabled(true);

                                if (jsonElement == null || !jsonElement.isEmpty()) {

                                    Toast.makeText(getActivity(), "نام کاربری یا رمز عبور اشتباه است.", Toast.LENGTH_SHORT).show();
                                } else {


                                    Company.deleteAll(Company.class);
                                    Company company = new Company();
                                    company.IP1 = IP1;
                                    company.IP2 = IP2;
                                    company.numberPos = saleCode;
                                    company.USER = userName;
                                    company.PASS = passWord;
                                    company.mode = 1;
                                    company.INSK_ID = "ir.kitgroup.saleinOrder";
                                    company.LAT = lat;
                                    company.LONG = lng;
                                    company.save();


                                    getActivity().getSupportFragmentManager().popBackStack();

                                    if (Select.from(Company.class).list().size() > 0) {
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_launcher, new LauncherOrganizationFragment(), "LauncherFragment").commit();
                                    }



                                }
                                customProgress.hideProgress();


                            }, throwable -> {
                                binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
                                binding.btnRegisterInformation.setEnabled(true);

                                Toast.makeText(getContext(), "آی پی سازمان اشتباه است", Toast.LENGTH_SHORT).show();
                                customProgress.hideProgress();


                            })
            );


        } catch (NetworkOnMainThreadException ex) {
            binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.btnRegisterInformation.setEnabled(true);
            customProgress.hideProgress();
            Toast.makeText(getContext(), "خطا در دریافت اطلاعات" + ex.toString(), Toast.LENGTH_SHORT).show();
        }


    }


}
