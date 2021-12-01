package ir.kitgroup.saleinOrder.Fragments;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;


import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.saleinOrder.DataBase.Account;
import ir.kitgroup.saleinOrder.R;

import ir.kitgroup.saleinOrder.databinding.FragmentSplashScreenBinding;
import ir.kitgroup.saleinOrder.DataBase.Company;

@AndroidEntryPoint
public class SplashScreenFragment extends Fragment {


    //region Parameter
    private FragmentSplashScreenBinding binding;
    //endregion Parameter

    @Inject
    Company company;


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


        Glide.with(this).load(Uri.parse("file:///android_asset/loading3.gif")).into(binding.animationView);
        binding.tvTitle.setText(company.N);
        binding.tvDescription.setText(company.DESC);


        try {
            String appVersion = appVersion();
            binding.tvversion.setText(" نسخه " + appVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        //region Thread

        Thread thread = new Thread(() -> {
            try {

                Thread.sleep(2000);


                FragmentTransaction addFragment = null;


                //regionClient Application
                if (company.mode == 2) {

                    //region Account Is Login & Register

                    if (Select.from(Account.class).list().size() > 0) {

                        if (company.INSK_ID.equals("ir.kitgroup.salein"))
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
                       addFragment= getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new LoginClientFragment(), "LoginClientFragment");
                    }






                }
                //endregionClient Application

                addFragment.commit();


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        //endregion Thread
    }

    //endregion Override Method


    //region Custom Method
    public String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        return pInfo.versionName;
    }
    //endregion Custom Method


}
