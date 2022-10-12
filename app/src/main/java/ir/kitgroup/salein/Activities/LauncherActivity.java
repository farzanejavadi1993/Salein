package ir.kitgroup.salein.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.orm.query.Select;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


import ir.kitgroup.salein.DataBase.AppInfo;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.classes.ApplicationInformation;
import ir.kitgroup.salein.databinding.ActivityLauncherBinding;

import ir.kitgroup.salein.classes.Util;


import ir.kitgroup.salein.ui.launcher.homeItem.HomeFragment;


@AndroidEntryPoint
public class LauncherActivity extends AppCompatActivity {
    //region Parameter
    @Inject
    SharedPreferences sharedPreferences;
    private ActivityLauncherBinding binding;
    private NavController navController;
    private Dialog dialog;
    private AppInfo appInfo;


    //endregion Parameter


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityLauncherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initAppInfo();
        if (getIntent() != null && getIntent().getExtras() != null) {
            String company = getIntent().getExtras().getString("companyId");
            sharedPreferences.edit().putString("companyId", Objects.requireNonNullElse(company, "")).apply();
            Toast.makeText(this, Objects.requireNonNullElse(company, ""), Toast.LENGTH_SHORT).show();
        } else
            sharedPreferences.edit().putString("company", "").apply();

        Util.ScreenSize(this);
        navigationHandler();
        castDialog();


    }


    //region Override Method

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBackPressed() {
        switch (navController.getCurrentDestination().getId()) {
            case R.id.HomeFragment:
                if (sharedPreferences.getBoolean("vip", false) || sharedPreferences.getBoolean("discount", false)) {
                    NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().getPrimaryNavigationFragment();
                    FragmentManager fragmentManager = navHostFragment.getChildFragmentManager();
                    Fragment fragment = fragmentManager.getPrimaryNavigationFragment();
                    if (fragment instanceof HomeFragment) {
                        ((HomeFragment) fragment).showData();
                    }
                } else {
                    if (!appInfo.isSalein_main() && navController.getBackQueue().size() == 2)
                        dialog.show();
                    else
                        super.onBackPressed();
                }

                break;

            case R.id.CompanyFragment:
                dialog.show();

                break;
            default:
                super.onBackPressed();
                break;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        // if (sharedPreferences.getBoolean("loginSuccess", false))
        finish();
    }

    //endregion Override Method


    //region Method

    private void initAppInfo() {
        ApplicationInformation applicationInformation = new ApplicationInformation();
         appInfo = applicationInformation.getInformation(getPackageName());
        if (Select.from(AppInfo.class).list().size() == 0)
            AppInfo.saveInTx(appInfo);
    }

    private void navigationHandler() {

        binding.navView.getOrCreateBadge(R.id.InvoiceFragment).clearNumber();
        binding.navView.getOrCreateBadge(R.id.InvoiceFragment).setBackgroundColor(getResources().getColor(R.color.purple_500));
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();

            switch (destination.getId()) {
                case R.id.LoginFragment:
                case R.id.VerifyFragment:
                case R.id.RegisterFragment:
                case R.id.FilterFragment:
                case R.id.InvoiceFragment:
                case R.id.OrderFragment:
                case R.id.CompanyFragment:
                case R.id.ContactUsFragment:
                case R.id.AboutUsFragment:
                case R.id.ProfileFragment:
                case R.id.SearchFragment:
                case R.id.RulesFragment:
                case R.id.PaymentFragment:
                case R.id.AllCompanyFragment:
                case R.id.SplashScreenFragment:
                case R.id.MoreFragment:
                    binding.navView.setVisibility(View.GONE);
                    break;
                case R.id.HomeFragment: {
                    binding.navView.setVisibility(View.VISIBLE);

                }
                break;


            }
        });
        NavigationUI.setupWithNavController(binding.navView, navController);


    }

    public void setShowProfileItem(boolean show) {
        binding.navView.getMenu().getItem(0).setVisible(show);
    }

    public void setCounterOrder(int count) {
        binding.navView.getOrCreateBadge(R.id.InvoiceFragment).setNumber(count);
    }

    public void setClearCounterOrder() {
        binding.navView.getOrCreateBadge(R.id.InvoiceFragment).clearNumber();
    }

    private void castDialog() {
        //region Cast Dialog
        dialog = new Dialog(LauncherActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);
        TextView textExitDialog = dialog.findViewById(R.id.tv_message);
        textExitDialog.setText("آیا مایل به خروج از برنامه می باشید؟");
        MaterialButton btnOk = dialog.findViewById(R.id.btn_ok);
        MaterialButton btnNo = dialog.findViewById(R.id.btn_cancel);
        btnNo.setOnClickListener(v -> dialog.dismiss());
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });
        //endregion Cast Dialog
    }


    public String getPackageName() {
        String packageName = "";
        try {
            packageName = getPackageManager().getPackageInfo(getPackageName(), 0).packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageName;
    }


    //endregion Method

}
