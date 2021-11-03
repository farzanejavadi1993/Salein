package ir.kitgroup.saleinBahraman.Fragments;

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
import ir.kitgroup.saleinBahraman.DataBase.Account;
import ir.kitgroup.saleinBahraman.R;

import ir.kitgroup.saleinBahraman.databinding.FragmentSplashScreenBinding;
import ir.kitgroup.saleinBahraman.models.Company;

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
        binding.tvTitle.setText(company.title);
        binding.tvDescription.setText(company.Description);



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


                FragmentTransaction replaceFragment = null;



                //regionClient Application
                if (company.mode == 2) {
                    //region Account Is Login & Register
                    if (Select.from(Account.class).list().size() > 0) {

                        //regionShow All Company
//                        if (company.namePackage.equals("ir.kitgroup.salein"))
//                            replaceFragment = getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, new StoriesFragment(), "StoriesFragment");


                        //endregionShow All Company

                            //region Go To MainOrderFragment Because Account Is Register
                        //else {
                            Bundle bundle = new Bundle();
                            bundle.putString("Ord_TYPE", "");
                            bundle.putString("Tbl_GUID", "");
                            bundle.putString("Inv_GUID", "");
                            MainOrderMobileFragment mainOrderMobileFragment = new MainOrderMobileFragment();
                            mainOrderMobileFragment.setArguments(bundle);
                            replaceFragment = requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment");
                       // }
                        //endregion Go To MainOrderFragment Because Account Is Register

                    }
                    //endregion Account Is Login & Register


                    //region Account Is Not Login & Register
                    else {

                        replaceFragment = getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new LoginClientFragment());

                    }
                    //endregion Account Is Not Login & Register
                }
                //endregionClient Application

                replaceFragment.commit();


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
