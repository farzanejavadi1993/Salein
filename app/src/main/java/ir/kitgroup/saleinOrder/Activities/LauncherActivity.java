package ir.kitgroup.saleinOrder.Activities;

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
import com.orm.query.Select;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.saleinOrder.DataBase.User;
import ir.kitgroup.saleinOrder.Fragments.LauncherOrganizationFragment;
import ir.kitgroup.saleinOrder.Fragments.LoginOrganizationFragment;
import ir.kitgroup.saleinOrder.Fragments.SplashScreenFragment;

import ir.kitgroup.saleinOrder.Fragments.MainOrderMobileFragment;
import ir.kitgroup.saleinOrder.R;
import ir.kitgroup.saleinOrder.classes.Util;
import ir.kitgroup.saleinOrder.databinding.ActivityLauncherBinding;
import ir.kitgroup.saleinOrder.models.Company;


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
        Util.ScreenSize(this);

        //region Set Layout to LauncherActivity class
        ActivityLauncherBinding binding = ActivityLauncherBinding.inflate(getLayoutInflater());
        View viewRoot = binding.getRoot();
        setContentView(viewRoot);
        //endregion Set Layout to LauncherActivity class


        if (company.mode==2){
            //region Call SplashScreenFragment
            FragmentTransaction addFragment = getSupportFragmentManager().beginTransaction().add(R.id.frame_main, new SplashScreenFragment());
            addFragment.commit();
            //endregion Call SplashScreenFragment
        }else {
            //When User Is Login
            FragmentTransaction replaceFragment = null;
            if (Select.from(User.class).list().size()>0) {
                replaceFragment = getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new LauncherOrganizationFragment(), "LauncherFragment");
            }

            //When User Is Not Login
            else {

                replaceFragment = getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new LoginOrganizationFragment());

            }
            replaceFragment.commit();


        }



        //region Cast ExitDialog


        ExitDialog = new Dialog(this);
        ExitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ExitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ExitDialog.setContentView(R.layout.custom_dialog);
        ExitDialog.setCancelable(false);

        messageTextExitDialog = ExitDialog.findViewById(R.id.tv_message);

        int imageIconDialog = company.imageLogo;
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


        else if (company.mode ==1 &&
                getSupportFragmentManager().getBackStackEntryAt(size - 1).getName().equals("MainOrderMobileF")
        ){
            Fragment fragment = LauncherActivity.this.getSupportFragmentManager().findFragmentByTag("LauncherFragment");
            if (fragment instanceof LauncherOrganizationFragment) {
                LauncherOrganizationFragment fgf = (LauncherOrganizationFragment) fragment;
                fgf.refreshAdapter();
            }
            getSupportFragmentManager().popBackStack();
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