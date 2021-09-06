package ir.kitgroup.order.Fragments.MobileView;

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

import ir.kitgroup.order.DataBase.Account;
import ir.kitgroup.order.DataBase.User;
import ir.kitgroup.order.Fragments.Client.Register.MapFragment;
import ir.kitgroup.order.Fragments.Client.Register.RegisterFragment;
import ir.kitgroup.order.R;
import ir.kitgroup.order.classes.App;
import ir.kitgroup.order.classes.CustomProgress;
import ir.kitgroup.order.databinding.FragmentProfileBinding;
import ir.kitgroup.order.models.ModelLog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    private String type = "";
    private String address = "";
    private CustomProgress customProgress;
    private String userName = "";
    private String passWord = "";

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());

        Bundle bundle = getArguments();

        customProgress = CustomProgress.getInstance();
        try {

            address = bundle.getString("address");
            type = bundle.getString("type");
        } catch (Exception ignore) {

        }


        userName = Select.from(User.class).first().userName;
        passWord = Select.from(User.class).first().passWord;

        binding.tvAddress1.setText("");
        binding.tvAddress2.setText("");
        binding.tvMobile.setText("");
        binding.tvName.setText("");

        Account account = Select.from(Account.class).first();


        if (account != null) {
            binding.tvName.setText(account.N);
            binding.tvMobile.setText(account.M);

            if (account.ADR != null && !account.ADR.equals("")) {
                binding.tvAddress1.setText(account.ADR);

            } else {
                binding.tvAddress1.setText("ناموجود");
            }


            if (account.ADR1 != null && !account.ADR1.equals("")) {
                binding.tvAddress2.setText(account.ADR1);

            } else {
                binding.tvAddress2.setText("ناموجود");
            }


            if (type.equals("1") && !address.equals("")) {
                binding.tvAddress1.setText(address);
            } else if (type.equals("2") && !address.equals("")) {
                binding.tvAddress2.setText(address);
            }


            binding.lAdr1.setOnClickListener(v -> {

                Bundle bundle1 = new Bundle();
                bundle1.putString("edit_address", "2");
                bundle1.putString("type", "1");
                MapFragment mapFragment = new MapFragment();
                mapFragment.setArguments(bundle1);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, mapFragment).addToBackStack("MapF").commit();
            });

            binding.lAdr2.setOnClickListener(v -> {

                Bundle bundle1 = new Bundle();
                bundle1.putString("edit_address", "2");
                bundle1.putString("type", "2");
                MapFragment mapFragment = new MapFragment();
                mapFragment.setArguments(bundle1);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, mapFragment).addToBackStack("MapF").commit();

            });

            binding.ivBackFragment.setOnClickListener(v -> getFragmentManager().popBackStack());

            binding.btnRegisterInformation.setOnClickListener(
                    v -> {
                        Account accountORG = Select.from(Account.class).first();
                        Account account1 = new Account();
                        account1.I=accountORG.I;
                        account1.M = binding.tvMobile.getText().toString();
                        account1.N = binding.tvName.getText().toString();
                        if (!binding.tvAddress1.getText().toString().equals("ناموجود"))
                            account1.ADR = binding.tvAddress1.getText().toString();
                        if (!binding.tvAddress2.getText().toString().equals("ناموجود"))
                            account1.ADR1 = binding.tvAddress2.getText().toString();

                        List<Account> list = new ArrayList<>();
                        list.add(account1);
                        UpdateAccount(userName, passWord, list);


                    });

        }


        return binding.getRoot();
    }

    private static class JsonObjectAccount {

        public List<Account> Account;

    }

    private void UpdateAccount(String userName, String pass, List<Account> accounts) {


        try {
            customProgress.showProgress(getContext(), "در حال ویرایش اطلاعات", false);
            JsonObjectAccount jsonObjectAcc = new JsonObjectAccount();
            jsonObjectAcc.Account = accounts;


            Gson gson = new Gson();
            Type typeJsonObject = new TypeToken<JsonObjectAccount>() {
            }.getType();

            Call<String> call = App.api.UpdateAccount(userName, pass, gson.toJson(jsonObjectAcc, typeJsonObject), "");
            binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
            binding.btnRegisterInformation.setEnabled(false);

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
                        Account.saveInTx(accounts);

                        Toast.makeText(getActivity(), "ویرایش با موفقیت انجام شد", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                    }
                    binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
                    binding.btnRegisterInformation.setEnabled(true);
                    customProgress.hideProgress();


                }


                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getContext(), "خطای تایم اوت در ثبت مشتری" + t.toString(), Toast.LENGTH_SHORT).show();

                    binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
                    binding.btnRegisterInformation.setEnabled(true);
                    customProgress.hideProgress();


                }
            });


        } catch (NetworkOnMainThreadException ex) {

            Toast.makeText(getContext(), "خطا در ثبت مشتری" + ex.toString(), Toast.LENGTH_SHORT).show();
            binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.btnRegisterInformation.setEnabled(true);
            customProgress.hideProgress();

        }


    }

}
