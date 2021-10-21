package ir.kitgroup.saleinmeat.Fragments.MobileView;


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
import androidx.fragment.app.FragmentTransaction;



import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;



import java.util.Objects;
import java.util.Random;


import ir.kitgroup.saleinmeat.Activities.Classes.LauncherActivity;
import ir.kitgroup.saleinmeat.classes.App;
import ir.kitgroup.saleinmeat.DataBase.User;

import ir.kitgroup.saleinmeat.R;
import ir.kitgroup.saleinmeat.Util.Util;
import ir.kitgroup.saleinmeat.databinding.FragmentLoginMobileBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginClientFragment extends Fragment {


    //region PARAMETER
    private FragmentLoginMobileBinding binding;
    private User user;

    private String messageWelcome = "";
    private int imageLogo;
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

        user = Select.from(User.class).first();

        //region Configuration Text Size
        int fontSize ;
        if (SplashScreenFragment.screenInches >= 7) {
            binding.tvWelcome.setTextSize(18);
            fontSize = 14;
        } else
            fontSize = 12;


        binding.tvLogin.setTextSize(fontSize);
        binding.btnLogin.setTextSize(fontSize);
        binding.tvEnterMobile.setTextSize(fontSize);
        binding.edtMobile.setTextSize(fontSize);
        //endregion Configuration Text Size


        //region Set Icon And Title
        switch (LauncherActivity.name) {
            case "ir.kitgroup.salein":
                messageWelcome = "به سالین دمو خوش آمدید";
                imageLogo = R.drawable.salein;

                break;

            case "ir.kitgroup.saleintop":
                messageWelcome = "به رستوران تاپ کباب خوش آمدید";
                imageLogo = R.drawable.top;

                break;


            case "ir.kitgroup.saleinmeat":
                messageWelcome = "به هایپر گوشت دنیوی خوش آمدید";
                imageLogo = R.drawable.goosht;

                break;

            case "ir.kitgroup.saleinnoon":
                messageWelcome = "به کافه نون دنیوی خوش آمدید";
                imageLogo = R.drawable.noon;

                break;
        }


        if (App.mode == 2) {
            binding.tvWelcome.setText(messageWelcome);
            binding.ivLogo.setImageResource(imageLogo);
        }
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
            int code = new Random(System.nanoTime()).nextInt(89000) + 10000;
           String messageCode =String.valueOf(code) ;
            String mobileNumber = Objects.requireNonNull(binding.edtMobile.getText()).toString();
            login(mobileNumber, code, messageCode);

        });
      //endregion Action btnLogin



    }


    //region Method
    private void login(String mobile, int code, String message) {


        try {

            binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
            binding.btnLogin.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);


            Call<String> call = App.api.getSmsLogin(user.userName, user.passWord, message, mobile,2);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
                    binding.btnLogin.setEnabled(true);

                    Bundle bundle = new Bundle();
                    bundle.putString("mobile", mobile);
                    bundle.putInt("code", code);
                    ConfirmCodeFragment confirmCodeFragment = new ConfirmCodeFragment();
                    confirmCodeFragment.setArguments(bundle);
                    FragmentTransaction addFragment = requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, confirmCodeFragment).addToBackStack("ConfirmCodeF");
                    addFragment.commit();


                }


                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    Toast.makeText(getActivity(), "خطا در ارسال پیامک", Toast.LENGTH_SHORT).show();
                    binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
                    binding.btnLogin.setEnabled(true);
                    binding.progressBar.setVisibility(View.GONE);
                }
            });


        } catch (NetworkOnMainThreadException ex) {
            Toast.makeText(getActivity(), "خطا در ارسال پیامک", Toast.LENGTH_SHORT).show();
            binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.btnLogin.setEnabled(true);
            binding.progressBar.setVisibility(View.GONE);


        }


    }
    //endregion Method




}
