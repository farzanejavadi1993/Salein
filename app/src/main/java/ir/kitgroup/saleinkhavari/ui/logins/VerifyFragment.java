package ir.kitgroup.saleinkhavari.ui.logins;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
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


import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;
import com.orm.query.Select;
import com.squareup.picasso.Picasso;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;
import in.aabhasjindal.otptextview.OTPListener;
import ir.kitgroup.saleinkhavari.Connect.CompanyViewModel;

import ir.kitgroup.saleinkhavari.Connect.MainViewModel;
import ir.kitgroup.saleinkhavari.DataBase.Account;
import ir.kitgroup.saleinkhavari.DataBase.SaleinShop;
import ir.kitgroup.saleinkhavari.DataBase.Company;
import ir.kitgroup.saleinkhavari.classes.AppSMSBroadcastReceiver;
import ir.kitgroup.saleinkhavari.databinding.FragmentVerifyBinding;

import ir.kitgroup.saleinkhavari.R;
import ir.kitgroup.saleinkhavari.classes.Util;
import ir.kitgroup.saleinkhavari.models.AppDetail;


@AndroidEntryPoint
public class VerifyFragment extends Fragment {

    //region  Parameter
    @Inject
    SharedPreferences sharedPreferences;

    private FragmentVerifyBinding binding;


    private CompanyViewModel companyViewModel;
    private MainViewModel mainViewModel;


    private String userName;
    private String passWord;
    private int code;
    private String mobile;


    private Company company;
    private CountDownTimer countDownTimer;
    private boolean resendCode = false;
    private long timer_left = 180000;

    private String applicationVersion="";
    private Account account;
    //endregion Parameter


