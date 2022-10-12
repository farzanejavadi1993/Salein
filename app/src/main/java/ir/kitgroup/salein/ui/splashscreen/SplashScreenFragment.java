package ir.kitgroup.salein.ui.splashscreen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.salein.Connect.CompanyViewModel;
import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.DataBase.InvoiceDetail;
import ir.kitgroup.salein.DataBase.Product;
import ir.kitgroup.salein.DataBase.Unit;

import ir.kitgroup.salein.classes.ApplicationInformation;
import ir.kitgroup.salein.classes.ConnectToServer;
import ir.kitgroup.salein.classes.CustomDialog;
import ir.kitgroup.salein.classes.Util;
import ir.kitgroup.salein.databinding.FragmentSplashScreenBinding;
import ir.kitgroup.salein.R;

import ir.kitgroup.salein.classes.HostSelectionInterceptor;

import ir.kitgroup.salein.DataBase.Company;
import ir.kitgroup.salein.models.AppDetail;
import ir.kitgroup.salein.DataBase.AppInfo;


@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
public class SplashScreenFragment extends Fragment {
    @Inject
    HostSelectionInterceptor hostSelectionInterceptor;

    @Inject
    SharedPreferences sharedPreferences;


    private CompanyViewModel myViewModel;
    private FragmentSplashScreenBinding binding;


    private final String appVersion = "";

    private String linkUpdate = "";
    private String newVersionUpdate = "";
    private String titleUpdate = "";
    private String messageUpdate = "";
    private boolean forcedUpdate = false;
    private String companyGuid = "";
    private CustomDialog customDialog;


    private AppInfo appInfo;


