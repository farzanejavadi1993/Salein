package ir.kitgroup.order.Fragments.MobileView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;


import java.lang.reflect.Type;
import java.util.Objects;


import ir.kitgroup.order.classes.App;
import ir.kitgroup.order.DataBase.User;

import ir.kitgroup.order.R;
import ir.kitgroup.order.Util.Util;
import ir.kitgroup.order.classes.CustomProgress;
import ir.kitgroup.order.databinding.FragmentLoginMobileBinding;
import ir.kitgroup.order.models.ModelLog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginClientFragment extends Fragment {


    //region PARAMETER
    private FragmentLoginMobileBinding binding;
    private User user;
    private CustomProgress customProgress;
    //endregion PARAMETER

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        binding = FragmentLoginMobileBinding.inflate(getLayoutInflater());
        permission();
        customProgress=CustomProgress.getInstance();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        user= Select.from(User.class).first();

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
         login(mobileNumber);

        });


    }

    //region Method
    private void login(String mobile) {


        try {

            binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
            binding.btnLogin.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);

            customProgress.showProgress(getActivity(),"لطفا منتظر بمانید.",false);
            Call<String> call = App.api.getSmsLogin(user.userName, user.passWord, "کد تایید ورود به گوشت دنیوی"+"\n"+"2234", mobile);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelLog>() {
                    }.getType();
                    ModelLog iDs = gson.fromJson(response.body(), typeIDs);
                    int message = 0;
                    String description = "";
                    if (iDs != null) {
                        message = iDs.getLogs().get(0).getMessage();
                        description = iDs.getLogs().get(0).getDescription();
                    }


                    if (message ==4) {
                        binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
                        binding.btnLogin.setEnabled(true);
                        customProgress.hideProgress();

                        Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                       /* Bundle bundle = new Bundle();
                        bundle.putString("mobile", mobile);
                        ConfirmCodeFragment confirmCodeFragment = new ConfirmCodeFragment();
                        confirmCodeFragment.setArguments(bundle);
                        FragmentTransaction addFragment = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().add(R.id.frame_main, confirmCodeFragment).addToBackStack("ConfirmCodeF");
                        addFragment.commit();*/

                       // return;
                    }else {
                        Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                    }


                    Bundle bundle = new Bundle();
                    bundle.putString("mobile", mobile);
                    ConfirmCodeFragment confirmCodeFragment = new ConfirmCodeFragment();
                    confirmCodeFragment.setArguments(bundle);
                    FragmentTransaction addFragment = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().add(R.id.frame_main, confirmCodeFragment).addToBackStack("ConfirmCodeF");
                    addFragment.commit();
                    binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
                    binding.btnLogin.setEnabled(true);
                    customProgress.hideProgress();
                }


                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getActivity(), "خطا در ارسال پیامک", Toast.LENGTH_SHORT).show();
                    binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
                    binding.btnLogin.setEnabled(true);
                    customProgress.hideProgress();

                }
            });


        } catch (NetworkOnMainThreadException ex) {
            Toast.makeText(getActivity(), "خطا در ارسال پیامک", Toast.LENGTH_SHORT).show();
            binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.btnLogin.setEnabled(true);
            customProgress.hideProgress();

        }


    }
    //endregion Method

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 44: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {
                    Toast.makeText(getActivity(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                    return;
                }
            }
        }



    }

    protected void permission () {


        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.SEND_SMS)) {

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.SEND_SMS},
                        44);
            }
        }
    }

    private void SendSms(String mobile,String message){}
}
