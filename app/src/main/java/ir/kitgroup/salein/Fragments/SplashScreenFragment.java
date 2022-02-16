package ir.kitgroup.salein.Fragments;

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
import es.dmoral.toasty.Toasty;
import ir.kitgroup.salein.Connect.MyViewModel;
import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.R;

import ir.kitgroup.salein.classes.BuildConfig1;
import ir.kitgroup.salein.classes.HostSelectionInterceptor;
import ir.kitgroup.salein.databinding.FragmentSplashScreenBinding;
import ir.kitgroup.salein.DataBase.Company;
import ir.kitgroup.salein.models.Config;
@AndroidEntryPoint
public class SplashScreenFragment extends Fragment {


    //region Parameter
    @Inject
    Config config;

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

        Company company = Select.from(Company.class).first();

        sharedPreferences.edit().putBoolean("status", false).apply();
        hostSelectionInterceptor.setHostBaseUrl();

        if (config.INSKU_ID.equals("ir.kitgroup.saleinmeat")) {
            try {
                Glide.with(this).asGif().load(Uri.parse("file:///android_asset/donyavi.gif")).into(binding.animationView);
                binding.mainBackground.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
            } catch (Exception ignored) {
            }
        } else
            Glide.with(this).load(Uri.parse("file:///android_asset/loading3.gif")).into(binding.animationView);


        String Title = company != null && company.DESC != null ? company.DESC : "";
        binding.tvTitle.setText(company != null && company.N != null && !config.INSKU_ID.equals("ir.kitgroup.salein") ? company.N : config.N);
        binding.tvDescription.setText(company != null && company.DESC != null && !config.INSKU_ID.equals("ir.kitgroup.salein") ? Title : config.DESC);

        try {
            binding.tvversion.setText(" نسخه " + appVersion());
            appId = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
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
            binding.animationView1.setImageResource(R.drawable.warning);
            binding.animationView1.setVisibility(View.VISIBLE);
            binding.animationView.setVisibility(View.GONE);
            binding.lnrError.setVisibility(View.VISIBLE);
            if (result == null) return;
            Toasty.warning(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
            myViewModel.getResultMessage().setValue(null);

        });


        myViewModel.getResultCompany().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;

            myViewModel.getResultCompany().setValue(null);
            if (result.size()>0){


                CollectionUtils.filter(result, i -> i.INSK_ID != null && i.INSK_ID.equals(appId.packageName));

                if (result.size() == 1) {
                    Company.deleteAll(Company.class);
                    Company.saveInTx(result.get(0));
                } else {
                   Toast.makeText(getActivity(), "اطلاعات شرکت در سرور وجود ندارد", Toast.LENGTH_SHORT).show();
                   return;
                }
                BuildConfig1.PRODUCTION_BASE_URL = "http://" + Select.from(Company.class).first().IP1 + "/api/REST/";
                sharedPreferences.edit().putBoolean("status", true).apply();
                hostSelectionInterceptor.setHostBaseUrl();

                FragmentTransaction addFragment;
                //region Account Is Login & Register
                if (Select.from(Account.class).list().size() > 0) {
                    if (config.INSKU_ID.equals("ir.kitgroup.salein"))
                        addFragment = getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new StoriesFragment(), "StoriesFragment");
                    else {
                        Bundle bundleMainOrder = new Bundle();
                        bundleMainOrder.putString("Inv_GUID", "");
                        bundleMainOrder.putString("Tbl_GUID", "");
                        bundleMainOrder.putString("Ord_TYPE", "");
                        MainOrderFragment mainOrderFragment = new MainOrderFragment();
                        mainOrderFragment.setArguments(bundleMainOrder);
                        addFragment = getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mainOrderFragment, "MainOrderFragment");
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
    public String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        return pInfo.versionName;
    }





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


}
