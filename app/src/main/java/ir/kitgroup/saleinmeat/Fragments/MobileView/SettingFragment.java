package ir.kitgroup.saleinmeat.Fragments.MobileView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.DecimalFormat;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.saleinmeat.Activities.Classes.LauncherActivity;
import ir.kitgroup.saleinmeat.DataBase.Account;
import ir.kitgroup.saleinmeat.DataBase.InvoiceDetail;
import ir.kitgroup.saleinmeat.DataBase.Product;
import ir.kitgroup.saleinmeat.DataBase.Tables;
import ir.kitgroup.saleinmeat.DataBase.User;

import ir.kitgroup.saleinmeat.R;

import ir.kitgroup.saleinmeat.classes.App;
import ir.kitgroup.saleinmeat.databinding.FragmentSettingBinding;
import ir.kitgroup.saleinmeat.models.ModelAccount;
import ir.kitgroup.saleinmeat.models.ModelLog;

public class SettingFragment extends Fragment {

    //region Parameter
    private FragmentSettingBinding binding;
    private final DecimalFormat format = new DecimalFormat("#,###,###,###");
    //endregion Parameter

    private SharedPreferences sharedPreferences;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private int fontSize = 0;
    private String userName;
    private String passWord;
    private String mobile;
    private String link = "";

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {


        binding = FragmentSettingBinding.inflate(getLayoutInflater());
        if (SplashScreenFragment.screenInches >= 7)
            fontSize = 14;
        else
            fontSize = 12;
        return binding.getRoot();


    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        try {
            switch (LauncherActivity.name) {


                case "ir.kitgroup.saleintop":
                    link = "http://185.201.49.204:4008/";

                    break; }
        } catch (Exception e) {

        }

        binding.tvProfile.setTextSize(fontSize);
        binding.tvComment.setTextSize(fontSize);
        binding.tvCredit.setTextSize(fontSize);
        binding.tvOrder.setTextSize(fontSize);
        binding.btnLogOut.setTextSize(fontSize);



        userName = Select.from(User.class).first().userName;
        passWord = Select.from(User.class).first().passWord;
        mobile = Select.from(Account.class).first().M;
        binding.btnComment.setOnClickListener(v -> Toast.makeText(getActivity(), "به زودی", Toast.LENGTH_SHORT).show());


        binding.btnCredit.setOnClickListener(v -> {
            if (!link.equals(""))
            {
                Uri uri = Uri.parse(link);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                startActivityForResult(intent, 44);
            }else {
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

            if (App.mode==1){
                if (User.count(User.class) > 0)
                    User.deleteAll(User.class);
            }


            if (Tables.count(Tables.class) > 0)
                Tables.deleteAll(Tables.class);


            sharedPreferences.edit().putBoolean("firstSync", false).apply();
            sharedPreferences.edit().putBoolean("firstSyncSetting", false).apply();






            getFragmentManager().popBackStack();
            getFragmentManager().popBackStack();
            LauncherActivity.name=LauncherActivity.namePackage;
            LoginClientFragment userFragment = new LoginClientFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, userFragment).commit();


        });


        try {
            binding.ivSupport.setColorFilter(getResources().getColor(R.color.color_svg), PorterDuff.Mode.SRC_IN);
        }catch (Exception e){}





        binding.lProfile.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, new ProfileFragment(), "ProfileFragment").addToBackStack("ProfileF").commit());

        binding.btnSupport.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, new AboutUsFragment(), "AboutUsFragment").addToBackStack("AboutUsF").commit());

        binding.btnOrderList.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, new OrderListFragment(), "OrderListFragment").addToBackStack("OrderListFMobile").commit());



    }


    @SuppressLint("SetTextI18n")
    private void getInquiryAccount1(String userName, String passWord, String mobile) {

        if (!isNetworkAvailable(getActivity())){
            binding.txtCredit.setTextColor(getActivity().getResources().getColor(R.color.red_table));
            binding.txtCredit.setText("خطا در اتصال به اینترنت");
            return;
        }

        try {
            compositeDisposable.add(
                    App.api.getInquiryAccount1(userName, passWord, mobile, "", "", 1, 1)
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



                                    }



                            }, throwable -> {

                                getActivity().getResources().getColor(R.color.red_table);
                                binding.txtCredit.setText("خطا در بروز رسانی موجودی ");


                            })
            );
        } catch (Exception e) {


        }

    }


    private   boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)  activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {

        getInquiryAccount1(userName,passWord,mobile);

        Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("SettingFragment");
        FragmentManager ft = getActivity().getSupportFragmentManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            ft.beginTransaction().detach(frg).commitNow();
            ft.beginTransaction().attach(frg).commitNow();

        } else {
            ft.beginTransaction().detach(frg).attach(frg).commit();
        }
    }
}

