package ir.kitgroup.saleindemo.ui.logins;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.orm.query.Select;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;
import in.aabhasjindal.otptextview.OTPListener;
import ir.kitgroup.saleindemo.Connect.MyViewModel;
import ir.kitgroup.saleindemo.DataBase.Salein;
import ir.kitgroup.saleindemo.DataBase.Users;
import ir.kitgroup.saleindemo.DataBase.Company;
import ir.kitgroup.saleindemo.databinding.FragmentVerifyBinding;
import ir.kitgroup.saleindemo.ui.map.MapFragment;

import ir.kitgroup.saleindemo.R;
import ir.kitgroup.saleindemo.classes.Util;


@AndroidEntryPoint

public class VerifyFragment extends Fragment {

    //region  Parameter
    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    Typeface typeface;

    private MyViewModel myViewModel;
    private Company company;
    private FragmentVerifyBinding binding;
    private int code;
    private String mobileNumber = "";
    //endregion Parameter


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentVerifyBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        company = Select.from(Company.class).first();

        //region Get Bundle And Set Data
        code = VerifyFragmentArgs.fromBundle(getArguments()).getCode();
        mobileNumber = VerifyFragmentArgs.fromBundle(getArguments()).getMobile();

        //endregion Get Bundle And Set Data

        binding.tvMessage.setText(getString(R.string.send_code_part1) + " " + mobileNumber + " " + getString(R.string.send_code_part2));



        //region Set Icon And
        Picasso.get()
                .load(Util.DEVELOPMENT_BASE_URL_Img+"/GetCompanyImage?id=" +
                        company.I + "&width=300&height=300")
                .error(R.drawable.loading)
                .placeholder(R.drawable.loading)
                .into(binding.imageLogo);

        //endregion Set Icon And


        binding.otpView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                binding.tvEnterCode.setTextColor(getActivity().getResources().getColor(R.color.medium_color));
                binding.tvEnterCode.setText("کد تایید 5 رقمی را وارد کنید");
            }

            @Override
            public void onOTPComplete(String otp) {
                if (Integer.parseInt(otp) == code) {

                    binding.otpView.showSuccess();
                    binding.otpView.setEnabled(false);
                    binding.progressBar.setVisibility(View.VISIBLE);
                    myViewModel.getInquiryAccount(company.USER, company.PASS, mobileNumber);
                } else {
                    binding.otpView.showError();
                    binding.tvEnterCode.setTextColor(getActivity().getResources().getColor(R.color.red));
                    binding.tvEnterCode.setText("کد وارد شده اشتباه است.");
                }

            }
        });



        //region Action ivBackFragment
        binding.ivBackFragment.setOnClickListener(v -> getFragmentManager().popBackStack());
        //endregion Action ivBackFragment

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.otpView.setEnabled(true);
            binding.otpView.resetState();
            if (result == null) return;
            Toasty.warning(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
        });


        myViewModel.getResultInquiryAccount().observe(getViewLifecycleOwner(), result -> {
            binding.progressBar.setVisibility(View.GONE);
            if (result == null)
                return;
            sharedPreferences.edit().putBoolean("disableAccount", false).apply();


            //region Account Is Register
            if (result.size() > 0) {
                Users.deleteAll(Users.class);
                Users.saveInTx(result);


                //region Show All Company
                if (Select.from(Salein.class).first() != null) {
                    NavDirections action = VerifyFragmentDirections.actionGoToStoriesFragment();
                      Navigation.findNavController(binding.getRoot()).navigate(action);
                }
                //endregion Show All Company


                //region Go To MainOrderFragment Because Account Is Register
                else {
                    NavDirections action = VerifyFragmentDirections.actionGoToMainFragment("");
                    Navigation.findNavController(binding.getRoot()).navigate(action);
                }
                //endregion Go To MainOrderFragment Because Account Is Register

            }
            //endregion Account Is Register


            //region Account Is Not Register
            else {
                Bundle bundleMap = new Bundle();
                bundleMap.putString("mobileNumber", mobileNumber);
                bundleMap.putString("edit_address", "");
                bundleMap.putString("type", "");
                MapFragment mapFragment = new MapFragment();
                mapFragment.setArguments(bundleMap);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mapFragment, "MapFragment").commit();

            }
            //endregion Account Is Not Register


        });

    }


    //region Method

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setEditBackground(int drawable, EditText view) {
        view.setBackground(getResources().getDrawable(drawable));
    }

    //endregion Method


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
