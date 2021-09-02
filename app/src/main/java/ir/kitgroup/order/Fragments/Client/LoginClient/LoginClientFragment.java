package ir.kitgroup.order.Fragments.Client.LoginClient;

import android.annotation.SuppressLint;
import android.os.Bundle;


import android.os.NetworkOnMainThreadException;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;


import java.lang.reflect.Type;
import java.util.Objects;


import ir.kitgroup.order.classes.App;
import ir.kitgroup.order.DataBase.User;

import ir.kitgroup.order.R;
import ir.kitgroup.order.Util.Util;
import ir.kitgroup.order.databinding.FragmentLoginMobileBinding;
import ir.kitgroup.order.models.ModelTypeOrder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginClientFragment extends Fragment {


    //region PARAMETER
    private FragmentLoginMobileBinding binding;
    private User user;
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




        if (App.mode == 2)
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

                    Util.hideKeyBoard(getActivity(), binding.edtMobile);

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
          // login(mobileNumber);
          Bundle bundle = new Bundle();
            bundle.putString("mobile", mobileNumber);
            ConfirmCodeFragment confirmCodeFragment = new ConfirmCodeFragment();
            confirmCodeFragment.setArguments(bundle);
            FragmentTransaction addFragment = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().add(R.id.frame_main, confirmCodeFragment).addToBackStack("ConfirmCodeF");
            addFragment.commit();
        });


    }

    //region Method
    private void login(String mobile) {


        try {

            Call<String> call = App.api.getSmsLogin(user.userName, user.passWord, "", mobile);
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
                    int p = 0;

                }
            });


        } catch (NetworkOnMainThreadException ex) {
            int p = 0;

        }


    }
    //endregion Method


}