    //region Override Method


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentSplashScreenBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        connectToServer(false);
        init();
        initAppInfo();
        iniAppVersion();
        initAnimation();
        initCustomDialog();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        myViewModel = new ViewModelProvider(this).get(CompanyViewModel.class);
        myViewModel.getResultMessage().setValue(null);
        getApp();


        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {

            if (result == null) return;

            if (getCompany() != null)
                navigate();

            else {
                binding.animationView.setVisibility(View.GONE);
                binding.btnWarning.setVisibility(View.VISIBLE);
                binding.txtErrorr.setText(result.getName());
            }
        });

        myViewModel.getResultGetApp().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;

            myViewModel.getResultGetApp().setValue(null);

            if (result.size() > 0) {
                Util.APPLICATION_ID = result.get(0).getAppId();
                if (result.get(0).getIsActive()) {
                    binding.btnError.setVisibility(View.GONE);
                    newVersionUpdate = result.get(0).getVersion();
                    titleUpdate = result.get(0).getUpdateTitle();
                    messageUpdate = result.get(0).getUpdateDesc();
                    linkUpdate = result.get(0).getLink();
                    sharedPreferences.edit().putString("link_update", linkUpdate).apply();
                    forcedUpdate = result.get(0).getForced();
                    companyGuid = result.get(0).getAccountId();
                    if (getAccount() != null)
                        myViewModel.getCustomerFromServer(getAccount().getM());
                    else
                        checkUpdate();

                } else {
                    binding.btnError.setVisibility(View.VISIBLE);
                    binding.txtErrorr.setText("اپلیکیشن غیر فعال شده است.");

                }
            }

        });

        myViewModel.getResultCustomerFromServer().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;

            myViewModel.getResultCustomerFromServer().setValue(null);

            //region Information Account From Server
            if (result.size() > 0) {
                String IMEI = Util.getAndroidID(getActivity());
                List<AppDetail> Apps = result.get(0).getApps();
                CollectionUtils.filter(Apps, l -> l.getAppId().equals(Util.APPLICATION_ID) && l.getIemi().equals(IMEI));

                if (Apps.size() > 0) {
                    if (!Apps.get(0).getIsActive()) {
                        binding.txtErrorr.setText("با این تلفن نمیتوانید وارد نرم افزار شوید.");
                        return;
                    }
                } else
                {

                    Account account=Select.from(Account.class).first();
                    if (getAccount()!=null){
                        account.setImei(IMEI);
                        account.setAppId(Util.APPLICATION_ID);
                        Util.JsonObjectAccount jsonObjectAcc = new Util.JsonObjectAccount();
                        ArrayList<Account> accounts=new ArrayList<>();
                        accounts.add(account);
                        jsonObjectAcc.Account = accounts;
                        myViewModel.addAccountToServer(jsonObjectAcc);
                    }

                }

            }
            else {
                clearData();

            }
            checkUpdate();
            //endregion Information Account From Server

        });

        myViewModel.getResultCompany().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            myViewModel.getResultCompany().setValue(null);
            if (result.size() > 0) {
                Company.deleteAll(Company.class);
                Company.saveInTx(result);
                navigate();
            }else
                binding.txtErrorr.setText("شرکت یافت نشد.");
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //endregion Override Method

    //region Custom Method
    public String appVersion() throws PackageManager.NameNotFoundException {
        return getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
    }



    private void checkUpdate() {
        if (forcedUpdate && !newVersionUpdate.equals("") && !appVersion.equals(newVersionUpdate)) {
            customDialog.showDialog(getActivity(), titleUpdate, false, "", "آپدیت", true, false);


        } else if (!forcedUpdate && !newVersionUpdate.equals("") && !appVersion.equals(newVersionUpdate)) {
            customDialog.showDialog(getActivity(), titleUpdate, false, "بعدا", "آپدیت", true, true);


        } else if (getAccount() != null && !messageUpdate.equals("") && !getAccount().getVersion().equals(newVersionUpdate)) {
            customDialog.showDialog(getActivity(), messageUpdate, false, "بستن و ادامه", "", false, true);
        } else
            myViewModel.getCompany(companyGuid);
    }
    private void initAppInfo() {
        ApplicationInformation applicationInformation = new ApplicationInformation();
        String packageName=getPackageName1();
        appInfo = applicationInformation.getInformation(packageName);
        if (Select.from(AppInfo.class).list().size() == 0)
            AppInfo.saveInTx(appInfo);
    }

    public String getPackageName1() {
        String packageName = "";
        try {
            packageName =getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageName;
    }


    private void init() {binding.btnWarning.setOnClickListener(v -> {
            binding.animationView.setVisibility(View.VISIBLE);
            binding.btnWarning.setVisibility(View.GONE);
            myViewModel.getApp(Util.APPLICATION_ID);
        });
    }

    private void connectToServer(boolean connect) {
        ConnectToServer connectToServer = new ConnectToServer();
        connectToServer.connect(sharedPreferences, hostSelectionInterceptor, connect, getIp());
    }



    private void iniAppVersion() {
        try {
            binding.tvversion.setText(String.format("%s%s", getString(R.string.application_version), appVersion()));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initAnimation() {

        Glide.with(this).asGif().load(Uri.parse(appInfo.getGif_url())).into(binding.animationView);

    }

    private void getApp() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                    myViewModel.getApp(appInfo.getApplication_code());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private void initCustomDialog() {
        customDialog = CustomDialog.getInstance();
        customDialog.setOnClickPositiveButton(() -> {
            if (!linkUpdate.equals("")) {
                Uri uri = Uri.parse(linkUpdate);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        customDialog.setOnClickNegativeButton(() -> {
            if (getAccount() != null && messageUpdate.equals("") && !getAccount().getVersion().equals(newVersionUpdate)) {
                Account account = getAccount();
                account.setVersion(appVersion);
                account.save();
            }
            customDialog.hideProgress();
            myViewModel.getCompany(companyGuid);
        });

    }

    private void navigate() {

        connectToServer(true);

        //region When The User Is Logged In
        if (getAccount() != null) {

            if (Select.from(AppInfo.class).first().isSalein_main())
                Navigation.findNavController(getView()).navigate(R.id.actionGoToCompanyFragment);

            else {
                NavDirections action = SplashScreenFragmentDirections.actionGoToHomeFragment("");
                Navigation.findNavController(getView()).navigate(action);
            }
        }
        //endregion When The User Is Not Logged In
        else
            Navigation.findNavController(getView()).navigate(R.id.actionGoToLoginFragment);
        //endregion When The User Is Not Logged In
    }

    private Company getCompany() {
        return Select.from(Company.class).first();
    }



    private Account getAccount() {
        return Select.from(Account.class).first();
    }
    private String getIp() {
        String ip;
        if (getCompany()!=null)
        ip= "http://" + getCompany().getIp1() + "/api/REST/";
        else
            ip="";
        return ip;
    }

    private void clearData() {
        sharedPreferences.edit().clear().apply();
        Account.deleteAll(Account.class);
        Company.deleteAll(Company.class);
        InvoiceDetail.deleteAll(InvoiceDetail.class);
        Company.deleteAll(Company.class);
        Product.deleteAll(Product.class);
        Unit.deleteAll(Unit.class);
        Account.deleteAll(Account.class);


    }


    /*   @SuppressLint("SetTextI18n")
    private void init() {
        binding.btnWarning.setOnClickListener(v -> {
            binding.animationView.setVisibility(View.VISIBLE);
            binding.btnWarning.setVisibility(View.GONE);
            myViewModel.getApp(Util.APPLICATION_ID);
        });
        if (getUser() != null)
          oldVersion = getUser().getVersion();
        sharedPreferences.edit().putBoolean("vip", false).apply();
        sharedPreferences.edit().putBoolean("discount", false).apply();
    }
    private void initSaleinInstance() {
        saleinInstance = Select.from(Salein.class).first();
        if (saleinInstance == null && packageName.equals("ir.kitgroup.salein")) {
            saleinInstance = new Salein();
            saleinInstance.ContainAllCompanie = true;
            saleinInstance.save();
        }
        if (saleinInstance != null)
            Company.deleteAll(Company.class);
    }

      private void setData() {

        title = company != null && company.getN() != null ? company.getN() : "";
        description = company != null && company.getDesc() != null ? company.getDesc() : "";
        binding.tvTitle.setText(title);
        binding.tvDescription.setText(description);
    }*/


    //endregion Custom Method


}
