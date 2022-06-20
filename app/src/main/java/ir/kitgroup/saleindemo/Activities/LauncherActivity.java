package ir.kitgroup.saleindemo.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
;
import ir.kitgroup.saleindemo.R;
import ir.kitgroup.saleindemo.classes.Util;


import ir.kitgroup.saleindemo.ui.launcher.homeItem.HomeFragment;
import ir.kitgroup.saleindemo.databinding.ActivityLauncherBinding;


@AndroidEntryPoint
public class LauncherActivity extends AppCompatActivity {
    //region Parameter
    @Inject
    SharedPreferences sharedPreferences;
    private ActivityLauncherBinding binding;
    private NavController navController;
    private int destinationId;
    private boolean exit=false;
    private int touchCount;
    //endregion Parameter


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityLauncherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Util.ScreenSize(this);

        navigationHandler();
    }


    //region Override Method

    @Override
    public void onBackPressed() {

      if (sharedPreferences.getBoolean("vip",false) || sharedPreferences.getBoolean("discount",false)) {
      /* *//*   NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
          navHostFragment.getChildFragmentManager().getFragments().get(0).;*//*

          NavHostFragment fragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
          HomeFragment frag = (HomeFragment) fragment.getFragmentManager().findFragmentById(R.id.HomeFragment);
          if (frag!=null)
          frag.showData();*/

          FragmentManager fm = getSupportFragmentManager();
          HomeFragment fragment = (HomeFragment) fm.findFragmentById(R.id.HomeFragment);
          fragment.sayHi();
      }

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


    //region Method
    private void navigationHandler() {

        binding.navView.getOrCreateBadge(R.id.InvoiceFragment).clearNumber();
        binding.navView.getOrCreateBadge(R.id.InvoiceFragment).setBackgroundColor(getResources().getColor(R.color.purple_500));
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            destinationId = destination.getId();

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


    public void setGoneProfileItem(boolean show) {

        binding.navView.getMenu().getItem(0).setVisible(show);
    }

    public void setCounterOrder(int count) {
        binding.navView.getOrCreateBadge(R.id.InvoiceFragment).setNumber(count);
    }

    public void setClearCounterOrder() {
        binding.navView.getOrCreateBadge(R.id.InvoiceFragment).clearNumber();
    }





    //endregion Method

}
