package ir.kitgroup.salein.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import com.orm.query.Select;
import java.util.Objects;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.salein.DataBase.Salein;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.databinding.ActivityLauncherBinding;
import ir.kitgroup.salein.ui.launcher.homeItem.HomeFragment;


@AndroidEntryPoint
public class LauncherActivity extends AppCompatActivity {

    //region Parameter
    @Inject
    SharedPreferences sharedPreferences;
    private ActivityLauncherBinding binding;
    private NavController navController;
    private int count;
    //endregion Parameter


    //region Override Method
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLauncherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getBundle();
        navigationHandler();
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBackPressed() {
        switch (navController.getCurrentDestination().getId()) {
            case R.id.HomeFragment:
                if (sharedPreferences.getBoolean("vip", false) || sharedPreferences.getBoolean("discount", false)) {
                  getDoActionInHomeFragment();
                }

                else {

                    if (Select.from(Salein.class).first()==null && navController.getBackQueue().size() == 2)
                   finishApp();

                    else
                        super.onBackPressed();
                }
                break;

            case R.id.CompanyFragment:
                finishApp();
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


    //region Custom Method
    private void getBundle() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            String companyId = getIntent().getExtras().getString("companyId");
            sharedPreferences.edit().putString(
                            "companyId", Objects.requireNonNullElse(companyId, ""))
                    .apply();
        } else
            sharedPreferences.edit().putString("company", "").apply();
    }

    @SuppressLint("NonConstantResourceId")
    private void navigationHandler() {
        binding.navView.getOrCreateBadge(R.id.InvoiceFragment).clearNumber();

        binding.navView.getOrCreateBadge(R.id.InvoiceFragment).setBackgroundColor(getResources().getColor(R.color.purple_500));

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
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

    private void finishApp() {
        if (count == 1) {
            finish();
            return;
        }
        count += 1;

    }

    private Fragment getFragment(){
        return  (NavHostFragment) getSupportFragmentManager().getPrimaryNavigationFragment().getChildFragmentManager().getPrimaryNavigationFragment();
    }

    private void getDoActionInHomeFragment(){
        Fragment fragment=getFragment();

        if (fragment instanceof HomeFragment) {
            ((HomeFragment) fragment).showData();
        }
    }
    //endregion Custom Method

}
