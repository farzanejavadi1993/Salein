package ir.kitgroup.saleindemo.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import com.orm.query.Select;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.saleindemo.DataBase.Company;
import ir.kitgroup.saleindemo.DataBase.InvoiceDetail;
import ir.kitgroup.saleindemo.ui.launcher.invoiceItem.InVoiceDetailFragment;
import ir.kitgroup.saleindemo.ui.organization.LauncherOrganizationFragment;
import ir.kitgroup.saleindemo.ui.organization.LoginOrganizationFragment;
import ir.kitgroup.saleindemo.ui.launcher.homeItem.MainOrderFragment;
import ir.kitgroup.saleindemo.ui.launcher.searchItem.SearchProductFragment;
import ir.kitgroup.saleindemo.ui.launcher.moreItem.SettingFragment;
import ir.kitgroup.saleindemo.ui.splashscreen.SplashScreenFragment;
import ir.kitgroup.saleindemo.R;
import ir.kitgroup.saleindemo.classes.Util;
import ir.kitgroup.saleindemo.classes.HostSelectionInterceptor;

import ir.kitgroup.saleindemo.databinding.ActivityLauncherBinding;


@AndroidEntryPoint
public class LauncherActivity extends AppCompatActivity {
    //region Parameter
    private ActivityLauncherBinding binding;
    //endregion Parameter


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityLauncherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Util.ScreenSize(this);
    }





    //region Override Method

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    @Override
    protected void onStart() {
        super.onStart();
        startActivity(getIntent());
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();

    }

    //endregion Override Method

}
