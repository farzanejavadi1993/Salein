package ir.kitgroup.saleinOrder.Fragments;


import android.annotation.SuppressLint;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;


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


import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


import ir.kitgroup.saleinOrder.DataBase.Company;
import ir.kitgroup.saleinOrder.R;


import ir.kitgroup.saleinOrder.classes.BuildConfig1;
import ir.kitgroup.saleinOrder.classes.HostSelectionInterceptor;
import ir.kitgroup.saleinOrder.classes.Util;
import ir.kitgroup.saleinOrder.databinding.FragmentOrganizationLoginBinding;


@AndroidEntryPoint
public class LoginOrganizationFragment extends Fragment {


    //region Parameter


    @Inject
    HostSelectionInterceptor hostSelectionInterceptor;

    @Inject
    SharedPreferences sharedPreferences;

    private FragmentOrganizationLoginBinding binding;



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
                    Objects.requireNonNull(binding.edtIp1.getText()).toString().isEmpty()

            ) {
                Toast.makeText(getActivity(), "فیلدها را به صورت کامل وارد کنید", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle bundleMap = new Bundle();
            bundleMap.putString("mobileNumber", "");
            bundleMap.putString("type", "");
            bundleMap.putString("edit_address", "10");
            bundleMap.putString("IP1", Util.toEnglishNumber( binding.edtIp1.getText().toString()));
            bundleMap.putString("userName", binding.edtUser.getText().toString());
            bundleMap.putString("passWord",  binding.edtPassword.getText().toString());
            bundleMap.putString("numberPos", binding.edtSaleCode.getText().toString());

            BuildConfig1.PRODUCTION_BASE_URL = "http://" + Util.toEnglishNumber( binding.edtIp1.getText().toString()) + "/api/REST/";
            sharedPreferences.edit().putBoolean("status", true).apply();
            hostSelectionInterceptor.setHostBaseUrl();

            MapFragment mapFragment = new MapFragment();
            mapFragment.setArguments(bundleMap);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mapFragment, "MapFragment").addToBackStack("MapF").commit();






        });


        try {
            String appVersion = appVersion();
            binding.loginVersionNumber.setText(" نسخه " + appVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        binding.tvDevelop.setMovementMethod(LinkMovementMethod.getInstance());
    }




    public String appVersion() throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        return pInfo.versionName;
    }

}
