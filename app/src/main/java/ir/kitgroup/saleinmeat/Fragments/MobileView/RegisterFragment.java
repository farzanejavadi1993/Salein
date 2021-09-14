package ir.kitgroup.saleinmeat.Fragments.MobileView;


import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


import java.util.Objects;
import java.util.UUID;

import ir.kitgroup.saleinmeat.Activities.Classes.LauncherActivity;
import ir.kitgroup.saleinmeat.classes.App;
import ir.kitgroup.saleinmeat.DataBase.Account;

import ir.kitgroup.saleinmeat.DataBase.User;

import ir.kitgroup.saleinmeat.models.ModelLog;
import ir.kitgroup.saleinmeat.R;
import ir.kitgroup.saleinmeat.databinding.FragmentRegisterBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterFragment extends Fragment  {
    //region  Parameter


    private FragmentRegisterBinding binding;
    private final List<Account> accountsList = new ArrayList<>();
    private User user;
    private int gender = 0;
    private int fontSize=0;




    //endregion Parameter
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(getLayoutInflater());

        Bundle bundle = getArguments();
        assert bundle != null;
        String mobile = bundle.getString("mobile");
        String address1 = bundle.getString("address1");
        String address2 = bundle.getString("address2");


        if (LauncherActivity.screenInches>=7) {

            fontSize = 14;
            binding.textView2.setTextSize(15);
        }
        else
            fontSize=12;





        binding.tvNameCustomer.setTextSize(fontSize);
        binding.edtFLNameCustomer.setTextSize(fontSize);
        binding.tvPhoneNumberCustomer.setTextSize(fontSize);
        binding.edtNumberPhoneCustomer.setTextSize(fontSize);
        binding.tvAddressCustomer.setTextSize(fontSize);
        binding.edtAddressCustomer.setTextSize(fontSize);
        binding.greentv.setTextSize(fontSize);
        binding.edtAddressCustomerComplete.setTextSize(fontSize);
        binding.tvPlaqueCustomer.setTextSize(fontSize);
        binding.edtPlaqueCustomer.setTextSize(fontSize);
        binding.radioMan.setTextSize(fontSize);
        binding.radioWoman.setTextSize(fontSize);
        binding.btnRegisterInformation.setTextSize(fontSize);


        binding.edtAddressCustomer.setText(address1);
        binding.edtAddressCustomerComplete.setText(address2);
        binding.edtNumberPhoneCustomer.setText(mobile);
        user = Select.from(User.class).first();

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


        binding.btnRegisterInformation.setOnClickListener(v -> {



                if (
                        binding.edtFLNameCustomer.getText().toString().isEmpty() ||
                                binding.edtNumberPhoneCustomer.getText().toString().isEmpty() ||
                                binding.edtAddressCustomer.getText().toString().isEmpty() ||
                                binding.edtPlaqueCustomer.getText().toString().isEmpty()
                ) {
                    Toast.makeText(getActivity(), "لطفا تمام فیلد ها را پر کنید", Toast.LENGTH_SHORT).show();
                    return;
                }

                Account account = new Account();
                account.I = UUID.randomUUID().toString();
                account.N = binding.edtFLNameCustomer.getText().toString();
                account.M = binding.edtNumberPhoneCustomer.getText().toString();
                account.ADR = binding.edtAddressCustomer.getText().toString() + " پلاک " + binding.edtPlaqueCustomer.getText().toString();
                account.S = String.valueOf(gender);
                accountsList.clear();
                accountsList.add(account);
                addAccount(user.userName, user.passWord, accountsList);


        });


        return binding.getRoot();
    }






    private static class JsonObjectAccount {

        public List<Account> Account;

}

    private void addAccount(String userName, String pass, List<Account> accounts) {


        try {
            JsonObjectAccount jsonObjectAcc = new JsonObjectAccount();
            jsonObjectAcc.Account = accounts;


            Gson gson = new Gson();
            Type typeJsonObject = new TypeToken<JsonObjectAccount>() {
            }.getType();

            Call<String> call = App.api.addAccount(userName, pass, gson.toJson(jsonObjectAcc, typeJsonObject), "");
            binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
            binding.btnRegisterInformation.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelLog>() {
                    }.getType();
                    ModelLog iDs = gson.fromJson(response.body(), typeIDs);

                    assert iDs != null;
                    int message = iDs.getLogs().get(0).getMessage();
                    String description = iDs.getLogs().get(0).getDescription();
                    if (message == 1) {
                        Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();

                        Account.deleteAll(Account.class);
                        Account.saveInTx(accountsList);
                        accountsList.clear();
                        Bundle bundle = new Bundle();
                        bundle.putString("Ord_TYPE", "");
                        bundle.putString("Tbl_GUID", "");
                        bundle.putString("Inv_GUID", "");
                        getFragmentManager().popBackStack();
                        MainOrderMobileFragment mainOrderMobileFragment = new MainOrderMobileFragment();
                        mainOrderMobileFragment.setArguments(bundle);
                        FragmentTransaction replaceFragment = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment");
                        replaceFragment.commit();


                    } else {

                        Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                    }
                    binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
                    binding.btnRegisterInformation.setEnabled(true);
                    binding.progressBar.setVisibility(View.GONE);


                }


                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getContext(), "خطای تایم اوت در ثبت مشتری" + t.toString(), Toast.LENGTH_SHORT).show();

                    binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
                    binding.btnRegisterInformation.setEnabled(true);
                    binding.progressBar.setVisibility(View.GONE);


                }
            });


        } catch (NetworkOnMainThreadException ex) {

            Toast.makeText(getContext(), "خطا در ثبت مشتری" + ex.toString(), Toast.LENGTH_SHORT).show();
            binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.btnRegisterInformation.setEnabled(true);
            binding.progressBar.setVisibility(View.GONE);

        }


    }


}
