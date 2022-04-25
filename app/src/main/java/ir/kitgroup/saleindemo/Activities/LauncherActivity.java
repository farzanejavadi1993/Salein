package ir.kitgroup.saleindemo.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import ir.kitgroup.saleindemo.classes.Constant;
import ir.kitgroup.saleindemo.classes.HostSelectionInterceptor;
import ir.kitgroup.saleindemo.classes.Util;
import ir.kitgroup.saleindemo.databinding.ActivityLauncherBinding;
import ir.kitgroup.saleindemo.models.Config;

@AndroidEntryPoint
public class LauncherActivity extends AppCompatActivity {
    //region Parameter
    @Inject
    Config config;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    HostSelectionInterceptor hostSelectionInterceptor;

    private ActivityLauncherBinding binding;
    private MainOrderFragment mainOrderFragment;
    private boolean loadProfile = true;
    private boolean loadSearch = true;
    private Dialog ExitDialog;
    private TextView messageTextExitDialog;

    //endregion Parameter
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityLauncherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Util.ScreenSize(this);

       /* FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful()) {

                        return;
                    }

                    String token = task.getResult();
                    return;
                });

        if (getIntent().getExtras() != null) {

            // get data of notification *****
        }
*/

        //region Cast ExitDialog
        ExitDialog = new Dialog(this);
        ExitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ExitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ExitDialog.setContentView(R.layout.custom_dialog);
        ExitDialog.setCancelable(false);
        messageTextExitDialog = ExitDialog.findViewById(R.id.tv_message);

        MaterialButton btnYesExitDialog = ExitDialog.findViewById(R.id.btn_ok);
        MaterialButton btnNoExitDialog = ExitDialog.findViewById(R.id.btn_cancel);

        btnNoExitDialog.setOnClickListener(v -> ExitDialog.dismiss());
        btnYesExitDialog.setOnClickListener(v -> {
            ExitDialog.dismiss();
            finish();
        });
        //endregion Cast ExitDialog

        binding.navView.getMenu().getItem(3).setEnabled(false);
        binding.navView.setSelectedItemId(R.id.homee);
        binding.navView.getOrCreateBadge(R.id.orders).clearNumber();
        binding.navView.getOrCreateBadge(R.id.orders).setBackgroundColor(getResources().getColor(R.color.red_table));

        binding.navView.setOnNavigationItemSelectedListener(item -> {

            sharedPreferences.edit().putString("FNM", "main").apply();
            final int size = getSupportFragmentManager().getBackStackEntryCount();
            String name = "";
            try {
                name = getSupportFragmentManager().getBackStackEntryAt(size - 1).getName();
            } catch (Exception ignored) {
            }


            String Inv_GUID1 = sharedPreferences.getString("Inv_GUID", "");
            int counter = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID1 + "'").list().size();


            if (!loadProfile) {
                Bundle bundleMain = new Bundle();
                bundleMain.putString("Inv_GUID", Inv_GUID1);
                bundleMain.putBoolean("EDIT", false);
                bundleMain.putBoolean("Seen", false);
                mainOrderFragment.setBundle(bundleMain);
            }


            Bundle bundle = mainOrderFragment.getBundle(false);


            switch (item.getItemId()) {

                case R.id.homee:
                    loadProfile = true;
                    loadSearch = true;
                    binding.navView.getMenu().getItem(3).setEnabled(false);
                    binding.navView.setVisibility(View.VISIBLE);

                    if (name.equals("SettingF") || name.equals("InVoiceDetailF") || name.equals("SearchProductF"))
                        getSupportFragmentManager().popBackStack();

                    int amount;
                    if (bundle.getBoolean("EDIT"))
                        amount = mainOrderFragment.counter1;
                    else
                        amount = counter;

                    if (amount == 0)
                        setClearCounterOrder();
                    else
                        setCounterOrder(amount);
                    return true;

                case R.id.search:
                    loadProfile = true;
                    binding.navView.getMenu().getItem(3).setEnabled(true);

                    if (loadSearch) {
                        if (name.equals("SettingF"))
                            getSupportFragmentManager().popBackStack();

                        Bundle bundleSearch = new Bundle();
                        bundleSearch.putString("Inv_GUID", bundle.getString("Inv_GUID"));
                        bundleSearch.putString("Tbl_GUID", bundle.getString("Tbl_GUID"));
                        bundleSearch.putBoolean("Seen", bundle.getBoolean("Seen"));
                        SearchProductFragment searchProductFragment = new SearchProductFragment();
                        searchProductFragment.setArguments(bundleSearch);
                        getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, searchProductFragment, "SearchProductFragment").addToBackStack("SearchProductF").commit();
                    }
                    loadSearch = false;
                    return true;

                case R.id.orders:

                    loadSearch = true;
                    binding.navView.getMenu().getItem(3).setEnabled(true);

                    if (name.equals("SettingF") || name.equals("SearchProductF"))
                        getSupportFragmentManager().popBackStack();

                    Bundle bundleOrder = new Bundle();
                    bundleOrder.putString("Inv_GUID", bundle.getString("Inv_GUID"));
                    bundleOrder.putString("Tbl_GUID", bundle.getString("Tbl_GUID"));
                    bundleOrder.putString("Tbl_NAME", bundle.getString("Tbl_NAME"));
                    bundleOrder.putString("Ord_TYPE", bundle.getString("Ord_TYPE"));
                    bundleOrder.putString("Acc_GUID", bundle.getString("Acc_GUID"));
                    bundleOrder.putString("Acc_NAME", bundle.getString("Acc_NAME"));
                    bundleOrder.putString("type", "2");
                    bundleOrder.putBoolean("EDIT", bundle.getBoolean("EDIT"));
                    bundleOrder.putBoolean("Seen", bundle.getBoolean("Seen"));
                    bundleOrder.putBoolean("setADR1", bundle.getBoolean("setADR1"));
                    if (!loadProfile) {
                        bundleOrder.putBoolean("EDIT", false);
                        bundleOrder.putString("Inv_GUID", Inv_GUID1);
                        bundleOrder.putBoolean("setADR1", false);
                        bundleOrder.putBoolean("Seen", false);
                    }
                    InVoiceDetailFragment inVoiceDetailFragment = new InVoiceDetailFragment();
                    inVoiceDetailFragment.setArguments(bundleOrder);
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, inVoiceDetailFragment, "InVoiceDetailFragment").addToBackStack("InVoiceDetailF").commit();
                    loadProfile = true;


