package ir.kitgroup.salein1;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.orm.query.Select;

import ir.kitgroup.salein1.Classes.App;
import ir.kitgroup.salein1.DataBase.User;
import ir.kitgroup.salein1.Fragments.Organization.LauncherOrganizationFragment;
import ir.kitgroup.salein1.Fragments.Organization.LoginOrganizationFragment;

public class MainActivity extends AppCompatActivity {

    //region Variable
    public static int width = 0;
    public static int height = 0;
    public static double screenInches = 0.0;

    private Dialog dialog;
    private TextView textExit;
    private MaterialButton btnOk;
    private MaterialButton btnNo;
    private int fontSize = 0;
    //endregion Variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;

        double x = Math.pow(width / dm.xdpi, 2);
        double y = Math.pow(height / dm.ydpi, 2);

        if (screenInches == 0.0)
            screenInches = Math.sqrt(x + y);


        if (screenInches >= 7)
            fontSize = 13;
        else
            fontSize = 12;

//region Cast Variable Dialog

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);

        textExit = dialog.findViewById(R.id.tv_message);
        textExit.setTextSize(fontSize);
        textExit.setText("اطلاعات ذخیره نشده است.خارج می شوید؟");
        btnOk = dialog.findViewById(R.id.btn_ok);
        btnNo = dialog.findViewById(R.id.btn_cancel);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (App.mode==1)
                getSupportFragmentManager().popBackStack();
                else
                    finish();

            }
        });

        //endregion Cast Variable Dialog


        if (Select.from(User.class).list().size() == 0 || (Select.from(User.class).list().size()>0 ) ) {
            LoginOrganizationFragment userFragment = new LoginOrganizationFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.frame_main, userFragment).addToBackStack("UserF").commit();
        } else {
            LauncherOrganizationFragment launcherFragment = new LauncherOrganizationFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.frame_main, launcherFragment, "LauncherFragment").addToBackStack("LauncherF").commit();

        }

    }

    @Override
    public void onBackPressed() {
        final int size = getSupportFragmentManager().getBackStackEntryCount();

        if (getSupportFragmentManager().getBackStackEntryAt(size - 1).getName().equals("OrderF")  ) {

            dialog.show();
        } else if (size == 1 ) {
            finish();
        }
         else if (size == 2 && App.mode==2){
            textExit.setText("آیا از برنامه خارج می شوید؟");
            dialog.show();
        }else {
            getSupportFragmentManager().popBackStack();
        }
    }


}