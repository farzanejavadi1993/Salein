package ir.kitgroup.saleinkhavari.ui.splashscreen;

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
import ir.kitgroup.saleinkhavari.Connect.MainViewModel;
import ir.kitgroup.saleinkhavari.DataBase.Account;

import ir.kitgroup.saleinkhavari.DataBase.Locations;
import ir.kitgroup.saleinkhavari.classes.ConnectToServer;
import ir.kitgroup.saleinkhavari.classes.CustomDialog;
import ir.kitgroup.saleinkhavari.classes.Util;
import ir.kitgroup.saleinkhavari.classes.application_information.ApplicationInformation;
import ir.kitgroup.saleinkhavari.classes.application_information.PackageName;
import ir.kitgroup.saleinkhavari.databinding.FragmentSplashScreenBinding;
import ir.kitgroup.saleinkhavari.R;

import ir.kitgroup.saleinkhavari.classes.HostSelectionInterceptor;

import ir.kitgroup.saleinkhavari.DataBase.Company;
import ir.kitgroup.saleinkhavari.DataBase.SaleinShop;
import ir.kitgroup.saleinkhavari.models.AppDetail;


@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
public class SplashScreenFragment extends Fragment {
    //region Parameter
    @Inject
    HostSelectionInterceptor hostSelectionInterceptor;

    @Inject
    SharedPreferences sharedPreferences;

    private FragmentSplashScreenBinding binding;
    private MainViewModel mainViewModel;

    private String appVersion = "";
    private String linkUpdate = "";
    private String newVersionUpdate = "";
    private String titleUpdate = "";
    private String messageUpdate = "";
    private boolean forcedUpdate = false;
    private CustomDialog customDialog;

    private SaleinShop saleinShop;
    private String IMEI = "";
    private String companyGuid = "";
    private Company company;
    private Account account;

    //region Parameter


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
        init();
        getCompany();
        getAccount();
        initAppInformation();
        iniAppVersion();
        initAnimation();
        initCustomDialog();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getResultMessage().setValue(null);


        mainViewModel.getApp(saleinShop.getApplication_code());


        mainViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;

            if (result.getCode() == 110 || result.getCode() == 111) {
                checkUpdate();
            } else if (company != null)
                navigate();

