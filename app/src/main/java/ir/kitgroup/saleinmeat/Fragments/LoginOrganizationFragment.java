package ir.kitgroup.saleinmeat.Fragments;

import android.annotation.SuppressLint;

import android.content.pm.ActivityInfo;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;


import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;

import java.util.Objects;

import java.util.concurrent.TimeUnit;

import ir.kitgroup.saleinmeat.Activities.Classes.LauncherActivity;
import ir.kitgroup.saleinmeat.Connect.API;
import ir.kitgroup.saleinmeat.Util.Util;
import ir.kitgroup.saleinmeat.classes.App;
import ir.kitgroup.saleinmeat.classes.CustomProgress;

import ir.kitgroup.saleinmeat.DataBase.User;


import ir.kitgroup.saleinmeat.R;

import ir.kitgroup.saleinmeat.databinding.FragmentOrganizationLoginBinding;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginOrganizationFragment extends Fragment {


    //region Parameter
    private FragmentOrganizationLoginBinding binding;
    private CustomProgress customProgress;
    //endregion Parameter


    @SuppressLint("SourceLockedOrientationActivity")
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (LauncherActivity.screenInches >= 7) {
            Objects.requireNonNull(getActivity()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        binding = FragmentOrganizationLoginBinding.inflate(getLayoutInflater());
        return binding.getRoot();

    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        customProgress = CustomProgress.getInstance();


        binding.btnLogin.setOnClickListener(v -> {


            if (Objects.requireNonNull(binding.edtUser.getText()).toString().isEmpty() || Objects.requireNonNull(binding.edtPassword.getText()).toString().isEmpty() || Objects.requireNonNull(binding.edtIp.getText()).toString().isEmpty()) {

                Toast.makeText(getActivity(), "اطلاعات را به صورت کامل وارد کنید", Toast.LENGTH_SHORT).show();
                return;

            }

            binding.btnLogin.setEnabled(false);
            Login(
                    binding.edtUser.getText().toString(),
                    binding.edtPassword.getText().toString(),
                    binding.edtIp.getText().toString(),
                    Objects.requireNonNull(binding.edtSaleCode.getText()).toString()
            );

        });


        try {
            String appVersion = appVersion();
            binding.loginVersionNumber.setText(" نسخه " + appVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        binding.tvDevelop.setMovementMethod(LinkMovementMethod.getInstance());
    }


    private void Login(String userName, String passWord, String ipOrganization, String saleCode) {

      String  baseUrl = "http://" + Util.toEnglishNumber(ipOrganization) + "/api/REST/";
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .setDateFormat(DateFormat.LONG)
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .setVersion(1.0)
                .create();


        try {
            App.retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();

            App.api = App.retrofit.create(API.class);
            Call<String> call = App.api.Login(userName, passWord);

            customProgress.showProgress(getContext(), getString(R.string.get_data_from_server), false);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    binding.btnLogin.setEnabled(true);
                    assert response.body() != null;
                    if (response.body().isEmpty()) {

                        if (User.count(User.class) > 0)
                            User.deleteAll(User.class);

                        User user = new User();
                        user.userName = userName;
                        user.passWord = passWord;
                        user.ipLocal = ipOrganization;
                        user.ipStatic = "";
                        user.numberPos = saleCode;
                        user.CheckUser = true;
                        user.save();

                        customProgress.hideProgress();
                        assert getFragmentManager() != null;
                        getFragmentManager().popBackStack();
                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new LauncherOrganizationFragment(), "LauncherFragment").commit();

                    } else {

                        Toast.makeText(getActivity(), "نام کاربری یا رمز عبور اشتباه است.", Toast.LENGTH_SHORT).show();
                        customProgress.hideProgress();
                    }


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    binding.btnLogin.setEnabled(true);
                    Toast.makeText(getContext(), "خطا در دریافت اطلاعات" + t.toString(), Toast.LENGTH_SHORT).show();
                    customProgress.hideProgress();

                }
            });


        } catch (NetworkOnMainThreadException ex) {
            binding.btnLogin.setEnabled(true);
            customProgress.hideProgress();
            Toast.makeText(getContext(), "خطا در دریافت اطلاعات" + ex.toString(), Toast.LENGTH_SHORT).show();
        }


    }

    public String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        return pInfo.versionName;
    }

}
