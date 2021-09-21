package ir.kitgroup.saleinmeat.Fragments.MobileView;

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

import ir.kitgroup.saleinmeat.Activities.Classes.LauncherActivity;
import ir.kitgroup.saleinmeat.DataBase.Account;
import ir.kitgroup.saleinmeat.DataBase.User;
import ir.kitgroup.saleinmeat.Fragments.LauncherOrganizationFragment;
import ir.kitgroup.saleinmeat.Fragments.LoginOrganizationFragment;
import ir.kitgroup.saleinmeat.R;
import ir.kitgroup.saleinmeat.Util.Util;
import ir.kitgroup.saleinmeat.classes.App;
import ir.kitgroup.saleinmeat.databinding.FragmentSplashScreenBinding;

public class SplashScreenFragment extends Fragment {

    private int imageIconDialog = 0;
    private String title = "";
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
        Util.playLottieAnimation("buygirl.json", binding.animationView);
        Util.playLottieAnimation("890-loading-animation.json", binding.animationView1);
        //endregion Utilize Animation

        //region Set Icon And Title
        switch (LauncherActivity.name) {
            case "ir.kitgroup.salein":
                imageIconDialog = R.drawable.salein;
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
        binding.imgLogo.setImageResource(imageIconDialog);
        binding.tvTitle.setText(title);
        //endregion Set Icon And Title

        //region Thread

        Thread thread = new Thread(() -> {
            try {

                Thread.sleep(5000);

                FragmentTransaction replaceFragment;

                //regionClient Application
                if (App.mode == 2) {

                    //region Account Is Login & Register
                    if (Select.from(Account.class).list().size() > 0) {

                        //regionShow All Company
                        if (LauncherActivity.name.equals("ir.kitgroup.salein"))
                            replaceFragment = getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new StoriesFragment(), "StoriesFragment");


                            //endregionShow All Company

                        //region Go To MainOrderFragment Because Account Is Register
                        else {
                            Bundle bundle = new Bundle();
                            bundle.putString("Ord_TYPE", "");
                            bundle.putString("Tbl_GUID", "");
                            bundle.putString("Inv_GUID", "");
                            MainOrderMobileFragment mainOrderMobileFragment = new MainOrderMobileFragment();
                            mainOrderMobileFragment.setArguments(bundle);
                            replaceFragment = Objects.requireNonNull(getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment"));
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
}
