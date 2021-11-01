package ir.kitgroup.saleinOrder.Fragments;


import android.annotation.SuppressLint;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;


import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;

import java.util.Objects;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.saleinOrder.Connect.API;
import ir.kitgroup.saleinOrder.Util.Util;
import ir.kitgroup.saleinOrder.classes.App;


import ir.kitgroup.saleinOrder.DataBase.User;


import ir.kitgroup.saleinOrder.R;

import ir.kitgroup.saleinOrder.databinding.FragmentOrganizationLoginBinding;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginOrganizationFragment extends Fragment {


    //region Parameter
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FragmentOrganizationLoginBinding binding;

    //endregion Parameter


    @SuppressLint("SourceLockedOrientationActivity")
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentOrganizationLoginBinding.inflate(getLayoutInflater());
        return binding.getRoot();

    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



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

        String baseUrl = "http://" + Util.toEnglishNumber(ipOrganization) + "/api/REST/";
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
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();

            App.api = App.retrofit.create(API.class);


            binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
            binding.btnLogin.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);
            compositeDisposable.add(
                    App.api.Login(userName, passWord)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {

                                binding.btnLogin.setEnabled(true);
                                assert jsonElement != null;
                                if (jsonElement.isEmpty()) {

                                    if (User.count(User.class) > 0)
                                        User.deleteAll(User.class);

                                    User user = new User();
                                    user.userName = userName;
                                    user.passWord = passWord;
                                    user.ipLocal = ipOrganization;
                                    user.ipStatic = "";
                                    user.numberPos = saleCode;
                                    user.lat = 36.318805483696735;
                                    user.lng = 59.555196457006296;
                                    user.CheckUser = true;
                                    user.save();

                                    binding.progressBar.setVisibility(View.GONE);
                                    assert getFragmentManager() != null;
                                    getFragmentManager().popBackStack();
                                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new LauncherOrganizationFragment(), "LauncherFragment").commit();

                                } else {

                                    Toast.makeText(getActivity(), "نام کاربری یا رمز عبور اشتباه است.", Toast.LENGTH_SHORT).show();
                                    binding.progressBar.setVisibility(View.GONE);
                                }


                            }, throwable -> {

                                binding.btnLogin.setEnabled(true);
                                Toast.makeText(getContext(), "خطا در دریافت اطلاعات", Toast.LENGTH_SHORT).show();
                                binding.progressBar.setVisibility(View.GONE);


                            })
            );


        } catch (NetworkOnMainThreadException ex) {
            binding.btnLogin.setEnabled(true);
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "خطا در دریافت اطلاعات" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        return pInfo.versionName;
    }

}
