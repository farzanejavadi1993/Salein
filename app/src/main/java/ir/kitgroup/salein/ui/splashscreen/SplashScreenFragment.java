package ir.kitgroup.salein.ui.splashscreen;

import android.annotation.SuppressLint;
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

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.salein.Connect.MyViewModel;
import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.DataBase.Users;
import ir.kitgroup.salein.DataBase.Salein;
import ir.kitgroup.salein.classes.ConnectToServer;
import ir.kitgroup.salein.classes.CustomDialog;
import ir.kitgroup.salein.classes.Util;
import ir.kitgroup.salein.databinding.FragmentSplashScreenBinding;
import ir.kitgroup.salein.R;

import ir.kitgroup.salein.classes.HostSelectionInterceptor;

import ir.kitgroup.salein.DataBase.Company;


@AndroidEntryPoint
public class SplashScreenFragment extends Fragment {


    //region Parameter
    @Inject
    HostSelectionInterceptor hostSelectionInterceptor;

    @Inject
    SharedPreferences sharedPreferences;


    private MyViewModel myViewModel;
    private FragmentSplashScreenBinding binding;
    private String packageName;
    private Salein saleinInstance;
    private String title;
    private String description;
    private Company company;
    private String linkUpdate = "";
    private String appVersion = "";
    private String message = "";
    private String newVersion = "";
    private String titleUpdate = "";
    private String messageUpdate = "";
    private boolean forcedUpdate = false;
    private Boolean forced = false;
    private String versionApp;
    private String companyGuid = "";
    private Users user;

    private CustomDialog customDialog;
    private String oldVersion = "";//please change farzane


    //endregion Parameter


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
        initPackageName();
        iniAppVersion();
        initSaleinInstance();
        connectToServer(false, "");
        setData();
        initAnimation();
        initCustomDialog();

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        myViewModel.getResultMessage().setValue(null);
        getApp();

        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {

            if (result == null) return;
            // Company Information Is Already Save In The Database And Can Be Used To Log In To The Application
            if (Select.from(Company.class).list().size() > 0)
                navigate(company);
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
                    newVersion = result.get(0).getVersion();
                    titleUpdate = result.get(0).getUpdateTitle();
                    messageUpdate = result.get(0).getUpdateDesc();
                    linkUpdate = result.get(0).getLink();
                    sharedPreferences.edit().putString("link_update", linkUpdate).apply();
                    forcedUpdate = result.get(0).getForced();
                    companyGuid = result.get(0).getAccountId();

                       /* if (user != null)
                            myViewModel.getCustomerFromServer(user.getM());
                        else*/
                            checkUpdate();

                } else {
                    binding.txtErrorr.setText("اپلیکیشن غیر فعال شده است.");
                    binding.btnError.setVisibility(View.VISIBLE);
                }

            }

        });
        myViewModel.getResultCompany().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;

            myViewModel.getResultCompany().setValue(null);

            if (result.size() > 0) {
                Company.deleteAll(Company.class);
                Company.saveInTx(result);
                navigate(result.get(0));
            }

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

    public String getPackageName() throws PackageManager.NameNotFoundException {
        return getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).packageName;
    }

    private void checkUpdate() {

        if (forcedUpdate && !newVersion.equals("") && !appVersion.equals(newVersion)) {
            customDialog.showDialog(getActivity(), titleUpdate, false, "", "آپدیت", true, false);


        } else if (!forcedUpdate && !newVersion.equals("") && !appVersion.equals(newVersion)) {
            customDialog.showDialog(getActivity(), titleUpdate, false, "بعدا", "آپدیت", true, true);


        } else if (user !=null) {
            if (!newVersion.equals("") && !oldVersion.equals(newVersion)) {
                customDialog.showDialog(getActivity(), titleUpdate, false, "", "بستن", true, false);
            }
        } else
            myViewModel.getCompany(companyGuid);


    }

    @SuppressLint("SetTextI18n")
    private void init() {
        user =Select.from(Users.class).first();
        if (user !=null)
            oldVersion= user.getVersion();

        sharedPreferences.edit().putBoolean("vip", false).apply();
        sharedPreferences.edit().putBoolean("discount", false).apply();


        binding.btnWarning.setOnClickListener(v -> {
            binding.animationView.setVisibility(View.VISIBLE);
            binding.btnWarning.setVisibility(View.GONE);
            myViewModel.getApp(Util.APPLICATION_ID);
        });


    }

    private void connectToServer(boolean connect, String url) {
        ConnectToServer connectToServer = new ConnectToServer();
        connectToServer.connect(sharedPreferences, hostSelectionInterceptor, connect, url);
    }

    private void initPackageName() {
        try {
            packageName = getPackageName();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void iniAppVersion() {
        try {
            binding.tvversion.setText(" نسخه " + appVersion());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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
        company = Select.from(Company.class).first();
        title = company != null && company.getN() != null ? company.getN() : "";
        description = company != null && company.getDesc() != null ? company.getDesc() : "";
        binding.tvTitle.setText(title);
        binding.tvDescription.setText(description);
    }

    private void initAnimation() {
        if (packageName.contains("meat"))
            Glide.with(this).asGif().load(Uri.parse("file:///android_asset/donyavi.gif")).into(binding.animationView);
        else
            Glide.with(this).load(Uri.parse("file:///android_asset/loading3.gif")).into(binding.animationView);
    }


    private void getApp() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                    myViewModel.getApp(Util.APPLICATION_CODE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }


    //please change farzane
    private void initCustomDialog() {
        customDialog = CustomDialog.getInstance();
        customDialog.setOnClickPositiveButton(() -> {

        });

        customDialog.setOnClickNegativeButton(() -> {

        });

    }

    private void navigate(Company company){
        connectToServer(true, "http://" + company.getIp1() + "/api/REST/");
        // When The User Is Logged In
        if (Select.from(Users.class).list().size() > 0) {
            if (saleinInstance != null)
                Navigation.findNavController(binding.getRoot()).navigate(R.id.actionGoToCompanyFragment);
            else {
                NavDirections action = SplashScreenFragmentDirections.actionGoToHomeFragment("");
                Navigation.findNavController(binding.getRoot()).navigate(action);
            }
        }
        //region When The User Is Not Logged In
        else
            Navigation.findNavController(binding.getRoot()).navigate(R.id.actionGoToLoginFragment);
        //endregion When The User Is Not Logged In
    }

    //endregion Custom Method


}
