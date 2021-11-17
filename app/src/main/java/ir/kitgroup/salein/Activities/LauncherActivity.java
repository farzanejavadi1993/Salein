package ir.kitgroup.salein.Activities;

import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.Toast;


import com.google.android.material.button.MaterialButton;
import com.orm.query.Select;
import com.squareup.leakcanary.LeakCanary;


import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.salein.DataBase.InvoiceDetail;
import ir.kitgroup.salein.DataBase.User;
import ir.kitgroup.salein.Fragments.InVoiceDetailFragment;

import ir.kitgroup.salein.Fragments.LauncherOrganizationFragment;
import ir.kitgroup.salein.Fragments.LoginClientFragment;
import ir.kitgroup.salein.Fragments.LoginOrganizationFragment;

import ir.kitgroup.salein.Fragments.MainOrderFragment;

import ir.kitgroup.salein.Fragments.PaymentMobileFragment;
import ir.kitgroup.salein.Fragments.ProfileFragment;
import ir.kitgroup.salein.Fragments.SearchProductFragment;


import ir.kitgroup.salein.Fragments.SettingFragment;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.classes.Util;
import ir.kitgroup.salein.databinding.ActivityLauncherBinding;
import ir.kitgroup.salein.models.Config;


@AndroidEntryPoint
public class LauncherActivity extends AppCompatActivity {

    //region Parameter


    @Inject
    Config config;

    @Inject
    SharedPreferences sharedPreferences;


    private ActivityLauncherBinding binding;






    //region Parameter Dialog
    private Dialog ExitDialog;
    private TextView messageTextExitDialog;
    private Boolean show;
    //endregion Parameter Dialog


    //endregion Parameter


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





            //region Set Layout to LauncherActivity class
            binding = ActivityLauncherBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            //endregion Set Layout to LauncherActivity class


            Util.ScreenSize(this);





            if (config.mode == 1) {
                //When User Is Login
                FragmentTransaction replaceFragment;
                if (Select.from(User.class).list().size() > 0) {
                    replaceFragment = getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new LauncherOrganizationFragment(), "LauncherFragment");
                }

                //When User Is Not Login
                else {
                    replaceFragment = getSupportFragmentManager().beginTransaction().replace(R.id.frame_launcher, new LoginOrganizationFragment());
                }

                replaceFragment.commit();

            } else {
                getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new LoginClientFragment(), "LoginClientFragment").commit();
            }


            //region Cast ExitDialog
            ExitDialog = new Dialog(this);
            ExitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            ExitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            ExitDialog.setContentView(R.layout.custom_dialog);
            ExitDialog.setCancelable(false);

            messageTextExitDialog = ExitDialog.findViewById(R.id.tv_message);


            ImageView imageIconExitDialog = ExitDialog.findViewById(R.id.iv_icon);
            imageIconExitDialog.setImageResource(config.imageLogo);

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





    /*public void setFistItem() {
        binding.navView.setSelectedItemId(R.id.homee);
    }*/


    //region Override Method
    @Override
    public void onBackPressed() {


        final int size = getSupportFragmentManager().getBackStackEntryCount();
         if (size == 0) {
            messageTextExitDialog.setText("آیا از برنامه خارج می شوید؟");
            ExitDialog.show();
        } else {
           // getVisibilityBottomBar(false);
            getSupportFragmentManager().popBackStack();

        }


    }







    //endregion Override Method




}