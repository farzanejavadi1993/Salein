package ir.kitgroup.salein.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.button.MaterialButton;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;

import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.List;


import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import ir.kitgroup.salein.Adapters.CompanyAdapterList;
import ir.kitgroup.salein.Connect.API;
import ir.kitgroup.salein.DataBase.Account;

import ir.kitgroup.salein.R;


import ir.kitgroup.salein.classes.ConfigRetrofit;
import ir.kitgroup.salein.classes.Util;
import ir.kitgroup.salein.databinding.FragmentMyCompanyBinding;
import ir.kitgroup.salein.models.Company;
import ir.kitgroup.salein.models.ModelAccount;

import ir.kitgroup.salein.models.ModelLog;



@AndroidEntryPoint
public class MyCompanyFragment extends Fragment {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FragmentMyCompanyBinding binding;


    @Inject
    API api;


    @Inject
    ArrayList<Company> companies;

    @Inject
    SharedPreferences sharedPreferences;

    private NavController navController;


    private Company companySelect;


    private ConfigRetrofit configRetrofit;
    private Account account;


    private Dialog dialog;
    private TextView textExit;
    private ImageView ivIcon;


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentMyCompanyBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        navController = Navigation.findNavController(binding.getRoot());

        configRetrofit = new ConfigRetrofit();


        ArrayList<Company> listCompany = new ArrayList<>();
        for (int i = 0; i < companies.size(); i++) {
            boolean check = sharedPreferences.getBoolean(companies.get(i).namePackage, false);
            if (check)
                listCompany.add(companies.get(i));
        }

        if (listCompany.size() == 0)
            binding.txtError.setText("لیست  علاقه مندی های شما خالی می باشد با مراجعه به تب فروشگاه ها ،فروشگاه  مورد نظر خود را به لیست علاقه مندی ها اضافه کنید کنید");

        else
            binding.txtError.setText("");
        CompanyAdapterList companyAdapterList = new CompanyAdapterList(listCompany, 1);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(companyAdapterList);

      companyAdapterList.setOnClickItemListener((company, check) ->
                {
                    companySelect=company;
                    Util.RetrofitValue = false;
                    String nameSave = sharedPreferences.getString(company.nameCompany, "");
                    if (!nameSave.equals("")) {
                        NavDirections action = StoriesFragmentDirections.actionGoToMainOrderFragment();
                        navController.navigate(action);
                    } else {
                        api = configRetrofit.getRetrofit(company.baseUrl).create(API.class);
                        getInquiryAccount1(company.nameCompany, company.userName, company.passWord, account.M);
                    }
                }
        );





        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);

        textExit = dialog.findViewById(R.id.tv_message);
         ivIcon = dialog.findViewById(R.id.iv_icon);



        MaterialButton btnOk = dialog.findViewById(R.id.btn_ok);
        MaterialButton btnNo = dialog.findViewById(R.id.btn_cancel);
        btnNo.setOnClickListener(v -> dialog.dismiss());
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            ArrayList<Account> AccList = new ArrayList<>();
            AccList.add(account);
            addAccount(companySelect.userName, companySelect.passWord, AccList);


        });


        account = Select.from(Account.class).first();


    }


    @SuppressLint("SetTextI18n")
    private void getInquiryAccount1(String name, String userName, String passWord, String mobile) {
        binding.progressbar.setVisibility(View.VISIBLE);
        try {
            compositeDisposable.add(
                api.getInquiryAccount1(userName, passWord, mobile, "", "", 1, 1)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                Gson gson = new Gson();
                                Type typeIDs = new TypeToken<ModelAccount>() {
                                }.getType();
                                ModelAccount iDs;
                                try {
                                    iDs = gson.fromJson(jsonElement, typeIDs);
                                } catch (Exception e) {
                                    Toast.makeText(getActivity(), "مدل دریافت شده از مشتریان نامعتبر است.", Toast.LENGTH_SHORT).show();
                                    binding.progressbar.setVisibility(View.GONE);
                                    return;
                                }


                                if (iDs != null) {
                                    if (iDs.getAccountList() == null) {
                                        Type typeIDs0 = new TypeToken<ModelLog>() {
                                        }.getType();
                                        ModelLog iDs0 = gson.fromJson(jsonElement, typeIDs0);

                                        if (iDs0.getLogs() != null) {
                                            String description = iDs0.getLogs().get(0).getDescription();
                                            Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                                        }

                                    } else {

                                        //user is register
                                        if (iDs.getAccountList().size() > 0) {
                                            Account.deleteAll(Account.class);
                                            Account.saveInTx(iDs.getAccountList());
                                            NavDirections action = StoriesFragmentDirections.actionGoToMainOrderFragment();
                                            navController.navigate(action);

                                        }

                                        //user not register
                                        else {
                                            textExit.setText(" شما مشترک " + name + " نیستید.آیا مشترک میشوید؟ ");
                                            ivIcon.setImageResource(companySelect.imageDialog);
                                            dialog.show();
                                        }
                                        binding.progressbar.setVisibility(View.GONE);


                                    }
                                } else {
                                    Toast.makeText(getActivity(), "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
                                    binding.progressbar.setVisibility(View.GONE);
                                }


                            }, throwable -> {

                                Toast.makeText(getContext(), "فروشگاه تعطیل می باشد.", Toast.LENGTH_SHORT).show();
                                binding.progressbar.setVisibility(View.GONE);

                            })
            );
        } catch (Exception e) {
            Toast.makeText(getContext(), "خطا در دریافت اطلاعات مشتریان.", Toast.LENGTH_SHORT).show();

            binding.progressbar.setVisibility(View.GONE);
        }

    }


    private static class JsonObjectAccount {

        public List<Account> Account;

    }

    private void addAccount(String userName, String pass, List<Account> accounts) {


        JsonObjectAccount jsonObjectAcc = new JsonObjectAccount();
        jsonObjectAcc.Account = accounts;

        Gson gson1 = new Gson();
        Type typeJsonObject = new TypeToken<JsonObjectAccount>() {
        }.getType();

        try {
            compositeDisposable.add(
                 api.addAccount1(userName, pass, gson1.toJson(jsonObjectAcc, typeJsonObject), "")
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {

                                Gson gson = new Gson();
                                Type typeIDs = new TypeToken<ModelLog>() {
                                }.getType();
                                ModelLog iDs = gson.fromJson(jsonElement, typeIDs);

                                assert iDs != null;
                                int message = iDs.getLogs().get(0).getMessage();
                                String description = iDs.getLogs().get(0).getDescription();
                                Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                                if (message == 1) {

                                    NavDirections action = StoriesFragmentDirections.actionGoToMainOrderFragment();
                                    navController.navigate(action);

                                }
                                binding.progressbar.setVisibility(View.GONE);

                            }, throwable -> {

                                Toast.makeText(getContext(), "خطای تایم اوت در ثبت اطلاعات مشتری.", Toast.LENGTH_SHORT).show();
                                binding.progressbar.setVisibility(View.GONE);

                            })
            );
        } catch (Exception e) {
            Toast.makeText(getContext(), "خطا در ثبت اطلاعات مشتری.", Toast.LENGTH_SHORT).show();
            binding.progressbar.setVisibility(View.GONE);
        }


    }


}
