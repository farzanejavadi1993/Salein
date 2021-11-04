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



import com.google.gson.Gson;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.orm.query.Select;


import org.jetbrains.annotations.NotNull;



import java.util.Objects;



import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.saleinOrder.Connect.API;

import ir.kitgroup.saleinOrder.classes.Util;



import ir.kitgroup.saleinOrder.DataBase.User;


import ir.kitgroup.saleinOrder.R;

import ir.kitgroup.saleinOrder.databinding.FragmentOrganizationLoginBinding;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


@AndroidEntryPoint
public class LoginOrganizationFragment extends Fragment {


    //region Parameter

    @Inject
    OkHttpClient okHttpClient;

    @Inject
    Gson gson;

    private FragmentOrganizationLoginBinding binding;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    //endregion Parameter


    @SuppressLint("SourceLockedOrientationActivity")
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentOrganizationLoginBinding.inflate(getLayoutInflater());
        return binding.getRoot();

    }

    @SuppressLint({"CommitPrefEdits", "SetTextI18n"})
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnLogin.setOnClickListener(v -> {


            if (Objects.requireNonNull(binding.edtUser.getText()).toString().isEmpty() || Objects.requireNonNull(binding.edtPassword.getText()).toString().isEmpty() || Objects.requireNonNull(binding.edtIp.getText()).toString().isEmpty()) {
                Toast.makeText(getActivity(), "فیلدها را به صورت کامل وارد کنید", Toast.LENGTH_SHORT).show();
                return;
            }


            binding.btnLogin.setEnabled(false);
            Login(binding.edtUser.getText().toString(),
                    binding.edtPassword.getText().toString(),
                    binding.edtIp.getText().toString(),
                    Objects.requireNonNull(binding.edtSaleCode.getText()).toString());

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
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build();

            API api = retrofit.create(API.class);


            binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
            binding.btnLogin.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);
            compositeDisposable.add(
                  api.Login(userName, passWord)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {

                                binding.btnLogin.setEnabled(true);
                                if (jsonElement == null || !jsonElement.isEmpty()) {

                                    Toast.makeText(getActivity(), "نام کاربری یا رمز عبور اشتباه است.", Toast.LENGTH_SHORT).show();
                                } else {

                                    if (User.count(User.class) > 0)
                                        User.deleteAll(User.class);

                                    User user = new User();

                                    user.ipLocal = ipOrganization;
                                    user.numberPos=saleCode;
                                    user.userName=userName;
                                    user.passWord=passWord;
                                    user.save();


                                    getActivity().getSupportFragmentManager().popBackStack();

                                    if (Select.from(User.class).list().size()>0) {
                                         getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, new LauncherOrganizationFragment(), "LauncherFragment").commit();
                                    }

                                }
                                binding.progressBar.setVisibility(View.GONE);


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