                    return true;

                case R.id.profile:

                    binding.navView.getMenu().getItem(3).setEnabled(true);
                    loadSearch = true;
                    if (loadProfile) {
                        if (name.equals("SearchProductF"))
                            getSupportFragmentManager().popBackStack();

                        getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new SettingFragment(), "SettingFragment").addToBackStack("SettingF").commit();
                    }

                    loadProfile = false;
                    return true;
            }
            return false;
        });

        FragmentTransaction replaceFragment;

        if (config.mode == 1) {
            //When User Is Login
            if (Select.from(Company.class).list().size() > 0) {

                Constant.PRODUCTION_BASE_URL = "http://" + Select.from(Company.class).first().IP1 + "/api/REST/";
                sharedPreferences.edit().putBoolean("status", true).apply();
                hostSelectionInterceptor.setHostBaseUrl();
                replaceFragment = getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new LauncherOrganizationFragment(), "LauncherFragment").addToBackStack("LauncherF");
                //When User Is Not Login

            } else {

                replaceFragment = getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new LoginOrganizationFragment());

            }
        } else {

            replaceFragment = getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new SplashScreenFragment(), "SplashScreenFragment");
        }

      replaceFragment.commit();
    }

    //region Method
    public void setCounterOrder(int count) {
        binding.navView.getOrCreateBadge(R.id.orders).setNumber(count);
    }

    public void setClearCounterOrder() {
        binding.navView.getOrCreateBadge(R.id.orders).clearNumber();
    }

    public void setInVisibiltyItem(boolean show) {
        binding.navView.getMenu().getItem(0).setVisible(show);
    }

    public void setMainOrderFragment(MainOrderFragment home) {
        this.mainOrderFragment = home;
    }

    public MainOrderFragment getMainOrderFragment() {
        return mainOrderFragment;
    }

    public void getVisibilityBottomBar(Boolean show) {
        if (show)
            binding.navView.setVisibility(View.VISIBLE);
        else
            binding.navView.setVisibility(View.GONE);
    }

    public void setFistItem() {
        binding.navView.setSelectedItemId(R.id.homee);
    }
    //endregion Method

    //region Override Method
    @Override
    public void onBackPressed() {
        final int size = getSupportFragmentManager().getBackStackEntryCount();
        String name = "";
        try {
            name = getSupportFragmentManager().getBackStackEntryAt(size - 1).getName();
        } catch (Exception ignored) {
        }

        if (size == 0) {
            boolean vip = sharedPreferences.getBoolean("vip", false);
            boolean discount = sharedPreferences.getBoolean("discount", false);

            if (vip || discount) {
                Fragment frg = getSupportFragmentManager().findFragmentByTag("MainOrderFragment");
                if (frg instanceof MainOrderFragment) {
                    MainOrderFragment fgf = (MainOrderFragment) frg;
                    fgf.setFilter(3);
                }

                sharedPreferences.edit().putBoolean("vip", false).apply();
                sharedPreferences.edit().putBoolean("discount", false).apply();
            } else {
                messageTextExitDialog.setText("آیا از برنامه خارج می شوید؟");
                ExitDialog.show();
            }


        }else if (config.mode==1 && size==1){
            messageTextExitDialog.setText("آیا از برنامه خارج می شوید؟");
            ExitDialog.show();
        }
        else if (config.mode == 1 && getSupportFragmentManager().getBackStackEntryAt(size - 1).getName().equals("MainOrderF")) {
            getVisibilityBottomBar(false);
            getSupportFragmentManager().popBackStack();

            Fragment frg = getSupportFragmentManager().findFragmentByTag("LauncherFragment");
            if (frg instanceof LauncherOrganizationFragment) {
                LauncherOrganizationFragment fgf = (LauncherOrganizationFragment) frg;
                fgf.refreshAdapter();
            }
        } else if (
                name.equals("SettingF") ||
                        name.equals("InVoiceDetailF") ||
                        name.equals("SearchProductF")) {
            setFistItem();
        } else if (
                name.equals("OrderListF") ||
                        name.equals("ProfileF") ||
                        name.equals("AboutUsF")) {
            setClearCounterOrder();
            getSupportFragmentManager().popBackStack();
            getVisibilityBottomBar(true);
            setInVisibiltyItem(true);
            loadProfile = false;
            binding.navView.setSelectedItemId(R.id.profile);
        }
        else {
            getVisibilityBottomBar(false);
            getSupportFragmentManager().popBackStack();
        }
    }
    //endregion Override Method


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
}
