package ir.kitgroup.salein.ui.logins;


import android.annotation.SuppressLint;

import android.content.SharedPreferences;
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


import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;

import ir.kitgroup.salein.Connect.CompanyViewModel;


import ir.kitgroup.salein.DataBase.Locations;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.classes.Util;

import ir.kitgroup.salein.DataBase.Company;
import ir.kitgroup.salein.databinding.FragmentLoginBinding;


@AndroidEntryPoint
public class LoginFragment extends Fragment {
    //region Parameter
    @Inject
    SharedPreferences sharedPreferences;

    private FragmentLoginBinding binding;

    private CompanyViewModel companyViewModel;
    private String mobile = "";
    private int code = 0;
    private Boolean acceptRule = true;


    private String userName;
    private String passWord;


    private Company company;
    //endregion Parameter

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

        getCompany();
        init();
        initTextWatcher();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        companyViewModel = new ViewModelProvider(this).get(CompanyViewModel.class);

        nullTheMutable();

        companyViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnLogin.setBackgroundResource(R.drawable.bottom_background);
            binding.btnLogin.setEnabled(true);
            if (result == null) return;
            Toasty.warning(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();

//            NavDirections action = LoginFragmentDirections.actionGoToVerifyFragment(mobile, code);
//            Navigation.findNavController(binding.getRoot()).navigate(action);
        });

        companyViewModel.getResultSmsLogin().observe(getViewLifecycleOwner(), result -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnLogin.setBackgroundResource(R.drawable.bottom_background);
            binding.btnLogin.setEnabled(true);
            if (result == null)
                return;
            companyViewModel.getResultSmsLogin().setValue(null);
            if (result.equals("")) {
                NavDirections action = LoginFragmentDirections.actionGoToVerifyFragment(mobile, code);
                Navigation.findNavController(binding.getRoot()).navigate(action);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    //endregion Override Method

    //region Method
    @SuppressLint("SetTextI18n")
    private void init() {
        Locations.deleteAll(Locations.class);
        userName = company.getUser();
        passWord = company.getPass();


        String title = company.getN();
        binding.tvWelcome.setText(" به " + title + " خوش آمدید ");
        binding.loginTvRules.setText("با ثبت نام در " + title);


        Picasso.get()
                .load(Util.Main_Url_IMAGE + "/GetCompanyImage?id=" +
                        company.getI() + "&width=300&height=300")
                .error(R.drawable.loading)
                .placeholder(R.drawable.loading)
                .into(binding.imageLogo);


        binding.btnLogin.setOnClickListener(v -> {
            if (acceptRule) {
                code = new Random(System.nanoTime()).nextInt(89000) + 10000;
                String messageCode = String.valueOf(code);
                mobile = Objects.requireNonNull(binding.edtMobile.getText()).toString();

                binding.btnLogin.setBackgroundResource(R.drawable.inactive_bottom);
                binding.btnLogin.setEnabled(false);
                binding.progressBar.setVisibility(View.VISIBLE);

                companyViewModel.getSmsLogin(userName, passWord, messageCode, mobile);
            }
        });


        binding.loginTvRules.setOnClickListener(v -> {
            NavDirections action = LoginFragmentDirections.actionGoToRulesFragment();
            Navigation.findNavController(binding.getRoot()).navigate(action);
        });


        binding.checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                acceptRule = true;
                binding.btnLogin.setVisibility(View.VISIBLE);
            } else {
                acceptRule = false;
                binding.btnLogin.setVisibility(View.GONE);
            }
        });


    }

    private void initTextWatcher(){
        binding.edtMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

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
    }

    private void getCompany() {
        company= Select.from(Company.class).first();
    }

    private void nullTheMutable(){
        companyViewModel.getResultMessage().setValue(null);
        companyViewModel.getResultSmsLogin().setValue(null);
    }
    //endregion Method
}
