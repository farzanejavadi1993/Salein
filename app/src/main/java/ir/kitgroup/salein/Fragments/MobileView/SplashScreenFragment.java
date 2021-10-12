package ir.kitgroup.salein.Fragments.MobileView;

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

import ir.kitgroup.salein.Activities.Classes.LauncherActivity;
import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.DataBase.User;
import ir.kitgroup.salein.Fragments.LauncherOrganizationFragment;
import ir.kitgroup.salein.Fragments.LoginOrganizationFragment;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.classes.App;
import ir.kitgroup.salein.databinding.FragmentSplashScreenBinding;

public class SplashScreenFragment extends Fragment {

    private int imageIconDialog = 0;
    private String title = "";
    private String description = "";
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

        // region Utilize Animation
      // Util.playLottieAnimation("buygirl.json", binding.animationView);
        //Util.playLottieAnimation("890-loading-animation.json", binding.animationView1);
        //endregion Utilize Animation

        //region Set Icon And Title
        switch (LauncherActivity.name) {
            case "ir.kitgroup.salein":
                imageIconDialog = R.drawable.salein;
                title = "سالین دمو";
                description = "سالین دمو ، راهنمای استفاده از اپلیکیشن";
                break;

            case "ir.kitgroup.saleintop":

                imageIconDialog = R.drawable.top_png;
                title = "تاپ کباب";
                description="عرضه کننده بهترین غذاها";

                break;


            case "ir.kitgroup.saleinmeat":

                imageIconDialog = R.drawable.meat_png;
                title = "گوشت دنیوی";
                description="عرضه کننده انواع گوشت";
                break;
            case "ir.kitgroup.saleinnoon":

                imageIconDialog = R.drawable.noon;
                title = "کافه نون";
                break;
        }

        try {
            String appVersion = appVersion();
            binding.tvversion.setText(" نسخه " + appVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
      // binding.imgLogo.setImageResource(imageIconDialog);
        Glide.with(this).load(Uri.parse("file:///android_asset/loader1.gif")).into(binding.animationView);
        binding.tvTitle.setText(title);
        binding.tvDescription.setText(description);
        //endregion Set Icon And Title

        //region Thread

        Thread thread = new Thread(() -> {
            try {

                Thread.sleep(3000);

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
                    if (Select.from(User.class).list().get(0).CheckUser) {
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
}
