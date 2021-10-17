package ir.kitgroup.saleinOrder.Activities.Classes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.material.button.MaterialButton;

import ir.kitgroup.saleinOrder.Fragments.LauncherOrganizationFragment;
import ir.kitgroup.saleinOrder.Fragments.MobileView.SplashScreenFragment;

import ir.kitgroup.saleinOrder.Fragments.MobileView.MainOrderMobileFragment;
import ir.kitgroup.saleinOrder.R;
import ir.kitgroup.saleinOrder.classes.App;
import ir.kitgroup.saleinOrder.databinding.ActivityLauncherBinding;

public class LauncherActivity extends AppCompatActivity {

    //region Parameter


    private Dialog dialog;
    private TextView textExit;
    private ImageView ivIcon;
    private int imageIconDialog;
    public static Typeface iranSansBold;
    public static String name;
    public  static    String namePackage;


    private int type = 0;
    //endregion Parameter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        iranSansBold = Typeface.createFromAsset(getAssets(), "iransans.ttf");

        ActivityLauncherBinding binding = ActivityLauncherBinding.inflate(getLayoutInflater());
        View viewRoot = binding.getRoot();
        setContentView(viewRoot);


        FragmentTransaction addFragment = getSupportFragmentManager().beginTransaction().add(R.id.frame_main, new SplashScreenFragment());
        addFragment.commit();




        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

            switch (pInfo.packageName) {
                case "ir.kitgroup.salein":

                    imageIconDialog = R.drawable.saleinicon128;
                    name = "ir.kitgroup.salein";
                    namePackage = "ir.kitgroup.salein";

                    break;

                case "ir.kitgroup.saleintop":

                    imageIconDialog = R.drawable.top_png;
                    name = "ir.kitgroup.saleintop";
                    namePackage = "ir.kitgroup.saleintop";

                    break;


                case "ir.kitgroup.saleinmeat":

                    imageIconDialog = R.drawable.meat_png;
                    name = "ir.kitgroup.saleinmeat";
                    namePackage = "ir.kitgroup.saleinmeat";

                    break;

                case "ir.kitgroup.saleinnoon":

                    imageIconDialog = R.drawable.noon;
                    name = "ir.kitgroup.saleinnoon";
                    namePackage = "ir.kitgroup.saleinnoon";

                    break;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);

        textExit = dialog.findViewById(R.id.tv_message);
        ivIcon = dialog.findViewById(R.id.iv_icon);
        ivIcon.setImageResource(imageIconDialog);

        MaterialButton btnOk = dialog.findViewById(R.id.btn_ok);
        MaterialButton btnNo = dialog.findViewById(R.id.btn_cancel);
        btnNo.setOnClickListener(v -> {
            dialog.dismiss();

        });
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();

            finish();

        });


    }




    @Override
    public void onBackPressed() {


        final int size = getSupportFragmentManager().getBackStackEntryCount();
        if (size == 0) {
            textExit.setText("آیا از برنامه خارج می شوید؟");
            type = 0;
            dialog.show();

        }
       else if (getSupportFragmentManager().getBackStackEntryAt(size - 1).getName().equals("SettingF")

        ) {
            textExit.setText("آیا از برنامه خارج می شوید؟");
            type = 0;
            dialog.show();
        } else if (getSupportFragmentManager().getBackStackEntryAt(size - 1).getName().equals("InVoiceDetailF")) {

            Fragment fragment = LauncherActivity.this.getSupportFragmentManager().findFragmentByTag("MainOrderMobileFragment");
            if (fragment instanceof MainOrderMobileFragment) {
                MainOrderMobileFragment fgf = (MainOrderMobileFragment) fragment;
                fgf.setHomeBottomBar();
            }



        }else if (App.mode==1 &&getSupportFragmentManager().getBackStackEntryAt(size - 1).getName().equals("MainOrderMobileF")){
            Fragment fragment = LauncherActivity.this.getSupportFragmentManager().findFragmentByTag("LauncherFragment");
            if (fragment instanceof LauncherOrganizationFragment) {
                LauncherOrganizationFragment fgf = (LauncherOrganizationFragment) fragment;
                fgf.refreshAdapter();
            }
            getSupportFragmentManager().popBackStack();
        }

        else
            getSupportFragmentManager().popBackStack();
    }


    public   String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        return pInfo.versionName;
    }
}