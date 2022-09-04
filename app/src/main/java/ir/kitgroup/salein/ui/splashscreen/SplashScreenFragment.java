package ir.kitgroup.salein.ui.splashscreen;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


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

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;
import ir.kitgroup.salein.Connect.MyViewModel;
import ir.kitgroup.salein.DataBase.Users;
import ir.kitgroup.salein.DataBase.Salein;
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
    private Boolean  forced=false;




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


        //region Config
        sharedPreferences.edit().putBoolean("vip", false).apply();
        sharedPreferences.edit().putBoolean("discount", false).apply();
        //endregion Config




        //region Set Version In TextView
        try {
            binding.tvversion.setText(" نسخه " + appVersion());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //endregion Set Version In TextView

        //region Request From The Server With The Original IP
        sharedPreferences.edit().putBoolean("status", false).apply();
        hostSelectionInterceptor.setHostBaseUrl();
        //endregion Request From The Server With The Original IP

        //region getPackageName And Set In Variable
        try {
            packageName = getPackageName();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //endregion getPackageName And Set In Variable

        //region Create Salein Instance When PackageName Equal With ...
        saleinInstance = Select.from(Salein.class).first();
        if (saleinInstance == null && packageName.equals("ir.kitgroup.salein")) {
            saleinInstance = new Salein();
            saleinInstance.ContainAllCompanie = true;
            saleinInstance.save();

        }
        //endregion Create Salein Instance When PackageName Equal With ...

        //region Set Title And Description In TextViews
        if (saleinInstance != null)
            Company.deleteAll(Company.class);



        //region Get The Company Is Save In The Database
        company = Select.from(Company.class).first();
        //endregion Get The Company Is Save In The Database



        title = company != null && company.getN() != null ? company.getN() : "";
        description = company != null && company.getDesc() != null ? company.getDesc() : "";


        binding.tvTitle.setText(title);
        binding.tvDescription.setText(description);
        //endregion Set Title And Description In TextViews


        //region Set Gif In AnimationView
        if (packageName.contains("meat"))
            Glide.with(this).asGif().load(Uri.parse("file:///android_asset/donyavi.gif")).into(binding.animationView);
        else
            Glide.with(this).load(Uri.parse("file:///android_asset/loading3.gif")).into(binding.animationView);
        //endregion Set Gif In AnimationView


        //region Press The BtnError For Re-Request
        binding.btnWarning.setOnClickListener(v -> {
            binding.animationView.setVisibility(View.VISIBLE);
            binding.btnWarning.setVisibility(View.GONE);
            myViewModel.getCompany("");
        });
        //endregion Press The BtnError For Re-Request


    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);


            myViewModel.getResultMessage().setValue(null);
     /*       myViewModel.getResultCustomerFromServer().observe(getViewLifecycleOwner(), result -> {

                if (result == null)
                    return;

                myViewModel.getResultCustomerFromServer().setValue(null);

                //region Information Account From Server
                if (result.size() > 0) {
                    List<AppDetail> list = result.get(0).getApps();

                    if (list.size() > 0) {
                        CollectionUtils.filter(list, r -> r.getIemi().equals(IMEI) && r.getAppId().equals(Constant.APPLICATION_ID));
                        if (list.size() > 0) {
                            if (!list.get(0).getIsActive()) {
                                from = "disable";
                                customDialog.showDialog(getActivity(), "با این تلفن نمیتوانید وارد نرم افزار شوید.", false, "خروج", "", false, false, true);
                                return;
                            }
                        }
                    }
                    else {

                        from = "disable";
                        customDialog.showDialog(getActivity(), "ورود به حساب کاربری ممکن نیست.", false, "خروج", "", false, false, true);
                        return;
                    }


                } else {
                    clearData();
                }

                checkUpdate();
                //endregion Information Account From Server


            });
            myViewModel.getResultgetApp().observe(getViewLifecycleOwner(), result -> {

                if (result == null)
                    return;

                myViewModel.getResultgetApp().setValue(null);
                if (result.size() > 0) {
                    Util.APPLICATION_ID = result.get(0).getAppId();
                    if (result.get(0).getIsActive()) {

                        newVersion = result.get(0).getVersion();
                        title = result.get(0).getUpdateTitle();
                        message = result.get(0).getUpdateDesc();
                        linkUpdate = result.get(0).getLink();
                        sharedPreferences.edit().putString("link_update", linkUpdate).apply();
                        forced = result.get(0).getForced();
                        if (account != null)
                            myViewModel.getCustomerFromServer(account.getM());
                        else
                            checkUpdate();


                    } else {
                        binding.tvError.setText("اپلیکیشن غیر فعال شده است.");
                        binding.tvError.setVisibility(View.VISIBLE);
                    }

                }

            });
            myViewModel.getApp(Util.APPLICATIONCODE);*/





        //region Get Company From Server After 1 Minute
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                    myViewModel.getCompany("");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        //endregion Get Company From Server After 1 Minute


        //region Get Result From The Server
        myViewModel.getResultCompany().observe(getViewLifecycleOwner(), result -> {


            if (result == null) return;

            myViewModel.getResultCompany().setValue(null);

            if (result.size() > 0) {

                //region Find A Company With An Application PackageName
                CollectionUtils.filter(result, i -> i.getInskId() != null && i.getInskId().equals(packageName));
                if (result.size() == 1) {
                    //region Save Company Information In Database For Use For Request From Server And Other Items
                    Company.deleteAll(Company.class);
                    Company.saveInTx(result.get(0));
                    //endregion Save Company Information In Database For Use For Request From Server And Other Items

                }
                //endregion Find A Company With An Application PackageName


                //region Can't Find A Company With The PackageName
                else {
                    binding.txtErrorr.setText("اطلاعات شرکت در سرور وجود ندارد.");
                    binding.animationView.setVisibility(View.GONE);
                    binding.btnWarning.setVisibility(View.VISIBLE);
                    return;
                }
                //endregion Can't Find A Company With The PackageName


                //region Request From The Server With The IP Of The Company That Was Found
                Util.PRODUCTION_BASE_URL = "http://" + Select.from(Company.class).first().getIp1() + "/api/REST/";
                sharedPreferences.edit().putBoolean("status", true).apply();
                hostSelectionInterceptor.setHostBaseUrl();
                //endregion Request From The Server With The IP Of The Company That Was Found


                //region When The User Is Logged In
                NavDirections action;
                if (Select.from(Users.class).list().size() > 0) {
                    if (saleinInstance != null)
                        action = SplashScreenFragmentDirections.actionGoToCompanyFragment();
                    else
                        action = SplashScreenFragmentDirections.actionGoToHomeFragment("");

                    Navigation.findNavController(binding.getRoot()).navigate(action);
                }
                //endregion When The User Is Logged In


                //region When The User Is Not Logged In
                else
                    Navigation.findNavController(binding.getRoot()).navigate(R.id.actionGoToLoginFragment);
                //endregion When The User Is Not Logged In


            }
        });

        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {

            if (result == null) return;


            //region Company Information Is Already Save In The Database And Can Be Used To Log In To The Application
            if (Select.from(Company.class).list().size() > 0) {

                //region Request From The Server With The IP Of The Company That Was Registered
                Util.PRODUCTION_BASE_URL = "http://" + Select.from(Company.class).first().getIp1() + "/api/REST/";
                sharedPreferences.edit().putBoolean("status", true).apply();
                hostSelectionInterceptor.setHostBaseUrl();
                //endregion Request From The Server With The IP Of The Company That Was Registered

                //region When The User Is Logged In
                if (Select.from(Users.class).list().size() > 0) {
                    if (saleinInstance != null)
                        Navigation.findNavController(binding.getRoot()).navigate(R.id.actionGoToLoginFragment);

                    else {


                        NavDirections action = SplashScreenFragmentDirections.actionGoToHomeFragment("");
                        Navigation.findNavController(binding.getRoot()).navigate(action);

                    }
                }
                //endregion When The User Is Logged In

                //region When The User Is Not Logged In
                else
                    Navigation.findNavController(binding.getRoot()).navigate(R.id.actionGoToLoginFragment);
                //endregion When The User Is Not Logged In
            }
            //endregion Company Information Is Already Save In The Database And Can Be Used To Log In To The Application


            //region Company Information Is Not Save In The Database
            else {
                binding.animationView.setVisibility(View.GONE);
                binding.btnWarning.setVisibility(View.VISIBLE);
                binding.txtErrorr.setText(result.getName());
            }
            //endregion Company Information Is Not Save In The Database

        });
        //endregion Get Result From The Server


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
    //endregion Custom Method


}
