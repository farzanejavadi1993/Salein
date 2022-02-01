package ir.kitgroup.saleinjam.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
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
import ir.kitgroup.saleinjam.Activities.LauncherActivity;
import ir.kitgroup.saleinjam.Connect.API;
import ir.kitgroup.saleinjam.DataBase.Account;
import ir.kitgroup.saleinjam.DataBase.InvoiceDetail;
import ir.kitgroup.saleinjam.DataBase.Product;
import ir.kitgroup.saleinjam.DataBase.Tables;
import ir.kitgroup.saleinjam.DataBase.Unit;
import ir.kitgroup.saleinjam.R;
import ir.kitgroup.saleinjam.classes.ConfigRetrofit;
import ir.kitgroup.saleinjam.classes.Util;
import ir.kitgroup.saleinjam.databinding.FragmentSettingBinding;
import ir.kitgroup.saleinjam.DataBase.Company;
import ir.kitgroup.saleinjam.models.Config;
import ir.kitgroup.saleinjam.models.ModelAccount;
import ir.kitgroup.saleinjam.models.ModelLog;

@AndroidEntryPoint
public class SettingFragment extends Fragment {

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    Config config;


    private FragmentSettingBinding binding;
    private API api;
    private Company company;
    private CompositeDisposable compositeDisposable;
    //region Dialog Sync
    private Dialog dialogSync;
    private TextView textMessageDialog;
    private MaterialButton btnOkDialog;
    private MaterialButton btnNoDialog;
    //endregion Dialog Sync
    private boolean seenActivity = true;
    private final DecimalFormat format = new DecimalFormat("#,###,###,###");
    private String mobile;
    private String linkPayment = "";

    private Account acc;

   private  Boolean disableAccount=false;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint({"SetTextI18n", "CommitPrefEdits"})
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((LauncherActivity) getActivity()).getVisibilityBottomBar(true);
        ((LauncherActivity) getActivity()).setInVisibiltyItem(true);

        compositeDisposable = new CompositeDisposable();

        company = Select.from(Company.class).first();
        api = ConfigRetrofit.getRetrofit("http://" + company.IP1 + "/api/REST/", false, 30).create(API.class);


        linkPayment = sharedPreferences.getString("payment_link", "");
         acc = Select.from(Account.class).first();
        if (!linkPayment.equals(""))
            linkPayment = linkPayment + "/ChargeClub?c=" + acc.getC();

        if (company!=null && company.ABUS !=null && !company.ABUS.equals("")){
            binding.view.setVisibility(View.VISIBLE);
            binding.btnAboutUs.setVisibility(View.VISIBLE);
        }
        //region Cast Variable Dialog Sync
        dialogSync = new Dialog(getActivity());
        dialogSync.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSync.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSync.setContentView(R.layout.custom_dialog);
        dialogSync.setCancelable(false);

        textMessageDialog = dialogSync.findViewById(R.id.tv_message);
        btnOkDialog = dialogSync.findViewById(R.id.btn_ok);
        btnNoDialog = dialogSync.findViewById(R.id.btn_cancel);
        btnNoDialog.setOnClickListener(v -> {
            dialogSync.dismiss();

            if (disableAccount){
               getActivity().finish();
            }

        });

        btnOkDialog.setOnClickListener(v -> {
            dialogSync.dismiss();
            sharedPreferences.edit().clear();

            if (Account.count(Account.class) > 0)
                Account.deleteAll(Account.class);

            if (InvoiceDetail.count(InvoiceDetail.class) > 0)
                InvoiceDetail.deleteAll(InvoiceDetail.class);

            if (Product.count(Product.class) > 0)
                Product.deleteAll(Product.class);

            if (Tables.count(Tables.class) > 0)
                Tables.deleteAll(Tables.class);

            if (Unit.count(Unit.class) > 0)
                Unit.deleteAll(Unit.class);

            if (Company.count(Company.class) > 0)
                Company.deleteAll(Company.class);

            ((LauncherActivity) getActivity()).setFistItem();
            ((LauncherActivity) getActivity()).getVisibilityBottomBar(false);

            final int size = getActivity().getSupportFragmentManager().getBackStackEntryCount();
            for (int i = 0; i < size; i++) {
                getActivity().getSupportFragmentManager().popBackStack();
            }

            getActivity().finish();
            startActivity(getActivity().getIntent());
            //getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new SplashScreenFragment(), "SplashScreenFragment").commit();
            //reload loginFragment
        });
        //endregion Cast Variable Dialog Sync

