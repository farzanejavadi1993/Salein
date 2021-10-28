package ir.kitgroup.salein.Fragments.MobileView;

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


import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.DataBase.User;
import ir.kitgroup.salein.Fragments.LauncherOrganizationFragment;
import ir.kitgroup.salein.Fragments.LoginOrganizationFragment;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.Util.Util;

import ir.kitgroup.salein.databinding.FragmentSplashScreenBinding;

public class SplashScreenFragment extends Fragment {



    //region Parameter
    private FragmentSplashScreenBinding binding;
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




        Glide.with(this).load(Uri.parse("file:///android_asset/loading3.gif")).into(binding.animationView);
        binding.tvTitle.setText(Util.getUser(getActivity()).title);
        binding.tvDescription.setText(Util.getUser(getActivity()).Description);



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


                FragmentTransaction replaceFragment;



                //regionClient Application
                if (Util.getUser(getActivity()).mode == 2) {
                    //region Account Is Login & Register
                    if (Select.from(Account.class).list().size() > 0) {

                        //regionShow All Company
                        if (Util.getUser(getActivity()).namePackage.equals("ir.kitgroup.salein"))
                            replaceFragment = getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, new StoriesFragment(), "StoriesFragment");


                        //endregionShow All Company

                            //region Go To MainOrderFragment Because Account Is Register
                        else {
                            Bundle bundle = new Bundle();
                            bundle.putString("Ord_TYPE", "");
                            bundle.putString("Tbl_GUID", "");
                            bundle.putString("Inv_GUID", "");
                            MainOrderMobileFragment mainOrderMobileFragment = new MainOrderMobileFragment();
                            mainOrderMobileFragment.setArguments(bundle);
                            replaceFragment = requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment");
                        }
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




                //regionOrganization Application
                else {
                    //When User Is Login

                    if (Select.from(User.class).list().size()>0 && Select.from(User.class).list().get(0).CheckUser) {
                        replaceFragment = getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new LauncherOrganizationFragment(), "LauncherFragment");
                    }

                    //When User Is Not Login
                    else {

                        replaceFragment = getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new LoginOrganizationFragment());

                    }

                }
                //endregionOrganization Application

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
