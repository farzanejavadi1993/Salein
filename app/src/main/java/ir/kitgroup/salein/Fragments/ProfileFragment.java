package ir.kitgroup.salein.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.salein.Activities.LauncherActivity;
import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.classes.CustomProgress;
import ir.kitgroup.salein.classes.Util;
import ir.kitgroup.salein.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {


    private FragmentProfileBinding binding;

    private String type = "";
    private String address = "";
    private CustomProgress customProgress;

    private int fontSize = 12;


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Bundle bundle = getArguments();
        address = bundle.getString("address");
        type = bundle.getString("type");



        if (Util.screenSize >= 7)
            fontSize = 14;
        else
            fontSize = 12;


        customProgress = CustomProgress.getInstance();


        binding.txtTitleToolbar.setTextSize(fontSize);
        binding.txtName.setTextSize(fontSize);
        binding.tvMobile.setTextSize(fontSize);
        binding.txtAddress2.setTextSize(fontSize);
        binding.txtAddress1.setTextSize(fontSize);


        Account account = Select.from(Account.class).first();


        if (account != null) {
            binding.txtName.setText(account.N);
            binding.tvMobile.setText(account.M);

            if (account.ADR != null && !account.ADR.equals("")) {

                String address = "";
                try {
                    address = account.ADR.replace(account.ADR.split("latitude")[1], "").replace("latitude", "").replace(account.ADR.split("longitude")[0], "").replace("longitude", "");
                } catch (Exception e) {
                    address = account.ADR;
                }

                binding.txtAddress1.setText(address);


            } else {
                binding.txtAddress1.setText("ناموجود");
            }


            if (account.ADR2 != null && !account.ADR2.equals("")) {


                String address = "";
                try {
                    address = account.ADR2.replace(account.ADR2.split("latitude")[1], "").replace("latitude", "").replace(account.ADR2.split("longitude")[0], "").replace("longitude", "");
                } catch (Exception e) {
                    address = account.ADR2;
                }

                binding.txtAddress2.setText(address);

            } else {
                binding.txtAddress2.setText("ناموجود");
            }


            if (type.equals("1") && !address.equals("")) {
                binding.txtAddress1.setText(address);
            } else if (type.equals("2") && !address.equals("")) {
                binding.txtAddress2.setText(address);
            }


            binding.editAddress1.setOnClickListener(v -> {


                Bundle bundleMap = new Bundle();
                bundleMap.putString("mobileNumber", "");
                bundleMap.putString("edit_address", "2");
                bundleMap.putString("type", "1");
                MapFragment mapFragment = new MapFragment();
                mapFragment.setArguments(bundleMap);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mapFragment, "MapFragment").commit();


            });

            binding.editAddress2.setOnClickListener(v -> {

                Bundle bundleMap = new Bundle();
                bundleMap.putString("mobileNumber", "");
                bundleMap.putString("edit_address", "2");
                bundleMap.putString("type", "2");
                MapFragment mapFragment = new MapFragment();
                mapFragment.setArguments(bundleMap);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mapFragment, "MapFragment").commit();

            });

            binding.ivBackFragment.setOnClickListener(v -> getFragmentManager().popBackStack());


        }
    }
}
