package ir.kitgroup.salein1.Fragments.Client.Register;

import android.Manifest;
import android.app.AlertDialog;

import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.LocationManager;

import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;



import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


import java.util.Objects;
import java.util.UUID;

import ir.kitgroup.salein1.Classes.App;
import ir.kitgroup.salein1.DataBase.Account;

import ir.kitgroup.salein1.DataBase.User;
import ir.kitgroup.salein1.Fragments.MobileView.MainOrderMobileFragment;

import ir.kitgroup.salein1.Models.ModelLog;
import ir.kitgroup.salein1.R;
import ir.kitgroup.salein1.Util.Util;
import ir.kitgroup.salein1.databinding.FragmentRegisterBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;

public class RegisterFragment extends Fragment implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {
    //region  Parameter
    private FragmentRegisterBinding binding;
    private Boolean name = false;
    private final Boolean phone = true;
    private Boolean address = false;
    private Boolean plaque = false;
    private final List<Account> accountsList = new ArrayList<>();
    private User user;
    private int gender = 0;
    private GoogleMap mGoogleMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 99;
    private Boolean getGps = false;
    private  LocationManager locationManager;
    private Boolean registerGps=false;



    //endregion Parameter
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Bundle bundle = getArguments();
        assert bundle != null;
        String mobile = bundle.getString("mobile");


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
            if (!registerGps){
                binding.accSingleMapOnForm.setVisibility(View.GONE);
                binding.cardInfoCustomer.setVisibility(View.VISIBLE);
                registerGps=true;
                binding.btnRegisterInformation.setEnabled(true);
                binding.btnRegisterInformation.setText("ثبت اطلاعات");
                binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
            }
            else {


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
                account.ADR = binding.edtAddressCustomer.getText().toString() + binding.edtPlaqueCustomer.getText().toString();
                account.S = String.valueOf(gender);
                accountsList.clear();
                accountsList.add(account);
                addAccount(user.userName, user.passWord, accountsList);

            }
        });



        locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        binding.accSingleMapOnForm.onCreate(savedInstanceState);
        binding.accSingleMapOnForm.onResume();
        binding.accSingleMapOnForm.getMapAsync(this);



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

            Call<String> call = App.api.addAccount(userName, pass, gson.toJson(jsonObjectAcc, typeJsonObject),"");
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
                        FragmentTransaction replaceFragment = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, mainOrderMobileFragment,"MainOrderMobileFragment");
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

            getGps=true;



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

            }else if (Objects.requireNonNull(locationManager).getProviders(true).size() < 2) {

                AlertNoGPS();
            } else {

                try {


                    mGoogleMap.setMyLocationEnabled(true);


                    mGoogleMap.setOnMyLocationChangeListener(location2 -> {


                        mGoogleMap.addMarker(new MarkerOptions().position(
                                    new LatLng(location2.getLatitude(), location2.getLongitude())));

                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(location2.getLatitude(), location2.getLongitude()), 15f));

                        binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
                        binding.btnRegisterInformation.setEnabled(true);


                            mGoogleMap.setOnMyLocationChangeListener(null);

                    });
                } catch (Exception ex) {

                    Toast.makeText(getContext(), "نرم افزار به مکان یاب دستگاه دسترسی ندارد", Toast.LENGTH_SHORT).show();
                    Objects.requireNonNull(getActivity()).finish();
                }
            }
        } catch (Exception ex) {

            Toast.makeText(getContext(), "نرم افزار به مکان یاب دستگاه دسترسی ندارد", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onMapReady(final @NotNull GoogleMap googleMap) {




        if (!getGps) {

            mGoogleMap = googleMap;
            enableMyLocation();
        }

        googleMap.setOnMapClickListener(latLng -> {
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(
                    new LatLng(latLng.latitude, latLng.longitude)));

            binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.btnRegisterInformation.setEnabled(true);

        });






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
                    getActivity().finish();
                } else {

                    enableMyLocation();
                }
            }
        }



    }







}
