package ir.kitgroup.salein.ui.logins;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
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

import com.orm.query.Select;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Random;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;
import in.aabhasjindal.otptextview.OTPListener;
import ir.kitgroup.salein.Connect.MyViewModel;

import ir.kitgroup.salein.DataBase.AppInfo;
import ir.kitgroup.salein.DataBase.Users;
import ir.kitgroup.salein.DataBase.Company;
import ir.kitgroup.salein.databinding.FragmentVerifyBinding;

import ir.kitgroup.salein.R;
import ir.kitgroup.salein.classes.Util;


@AndroidEntryPoint
public class VerifyFragment extends Fragment {

    //region  Parameter
    @Inject
    SharedPreferences sharedPreferences;


    private MyViewModel myViewModel;
    private FragmentVerifyBinding binding;

    private Company company;
    private String userName;
    private String passWord;
    private int code;
    private String mobile;

    private CountDownTimer countDownTimer;
    private boolean resendCode = false;
    private long timer_left = 180000;
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


        startTimer();

        //region Get Bundle And Set Data
        code = VerifyFragmentArgs.fromBundle(getArguments()).getCode();
        mobile = VerifyFragmentArgs.fromBundle(getArguments()).getMobile();
        //endregion Get Bundle And Set Data

        //region Get The Company Is Save In The Database
        company = Select.from(Company.class).first();
        userName = company.getUser();
        passWord = company.getPass();
        //endregion Get The Company Is Save In The Database


        //region set Title To EditText
        binding.tvMessage.setText(getString(R.string.send_code_part1) + " " + mobile + " " + getString(R.string.send_code_part2));
        //endregion set Title To EditText


        //region Set Icon And
        Picasso.get()
                .load(Util.DEVELOPMENT_BASE_URL_Img + "/GetCompanyImage?id=" +
                        company.getI() + "&width=300&height=300")
                .error(R.drawable.loading)
                .placeholder(R.drawable.loading)
                .into(binding.imageLogo);
        //endregion Set Icon And


        //region OtpConfig
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
                    myViewModel.getInquiryAccount(userName, passWord, mobile);
                } else {
                    binding.otpView.showError();
                    binding.tvEnterCode.setTextColor(getActivity().getResources().getColor(R.color.red));
                    binding.tvEnterCode.setText("کد وارد شده اشتباه است.");
                }

            }
        });
        //endregion OtpConfig


        //region Pressed  ivBackFragment
        binding.ivBackFragment.setOnClickListener(v -> Navigation.findNavController(binding.getRoot()).popBackStack());
        //endregion Pressed  ivBackFragment


        //region Pressed  tvTimer
        binding.tvTimer.setOnClickListener(v -> {
                    if (resendCode) {
                        resendCode = false;
                        timer_left = 90000;
                        startTimer();
                        code = new Random(System.nanoTime()).nextInt(89000) + 10000;
                        String messageCode = String.valueOf(code);
                        binding.progressResendCode.setVisibility(View.VISIBLE);
                        myViewModel.getSmsLogin(userName, passWord, messageCode, mobile);

                    }
                }
        );
        //endregion Pressed  tvTimer

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        myViewModel.getResultMessage().setValue(null);

        myViewModel.getResultInquiryAccount().observe(getViewLifecycleOwner(), result -> {
            binding.progressBar.setVisibility(View.GONE);
            if (result == null)
                return;
            sharedPreferences.edit().putBoolean("disableAccount", false).apply();

            //region When The User Is Register In
            if (result.size() > 0) {
                Users.deleteAll(Users.class);
                Users.saveInTx(result);


                //region Go To CompanyFragment Because Account Is Register
                if (Select.from(AppInfo.class).first().isSalein_main()) {
                    NavDirections action = VerifyFragmentDirections.actionGoToCompanyFragment();
                    Navigation.findNavController(binding.getRoot()).navigate(action);
                }
                //endregion Go To CompanyFragment Because Account Is Register


                //region Go To MainFragment Because Account Is Register
                else {
                    NavDirections action = VerifyFragmentDirections.actionGoToHomeFragment("");
                    Navigation.findNavController(binding.getRoot()).navigate(action);

                }
                //endregion Go To MainFragment Because Account Is Register

            }
            //endregion When The User Is Register In

            //region When The User Is Not Register In
            else {
                NavDirections action = VerifyFragmentDirections.actionGoToRegisterFragment("VerifyFragment", mobile, -1);
                Navigation.findNavController(binding.getRoot()).navigate(action);

            }
            //endregion When The User Is Not Register In

        });

        myViewModel.getResultSmsLogin().observe(getViewLifecycleOwner(), result -> {
            binding.progressResendCode.setVisibility(View.GONE);
            if (result == null)
                return;
            myViewModel.getResultSmsLogin().setValue(null);
            if (result.equals("")) {
                Toasty.success(requireActivity(), "پیامک با موفقیت ارسال شد.", Toast.LENGTH_SHORT, true).show();
            }

        });

        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.otpView.setEnabled(true);
            binding.otpView.resetState();
            if (result == null) return;
            Toasty.warning(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
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
        String timeLeftFormated = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        binding.tvTimer.setText("دریافت مجدد کد تایید تا " + timeLeftFormated);
    }


    private Company getCompany() {
        return Select.from(Company.class).first();
    }
    //endregion Method

}