    //region Override Method
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentVerifyBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getBundle();
        appVersion();
        getCodeFromSms();
        getCompany();
        init();
        initTextWatcher();
        startTimer();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        companyViewModel = new ViewModelProvider(this).get(CompanyViewModel.class);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);


        nullTheMutable();


        companyViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            binding.progressBar.setVisibility(View.GONE);
            binding.otpView.setEnabled(true);
            binding.otpView.resetState();

            Toasty.warning(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
        });

        mainViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            navigate();
        });


        companyViewModel.getResultInquiryAccount().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;
            companyViewModel.getResultInquiryAccount().setValue(null);

            sharedPreferences.edit().putBoolean("disableAccount", false).apply();

            if (result.size() > 0) {
                Account.deleteAll(Account.class);
                result.get(0).setVersion(applicationVersion);
                Account.saveInTx(result);
                mainViewModel.getCustomerFromServer(mobile);
            }

            else {
                binding.progressBar.setVisibility(View.GONE);
                NavDirections action = VerifyFragmentDirections.actionGoToRegisterFragment("VerifyFragment", mobile, -1);
                Navigation.findNavController(binding.getRoot()).navigate(action);
            }
        });

        mainViewModel.getResultCustomerFromServer().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;

            mainViewModel.getResultCustomerFromServer().setValue(null);

            //region Information Account From Server
            if (result.size() > 0) {

                setIdServerToAccount(result.get(0).getI());

                String IMEI = Util.getAndroidID(getActivity());
                List<AppDetail> Apps = result.get(0).getApps();
                CollectionUtils.filter(Apps, l -> l.getAppId().equals(Util.APPLICATION_ID) && l.getIemi().equals(IMEI));

                if (Apps.size() > 0)
                    navigate();
                else
                    addCustomerToSerVer();

            }
            else {
                addCustomerToSerVer();
            }

            //endregion Information Account From Server
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
    public void onDestroyView() {
        super.onDestroyView();
        countDownTimer.cancel();
        binding = null;
    }
    //endregion Override Method

    //region Method
    private void startTimer() {
        countDownTimer = new CountDownTimer(timer_left, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer_left = millisUntilFinished;
                updateTimerText();
            }
            @Override
            public void onFinish() {
                code = -10;
                resendCode = true;
                binding.tvTimer.setText("دریافت مجدد کد تایید");
            }
        }.start();
    }

    @SuppressLint("SetTextI18n")
    private void updateTimerText() {
        int minutes = (int) (timer_left / 1000) / 60;
        int seconds = (int) (timer_left / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        binding.tvTimer.setText("دریافت مجدد کد تایید تا " + timeLeftFormatted);
    }

    private void getCompany() {
        company= Select.from(Company.class).first();
    }

    private void getBundle() {
        code = VerifyFragmentArgs.fromBundle(getArguments()).getCode();
        mobile = VerifyFragmentArgs.fromBundle(getArguments()).getMobile();
    }

    @SuppressLint("SetTextI18n")
    private void init() {

        userName = company.getUser();
        passWord = company.getPass();

        binding.tvMessage.setText(getString(R.string.send_code_part1) + " " + mobile + " " + getString(R.string.send_code_part2));


        Picasso.get()
                .load(Util.Main_Url_IMAGE + "/GetCompanyImage?id=" +
                        company.getI() + "&width=300&height=300")
                .error(R.drawable.loading)
                .placeholder(R.drawable.loading)
                .into(binding.imageLogo);




        binding.ivBackFragment.setOnClickListener(v -> Navigation.findNavController(binding.getRoot()).popBackStack());


        binding.tvTimer.setOnClickListener(v -> {
            if (resendCode) {
                resendCode = false;
                timer_left = 90000;
                startTimer();
                code = new Random(System.nanoTime()).nextInt(89000) + 10000;
                String messageCode = String.valueOf(code);
                binding.progressResendCode.setVisibility(View.VISIBLE);
                companyViewModel.getSmsLogin(userName, passWord, messageCode, mobile);
            }
        });
    }
    private void initTextWatcher(){
        binding.otpView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                binding.tvEnterCode.setTextColor(getActivity().getResources().getColor(R.color.medium_color));
                binding.tvEnterCode.setText("کد تایید 5 رقمی را وارد کنید");
            }

            @Override
            public void onOTPComplete(String otp) {
                if (Integer.parseInt(otp) == code) {
                    binding.otpView.showSuccess();
                    binding.otpView.setEnabled(false);
                    binding.progressBar.setVisibility(View.VISIBLE);
                    companyViewModel.getInquiryAccount(userName, passWord, mobile);
                }
                else {
                    binding.otpView.showError();
                    binding.otpView.resetState();
                    binding.tvEnterCode.setTextColor(getActivity().getResources().getColor(R.color.red));
                    binding.tvEnterCode.setText("کد وارد شده اشتباه است.");
                }
            }
        });
    }

    private void addCustomerToSerVer() {
        account = Select.from(Account.class).first();
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
        binding.progressBar.setVisibility(View.GONE);
        if (Select.from(SaleinShop.class).first().isPublicApp()) {
            NavDirections action = VerifyFragmentDirections.actionGoToCompanyFragment();
            Navigation.findNavController(binding.getRoot()).navigate(action);
        } else {
            NavDirections action = VerifyFragmentDirections.actionGoToHomeFragment("");
            Navigation.findNavController(binding.getRoot()).navigate(action);
        }
    }

    private void getCodeFromSms() {
        try {
            smsListener();
            initBroadCast();
        } catch (Exception ignored) {}

    }

    private void initBroadCast() {
        AppSMSBroadcastReceiver appSMSBroadcastReceiver = new AppSMSBroadcastReceiver();
        appSMSBroadcastReceiver.setOnSmsReceiveListener(code -> {
            binding.otpView.setOTP(code);
        });
    }

    private void smsListener() {
        SmsRetrieverClient client = SmsRetriever.getClient(getContext());
        Task<Void> task = client.startSmsRetriever();
        task.addOnSuccessListener(aVoid -> {
        });

        task.addOnFailureListener(e -> {
        });
    }

    public void appVersion(){
    try {
        applicationVersion= getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
    } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
    }
    }
    private void nullTheMutable(){
        companyViewModel.getResultMessage().setValue(null);
        mainViewModel.getResultMessage().setValue(null);
        companyViewModel.getResultInquiryAccount().setValue(null);
        mainViewModel.getResultCustomerFromServer().setValue(null);
        mainViewModel.getResultAddAccountToServer().setValue(null);
    }

    private void setIdServerToAccount(String id){
        sharedPreferences.edit().putString("idServer",id).apply();
    }

    //endregion Method

}
