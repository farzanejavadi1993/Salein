package ir.kitgroup.saleinmeat.Activities.Classes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Dialog;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.preference.PreferenceManager;
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

import ir.kitgroup.saleinmeat.Fragments.LauncherOrganizationFragment;
import ir.kitgroup.saleinmeat.Fragments.MobileView.SplashScreenFragment;

import ir.kitgroup.saleinmeat.Fragments.MobileView.MainOrderMobileFragment;
import ir.kitgroup.saleinmeat.R;
import ir.kitgroup.saleinmeat.classes.App;
import ir.kitgroup.saleinmeat.databinding.ActivityLauncherBinding;

public class LauncherActivity extends AppCompatActivity {

    //region Parameter
    private SharedPreferences sharedPreferences;

    private Dialog dialog;
    private TextView textExit;
    private ImageView ivIcon;
    private int imageIconDialog=R.drawable.saleinorder_png;
    public static Typeface iranSansBold;
    public static String name;
    public  static  String namePackage;


    private int type = 0;
    private Boolean showView = false;
    //endregion Parameter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        showView=false;
        iranSansBold = Typeface.createFromAsset(getAssets(), "iransans.ttf");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


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
        else if (App.mode==1 &&
                getSupportFragmentManager().getBackStackEntryAt(size - 1).getName().equals("MainOrderMobileF")
                        ){
            Fragment fragment = LauncherActivity.this.getSupportFragmentManager().findFragmentByTag("LauncherFragment");
            if (fragment instanceof LauncherOrganizationFragment) {
                LauncherOrganizationFragment fgf = (LauncherOrganizationFragment) fragment;
                fgf.refreshAdapter();
            }
            getSupportFragmentManager().popBackStack();
        }
        else if (LauncherActivity.namePackage.equals("ir.kitgroup.salein") &&getSupportFragmentManager().getBackStackEntryAt(size - 1).getName().equals("SettingF")

        ) {
            for (int i=0;i<size;i++){
                getSupportFragmentManager().popBackStack();
         }

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



        }

        else
            getSupportFragmentManager().popBackStack();
    }


    public   String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        return pInfo.versionName;
    }



    @Override
    public void onResume() {
        super.onResume();

        int p=0;
        if (showView) {
            String dateOld = sharedPreferences.getString("timeOld", "");
            Date oldDate;
            Date dateNow = Calendar.getInstance().getTime();
            if (!dateOld.equals("")) {
                oldDate = stringToDate(dateOld, "dd/MM/yyyy HH:mm:ss");
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
            showView=false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (!showView) {
            showView = true;
            Date dateOld = Calendar.getInstance().getTime();
            @SuppressLint("SimpleDateFormat") DateFormat dateFormats =
                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            sharedPreferences.edit().putString("timeOld", dateFormats.format(dateOld)).apply();
        }
    }


    private Date stringToDate(String aDate, String aFormat) {

        if (aDate == null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;

    }
}