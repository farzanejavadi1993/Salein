package ir.kitgroup.salein.Activities.Classes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Dialog;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;


import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.material.button.MaterialButton;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.salein.DataBase.User;
import ir.kitgroup.salein.Fragments.LauncherOrganizationFragment;
import ir.kitgroup.salein.Fragments.MobileView.SplashScreenFragment;

import ir.kitgroup.salein.Fragments.MobileView.MainOrderMobileFragment;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.Util.Util;
import ir.kitgroup.salein.classes.App;
import ir.kitgroup.salein.databinding.ActivityLauncherBinding;
import ir.kitgroup.salein.models.Company;


@AndroidEntryPoint
public class LauncherActivity extends AppCompatActivity {

    //region Parameter

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    Company company;


    //region Parameter Dialog
    private Dialog ExitDialog;
    private TextView messageTextExitDialog;
    //endregion Parameter Dialog



    private Boolean refreshApplication = false;

    //endregion Parameter





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        refreshApplication =false;

        //region Set Layout to LauncherActivity class
        ActivityLauncherBinding binding = ActivityLauncherBinding.inflate(getLayoutInflater());
        View viewRoot = binding.getRoot();
        setContentView(viewRoot);
        //endregion Set Layout to LauncherActivity class


        //region Call SplashScreenFragment
        FragmentTransaction addFragment = getSupportFragmentManager().beginTransaction().add(R.id.frame_main, new SplashScreenFragment());
        addFragment.commit();
        //endregion Call SplashScreenFragment


        //region Cast ExitDialog


        ExitDialog = new Dialog(this);
        ExitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ExitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ExitDialog.setContentView(R.layout.custom_dialog);
        ExitDialog.setCancelable(false);

        messageTextExitDialog = ExitDialog.findViewById(R.id.tv_message);

        int imageIconDialog = company.image;
        ImageView imageIconExitDialog = ExitDialog.findViewById(R.id.iv_icon);
        imageIconExitDialog.setImageResource(imageIconDialog);

        MaterialButton btnYesExitDialog = ExitDialog.findViewById(R.id.btn_ok);
        MaterialButton btnNoExitDialog = ExitDialog.findViewById(R.id.btn_cancel);


        //region action ExitDialog's buttons
        btnNoExitDialog.setOnClickListener(v -> ExitDialog.dismiss());
        btnYesExitDialog.setOnClickListener(v -> {
            ExitDialog.dismiss();

            finish();

        });
        //endregion action ExitDialog's buttons


        //endregion Cast ExitDialog


    }




    //region Override Method
    @Override
    public void onBackPressed() {


        final int size = getSupportFragmentManager().getBackStackEntryCount();


        if (size == 0) {
            messageTextExitDialog.setText("آیا از برنامه خارج می شوید؟");
            ExitDialog.show();

        }


        else if (Util.getUser(LauncherActivity.this).mode ==1 &&
                getSupportFragmentManager().getBackStackEntryAt(size - 1).getName().equals("MainOrderMobileF")
        ){
            Fragment fragment = LauncherActivity.this.getSupportFragmentManager().findFragmentByTag("LauncherFragment");
            if (fragment instanceof LauncherOrganizationFragment) {
                LauncherOrganizationFragment fgf = (LauncherOrganizationFragment) fragment;
                fgf.refreshAdapter();
            }
            getSupportFragmentManager().popBackStack();
        }
        else if (Util.getUser(LauncherActivity.this).namePackage!=null && Util.getUser(LauncherActivity.this).namePackage.equals("ir.kitgroup.salein") &&getSupportFragmentManager().getBackStackEntryAt(size - 1).getName().equals("SettingF")

        ) {
            for (int i=0;i<size;i++){
                getSupportFragmentManager().popBackStack();
            }

        }
        else if (getSupportFragmentManager().getBackStackEntryAt(size - 1).getName().equals("SettingF")

        ) {
            messageTextExitDialog.setText("آیا از برنامه خارج می شوید؟");
            ExitDialog.show();
        } else if (getSupportFragmentManager().getBackStackEntryAt(size - 1).getName().equals("InVoiceDetailF")) {

            Fragment fragment = LauncherActivity.this.getSupportFragmentManager().findFragmentByTag("MainOrderMobileFragment");
            if (fragment instanceof MainOrderMobileFragment) {
                MainOrderMobileFragment fgf = (MainOrderMobileFragment) fragment;
                fgf.setHomeBottomBar();
            }



        }

        else
            getSupportFragmentManager().popBackStack();
    }


    @Override
    public void onResume() {
        super.onResume();

        if (refreshApplication) {
            String dateOld = sharedPreferences.getString("timeOld", "");
            Date oldDate;
            Date dateNow = Calendar.getInstance().getTime();
            if (!dateOld.equals("")) {
                oldDate = stringToDate(dateOld);
            } else {
                oldDate = dateNow;
            }


            long diff = dateNow.getTime() - oldDate.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;

            if (minutes >=2) {

                finish();
                startActivity(getIntent());
            }
            refreshApplication =false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (!refreshApplication) {
            refreshApplication = true;
            Date dateOld = Calendar.getInstance().getTime();
            @SuppressLint("SimpleDateFormat") DateFormat dateFormats =
                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            sharedPreferences.edit().putString("timeOld", dateFormats.format(dateOld)).apply();
        }
    }
    //endregion Override Method




    //region Custom  Method
    private Date stringToDate(String aDate) {

        if (aDate == null) return null;
        ParsePosition pos = new ParsePosition(0);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpledateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return simpledateformat.parse(aDate, pos);

    }
    public   String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        return pInfo.versionName;
    }
    //endregion Custom Method

}