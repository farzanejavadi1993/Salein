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


import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;



import java.util.Objects;


import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.saleinOrder.Connect.API;
import ir.kitgroup.saleinOrder.DataBase.Company;
import ir.kitgroup.saleinOrder.classes.ConfigRetrofit;
import ir.kitgroup.saleinOrder.classes.Util;






import ir.kitgroup.saleinOrder.R;

import ir.kitgroup.saleinOrder.databinding.FragmentOrganizationLoginBinding;


@AndroidEntryPoint
public class LoginOrganizationFragment extends Fragment {


    //region Parameter

    private API api;

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


            if (Objects.requireNonNull(binding.edtUser.getText()).toString().isEmpty() ||
                    Objects.requireNonNull(binding.edtPassword.getText()).toString().isEmpty() ||
                    Objects.requireNonNull(binding.edtIp.getText()).toString().isEmpty() ||
                    Objects.requireNonNull(binding.edtLng.getText()).toString().isEmpty() ||
                    Objects.requireNonNull(binding.edtLong.getText()).toString().isEmpty()

            ) {
                Toast.makeText(getActivity(), "فیلدها را به صورت کامل وارد کنید", Toast.LENGTH_SHORT).show();
                return;
            }


            binding.btnLogin.setEnabled(false);
            Login(binding.edtUser.getText().toString(),
                    binding.edtPassword.getText().toString(),
                    binding.edtIp.getText().toString(),
                    binding.edtIp2.getText().toString(),
                    Objects.requireNonNull(binding.edtSaleCode.getText()).toString(),
                    Objects.requireNonNull(binding.edtLng.getText()).toString(),
                    Objects.requireNonNull(binding.edtLong.getText()).toString()
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


    private void Login(String userName, String passWord, String ipOrganization,  String ip2,String saleCode,String lat,String lng) {

        String baseUrl = "http://" + Util.toEnglishNumber(ipOrganization) + "/api/REST/";
        try {


            api = ConfigRetrofit.getRetrofit(baseUrl,true).create(API.class);



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


                                    Company.deleteAll(Company.class);
                                    Company company=new Company();
                                    company.IP1 = ipOrganization;
                                    company.numberPos=saleCode;
                                    company.USER=userName;
                                    company.PASS=passWord;
                                    company.mode=1;
                                    company.INSK_ID="ir.kitgroup.saleinOrder";
                                    company.LAT=lat;
                                    company.LONG=lng;
                                    company.save();


                                    getActivity().getSupportFragmentManager().popBackStack();

                                    if (Select.from(Company.class).list().size()>0) {
                                         getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_launcher, new LauncherOrganizationFragment(), "LauncherFragment").commit();
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