            else
                showError(result.getDescription());
        });

        mainViewModel.getResultGetApp().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;

            mainViewModel.getResultGetApp().setValue(null);

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

                    if (account != null)
                        mainViewModel.getCustomerFromServer(account.getM());
                    else
                        checkUpdate();
                } else {
                    binding.btnError.setVisibility(View.VISIBLE);
                    binding.txtErrorr.setText("اپلیکیشن غیر فعال شده است.");
                }
            } else
                showError("مشخصات این شرکت در سرور یافت نشد.");

        });

        mainViewModel.getResultCompany().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            mainViewModel.getResultCompany().setValue(null);
            if (result.size() > 0) {
                company = result.get(0);
                Company.deleteAll(Company.class);
                Company.saveInTx(result);
                navigate();
            } else
                showError("شرکت یافت نشد.");

        });

        mainViewModel.getResultCustomerFromServer().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            mainViewModel.getResultCustomerFromServer().setValue(null);

            if (result.size() > 0) {

                setIdServerToAccount(result.get(0).getI());

                List<AppDetail> Apps = result.get(0).getApps();
                CollectionUtils.filter(Apps, l -> l.getAppId().equals(Util.APPLICATION_ID) && l.getIemi().equals(IMEI));

                if (Apps.size() > 0) {
                    if (!Apps.get(0).getIsActive()) {
                        showError("با این تلفن نمیتوانید وارد نرم افزار شوید.");
                    } else
                        checkUpdate();
                } else
                    addCustomerToSerVer();
            } else
                addCustomerToSerVer();
        });

        mainViewModel.getResultAddAccountToServer().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            setIdServerToAccount(result.get(0).getCurrent());

            account.save();
            mainViewModel.getResultAddAccountToServer().setValue(null);
            checkUpdate();
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
        appVersion = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        return appVersion;
    }


    private void checkUpdate() {
        if (forcedUpdate && !newVersionUpdate.equals("") && !appVersion.equals(newVersionUpdate)) {
            customDialog.showDialog(getActivity(), titleUpdate, false, "", "آپدیت", true, false);
        } else if (!forcedUpdate && !newVersionUpdate.equals("") && !appVersion.equals(newVersionUpdate)) {
            customDialog.showDialog(getActivity(), titleUpdate, false, "بعدا", "آپدیت", true, true);
        } else if (account != null && account.getVersion() != null && !account.getVersion().equals(newVersionUpdate)) {
            if (!messageUpdate.equals(""))
                customDialog.showDialog(getActivity(), messageUpdate, false, "بستن و ادامه", "", false, true);

            else {
                account.setVersion(appVersion);
                account.save();
                binding.animationView.setVisibility(View.VISIBLE);
                mainViewModel.getCompany(companyGuid);
            }
        } else
            mainViewModel.getCompany(companyGuid);
    }

    private void initAppInformation() {
        ApplicationInformation applicationInformation = new ApplicationInformation();
        PackageName packageName = new PackageName();

        saleinShop = applicationInformation.getInformation(packageName, getActivity());

        if (Select.from(SaleinShop.class).first() == null)
            SaleinShop.saveInTx(saleinShop);

    }

    private void init() {
        SaleinShop.deleteAll(SaleinShop.class);
        Locations.deleteAll(Locations.class);
        IMEI = Util.getAndroidID(getActivity());

        binding.btnWarning.setOnClickListener(v -> {
            binding.animationView.setVisibility(View.VISIBLE);
            binding.btnWarning.setVisibility(View.GONE);
            mainViewModel.getApp(Util.APPLICATION_ID);
        });
    }

    private void iniAppVersion() {
        try {
            binding.tvversion.setText(String.format("%s%s", getString(R.string.application_version)+" ", appVersion()));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initAnimation() {
        try {
            Glide.with(this).asGif().load(Uri.parse(saleinShop.getGif_url())).into(binding.animationView);
        }catch (Exception ignored){}


        try {
            binding.halloweenLottie.setAnimation(saleinShop.getGif_url());
            binding.halloweenLottie.loop(true);
            binding.halloweenLottie.setSpeed(2f);
            binding.halloweenLottie.playAnimation();
        }catch (Exception ignored){}

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
            if (account != null && messageUpdate.equals("") && !account.getVersion().equals(newVersionUpdate)) {
                account.setVersion(appVersion);
                account.save();
            }
            customDialog.hideProgress();
            binding.animationView.setVisibility(View.VISIBLE);
            mainViewModel.getCompany(companyGuid);
        });
    }

    private void navigate() {

        connectToServer();

        if (account != null) {
            if (saleinShop.isPublicApp())
                Navigation.findNavController(getView()).navigate(R.id.actionGoToCompanyFragment);
            else {
                NavDirections action = (NavDirections) SplashScreenFragmentDirections.actionGoToHomeFragment("");
                Navigation.findNavController(getView()).navigate(action);
            }
        } else
            Navigation.findNavController(getView()).navigate(R.id.actionGoToLoginFragment);

    }

    private void getCompany() {
        company = Select.from(Company.class).first();
    }

    private void getAccount() {
        account = Select.from(Account.class).first();
    }

    private void addCustomerToSerVer() {
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


    private void showError(String error) {
        binding.animationView.setVisibility(View.GONE);
        binding.btnWarning.setVisibility(View.VISIBLE);
        binding.txtErrorr.setText(error);
    }

    private void connectToServer() {
        ConnectToServer connectToServer = new ConnectToServer();
        connectToServer.connect(sharedPreferences, hostSelectionInterceptor, true, getIp());
    }

    private String getIp() {
        return "http://" + company.getIp1() + "/api/REST/";
    }

    private void setIdServerToAccount(String id) {
        sharedPreferences.edit().putString("idServer",id).apply();
    }
    //endregion Custom Method


}
