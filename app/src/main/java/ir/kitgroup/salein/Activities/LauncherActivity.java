package ir.kitgroup.salein.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import android.annotation.SuppressLint;
import android.app.Dialog;

import android.app.Fragment;
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


import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.salein.DataBase.User;
import ir.kitgroup.salein.Fragments.InVoiceDetailFragmentDirections;
import ir.kitgroup.salein.Fragments.LauncherOrganizationFragment;
import ir.kitgroup.salein.Fragments.LoginOrganizationFragment;

import ir.kitgroup.salein.Fragments.MainOrderFragment;
import ir.kitgroup.salein.Fragments.MainOrderFragmentDirections;
import ir.kitgroup.salein.Fragments.SearchProductFragmentDirections;

import ir.kitgroup.salein.Fragments.SettingFragmentDirections;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.classes.Util;
import ir.kitgroup.salein.databinding.ActivityLauncherBinding;
import ir.kitgroup.salein.models.Config;


@AndroidEntryPoint
public class LauncherActivity extends AppCompatActivity {

    //region Parameter


    @Inject
    Config config;

    private ActivityLauncherBinding binding;

    private int counter = 0;
    private  MainOrderFragment mainOrderFragment;


    private Boolean mainOrder = false;
    private Boolean searchProduct = false;
    private Boolean invoiceDetail = false;
    private Boolean setting = false;


    //region Parameter Dialog
    private Dialog ExitDialog;
    private TextView messageTextExitDialog;
    private   NavController navController;
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


         navController = Navigation.findNavController(this, R.id.nav_host_fragment);



        binding.navView.setSelectedItemId(R.id.homee);


        binding.navView.getOrCreateBadge(R.id.orders).clearNumber();

        binding.navView.getOrCreateBadge(R.id.orders).setBackgroundColor(getResources().getColor(R.color.red_table));


