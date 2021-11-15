package ir.kitgroup.salein.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.DecimalFormat;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.salein.Connect.API;
import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.DataBase.InvoiceDetail;
import ir.kitgroup.salein.DataBase.Product;
import ir.kitgroup.salein.DataBase.Tables;
import ir.kitgroup.salein.DataBase.User;

import ir.kitgroup.salein.R;

import ir.kitgroup.salein.classes.ConfigRetrofit;
import ir.kitgroup.salein.classes.Util;
import ir.kitgroup.salein.databinding.FragmentSettingBinding;
import ir.kitgroup.salein.models.Company;
import ir.kitgroup.salein.models.ModelAccount;
import ir.kitgroup.salein.models.ModelLog;


@AndroidEntryPoint
public class SettingFragment extends Fragment {


    @Inject
    Company company;

    @Inject
    API api;

    @Inject
    SharedPreferences sharedPreferences;


    private NavController navController;

    //region Parameter
    private boolean Seen = true;
    private FragmentSettingBinding binding;
    private final DecimalFormat format = new DecimalFormat("#,###,###,###");
    //endregion Parameter


    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private int fontSize = 12;

    private String mobile;


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {


        binding = FragmentSettingBinding.inflate(getLayoutInflater());
        if (Util.screenSize >= 7)
            fontSize = 14;
        else
            fontSize = 12;


        return binding.getRoot();


    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(binding.getRoot());

        if (!Util.RetrofitValue) {
            ConfigRetrofit configRetrofit = new ConfigRetrofit();
            String name = sharedPreferences.getString("CN", "");
            company = configRetrofit.getCompany(name);
            api = configRetrofit.getRetrofit(company.baseUrl).create(API.class);

        }


        binding.tvProfile.setTextSize(fontSize);
        binding.tvComment.setTextSize(fontSize);
        binding.tvCredit.setTextSize(fontSize);
        binding.tvOrder.setTextSize(fontSize);
        binding.btnLogOut.setTextSize(fontSize);


        mobile = Select.from(Account.class).first().M;
        binding.btnComment.setOnClickListener(v -> Toast.makeText(getActivity(), "به زودی", Toast.LENGTH_SHORT).show());


        binding.btnCredit.setOnClickListener(v -> {

            if (!company.paymentLink.equals("")) {
                if (Seen) {
                    Seen = false;

                    Uri uri = Uri.parse(company.paymentLink);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    startActivityForResult(intent, 44);
                }


            } else {
                Toast.makeText(getActivity(), "دسترسی به باشگاه امکان پذیر نمی باشد", Toast.LENGTH_SHORT).show();
            }

        });
        binding.btnLogOut.setOnClickListener(v -> {
            if (Account.count(Account.class) > 0)
                Account.deleteAll(Account.class);


            if (InvoiceDetail.count(InvoiceDetail.class) > 0)
                InvoiceDetail.deleteAll(InvoiceDetail.class);


            if (Product.count(Product.class) > 0)
                Product.deleteAll(Product.class);


            if (Tables.count(Tables.class) > 0)
                Tables.deleteAll(Tables.class);

            if (company.mode == 1) {
                if (User.count(User.class) > 0)
                    User.deleteAll(User.class);
            }


            if (Tables.count(Tables.class) > 0)
                Tables.deleteAll(Tables.class);


            sharedPreferences.edit().putBoolean("firstSync", false).apply();
            sharedPreferences.edit().putBoolean("firstSyncSetting", false).apply();


          navController.popBackStack();
          navController.popBackStack();
          //reload loginFragment



        });


        try {
            binding.ivSupport.setColorFilter(getResources().getColor(R.color.color_svg), PorterDuff.Mode.SRC_IN);
        } catch (Exception e) {
        }


        binding.lProfile.setOnClickListener(v ->
                {
                    NavDirections action = SettingFragmentDirections.actionGoToProfileFragment();
                    navController.navigate(action);
                }
        );

        binding.btnSupport.setOnClickListener(v ->


                {
                    NavDirections action = SettingFragmentDirections.actionGoToAboutFragment();
                    navController.navigate(action);
                }



        );

        binding.btnOrderList.setOnClickListener(v ->


                {
                    NavDirections action = SettingFragmentDirections.actionGoToOrderListFragment();
                    navController.navigate(action);
                }


        );

        Account acc = Select.from(Account.class).first();
        if (acc != null && acc.CRDT != null) {
            binding.txtCredit.setText("موجودی : " + format.format(acc.CRDT) + " ریال ");
        } else {
            binding.txtCredit.setText("موجودی : " + "0" + " ریال ");
        }


    }


    @SuppressLint("SetTextI18n")
    private void getInquiryAccount1(String mobile) {

        if (!isNetworkAvailable(getActivity())) {
            binding.txtCredit.setTextColor(getActivity().getResources().getColor(R.color.red_table));
            binding.txtCredit.setText("خطا در اتصال به اینترنت");
            return;
        }

        try {
            compositeDisposable.add(
                    api.getInquiryAccount1(company.userName, company.passWord, mobile, "", "", 1, 1)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                Gson gson = new Gson();
                                Type typeIDs = new TypeToken<ModelAccount>() {
                                }.getType();
                                ModelAccount iDs;
                                try {
                                    iDs = gson.fromJson(jsonElement, typeIDs);
                                } catch (Exception e) {
                                    Toast.makeText(getActivity(), "مدل دریافت شده از مشتریان نامعتبر است.", Toast.LENGTH_SHORT).show();
                                    return;
                                }


                                if (iDs != null)
                                    if (iDs.getAccountList() == null) {
                                        Type typeIDs0 = new TypeToken<ModelLog>() {
                                        }.getType();
                                        ModelLog iDs0 = gson.fromJson(jsonElement, typeIDs0);

                                        if (iDs0.getLogs() != null) {
                                            String description = iDs0.getLogs().get(0).getDescription();
                                            Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                                        }

                                    } else {

                                        //user is register
                                        try {
                                            if (iDs.getAccountList().size() > 0) {
                                                Account.deleteAll(Account.class);
                                                Account.saveInTx(iDs.getAccountList());


                                                Account acc = Select.from(Account.class).first();
                                                if (acc != null && acc.CRDT != null)
                                                    binding.txtCredit.setTextColor(getActivity().getResources().getColor(R.color.medium_color));

                                                binding.txtCredit.setText("موجودی : " + format.format(acc.CRDT) + " ریال ");


                                            } else {

                                                binding.txtCredit.setTextColor(getActivity().getResources().getColor(R.color.red_table));
                                                binding.txtCredit.setText("خطا در بروز رسانی موجودی ");

                                            }
                                        } catch (Exception e) {

                                        }


                                    }


                            }, throwable -> {

                                getActivity().getResources().getColor(R.color.red_table);
                                binding.txtCredit.setText("خطا در بروز رسانی موجودی ");


                            })
            );
        } catch (Exception e) {


        }

    }


    private boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {

        Seen = true;
        getInquiryAccount1(mobile);

    }

    @Override
    public void onDestroy() {
        Seen = false;

        super.onDestroy();
    }


    public void refreshProductList() {

        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("MainFragment");
        if (fragment instanceof MainFragment) {
            MainFragment fgf = (MainFragment) fragment;
            fgf.setHomeBottomBarFromSetting();

        }
    }
}

