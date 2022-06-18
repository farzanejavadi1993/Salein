package ir.kitgroup.saleindemo.ui.logins;


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
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;


import com.orm.query.Select;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;


import java.util.Objects;
import java.util.Random;


import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;

import ir.kitgroup.saleindemo.Connect.MyViewModel;

import ir.kitgroup.saleindemo.R;
import ir.kitgroup.saleindemo.classes.Util;

import ir.kitgroup.saleindemo.DataBase.Company;
import ir.kitgroup.saleindemo.databinding.FragmentLoginBinding;


@AndroidEntryPoint
public class LoginFragment extends Fragment {

    //region PARAMETER
    private MyViewModel myViewModel;
    private FragmentLoginBinding binding;
    private String mobile = "";
    private int code = 0;
    private Boolean acceptRule = true;
    private Company company;
    private String userName;
    private String passWord;
    //endregion PARAMETER


    //region Override Method
    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //region Get The Company Is Save In The Database
        company = Select.from(Company.class).first();
        userName=company.getUser();
        passWord=company.getPass();
       //endregion Get The Company Is Save In The Database





        //region Set Title to textView
        binding.tvWelcome.setText(" به " + company.getN() + " خوش آمدید ");
        //endregion Set Title to textView


       //region Set Title to RuleTextView
        binding.loginTvRules.setText("با ثبت نام در " + company.getN());
       //endregion Set Title to RuleTextView



        //region Set UrlPath to ImageView
        Picasso.get()
                .load(Util.DEVELOPMENT_BASE_URL_Img + "/GetCompanyImage?id=" +
                        company.getI() + "&width=300&height=300")
                .error(R.drawable.loading)
                .placeholder(R.drawable.loading)
                .into(binding.imageLogo);
        //endregion Set UrlPath to ImageView



        //region TextWatcher For EdtMobile
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
                    binding.btnLogin.setBackgroundResource(R.drawable.bottom_background);
                    binding.btnLogin.setEnabled(true);
                } else {
                    binding.btnLogin.setBackgroundResource(R.drawable.inactive_bottom);
                    binding.btnLogin.setEnabled(false);
                }
            }
        });
        //endregion TextWatcher For EdtMobile



        //region Pressed btnLogin Button
        binding.btnLogin.setOnClickListener(v -> {
            if (acceptRule) {
                code = new Random(System.nanoTime()).nextInt(89000) + 10000;
                String messageCode = String.valueOf(code);
                mobile = Objects.requireNonNull(binding.edtMobile.getText()).toString();
                binding.btnLogin.setBackgroundResource(R.drawable.inactive_bottom);
                binding.btnLogin.setEnabled(false);
                binding.progressBar.setVisibility(View.VISIBLE);
                myViewModel.getSmsLogin(userName,passWord, messageCode, mobile);
            }
        });
        //endregion Pressed btnLogin Button


        //region Pressed btnRule Button
        binding.loginTvRules.setOnClickListener(v ->
                {
                    NavDirections action = LoginFragmentDirections.actionGoToRulesFragment();
                    Navigation.findNavController(binding.getRoot()).navigate(action);
                }
        );
        //endregion Pressed btnRule Button


        //region Press Or UnPress CheckBox
        binding.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                acceptRule = true;
                binding.btnLogin.setVisibility(View.VISIBLE);
            } else {
                acceptRule = false;
                binding.btnLogin.setVisibility(View.GONE);
            }
        });
        //endregion Press Or UnPress CheckBox


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);
        myViewModel.getResultMessage().setValue(null);

        //region Get Result From The Server


        myViewModel.getResultSmsLogin().observe(getViewLifecycleOwner(), result -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnLogin.setBackgroundResource(R.drawable.bottom_background);
            binding.btnLogin.setEnabled(true);
            if (result == null)
                return;
            myViewModel.getResultSmsLogin().setValue(null);
            if (result.equals("")) {
                NavDirections action = LoginFragmentDirections.actionGoToVerifyFragment(mobile, code);
                Navigation.findNavController(binding.getRoot()).navigate(action);
            }

        });

        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnLogin.setBackgroundResource(R.drawable.bottom_background);
            binding.btnLogin.setEnabled(true);
            if (result == null) return;
            Toasty.warning(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
        });
        //endregion Get Result From The Server


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    //endregion Override Method


}
