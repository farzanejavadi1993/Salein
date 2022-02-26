package ir.kitgroup.salein.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import es.dmoral.toasty.Toasty;
import ir.kitgroup.salein.Connect.MyViewModel;
import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.classes.Util;
import ir.kitgroup.salein.DataBase.Company;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.databinding.FragmentRegisterBinding;
import ir.kitgroup.salein.models.Setting;


public class RegisterFragment extends Fragment {
    private MyViewModel myViewModel;
    private FragmentRegisterBinding binding;
    private Company company;
    private final List<Account> accountsList = new ArrayList<>();
    private int gender = 0;
    private boolean ACCSTP = true;


    //endregion Parameter
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        company = Select.from(Company.class).first();

        Bundle bundle = getArguments();
        String mobileNumber = bundle.getString("mobileNumber");
        String address2 = bundle.getString("address2");
        double latitude = bundle.getDouble("lat");
        double longitude = bundle.getDouble("lng");

        binding.edtAddressCustomerComplete.setText(address2);
        binding.edtNumberPhoneCustomer.setText(mobileNumber);

        //region Configuration Text Size
        int fontSize;
        if (Util.screenSize >= 7) {
            fontSize = 14;
            binding.textView2.setTextSize(15);
        } else
            fontSize = 12;

        binding.tvNameCustomer.setTextSize(fontSize);
        binding.edtFLNameCustomer.setTextSize(fontSize);
        binding.tvPhoneNumberCustomer.setTextSize(fontSize);
        binding.edtNumberPhoneCustomer.setTextSize(fontSize);
        binding.tvAddressCustomer.setTextSize(fontSize);
        binding.greentv.setTextSize(fontSize);
        binding.edtAddressCustomerComplete.setTextSize(fontSize);
        binding.tvPlaqueCustomer.setTextSize(fontSize);
        binding.edtPlaqueCustomer.setTextSize(fontSize);
        binding.radioMan.setTextSize(fontSize);
        binding.radioWoman.setTextSize(fontSize);
        binding.btnRegisterInformation.setTextSize(fontSize);
        //endregion Configuration Text Size

        //region Action RadioButton
        binding.radioMan.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                gender = 0;
            }
        });
        binding.radioWoman.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                gender = 1;
            }
        });
        //endregion Action RadioButton

        //region Action btnRegisterInformation
        binding.btnRegisterInformation.setOnClickListener(v -> {
            if (binding.edtFLNameCustomer.getText().toString().isEmpty() ||
                    binding.edtNumberPhoneCustomer.getText().toString().isEmpty() ||
                    binding.edtAddressCustomerComplete.getText().toString().isEmpty() ||
                    binding.edtPlaqueCustomer.getText().toString().isEmpty()
            ) {
                Toast.makeText(getActivity(), "لطفا تمام فیلد ها را پر کنید", Toast.LENGTH_SHORT).show();
                return;
            }
            Account account = new Account();
            account.I = UUID.randomUUID().toString();
            account.N = binding.edtFLNameCustomer.getText().toString();
            account.M = binding.edtNumberPhoneCustomer.getText().toString();
            account.PSW = binding.edtNumberPhoneCustomer.getText().toString();
            account.ADR = binding.edtAddressCustomerComplete.getText().toString() + " پلاک " + binding.edtPlaqueCustomer.getText().toString();
            account.LAT = String.valueOf(latitude);
            account.LNG = String.valueOf(longitude);
            account.S = String.valueOf(gender);
            account.PC = binding.edtCodeIntroduction.getText().toString();
            account.STAPP = ACCSTP;
            accountsList.clear();
            accountsList.add(account);
            myViewModel.getSetting(company.USER, company.PASS);
        });
        //endregion Action btnRegisterInformation
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);


        myViewModel.getResultAddAccount().observe(getViewLifecycleOwner(), result -> {
            binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.btnRegisterInformation.setEnabled(true);
            binding.progressBar.setVisibility(View.GONE);

            if (result == null)
                return;

            myViewModel.getResultAddAccount().setValue(null);

            if (result) {


                Account.deleteAll(Account.class);
                Account.saveInTx(accountsList);
                accountsList.clear();

                getActivity().getSupportFragmentManager().popBackStack();
                //region Show All Company
                if (company.INSK_ID.equals("ir.kitgroup.salein")) {
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new StoriesFragment(), "StoriesFragment").commit();
                }
                //endregion Show All Company

                //region Go To MainOrderFragment Because Account Is Register
                else {
                    Bundle bundleMainOrder = new Bundle();
                    bundleMainOrder.putString("Inv_GUID", "");
                    bundleMainOrder.putString("Tbl_GUID", "");
                    bundleMainOrder.putString("Ord_TYPE", "");

                    MainOrderFragment mainOrderFragment = new MainOrderFragment();
                    mainOrderFragment.setArguments(bundleMainOrder);
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mainOrderFragment, "MainOrderFragment").commit();
                }
                //endregion Go To MainOrderFragment Because Account Is Register
            }
        });
        myViewModel.getResultSetting().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;
            myViewModel.getResultSetting().setValue(null);
            List<Setting> settingsList = new ArrayList<>(result);
            if (settingsList.size() > 0) {
                String accStp = settingsList.get(0).ACC_STATUS_APP;
                ACCSTP = !accStp.equals("0");
                accountsList.get(0).STAPP = ACCSTP;
                binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
                binding.btnRegisterInformation.setEnabled(false);
                binding.progressBar.setVisibility(View.VISIBLE);
                myViewModel.addAccount(company.USER, company.PASS, accountsList);
            }


        });

        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {
            binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.btnRegisterInformation.setEnabled(true);
            binding.progressBar.setVisibility(View.GONE);
            if (result == null)
                return;
            myViewModel.getResultMessage().setValue(null);
            Toasty.warning(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
        });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
