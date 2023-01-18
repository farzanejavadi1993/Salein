package ir.kitgroup.saleinjam.ui.logins;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;
import ir.kitgroup.saleinjam.Connect.CompanyViewModel;
import ir.kitgroup.saleinjam.Connect.MainViewModel;
import ir.kitgroup.saleinjam.DataBase.Account;
import ir.kitgroup.saleinjam.DataBase.SaleinShop;
import ir.kitgroup.saleinjam.classes.Util;

import ir.kitgroup.saleinjam.DataBase.Company;
import ir.kitgroup.saleinjam.R;
import ir.kitgroup.saleinjam.databinding.RegisterFragmentBinding;
import ir.kitgroup.saleinjam.models.AppDetail;
import ir.kitgroup.saleinjam.models.Setting;


@AndroidEntryPoint
public class RegisterFragment extends Fragment {

    @Inject
    SharedPreferences sharedPreferences;

    //region Variable
    private CompanyViewModel companyViewModel;
    private MainViewModel mainViewModel;

    private RegisterFragmentBinding binding;

    private String userName;

    private String passWord;

    private List<Account> accountsList;

    private int radioValue;

    private boolean ACCSTP = true;

    private String mobile;

    private String applicationVersion = "";


    //endregion Variable


    //region Override Method
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = RegisterFragmentBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            getBundle();
            appVersion();
            init();
            onClickBtnRegister();
            initRadioButton();
        } catch (Exception ignored) {
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        companyViewModel = new ViewModelProvider(this).get(CompanyViewModel.class);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        companyViewModel.getResultMessage().setValue(null);
        mainViewModel.getResultMessage().setValue(null);


        companyViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;

            binding.btnRegisterInformation.setBackgroundResource(R.drawable.bottom_background);
            binding.btnRegisterInformation.setEnabled(true);
            Toasty.error(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
        });


        companyViewModel.getResultSetting().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            companyViewModel.getResultSetting().setValue(null);
            List<Setting> settingsList = new ArrayList<>(result);
            if (settingsList.size() > 0) {
                String accStp = settingsList.get(0).ACC_STATUS_APP;
                ACCSTP = !accStp.equals("0");
                accountsList.get(0).STAPP = ACCSTP;
                binding.btnRegisterInformation.setBackgroundResource(R.drawable.inactive_bottom);
                binding.btnRegisterInformation.setEnabled(false);
                companyViewModel.addAccount(userName, passWord, accountsList);
            }
        });

        companyViewModel.getResultAddAccount().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            companyViewModel.getResultAddAccount().setValue(null);

            if (result) {
                Account.deleteAll(Account.class);
                Account.saveInTx(accountsList);
                accountsList.clear();
                mainViewModel.getCustomerFromServer(mobile);
            }
        });

        mainViewModel.getResultCustomerFromServer().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;

            mainViewModel.getResultCustomerFromServer().setValue(null);

            if (result.size() > 0) {
                setIdServerToAccount(result.get(0).getI());

                String IMEI = Util.getAndroidID(getActivity());
                List<AppDetail> Apps = result.get(0).getApps();
                CollectionUtils.filter(Apps, l -> l.getAppId().equals(Util.APPLICATION_ID) && l.getIemi().equals(IMEI));

                if (Apps.size() > 0)
                    navigate();
                else
                    addCustomerToSerVer();

            } else {
                addCustomerToSerVer();
            }

        });

        mainViewModel.getResultAddAccountToServer().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;
            setIdServerToAccount(result.get(0).getCurrent());

            mainViewModel.getResultAddAccountToServer().setValue(null);
            navigate();
        });

    }


    @Override
    public void onPause() {
        super.onPause();
        sharedPreferences.edit().putBoolean("loginSuccess", false).apply();
    }

    //endregion Override Method


    //region Method
    private void getBundle() {
        mobile = RegisterFragmentArgs.fromBundle(getArguments()).getMobile();
    }

    public void appVersion() {
        try {
            applicationVersion = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    private Company getCompany() {
        return Select.from(Company.class).first();
    }

    private void init() {
        accountsList = new ArrayList<>();
        userName = getCompany().getUser();
        passWord = getCompany().getPass();

        binding.ivBack.setOnClickListener(v -> Navigation.findNavController(binding.getRoot()).popBackStack());
    }

    private void onClickBtnRegister() {
        binding.btnRegisterInformation.setOnClickListener(v -> {

            if (binding.edtName.getText().toString().isEmpty()
                    ||
                    binding.edtLastName.getText().toString().isEmpty()
                    /*||
                    radioValue == -1*/
            ) {
                Toasty.error(requireActivity(), "فیلد نام و نام خانوادگی را پر کنید.", Toast.LENGTH_SHORT, true).show();
                return;
            }

            Account acc = new Account();

            //region Add Account

            acc.setAdr("");
            acc.LAT = String.valueOf(0.0);
            acc.LNG = String.valueOf(0.0);
            acc.setVersion(applicationVersion);
            acc.setI(UUID.randomUUID().toString());
            acc.setN(binding.edtName.getText().toString());
            acc.setM(mobile);
            acc.PSW = mobile;
            acc.S = String.valueOf(radioValue);
            acc.PC = binding.edtCode.getText().toString();
            acc.STAPP = ACCSTP;
            accountsList.clear();
            accountsList.add(acc);
            companyViewModel.getSetting(userName, passWord);

            //endregion Add Account


        });
    }

    private void addCustomerToSerVer() {
        Account account = Select.from(Account.class).first();
        if (account != null) {
            account.setImei(Util.getAndroidID(getActivity()));
            account.setAppId(Util.APPLICATION_ID);
            Util.JsonObjectAccount jsonObjectAcc = new Util.JsonObjectAccount();
            ArrayList<Account> accounts = new ArrayList<>();
            accounts.add(account);
            jsonObjectAcc.Account = accounts;
            mainViewModel.addAccountToServer(jsonObjectAcc);
        }

    }

    private void navigate() {
        binding.btnRegisterInformation.setBackgroundResource(R.drawable.bottom_background);
        binding.btnRegisterInformation.setEnabled(true);

        if (Select.from(SaleinShop.class).first().isPublicApp()) {
            NavDirections action = RegisterFragmentDirections.actionGoToCompanyFragment();
            Navigation.findNavController(binding.getRoot()).navigate(action);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("Inv_GUID", "");
            Navigation.findNavController(binding.getRoot()).navigate(R.id.actionGoToHomeFragment, bundle);
        }

    }

    private void setIdServerToAccount(String id) {
        sharedPreferences.edit().putString("idServer", id).apply();
    }

    private void initRadioButton() {
        binding.radio1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                radioValue = 1;//Woman
            }
        });

        binding.radio2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                radioValue = 0;//Man
            }
        });
    }
    //endregion Method
}