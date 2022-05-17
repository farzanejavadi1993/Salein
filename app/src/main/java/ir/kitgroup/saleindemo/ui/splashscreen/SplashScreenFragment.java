package ir.kitgroup.saleindemo.ui.splashscreen;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.saleindemo.Connect.MyViewModel;
import ir.kitgroup.saleindemo.DataBase.Account;
import ir.kitgroup.saleindemo.classes.Util;
import ir.kitgroup.saleindemo.databinding.FragmentSplashScreenBinding;
import ir.kitgroup.saleindemo.ui.logins.LoginClientFragment;
import ir.kitgroup.saleindemo.ui.launcher.homeItem.MainOrderFragment;
import ir.kitgroup.saleindemo.ui.companies.StoriesFragment;
import ir.kitgroup.saleindemo.R;

import ir.kitgroup.saleindemo.classes.HostSelectionInterceptor;

import ir.kitgroup.saleindemo.DataBase.Company;



@AndroidEntryPoint
public class SplashScreenFragment extends Fragment {


    //region Parameter


    @Inject
    HostSelectionInterceptor hostSelectionInterceptor;

    @Inject
    SharedPreferences sharedPreferences;

    private MyViewModel myViewModel;
    private FragmentSplashScreenBinding binding;
    private PackageInfo appId;
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

        try {



            sharedPreferences.edit().putBoolean("status", false).apply();
            hostSelectionInterceptor.setHostBaseUrl();


            if (Util.getPackageName(getActivity()).equals("ir.kitgroup.saleinmeat")) {
                try {
                    Glide.with(this).asGif().load(Uri.parse("file:///android_asset/donyavi.gif")).into(binding.animationView);
                    binding.mainBackground.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                } catch (Exception ignored) {
                }
            } else if (Util.getPackageName(getActivity()).equals("ir.kitgroup.salein")){
                Glide.with(this).load(Uri.parse("file:///android_asset/loading3.gif")).into(binding.animationView);
                Company.deleteAll(Company.class);

            }else {
                Glide.with(this).load(Uri.parse("file:///android_asset/loading3.gif")).into(binding.animationView);
            }

            Company company = Select.from(Company.class).first();
            String description = company != null && company.DESC != null ? company.DESC : "";
            String title = company != null && company.N != null ? company.N : "";
            binding.tvTitle.setText(title);
            binding.tvDescription.setText(description);

            try {
                binding.tvversion.setText(" نسخه " + appVersion());
                appId = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    //endregion Override Method

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

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

        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;
         //   myViewModel.getResultMessage().setValue(null);
            if (Select.from(Company.class).list().size() > 0) {
                Util.PRODUCTION_BASE_URL = "http://" + Select.from(Company.class).first().IP1 + "/api/REST/";
                sharedPreferences.edit().putBoolean("status", true).apply();
                hostSelectionInterceptor.setHostBaseUrl();
                FragmentTransaction addFragment;
                //region Account Is Login & Register
                if (Select.from(Account.class).list().size() > 0) {
                    if (Util.getPackageName(getActivity()).equals("ir.kitgroup.salein"))
                        addFragment = getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new StoriesFragment(), "StoriesFragment");
                    else {
                        Bundle bundleMainOrder = new Bundle();
                        bundleMainOrder.putString("Inv_GUID", "");
                        bundleMainOrder.putString("Tbl_GUID", "");
                        bundleMainOrder.putString("Ord_TYPE", "");
                        MainOrderFragment mainOrderFragment = new MainOrderFragment();
                        mainOrderFragment.setArguments(bundleMainOrder);
                        addFragment = getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mainOrderFragment, "MainOrderFragment")
                        //.addToBackStack("MainOrderF")
                        ;
                    }
                }
                //endregion Account Is Login & Register
                else {
                    addFragment = getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new LoginClientFragment(), "LoginClientFragment");
                }
                addFragment.commit();


            } else {
                binding.animationView1.setImageResource(R.drawable.warning);
                binding.animationView1.setVisibility(View.VISIBLE);
                binding.animationView.setVisibility(View.GONE);
                binding.lnrError.setVisibility(View.VISIBLE);
                binding.txtErrorr.setText(result.getName());
                Util.PRODUCTION_BASE_URL = "";
                sharedPreferences.edit().putBoolean("status", false).apply();
                hostSelectionInterceptor.setHostBaseUrl();
            }
        });


        myViewModel.getResultCompany().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;

            myViewModel.getResultCompany().setValue(null);
            if (result.size() > 0) {
                CollectionUtils.filter(result, i -> i.INSK_ID != null && i.INSK_ID.equals(appId.packageName));
                if (result.size() == 1) {
                    Company.deleteAll(Company.class);
                    Company.saveInTx(result.get(0));
                } else {
                    binding.txtErrorr.setText("اطلاعات شرکت در سرور وجود ندارد.");
                    binding.animationView.setVisibility(View.GONE);
                    binding.animationView1.setVisibility(View.VISIBLE);
                    binding.lnrError.setVisibility(View.VISIBLE);
                    return;
                }
                Util.PRODUCTION_BASE_URL = "http://" + Select.from(Company.class).first().IP1 + "/api/REST/";
                sharedPreferences.edit().putBoolean("status", true).apply();
                hostSelectionInterceptor.setHostBaseUrl();

                FragmentTransaction addFragment;
                //region Account Is Login & Register
                if (Select.from(Account.class).list().size() > 0) {
                    if (Util.getPackageName(getActivity()).equals("ir.kitgroup.salein"))
                        addFragment = getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new StoriesFragment(), "StoriesFragment");
                    else {
                        Bundle bundleMainOrder = new Bundle();
                        bundleMainOrder.putString("Inv_GUID", "");
                        bundleMainOrder.putString("Tbl_GUID", "");
                        bundleMainOrder.putString("Ord_TYPE", "");
                        MainOrderFragment mainOrderFragment = new MainOrderFragment();
                        mainOrderFragment.setArguments(bundleMainOrder);
                        addFragment = getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mainOrderFragment, "MainOrderFragment")
                        //  .addToBackStack("MainOrderF")
                        ;
                    }
                }
                //endregion Account Is Login & Register
                else {
                    addFragment = getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new LoginClientFragment(), "LoginClientFragment");
                }
                addFragment.commit();
            }
        });


        binding.lnrError.setOnClickListener(v -> {

            binding.animationView.setVisibility(View.VISIBLE);
            binding.animationView1.setVisibility(View.GONE);
            binding.lnrError.setVisibility(View.GONE);
            myViewModel.getCompany("");
        });


    }

    //region Custom Method


    //endregion Custom Method


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;


    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        return pInfo.versionName;
    }



}