        int fontSize;
        if (Util.screenSize >= 7)
            fontSize = 14;
        else
            fontSize = 12;
        binding.tvProfile.setTextSize(fontSize);
        binding.tvComment.setTextSize(fontSize);
        binding.tvCredit.setTextSize(fontSize);
        binding.tvOrder.setTextSize(fontSize);


        mobile = Select.from(Account.class).first().M;
        binding.btnShare.setOnClickListener(v -> {
            Bundle bundle=new Bundle();
            bundle.putInt("type",2);
            ContactUsFragment contactUsFragment=new ContactUsFragment();
            contactUsFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, contactUsFragment, "ContactUsFragment").addToBackStack("AboutUsF").commit();


        });

        binding.btnCredit.setOnClickListener(v -> {
            if (!linkPayment.equals("")) {
                if (seenActivity) {
                    seenActivity = false;
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
        showError( "آیا مایل به خروج از برنامه هستید؟",1);
        });

        try {
            binding.ivSupport.setColorFilter(getResources().getColor(R.color.color_svg), PorterDuff.Mode.SRC_IN);
        } catch (Exception ignored) {}


        binding.lProfile.setOnClickListener(v ->
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new ProfileFragment(), "ProfileFragment").addToBackStack("ProfileF").commit());


        binding.btnSupport.setOnClickListener(v ->{
            Bundle bundle=new Bundle();
            bundle.putInt("type",1);
                    ContactUsFragment contactUsFragment=new ContactUsFragment();
                    contactUsFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, contactUsFragment, "ContactUsFragment").addToBackStack("AboutUsF").commit();
                }

        );


        binding.btnAboutUs.setOnClickListener(v ->
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new AboutUsFragment(), "AboutUsFragment").addToBackStack("AboutUsF").commit()
        );
        binding.btnOrderList.setOnClickListener(v ->
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new OrderListFragment(), "OrderListFragment").addToBackStack("OrderListF").commit());

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
                                            int  message = iDs0.getLogs().get(0).getMessage();
                                            if (message == 3) {
                                                disableAccount=true;
                                                sharedPreferences.edit().putBoolean("disableAccount", true).apply();
                                                showError(description,2);
                                            }
                                        }
                                    } else {
                                        disableAccount=false;
                                        sharedPreferences.edit().putBoolean("disableAccount", false).apply();
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
                                                disableAccount=sharedPreferences.getBoolean("disableAccount",false);
                                                if (disableAccount)
                                                    showError("حساب شما غیر فعال است بعداز بررسی و تایید کارشناس جم پارت قطعه فعال میگردد",2);
                                            }
                                        } catch (Exception ignored) {}
                                    }
                                }, throwable -> {
                                getActivity().getResources().getColor(R.color.red_table);
                                binding.txtCredit.setText("خطا در بروز رسانی موجودی ");
                            }));
        } catch (Exception ignored) {} }
    private boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void showError(String error, int type) {
        textMessageDialog.setText(error);

        dialogSync.dismiss();
        if (type==2) {
            btnOkDialog.setVisibility(View.GONE);
            btnNoDialog.setText("بستن");

        }
        else {
            btnOkDialog.setVisibility(View.VISIBLE);
            btnNoDialog.setText("خیر");
            btnOkDialog.setText("بله");
        }
        dialogSync.setCancelable(false);
        dialogSync.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        seenActivity = true;
        getInquiryAccount1(mobile);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        seenActivity = false;
        compositeDisposable.dispose();
        binding = null;
    }
    @Override
    public void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }



}