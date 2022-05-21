package ir.kitgroup.salein.ui.logins;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import ir.kitgroup.salein.Connect.MyViewModel;
import ir.kitgroup.salein.DataBase.Salein;
import ir.kitgroup.salein.DataBase.Users;
import ir.kitgroup.salein.DataBase.Company;
import ir.kitgroup.salein.databinding.FragmentVerifyBinding;

import ir.kitgroup.salein.R;
import ir.kitgroup.salein.classes.Util;


@AndroidEntryPoint

public class VerifyFragment extends Fragment {

    //region  Parameter
    @Inject
    SharedPreferences sharedPreferences;


    private MyViewModel myViewModel;
    private Company company;
    private FragmentVerifyBinding binding;
    private int code;
    private String mobile;
    //endregion Parameter




    //region Override Method

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
        mobile = VerifyFragmentArgs.fromBundle(getArguments()).getMobile();

        //endregion Get Bundle And Set Data

        binding.tvMessage.setText(getString(R.string.send_code_part1) + " " + mobile + " " + getString(R.string.send_code_part2));



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
                    myViewModel.getInquiryAccount(company.USER, company.PASS, mobile);
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


        //region Get Result From The Server

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

            //region User Is Register
            if (result.size() > 0) {
                Users.deleteAll(Users.class);
                Users.saveInTx(result);


                //region Show All Company
                if (Select.from(Salein.class).first() != null) {
                    NavDirections action = VerifyFragmentDirections.actionGoToStoriesFragment();
                      Navigation.findNavController(binding.getRoot()).navigate(action);
                }
                //endregion Show All Company


                //region Go To MainFragment Because Account Is Register
                else {
                    NavDirections action = VerifyFragmentDirections.actionGoToMainFragment("");
                    Navigation.findNavController(binding.getRoot()).navigate(action);
                }
                //endregion Go To MainFragment Because Account Is Register

            }
            //endregion Account Is Register



            //region Account Is Not Register
            else {
                NavDirections action = VerifyFragmentDirections.actionGoToRegisterFragment(mobile,"Verify");
                Navigation.findNavController(binding.getRoot()).navigate(action);

            }
            //endregion Account Is Not Register

            //endregion Get Result From The Server
        });

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    //endregion Override Method

}
