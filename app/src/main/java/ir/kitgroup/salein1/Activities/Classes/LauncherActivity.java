package ir.kitgroup.salein1.Activities.Classes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.TextView;


import com.google.android.material.button.MaterialButton;
import com.mapbox.mapboxsdk.Mapbox;
import com.orm.query.Select;

import java.util.Objects;

import ir.kitgroup.salein1.classes.App;
import ir.kitgroup.salein1.DataBase.Account;

import ir.kitgroup.salein1.DataBase.User;
import ir.kitgroup.salein1.Fragments.MobileView.MainOrderMobileFragment;
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

    private Dialog dialog;
    //endregion Parameter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        ActivityLauncherBinding binding = ActivityLauncherBinding.inflate(getLayoutInflater());
        View viewRoot = binding.getRoot();
        setContentView(viewRoot);


        getSizeMobile();

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);

        TextView textExit = dialog.findViewById(R.id.tv_message);
        textExit.setText("اطلاعات ذخیره نشده است.خارج می شوید؟");
        MaterialButton btnOk = dialog.findViewById(R.id.btn_ok);
        MaterialButton btnNo = dialog.findViewById(R.id.btn_cancel);
        btnNo.setOnClickListener(v -> dialog.dismiss());
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
//            if (App.mode==1)
//                getSupportFragmentManager().popBackStack();
//            else
                finish();

        });



        FragmentTransaction replaceFragment;
        if (App.mode == 2) {
            if (Select.from(Account.class).list().size() > 0) {

                Bundle bundle=new Bundle();
                bundle.putString("Ord_TYPE","");
                bundle.putString("Tbl_GUID","");
                bundle.putString("Inv_GUID","");
                MainOrderMobileFragment mainOrderMobileFragment=new MainOrderMobileFragment();
                mainOrderMobileFragment.setArguments(bundle);
                replaceFragment = Objects.requireNonNull(getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, mainOrderMobileFragment,"MainOrderMobileFragment"));

            } else {

                replaceFragment = getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new LoginClientFragment());

            }

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


    @Override
    public void onBackPressed() {
        final int size = getSupportFragmentManager().getBackStackEntryCount();
        if (size==0){
            dialog.show();
        }
        else if (
                getSupportFragmentManager().getBackStackEntryAt(size - 1).getName().equals("InVoiceDetailF") ||
                getSupportFragmentManager().getBackStackEntryAt(size - 1).getName().equals("ProfileF")

        ) {
            dialog.show();
        }
        else
            getSupportFragmentManager().popBackStack();
    }
}