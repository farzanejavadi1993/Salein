package ir.kitgroup.salein.Fragments.MobileView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import ir.kitgroup.salein.Activities.Classes.LauncherActivity;
import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.DataBase.User;
import ir.kitgroup.salein.Fragments.LauncherOrganizationFragment;
import ir.kitgroup.salein.Fragments.LoginOrganizationFragment;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.Util.Util;
import ir.kitgroup.salein.classes.App;
import ir.kitgroup.salein.databinding.FragmentSplashScreenBinding;

public class SplashScreenFragment extends Fragment {

    private FragmentSplashScreenBinding binding;
    private int imageIconDialog = 0;
    private String title = "";
    private String nameProject = "";

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        binding = FragmentSplashScreenBinding.inflate(getLayoutInflater());
        // region Utilize Animation
        Util.playLottieAnimation("buy.json", binding.animationView);
        Util.playLottieAnimation("890-loading-animation.json", binding.animationView1);
        //endregion Utilize Animation


        try {
            //  PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);

            switch (LauncherActivity.name) {
                case "ir.kitgroup.salein":

                    imageIconDialog = R.drawable.logo1;
                    title = "سالین";

                    break;

                case "ir.kitgroup.saleintop":

                    imageIconDialog = R.drawable.top_png;
                    title = "تاپ کباب";

                    break;


                case "ir.kitgroup.saleinmeat":

                    imageIconDialog = R.drawable.meat_png;
                    title = "گوشت دنیوی";
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        binding.imgLogo.setImageResource(imageIconDialog);
        binding.tvTitle.setText(title);

        Thread thread = new Thread(() -> {
            try {

                Thread.sleep(3000);

                FragmentTransaction replaceFragment;
                if (App.mode == 2) {
                    Bundle bundle = new Bundle();


                    if (Select.from(Account.class).list().size() > 0) {
                        if (LauncherActivity.name.equals("ir.kitgroup.salein"))
                        replaceFragment = getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new StoriesFragment(), "StoriesFragment");
                        else {
                            bundle.putString("Ord_TYPE", "");
                            bundle.putString("Tbl_GUID", "");
                            bundle.putString("Inv_GUID", "");
                            MainOrderMobileFragment mainOrderMobileFragment = new MainOrderMobileFragment();
                            mainOrderMobileFragment.setArguments(bundle);
                            replaceFragment = Objects.requireNonNull(getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment"));
                        }


                    } else {
                        replaceFragment = getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new LoginClientFragment());

                    }
                } else {
                    if (Select.from(User.class).list().get(0).CheckUser) {

                        replaceFragment = getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new LauncherOrganizationFragment(), "LauncherFragment");
                    } else {

                        replaceFragment = getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new LoginOrganizationFragment());

                    }

                }

                replaceFragment.commit();


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();

        return binding.getRoot();


    }
}
