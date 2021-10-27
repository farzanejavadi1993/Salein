package ir.kitgroup.saleinOrder.Fragments.MobileView;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
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

import ir.kitgroup.saleinOrder.Activities.Classes.LauncherActivity;
import ir.kitgroup.saleinOrder.DataBase.Account;
import ir.kitgroup.saleinOrder.DataBase.User;
import ir.kitgroup.saleinOrder.Fragments.LauncherOrganizationFragment;
import ir.kitgroup.saleinOrder.Fragments.LoginOrganizationFragment;
import ir.kitgroup.saleinOrder.R;
import ir.kitgroup.saleinOrder.classes.App;
import ir.kitgroup.saleinOrder.databinding.FragmentSplashScreenBinding;

public class SplashScreenFragment extends Fragment {

    public static int width = 0;
    public static int height = 0;
    public static double screenInches = 0.0;
    private String title = "SaleIn Order";
    private String description = "اپلیکیشن سفارش گیر مشتریان سالین";
    private FragmentSplashScreenBinding binding;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        binding = FragmentSplashScreenBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        getSizeMobile();
        //region Set Icon And Title
        try {
            switch (LauncherActivity.name) {
                case "ir.kitgroup.salein":

                    title = "سالین دمو";
                    description = "سالین دمو ، راهنمای استفاده از اپلیکیشن";
                    break;

                case "ir.kitgroup.saleintop":

                    title = "تاپ کباب";
                    description="عرضه کننده بهترین غذاها";

                    break;


                case "ir.kitgroup.saleinmeat":

                    title = "گوشت دنیوی";
                    description="عرضه کننده انواع گوشت";
                    break;
                case "ir.kitgroup.saleinnoon":

                    title = "کافه نون";
                    break;
            }
        }catch (Exception ignore){

        }


        try {
            String appVersion = appVersion();
            binding.tvversion.setText(" نسخه " + appVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Glide.with(this).load(Uri.parse("file:///android_asset/loading3.gif")).into(binding.animationView);
        binding.tvTitle.setText(title);
        binding.tvDescription.setText(description);
        //endregion Set Icon And Title

        //region Thread

        Thread thread = new Thread(() -> {
            try {

                Thread.sleep(2000);

                FragmentTransaction replaceFragment;

                //regionClient Application
                if (App.mode == 2) {

                    //region Account Is Login & Register
                    if (Select.from(Account.class).list().size() > 0) {

                        //regionShow All Company
                        if (LauncherActivity.name.equals("ir.kitgroup.salein"))
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

    public String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        return pInfo.versionName;
    }
    public void getSizeMobile() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        double x = Math.pow(width / dm.xdpi, 2);
        double y = Math.pow(height / dm.ydpi, 2);
        screenInches = Math.sqrt(x + y);


    }
}
