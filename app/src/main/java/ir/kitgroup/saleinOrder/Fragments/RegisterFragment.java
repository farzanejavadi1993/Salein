package ir.kitgroup.saleinOrder.Fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;



import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.saleinOrder.Connect.API;
import ir.kitgroup.saleinOrder.DataBase.Account;

import ir.kitgroup.saleinOrder.classes.Util;
import ir.kitgroup.saleinOrder.DataBase.Company;
import ir.kitgroup.saleinOrder.models.ModelLog;
import ir.kitgroup.saleinOrder.R;
import ir.kitgroup.saleinOrder.databinding.FragmentRegisterBinding;

@AndroidEntryPoint
public class RegisterFragment extends Fragment {
    //region  Parameter


    @Inject
    Company company;

    @Inject
    API api;





    private  CompositeDisposable compositeDisposable ;
    private FragmentRegisterBinding binding;
    private final List<Account> accountsList = new ArrayList<>();

    private int gender = 0;


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

        compositeDisposable = new CompositeDisposable();
        //region Get Bundle And Set Data

        Bundle bundle=getArguments();
        String mobileNumber = bundle.getString("mobileNumber");
        String address2 = bundle.getString("address2");
        double latitude=bundle.getDouble("lat");
        double longitude= bundle.getDouble("lng");





        binding.edtAddressCustomerComplete.setText(address2);
        binding.edtNumberPhoneCustomer.setText(mobileNumber);
        //endregion Get Bundle And Set Data

        //region Configuration Text Size
        int fontSize;
        if (Util.screenSize >= 7) {

            fontSize = 14;
            binding.textView2.setTextSize(15);
        } else
            fontSize = 12;


        binding.tvNameCustomer.setTextSize(fontSize);
        binding.edtFLNameCustomer.setTextSize(fontSize);
        binding.tvPhoneNumberCustomer.setTextSize(fontSize);
        binding.edtNumberPhoneCustomer.setTextSize(fontSize);
        binding.tvAddressCustomer.setTextSize(fontSize);

        binding.greentv.setTextSize(fontSize);
        binding.edtAddressCustomerComplete.setTextSize(fontSize);
        binding.tvPlaqueCustomer.setTextSize(fontSize);
        binding.edtPlaqueCustomer.setTextSize(fontSize);
        binding.radioMan.setTextSize(fontSize);
        binding.radioWoman.setTextSize(fontSize);
        binding.btnRegisterInformation.setTextSize(fontSize);
        //endregion Configuration Text Size


        //region Action RadioButton
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
        //endregion Action RadioButton


        //region Action btnRegisterInformation
        binding.btnRegisterInformation.setOnClickListener(v -> {


            if (
                    binding.edtFLNameCustomer.getText().toString().isEmpty() ||
                            binding.edtNumberPhoneCustomer.getText().toString().isEmpty() ||
                            binding.edtAddressCustomerComplete.getText().toString().isEmpty() ||
                            binding.edtPlaqueCustomer.getText().toString().isEmpty()
            ) {
                Toast.makeText(getActivity(), "لطفا تمام فیلد ها را پر کنید", Toast.LENGTH_SHORT).show();
                return;
            }

            Account account = new Account();
            account.I = UUID.randomUUID().toString();
            account.N = binding.edtFLNameCustomer.getText().toString();
            account.M = binding.edtNumberPhoneCustomer.getText().toString();
            account.PSW = binding.edtNumberPhoneCustomer.getText().toString();
            account.ADR = longitude + "longitude" + binding.edtAddressCustomerComplete.getText().toString() + " پلاک " + binding.edtPlaqueCustomer.getText().toString() + "latitude" + latitude;
            account.S = String.valueOf(gender);
            accountsList.clear();
            accountsList.add(account);
            addAccount(company.USER, company.PASS, accountsList);

        });
        //endregion Action btnRegisterInformation


    }


    //region Method
    private static class JsonObjectAccount {
        public List<Account> Account;
    }


    private void addAccount(String userName, String pass, List<Account> accounts) {

        if (!isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), "خطا در اتصال به اینترنت", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
            binding.btnRegisterInformation.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);

            JsonObjectAccount jsonObjectAcc = new JsonObjectAccount();
            jsonObjectAcc.Account = accounts;


            Gson gson = new Gson();
            Type typeJsonObject = new TypeToken<JsonObjectAccount>() {
            }.getType();

            compositeDisposable.add(
                    api.addAccount(userName, pass, gson.toJson(jsonObjectAcc, typeJsonObject), "")
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {

                                Gson gson1 = new Gson();
                                Type typeIDs = new TypeToken<ModelLog>() {
                                }.getType();
                                ModelLog iDs = gson1.fromJson(jsonElement, typeIDs);

                                assert iDs != null;
                                int message = iDs.getLogs().get(0).getMessage();
                                String description = iDs.getLogs().get(0).getDescription();
                                Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                                if (message == 1) {

                                    Account.deleteAll(Account.class);
                                    Account.saveInTx(accountsList);
                                    accountsList.clear();

                                    getActivity().getSupportFragmentManager().popBackStack();
                                    //region Show All Company

                                    if (company.INSK_ID.equals("ir.kitgroup.salein")) {
                                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new StoriesFragment(), "StoriesFragment").commit();
                                    }
                                    //endregion Show All Company


                                    //region Go To MainOrderFragment Because Account Is Register
                                    else {
                                        Bundle bundleMainOrder= new Bundle();
                                        bundleMainOrder.putString("Inv_GUID", "");
                                        bundleMainOrder.putString("Tbl_GUID", "");
                                        bundleMainOrder.putString("Ord_TYPE", "");

                                        MainOrderFragment mainOrderFragment=new MainOrderFragment();
                                        mainOrderFragment.setArguments(bundleMainOrder);
                                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mainOrderFragment, "MainOrderFragment").commit();

                                    }
                                    //endregion Go To MainOrderFragment Because Account Is Register

                                }
                                binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
                                binding.btnRegisterInformation.setEnabled(true);
                                binding.progressBar.setVisibility(View.GONE);

                            }, throwable -> {
                                Toast.makeText(getContext(), "خطای تایم اوت در ثبت مشتری", Toast.LENGTH_SHORT).show();

                                binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
                                binding.btnRegisterInformation.setEnabled(true);
                                binding.progressBar.setVisibility(View.GONE);

                            })
            );
        } catch (Exception e) {

            Toast.makeText(getContext(), "خطا در ثبت مشتری", Toast.LENGTH_SHORT).show();
            binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.btnRegisterInformation.setEnabled(true);
            binding.progressBar.setVisibility(View.GONE);
        }


    }


    //endregion Method


    private boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
