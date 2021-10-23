package ir.kitgroup.salein.Fragments.MobileView;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import ir.kitgroup.salein.Activities.Classes.LauncherActivity;
import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.DataBase.InvoiceDetail;
import ir.kitgroup.salein.DataBase.Product;
import ir.kitgroup.salein.DataBase.Tables;
import ir.kitgroup.salein.DataBase.User;

import ir.kitgroup.salein.R;

import ir.kitgroup.salein.classes.App;
import ir.kitgroup.salein.databinding.FragmentSettingBinding;

public class SettingFragment extends Fragment {

    //region Parameter
    private FragmentSettingBinding binding;
    //endregion Parameter

    private SharedPreferences sharedPreferences;

    private int fontSize = 0;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {


        binding = FragmentSettingBinding.inflate(getLayoutInflater());
        if (SplashScreenFragment.screenInches >= 7)
            fontSize = 14;
        else
            fontSize = 12;
        return binding.getRoot();


    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());



        binding.tvProfile.setTextSize(fontSize);
        binding.tvComment.setTextSize(fontSize);
        binding.tvCredit.setTextSize(fontSize);
        binding.tvOrder.setTextSize(fontSize);
        binding.btnLogOut.setTextSize(fontSize);



        binding.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "به زودی", Toast.LENGTH_SHORT).show();
            }
        });


        binding.btnCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "به زودی", Toast.LENGTH_SHORT).show();
            }
        });
        binding.btnLogOut.setOnClickListener(v -> {
            if (Account.count(Account.class) > 0)
                Account.deleteAll(Account.class);


            if (InvoiceDetail.count(InvoiceDetail.class) > 0)
                InvoiceDetail.deleteAll(InvoiceDetail.class);



            if (Product.count(Product.class) > 0)
                Product.deleteAll(Product.class);







            if (Tables.count(Tables.class) > 0)
                Tables.deleteAll(Tables.class);

            if (App.mode==1){
                if (User.count(User.class) > 0)
                    User.deleteAll(User.class);
            }


            if (Tables.count(Tables.class) > 0)
                Tables.deleteAll(Tables.class);


            sharedPreferences.edit().putBoolean("firstSync", false).apply();
            sharedPreferences.edit().putBoolean("firstSyncSetting", false).apply();






            getFragmentManager().popBackStack();
            getFragmentManager().popBackStack();
            LauncherActivity.name=LauncherActivity.namePackage;
            LoginClientFragment userFragment = new LoginClientFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, userFragment).commit();


        });


        try {
            binding.ivSupport.setColorFilter(getResources().getColor(R.color.color_svg), PorterDuff.Mode.SRC_IN);
        }catch (Exception e){}





        binding.lProfile.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, new ProfileFragment(), "ProfileFragment").addToBackStack("ProfileF").commit());

        binding.btnSupport.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, new AboutUsFragment(), "AboutUsFragment").addToBackStack("AboutUsF").commit());

        binding.btnOrderList.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, new OrderListFragment(), "OrderListFragment").addToBackStack("OrderListFMobile").commit());
    }
}
