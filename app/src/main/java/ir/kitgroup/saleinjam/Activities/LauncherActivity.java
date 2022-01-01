package ir.kitgroup.saleinjam.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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


import com.google.android.material.button.MaterialButton;
import com.orm.query.Select;


import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.saleinjam.DataBase.Company;
import ir.kitgroup.saleinjam.DataBase.InvoiceDetail;

import ir.kitgroup.saleinjam.Fragments.InVoiceDetailFragment;

import ir.kitgroup.saleinjam.Fragments.LauncherOrganizationFragment;
import ir.kitgroup.saleinjam.Fragments.LoginOrganizationFragment;

import ir.kitgroup.saleinjam.Fragments.MainOrderFragment;

import ir.kitgroup.saleinjam.Fragments.SearchProductFragment;


import ir.kitgroup.saleinjam.Fragments.SettingFragment;
import ir.kitgroup.saleinjam.Fragments.SplashScreenFragment;
import ir.kitgroup.saleinjam.R;
import ir.kitgroup.saleinjam.classes.Util;
import ir.kitgroup.saleinjam.databinding.ActivityLauncherBinding;
import ir.kitgroup.saleinjam.models.Config;


@AndroidEntryPoint
public class LauncherActivity extends AppCompatActivity {

    //region Parameter


    @Inject
    Config config;

    @Inject
    SharedPreferences sharedPreferences;


    private ActivityLauncherBinding binding;


    private MainOrderFragment mainOrderFragment;


    private boolean loadProfile = true;


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


        binding.navView.getMenu().getItem(3).setEnabled(false);

        binding.navView.setSelectedItemId(R.id.homee);

        binding.navView.getOrCreateBadge(R.id.orders).clearNumber();

        binding.navView.getOrCreateBadge(R.id.orders).setBackgroundColor(getResources().getColor(R.color.red_table));


        binding.navView.setOnNavigationItemSelectedListener(item -> {

            final int size = getSupportFragmentManager().getBackStackEntryCount();
            String name = "";
            try {
                name = getSupportFragmentManager().getBackStackEntryAt(size - 1).getName();
            }catch (Exception ignored){

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

                    int amount=0;
                    if (bundle.getBoolean("EDIT"))
                        amount=mainOrderFragment.counter1;
                    else
                        amount=counter;

                    if (amount == 0)
                        setClearCounterOrder();
                    else
                        setCounterOrder(amount);
                    binding.navView.getMenu().getItem(3).setEnabled(false);
                    binding.navView.setVisibility(View.VISIBLE);


                    if (name.equals("SettingF") || name.equals("InVoiceDetailF") || name.equals("SearchProductF"))
                        getSupportFragmentManager().popBackStack();



                    return true;

                case R.id.search:
                    binding.navView.getMenu().getItem(3).setEnabled(true);
                    loadProfile = true;



                    if (name.equals("SettingF"))
                        getSupportFragmentManager().popBackStack();



                    Bundle bundleSearch = new Bundle();
                    bundleSearch.putString("Inv_GUID", bundle.getString("Inv_GUID"));
                    bundleSearch.putString("Tbl_GUID", bundle.getString("Tbl_GUID"));
                    bundleSearch.putBoolean("Seen", bundle.getBoolean("Seen"));
                    SearchProductFragment searchProductFragment = new SearchProductFragment();
                    searchProductFragment.setArguments(bundleSearch);
                    getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, searchProductFragment, "SearchProductFragment").addToBackStack("SearchProductF").commit();

                    return true;


                case R.id.orders:

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

                        String Inv_GUID = sharedPreferences.getString("Inv_GUID", "");

                        bundleOrder.putBoolean("EDIT", false);
                        bundleOrder.putString("Inv_GUID", Inv_GUID);
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


                    if (loadProfile) {
                        if (name.equals("SearchProductF"))
                            getSupportFragmentManager().popBackStack();


                        getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new SettingFragment(), "SettingFragment").addToBackStack("SettingF").commit();
                    }


                    return true;


            }


            return false;
        });
        FragmentTransaction replaceFragment = null;

        if (config.mode == 1) {
            //When User Is Login

            if (Select.from(Company.class).list().size() > 0) {
                replaceFragment = getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new LauncherOrganizationFragment(), "LauncherFragment");
            }

            //When User Is Not Login
            else {
                replaceFragment = getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new LoginOrganizationFragment());
            }


        } else {
            replaceFragment = getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new SplashScreenFragment(), "SplashScreenFragment");
        }


        replaceFragment.commit();


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




    //region Override Method
    @Override
    public void onBackPressed() {

        final int size = getSupportFragmentManager().getBackStackEntryCount();
        String name="";
        try {
            name = getSupportFragmentManager().getBackStackEntryAt(size - 1).getName();
        }catch (Exception ignored){

        }
        if (size == 0) {
            messageTextExitDialog.setText("آیا از برنامه خارج می شوید؟");
            ExitDialog.show();
        }   else if (config.mode==1 && getSupportFragmentManager().getBackStackEntryAt(size-1).getName().equals("MainOrderF")){
            getVisibilityBottomBar(false);
            getSupportFragmentManager().popBackStack();

            Fragment frg = getSupportFragmentManager().findFragmentByTag("LauncherFragment");

            if (frg instanceof LauncherOrganizationFragment) {
                LauncherOrganizationFragment fgf = (LauncherOrganizationFragment) frg;

                fgf.refreshAdapter();
            }

        }


        else if (
                name.equals("SettingF") ||
                        name.equals("InVoiceDetailF") ||
                        name.equals("SearchProductF")
        ) {

            setFistItem();
        } else if (
                name.equals("OrderListF") ||
                        name.equals("ProfileF") ||
                        name.equals("AboutUsF")


        ) {

            setClearCounterOrder();
            getSupportFragmentManager().popBackStack();
            getVisibilityBottomBar(true);
            setInVisibiltyItem(true);
            loadProfile = false;
            binding.navView.setSelectedItemId(R.id.profile);
        } else {
            getVisibilityBottomBar(false);
            getSupportFragmentManager().popBackStack();

        }




















    }


    //endregion Override Method


    //region Custom  Method
    public String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        return pInfo.versionName;
    }
    //endregion Custom Method


}