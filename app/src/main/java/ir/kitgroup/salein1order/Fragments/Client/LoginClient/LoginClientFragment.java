package ir.kitgroup.salein1order.Fragments.Client.LoginClient;

import android.annotation.SuppressLint;
import android.os.Bundle;


import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import org.jetbrains.annotations.NotNull;


import java.util.Objects;


import ir.kitgroup.salein1order.classes.App;
import ir.kitgroup.salein1order.DataBase.User;

import ir.kitgroup.salein1order.R;
import ir.kitgroup.salein1order.Util.Util;
import ir.kitgroup.salein1order.databinding.FragmentLoginMobileBinding;


public class LoginClientFragment extends Fragment {


    //region PARAMETER
    private FragmentLoginMobileBinding binding;
    //endregion PARAMETER

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        binding = FragmentLoginMobileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);





        //region Create User
        User.deleteAll(User.class);
        User user = new User();
       /* user.userName = "admin";
        user.passWord = "123";
        user.ipLocal = "192.168.20.8:96";*/
        user.ipLocal = "192.168.20.8:96";
       user.userName = "admin";
       user.passWord = "123";
      // user.ipLocal = "185.201.49.204:9696";
      // user.ipLocal = "109.125.133.149:9999";
        user.save();
        //endregion Create User




        if (App.mode==2)
            binding.tvWelcome.setText("به هایپر گوشت دنیوی خوش آمدید");



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
                    binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
                    binding.btnLogin.setEnabled(true);
                } else {
                    binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
                    binding.btnLogin.setEnabled(false);
                }
            }
        });
        binding.btnLogin.setOnClickListener(v -> {
            String mobileNumber = Objects.requireNonNull(binding.edtMobile.getText()).toString();

            Bundle bundle = new Bundle();
            bundle.putString("mobile", mobileNumber);
            ConfirmCodeFragment confirmCodeFragment = new ConfirmCodeFragment();
            confirmCodeFragment.setArguments(bundle);
            FragmentTransaction addFragment = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().add(R.id.frame_main, confirmCodeFragment).addToBackStack("ConfirmCodeF");
            addFragment.commit();

        });


    }

   /* //region Method
    private void login(String mobile) {


        try {

            Call<String> call = App.api.getSmsLogin(user.userName, user.passWord,"",mobile);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelTypeOrder>() {
                    }.getType();

                    ModelTypeOrder iDs;


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    int p=0;

                }
            });


        } catch (NetworkOnMainThreadException ex) {
            int p=0;

        }


    }
    //endregion Method*/


}