        binding.navView.setOnNavigationItemSelectedListener(item -> {

            switch (item.getItemId()) {

                case R.id.homee:
                    if (!mainOrder){
                        binding.navView.getMenu().getItem(3).setEnabled(false);
                        navController.popBackStack();
                    }

                    return true;

                case R.id.search:

                    binding.navView.getMenu().getItem(3).setEnabled(true);
                    NavDirections actionSearch = null;

                    if (mainOrder) {
                        actionSearch = (NavDirections) MainOrderFragmentDirections.actionGoToSearchProductFragment();

                    } else if (invoiceDetail) {
                        actionSearch = (NavDirections) InVoiceDetailFragmentDirections.actionGoToSearchProductFragment();
                    } else if (setting) {
                        actionSearch = (NavDirections) SettingFragmentDirections.actionGoToSearchProductFragment();
                    }

                    navController.navigate(actionSearch);
                    return true;


                case R.id.orders:

                    binding.navView.getMenu().getItem(3).setEnabled(true);
                    ArrayList<String> list = new ArrayList<>();
                    list.addAll(mainOrderFragment.getValueOfParameter());

                    NavDirections actionInvoiceDetail = null;

                    if (mainOrder) {

                        actionInvoiceDetail = (NavDirections) MainOrderFragmentDirections.actionGoToInvoiceDetailFragment()
                                .setInvGUID(list.get(0))
                                .setTblGUID(list.get(1))
                                .setTblNAME(list.get(2))
                                .setOrdTYPE(list.get(3))
                                .setAccGUID(list.get(4))
                                .setAccNAME(list.get(5))
                                .setType(list.get(6))
                                .setEDIT(list.get(7).equals("1") ? true : false)
                                .setSEEN(list.get(8).equals("1") ? true : false)
                                .setSetADR1(list.get(9).equals("1") ? true : false);
                    } else if (searchProduct) {
                        actionInvoiceDetail = (NavDirections) SearchProductFragmentDirections.actionGoToInvoiceDetailFragment()
                                .setInvGUID(list.get(0))
                                .setTblGUID(list.get(1))
                                .setTblNAME(list.get(2))
                                .setOrdTYPE(list.get(3))
                                .setAccGUID(list.get(4))
                                .setAccNAME(list.get(5))
                                .setType(list.get(6))
                                .setEDIT(list.get(7).equals("1") ? true : false)
                                .setSEEN(list.get(8).equals("1") ? true : false)
                                .setSetADR1(list.get(9).equals("1") ? true : false);
                    } else if (setting) {
                        actionInvoiceDetail = (NavDirections) SettingFragmentDirections.actionGoToInvoiceDetailFragment()
                                .setInvGUID(list.get(0))
                                .setTblGUID(list.get(1))
                                .setTblNAME(list.get(2))
                                .setOrdTYPE(list.get(3))
                                .setAccGUID(list.get(4))
                                .setAccNAME(list.get(5))
                                .setType(list.get(6))
                                .setEDIT(list.get(7).equals("1") ? true : false)
                                .setSEEN(list.get(8).equals("1") ? true : false)
                                .setSetADR1(list.get(9).equals("1") ? true : false);
                    }

                    navController.navigate(actionInvoiceDetail);
                    return true;


                case R.id.profile:
                    binding.navView.getMenu().getItem(3).setEnabled(true);
                    NavDirections actionProfile = null;

                    if (mainOrder) {
                        actionProfile = (NavDirections) MainOrderFragmentDirections.actionGoToSettingFragment();

                    } else if (searchProduct) {
                        actionProfile = (NavDirections) SearchProductFragmentDirections.actionGoToSettingFragment();
                    } else if (invoiceDetail) {
                        actionProfile = (NavDirections) InVoiceDetailFragmentDirections.actionGoToSettingFragment();
                    }

                    navController.navigate(actionProfile);
                    return true;


            }


            return false;
        });

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {


            switch (destination.getId()) {
                case R.id.MainOrderFragment:

                    mainOrder = true;
                    searchProduct = false;
                    invoiceDetail = false;
                    setting = false;
                    break;

                case R.id.SearchProductFragment:

                    mainOrder = false;
                    searchProduct = true;
                    invoiceDetail = false;
                    setting = false;
                    break;
                case R.id.InvoiceDetailFragment:

                    mainOrder = false;
                    searchProduct = false;
                    invoiceDetail = true;
                    setting = false;
                    break;
                case R.id.SettingFragment:

                    mainOrder = false;
                    searchProduct = false;
                    invoiceDetail = false;
                    setting = true;
                    break;

                default:

                    break;


            }
        });


        if (config.mode == 1) {
            //When User Is Login
            FragmentTransaction replaceFragment;
            if (Select.from(User.class).list().size() > 0) {
                replaceFragment = getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new LauncherOrganizationFragment(), "LauncherFragment");
            }

            //When User Is Not Login
            else {

                replaceFragment = getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new LoginOrganizationFragment());

            }
            replaceFragment.commit();


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


    public void setCounterOrder(int count) {
        counter = count;
        binding.navView.getOrCreateBadge(R.id.orders).setNumber(count);

    }

    public void setClearCounterOrder() {
        binding.navView.getOrCreateBadge(R.id.orders).clearNumber();
    }


    public void setInVisibiltyItem() {
        binding.navView.getMenu().getItem(0).setVisible(false);
    }


    public void setMainOrder(MainOrderFragment mainOrder) {
        mainOrderFragment=mainOrder;
    }


    public void getVisibilityBottomBar(Boolean show) {
        if (show)
        binding.navView.setVisibility(View.VISIBLE);
        else
            binding.navView.setVisibility(View.GONE);
    }



    //region Override Method
    @Override
    public void onBackPressed() {

        super.onBackPressed();
        if (mainOrder){
            binding.navView.setSelected(false);
            binding.navView.setSelectedItemId(R.id.homee);

        }
   /*     final int size = getSupportFragmentManager().getBackStackEntryCount();



        if (size == 0) {
            messageTextExitDialog.setText("آیا از برنامه خارج می شوید؟");
            ExitDialog.show();
        }

        else if (config.mode == 1 &&
            getSupportFragmentManager().getBackStackEntryAt(size - 1).getName().equals("MainF")) {
            Fragment fragment = LauncherActivity.this.getSupportFragmentManager().findFragmentByTag("LauncherFragment");
            if (fragment instanceof LauncherOrganizationFragment) {
                LauncherOrganizationFragment fgf = (LauncherOrganizationFragment) fragment;
                fgf.refreshAdapter();
            }
            getSupportFragmentManager().popBackStack();
        }


        else if (getSupportFragmentManager().getBackStackEntryAt(size - 1).getName().equals("InVoiceDetailFMobile")) {
            Fragment fragment = LauncherActivity.this.getSupportFragmentManager().findFragmentByTag("MainOrderMobileFragment");
            if (fragment instanceof MainOrderFragment) {
                MainOrderFragment fgf = (MainOrderFragment) fragment;
                fgf.refreshProductList();
            }else {
                fragment=  LauncherActivity.this.getSupportFragmentManager().findFragmentByTag("SettingFragment");
                SettingFragment fgf = (SettingFragment) fragment;
                fgf.refreshProductList();
            }
            getSupportFragmentManager().popBackStack();
        }

        else if (getSupportFragmentManager().getBackStackEntryAt(size - 1).getName().equals("OrderListFMobile")) {
            Fragment fragment = LauncherActivity.this.getSupportFragmentManager().findFragmentByTag("MainFragment");
            if (fragment instanceof MainFragment) {
                MainFragment fgf = (MainFragment) fragment;
                fgf.setVisibleItem();
            }
            getSupportFragmentManager().popBackStack();
        }



        else if (getSupportFragmentManager().getBackStackEntryAt(size - 1).getName().equals("SearchProductF")) {
            Fragment fragment = LauncherActivity.this.getSupportFragmentManager().findFragmentByTag("MainOrderMobileFragment");
            if (fragment instanceof MainOrderFragment) {
                MainOrderFragment fgf = (MainOrderFragment) fragment;
                fgf.refreshProductFromSearch();
            }
            getSupportFragmentManager().popBackStack();
        }




        else if (getSupportFragmentManager().getBackStackEntryAt(size - 1).getName().equals("SettingF")) {
            messageTextExitDialog.setText("آیا از برنامه خارج می شوید؟");
            ExitDialog.show();
        }




        else
            getSupportFragmentManager().popBackStack();*/
    }


    //endregion Override Method


    //region Custom  Method
    public String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        return pInfo.versionName;
    }
    //endregion Custom Method


}