package ir.kitgroup.saleinmeat.Fragments;

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
import ir.kitgroup.saleinmeat.Adapters.CompanyAdapterList;
import ir.kitgroup.saleinmeat.Connect.API;
import ir.kitgroup.saleinmeat.DataBase.Account;
import ir.kitgroup.saleinmeat.DataBase.Company;
import ir.kitgroup.saleinmeat.R;
import ir.kitgroup.saleinmeat.classes.ConfigRetrofit;
import ir.kitgroup.saleinmeat.databinding.FragmentCompanyBinding;
import ir.kitgroup.saleinmeat.models.Config;
import ir.kitgroup.saleinmeat.models.ModelAccount;
import ir.kitgroup.saleinmeat.models.ModelCompany;
import ir.kitgroup.saleinmeat.models.ModelLog;


@AndroidEntryPoint
public class CompanyFragment extends Fragment {

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    Config config;



    private API api;

    private FragmentCompanyBinding binding;

    private CompositeDisposable compositeDisposable;


    private ArrayList<Company> companies;
    CompanyAdapterList companyAdapterList;


    private Company companySelect;



    private Account account;


    private Dialog dialog;
    private TextView textExit;
    private ImageView ivIcon;


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentCompanyBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        compositeDisposable = new CompositeDisposable();
        companies = new ArrayList<>();

        companyAdapterList = new CompanyAdapterList(companies, 2, config);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(companyAdapterList);

        companyAdapterList.setOnClickItemListener((company, check) ->
                {


                    compositeDisposable.clear();
                    companySelect = company;

                    String nameSave = sharedPreferences.getString(company.N, "");
                    if (!nameSave.equals("")) {
                        Company.deleteAll(Company.class);
                        Company.saveInTx(company);
                        Bundle bundleMainOrder = new Bundle();
                        bundleMainOrder.putString("Inv_GUID", "");
                        bundleMainOrder.putString("Tbl_GUID", "");
                        bundleMainOrder.putString("Ord_TYPE", "");
                        MainOrderFragment mainOrderFragment = new MainOrderFragment();
                        mainOrderFragment.setArguments(bundleMainOrder);
                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mainOrderFragment, "MainOrderFragment").addToBackStack("MainOrderF").commit();
                    } else {

                        api = ConfigRetrofit.getRetrofit("http://" + company.IP1 + "/api/REST/",true,30).create(API.class);
                        getInquiryAccount1(company.N, company.USER, company.PASS, account.M);
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
            addAccount(companySelect.USER, companySelect.PASS, AccList);


        });


        account = Select.from(Account.class).first();


        getCompany();
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


                                            Company.deleteAll(Company.class);
                                            Company.saveInTx(companySelect);


                                            sharedPreferences.edit().putString(name, "login").apply();


                                            Bundle bundleMainOrder = new Bundle();
                                            bundleMainOrder.putString("Inv_GUID", "");
                                            bundleMainOrder.putString("Tbl_GUID", "");
                                            bundleMainOrder.putString("Ord_TYPE", "");

                                            MainOrderFragment mainOrderFragment = new MainOrderFragment();
                                            mainOrderFragment.setArguments(bundleMainOrder);
                                            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mainOrderFragment, "MainOrderFragment").addToBackStack("MainOrderF").commit();

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


    @SuppressLint("SetTextI18n")
    private void getCompany() {
        binding.progressbar.setVisibility(View.VISIBLE);
        try {
            compositeDisposable.add(
                    api.getCompany("")
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {

                                Gson gson = new Gson();
                                Type typeIDs = new TypeToken<ModelCompany>() {
                                }.getType();
                                ModelCompany iDs;
                                try {
                                    iDs = gson.fromJson(jsonElement, typeIDs);
                                } catch (Exception e) {
                                    Toast.makeText(getActivity(), "مدل دریافت شده از شرکت ها نامعتبر است.", Toast.LENGTH_SHORT).show();
                                    binding.progressbar.setVisibility(View.GONE);
                                    return;
                                }


                                if (iDs != null) {
                                    if (iDs.getCompany() != null) {

                                        companies.clear();
                                        companies.addAll(iDs.getCompany());
                                        companyAdapterList.notifyDataSetChanged();
                                    }


                                } else {
                                    Toast.makeText(getActivity(), "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();

                                }
                                binding.progressbar.setVisibility(View.GONE);


                            }, throwable -> {

                                Toast.makeText(getContext(), "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
                                binding.progressbar.setVisibility(View.GONE);

                            })
            );
        } catch (Exception e) {
            Toast.makeText(getContext(), "خطا در ارتباط با سرور.", Toast.LENGTH_SHORT).show();

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

                                    Company.deleteAll(Company.class);
                                    Company.saveInTx(companySelect);


                                    Bundle bundleMainOrder = new Bundle();
                                    bundleMainOrder.putString("Inv_GUID", "");
                                    bundleMainOrder.putString("Tbl_GUID", "");
                                    bundleMainOrder.putString("Ord_TYPE", "");

                                    MainOrderFragment mainOrderFragment = new MainOrderFragment();
                                    mainOrderFragment.setArguments(bundleMainOrder);
                                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mainOrderFragment, "MainOrderFragment").addToBackStack("MainOrderF").commit();

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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        compositeDisposable.dispose();

        binding = null;
    }


    @Override
    public void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }

}
