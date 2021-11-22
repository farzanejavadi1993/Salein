package ir.kitgroup.saleinmeat.Fragments;


import android.annotation.SuppressLint;

import android.os.Bundle;


import android.os.NetworkOnMainThreadException;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;


import org.jetbrains.annotations.NotNull;


import java.util.Objects;
import java.util.Random;


import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.saleinmeat.Connect.API;

import ir.kitgroup.saleinmeat.R;
import ir.kitgroup.saleinmeat.classes.Util;
import ir.kitgroup.saleinmeat.databinding.FragmentLoginMobileBinding;
import ir.kitgroup.saleinmeat.DataBase.Company;

@AndroidEntryPoint
public class LoginClientFragment extends Fragment {


    //region PARAMETER

    @Inject
    Company company;

    @Inject
    API api;


    private  CompositeDisposable compositeDisposable;
    private FragmentLoginMobileBinding binding;
    private Boolean acceptRule = true;

    //endregion PARAMETER

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentLoginMobileBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        try {

            compositeDisposable = new CompositeDisposable();

            //region Configuration Text Size
            int fontSize;
            if (Util.screenSize >= 7) {
                binding.tvWelcome.setTextSize(18);
                fontSize = 14;
            } else
                fontSize = 12;


            binding.loginTvRules.setTextSize(fontSize);
            binding.loginTvRules1.setTextSize(fontSize);
            binding.loginTvRules2.setTextSize(fontSize);
            binding.tvLogin.setTextSize(fontSize);
            binding.btnLogin.setTextSize(fontSize);
            binding.tvEnterMobile.setTextSize(fontSize);
            binding.edtMobile.setTextSize(fontSize);
            //endregion Configuration Text Size


            binding.loginTvRules.setText("با ثبت نام در " + company.N);



            //region Set Icon And Title
            binding.tvWelcome.setText(company.messageWelcome);
            binding.imageLogo.setImageResource(company.imageLogo);
            //endregion Set Icon And Title




            //region TextWatcher edtMobile
            binding.edtMobile.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {


                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (Util.isValid(s.toString())) {

                        Util.hideKeyBoard(getActivity(), binding.edtMobile);

                        binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
                        binding.btnLogin.setEnabled(true);
                    } else {
                        binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
                        binding.btnLogin.setEnabled(false);
                    }
                }
            });
            //endregion TextWatcher edtMobile






            //region Action btnLogin
            binding.btnLogin.setOnClickListener(v -> {
                if (acceptRule) {
                    int code = new Random(System.nanoTime()).nextInt(89000) + 10000;
                    String messageCode = String.valueOf(code);
                    String mobileNumber = Objects.requireNonNull(binding.edtMobile.getText()).toString();
                    login(mobileNumber, code, messageCode);
                }


            });
            //endregion Action btnLogin






            //region Action btnLogin
            binding.loginTvRules1.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new RulesFragment(), "RulesFragment").addToBackStack("RulesF").commit());
            //endregion Action btnLogin



            //region Action CheckBox
            binding.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    acceptRule = true;
                    binding.btnLogin.setVisibility(View.VISIBLE);
                } else {
                    acceptRule = false;
                    binding.btnLogin.setVisibility(View.GONE);
                }
            });
            //endregion Action CheckBox





        } catch (Exception ignored) {
        }
    }


    //region Method
    private void login(String mobileNumber, int code, String message) {
        try {
            binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
            binding.btnLogin.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);
            compositeDisposable.add(
                    api.getSmsLogin(company.userName, company.passWord, message, mobileNumber, 2)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                binding.progressBar.setVisibility(View.GONE);
                                binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
                                binding.btnLogin.setEnabled(true);


                                Bundle bundleOrder = new Bundle();
                                bundleOrder.putString("mobileNumber",mobileNumber);
                                bundleOrder.putInt("code", code);

                                ConfirmCodeFragment inVoiceDetailFragment=new ConfirmCodeFragment();
                                inVoiceDetailFragment.setArguments(bundleOrder);
                                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, inVoiceDetailFragment, "ConfirmCodeFragment").addToBackStack("ConfirmCodeF").commit();








                            }, throwable -> {

                                Toast.makeText(getActivity(), "خطا در ارسال پیامک", Toast.LENGTH_SHORT).show();
                                binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
                                binding.btnLogin.setEnabled(true);
                                binding.progressBar.setVisibility(View.GONE);


                            })
            );
        } catch (NetworkOnMainThreadException ex) {
            Toast.makeText(getActivity(), "خطا در ارسال پیامک", Toast.LENGTH_SHORT).show();
            binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.btnLogin.setEnabled(true);
            binding.progressBar.setVisibility(View.GONE);


        }

    }
    //endregion Method


}
