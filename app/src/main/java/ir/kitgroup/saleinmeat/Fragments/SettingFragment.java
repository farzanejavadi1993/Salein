package ir.kitgroup.saleinmeat.Fragments;

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
import ir.kitgroup.saleinmeat.Activities.LauncherActivity;
import ir.kitgroup.saleinmeat.Connect.API;
import ir.kitgroup.saleinmeat.DataBase.Account;
import ir.kitgroup.saleinmeat.DataBase.InvoiceDetail;
import ir.kitgroup.saleinmeat.DataBase.Product;
import ir.kitgroup.saleinmeat.DataBase.Tables;
import ir.kitgroup.saleinmeat.DataBase.Unit;

import ir.kitgroup.saleinmeat.R;

import ir.kitgroup.saleinmeat.classes.ConfigRetrofit;
import ir.kitgroup.saleinmeat.classes.Util;
import ir.kitgroup.saleinmeat.databinding.FragmentSettingBinding;
import ir.kitgroup.saleinmeat.DataBase.Company;
import ir.kitgroup.saleinmeat.models.ModelAccount;
import ir.kitgroup.saleinmeat.models.ModelLog;


@AndroidEntryPoint
public class SettingFragment extends Fragment {


    private API api;

    @Inject
    SharedPreferences sharedPreferences;


    private Company company;

    //region Parameter
    private boolean Seen = true;
    private FragmentSettingBinding binding;
    private final DecimalFormat format = new DecimalFormat("#,###,###,###");
    //endregion Parameter


    private CompositeDisposable compositeDisposable;

    private int fontSize;

    private String mobile;

    private String linkPayment = "";


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {


        binding = FragmentSettingBinding.inflate(getLayoutInflater());
        return binding.getRoot();


    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ((LauncherActivity) getActivity()).getVisibilityBottomBar(true);
        ((LauncherActivity) getActivity()).setInVisibiltyItem(true);
        compositeDisposable = new CompositeDisposable();


        linkPayment = sharedPreferences.getString("payment_link", "");




        company = null;
        api = null;
        company = Select.from(Company.class).first();
        api = ConfigRetrofit.getRetrofit("http://" + company.IP1 + "/api/REST/", false,30).create(API.class);


        if (Util.screenSize >= 7)
            fontSize = 14;
        else
            fontSize = 12;



        binding.tvProfile.setTextSize(fontSize);
        binding.tvComment.setTextSize(fontSize);
        binding.tvCredit.setTextSize(fontSize);
        binding.tvOrder.setTextSize(fontSize);


        mobile = Select.from(Account.class).first().M;
        binding.btnComment.setOnClickListener(v -> Toast.makeText(getActivity(), "به زودی", Toast.LENGTH_SHORT).show());


        binding.btnCredit.setOnClickListener(v -> {

            if (!linkPayment.equals("")) {
                if (Seen) {
                    Seen = false;

                    Uri uri = Uri.parse(linkPayment);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    startActivityForResult(intent, 44);
                }


            } else {
                Toast.makeText(getActivity(), "دسترسی به باشگاه امکان پذیر نمی باشد", Toast.LENGTH_SHORT).show();
            }

        });
        binding.ivPower.setOnClickListener(v -> {
            if (Account.count(Account.class) > 0)
                Account.deleteAll(Account.class);


            if (InvoiceDetail.count(InvoiceDetail.class) > 0)
                InvoiceDetail.deleteAll(InvoiceDetail.class);


            if (Product.count(Product.class) > 0)
                Product.deleteAll(Product.class);


            if (Tables.count(Tables.class) > 0)
                Tables.deleteAll(Tables.class);


            if (Tables.count(Tables.class) > 0)
                Tables.deleteAll(Tables.class);

            if (Unit.count(Unit.class) > 0)
                Unit.deleteAll(Unit.class);

            if (Company.count(Company.class) > 0)
                Company.deleteAll(Company.class);



            ((LauncherActivity) getActivity()).getVisibilityBottomBar(false);


            final int size = getActivity().getSupportFragmentManager().getBackStackEntryCount();
            for (int i = 0; i < size; i++) {
                getActivity().getSupportFragmentManager().popBackStack();
            }


            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new SplashScreenFragment(), "SplashScreenFragment").commit();


            //reload loginFragment


        });


        try {
            binding.ivSupport.setColorFilter(getResources().getColor(R.color.color_svg), PorterDuff.Mode.SRC_IN);
        } catch (Exception ignored) {
        }


        binding.lProfile.setOnClickListener(v ->
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new ProfileFragment(), "ProfileFragment").addToBackStack("ProfileF").commit()
        );

        binding.btnSupport.setOnClickListener(v ->


                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new AboutUsFragment(), "AboutUsFragment").addToBackStack("AboutUsF").commit()


        );

        binding.btnOrderList.setOnClickListener(v ->

                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new OrderListFragment(), "OrderListFragment").addToBackStack("OrderListF").commit()
        );

        Account acc = Select.from(Account.class).first();
        if (!linkPayment.equals(""))
            linkPayment=linkPayment+"/pay?s="+acc.getC();



        if (acc != null && acc.CRDT != null) {
            binding.txtCredit.setText("موجودی : " + format.format(acc.CRDT) + " ریال ");
        } else {
            binding.txtCredit.setText("موجودی : " + "0" + " ریال ");
        }


        getInquiryAccount1(acc.M);


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
                    api.getInquiryAccount1(company.USER, company.PASS, mobile, "", "", 1, 1)
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
                                        } catch (Exception ignored) {

                                        }


                                    }


                            }, throwable -> {

                                getActivity().getResources().getColor(R.color.red_table);
                                binding.txtCredit.setText("خطا در بروز رسانی موجودی ");


                            })
            );
        } catch (Exception ignored) {


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
    public void onDestroyView() {
        super.onDestroyView();
        Seen = false;
        compositeDisposable.dispose();
        binding = null;


    }

    @Override
    public void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }


}

