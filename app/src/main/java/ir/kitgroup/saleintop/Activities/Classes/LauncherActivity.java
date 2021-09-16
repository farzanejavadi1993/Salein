package ir.kitgroup.saleintop.Activities.Classes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.material.button.MaterialButton;

import com.orm.query.Select;

import java.util.Objects;

import ir.kitgroup.saleintop.classes.App;
import ir.kitgroup.saleintop.DataBase.Account;

import ir.kitgroup.saleintop.DataBase.User;
import ir.kitgroup.saleintop.Fragments.MobileView.MainOrderMobileFragment;
import ir.kitgroup.saleintop.Fragments.LauncherOrganizationFragment;
import ir.kitgroup.saleintop.Fragments.MobileView.LoginClientFragment;
import ir.kitgroup.saleintop.Fragments.LoginOrganizationFragment;
import ir.kitgroup.saleintop.R;
import ir.kitgroup.saleintop.databinding.ActivityLauncherBinding;

public class LauncherActivity extends AppCompatActivity {

    //region Parameter
    public static int width = 0;
    public static int height = 0;
    public static double screenInches = 0.0;

    private Dialog dialog;
    private  TextView textExit;
    private ImageView ivIcon;
    private int imageIconDialog;


    private int type=0;
        //endregion Parameter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





        ActivityLauncherBinding binding = ActivityLauncherBinding.inflate(getLayoutInflater());
        View viewRoot = binding.getRoot();
        setContentView(viewRoot);


        getSizeMobile();




        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

            switch (pInfo.packageName){
                case "ir.kitgroup.salein":

                    imageIconDialog=R.drawable.saleinicon128;

                    break;

                case "ir.kitgroup.saleintop":

                    imageIconDialog=R.drawable.top_png;

                    break;


                case "ir.kitgroup.saleinmeat":

                    imageIconDialog=R.drawable.meat_png;

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
        btnNo.setOnClickListener(v ->{
            dialog.dismiss();

        });
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();

            finish();

        });



        FragmentTransaction replaceFragment;
        if (App.mode == 2) {
            Bundle bundle=new Bundle();
            bundle.putString("Ord_TYPE","");
            bundle.putString("Tbl_GUID","");
            bundle.putString("Inv_GUID","");
            if (Select.from(Account.class).list().size() > 0) {
                MainOrderMobileFragment mainOrderMobileFragment=new MainOrderMobileFragment();
                mainOrderMobileFragment.setArguments(bundle);
                replaceFragment = Objects.requireNonNull(getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, mainOrderMobileFragment,"MainOrderMobileFragment"));

            }
            else {
                replaceFragment = getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new LoginClientFragment());

            }

        }




        else {
            if ( Select.from(User.class).list().get(0).CheckUser ) {

                replaceFragment = getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new LauncherOrganizationFragment(),"LauncherFragment");
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


    @Override
    public void onBackPressed() {



        final int size = getSupportFragmentManager().getBackStackEntryCount();
        if (size==0){
            textExit.setText("آیا از برنامه خارج می شوید؟");
            type=0;
            dialog.show();

        }
        else if (getSupportFragmentManager().getBackStackEntryAt(size - 1).getName().equals("SettingF")

        ) {
            textExit.setText("آیا از برنامه خارج می شوید؟");
          type=0;
            dialog.show();
        }

        else  if (getSupportFragmentManager().getBackStackEntryAt(size - 1).getName().equals("InVoiceDetailF")){


            Fragment fragment = LauncherActivity.this.getSupportFragmentManager().findFragmentByTag("MainOrderMobileFragment");
            if (fragment instanceof MainOrderMobileFragment) {
                MainOrderMobileFragment fgf = (MainOrderMobileFragment) fragment;
                fgf.setHomeBottomBar();
            }
        }

        else
            getSupportFragmentManager().popBackStack();
    }



    public String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        return pInfo.versionName;
    }
}