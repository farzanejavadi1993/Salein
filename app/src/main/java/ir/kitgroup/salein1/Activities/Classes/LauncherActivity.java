package ir.kitgroup.salein1.Activities.Classes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;


import com.orm.query.Select;

import ir.kitgroup.salein1.Classes.App;
import ir.kitgroup.salein1.DataBase.User;
import ir.kitgroup.salein1.Fragments.Organization.LauncherOrganizationFragment;
import ir.kitgroup.salein1.Fragments.Client.LoginClient.LoginClientFragment;
import ir.kitgroup.salein1.Fragments.Organization.LoginOrganizationFragment;
import ir.kitgroup.salein1.R;
import ir.kitgroup.salein1.databinding.ActivityLauncherBinding;

public class LauncherActivity extends AppCompatActivity {

    //region Parameter
    public static int width = 0;
    public static int height = 0;
    public static double screenInches = 0.0;
    //endregion Parameter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLauncherBinding binding = ActivityLauncherBinding.inflate(getLayoutInflater());
        View viewRoot = binding.getRoot();
        setContentView(viewRoot);


        getSizeMobile();


        FragmentTransaction replaceFragment;
        if (App.mode == 2) {
            replaceFragment = getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new LoginClientFragment());
        } else {
            if (Select.from(User.class).list().size() > 0) {

                replaceFragment = getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new LauncherOrganizationFragment());
            } else {

                replaceFragment = getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new LoginOrganizationFragment());

            }

        }
        replaceFragment.commit();


    }

    public void getSizeMobile() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        double x = Math.pow(width / dm.xdpi, 2);
        double y = Math.pow(height / dm.ydpi, 2);
        screenInches = Math.sqrt(x + y);


    }


}