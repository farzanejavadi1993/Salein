package ir.kitgroup.saleinhamsafar.ui.logins;


import android.annotation.SuppressLint;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.orm.query.Select;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;


import java.util.Objects;
import java.util.Random;


import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;

import ir.kitgroup.saleinhamsafar.Connect.MyViewModel;
import ir.kitgroup.saleinhamsafar.R;
import ir.kitgroup.saleinhamsafar.classes.Util;

import ir.kitgroup.saleinhamsafar.DataBase.Company;
import ir.kitgroup.saleinhamsafar.databinding.FragmentLoginMobileBinding;
import ir.kitgroup.saleinhamsafar.models.Config;


@AndroidEntryPoint
public class LoginClientFragment extends Fragment {
    //region PARAMETER




    private MyViewModel myViewModel;
    private String mobileNumber = "";
    private int code = 0;
    private FragmentLoginMobileBinding binding;
    private Boolean acceptRule = true;
    private  Company company;
    //endregion PARAMETER

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {
            myViewModel.getResultMessage().setValue(null);
            binding.progressBar.setVisibility(View.GONE);
            binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.btnLogin.setEnabled(true);
            if (result == null) return;
            Toasty.warning(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
        });

        myViewModel.getResultSmsLogin().observe(getViewLifecycleOwner(), result -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.btnLogin.setEnabled(true);
            if (result == null)
                return;
            myViewModel.getResultSmsLogin().setValue(null);
            if (result.equals("")) {
                Bundle bundleOrder = new Bundle();
                bundleOrder.putString("mobileNumber", mobileNumber);
                bundleOrder.putInt("code", code);
                ConfirmCodeFragment inVoiceDetailFragment = new ConfirmCodeFragment();
                inVoiceDetailFragment.setArguments(bundleOrder);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, inVoiceDetailFragment, "ConfirmCodeFragment").addToBackStack("ConfirmCodeF").commit();
            }

        });

    }


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


            company = Select.from(Company.class).first();

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
            binding.tvWelcome.setText(" به " + company.N + " خوش آمدید ");
            Picasso.get()
                    .load("http://api.kitgroup.ir/GetCompanyImage?id=" +
                            company.I+"&width=300&height=300")
                    .error(R.drawable.loading)
                    .placeholder(R.drawable.loading)
                    .into(binding.imageLogo);
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

                    code = new Random(System.nanoTime()).nextInt(89000) + 10000;
                    String messageCode = String.valueOf(code);
                    mobileNumber = Objects.requireNonNull(binding.edtMobile.getText()).toString();
                    binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
                    binding.btnLogin.setEnabled(false);
                    binding.progressBar.setVisibility(View.VISIBLE);
                    myViewModel.getSmsLogin(company.USER,company.PASS,messageCode, mobileNumber);
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



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;


    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
