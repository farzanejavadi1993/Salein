package ir.kitgroup.saleinOrder.Fragments;

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
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.saleinOrder.Activities.LauncherActivity;
import ir.kitgroup.saleinOrder.Adapters.CompanyAdapterList;
import ir.kitgroup.saleinOrder.Connect.API;
import ir.kitgroup.saleinOrder.DataBase.Account;
import ir.kitgroup.saleinOrder.DataBase.Company;
import ir.kitgroup.saleinOrder.R;
import ir.kitgroup.saleinOrder.classes.ConfigRetrofit;
import ir.kitgroup.saleinOrder.databinding.FragmentCompanyBinding;
import ir.kitgroup.saleinOrder.models.ModelAccount;
import ir.kitgroup.saleinOrder.models.ModelCompany;
import ir.kitgroup.saleinOrder.models.ModelLog;

@AndroidEntryPoint
public class CompanyFragment extends Fragment {

    @Inject
    SharedPreferences sharedPreferences;

    private FragmentCompanyBinding binding;
    private Company companyDemo;
    private Company companySelect;
    private API api;
    private StoriesFragment storiesFragment;
    private CompositeDisposable compositeDisposable;
    private ArrayList<Company> companies;
    private CompanyAdapterList companyAdapterList;
    private Account account;
    private ImageView ivIcon;
    private String ParentId = "";
    private Dialog dialog;
    private TextView textExitDialog;

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

        ((LauncherActivity) getActivity()).getVisibilityBottomBar(false);

        compositeDisposable = new CompositeDisposable();

        companies = new ArrayList<>();
        companyAdapterList = new CompanyAdapterList(companies, 2);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(companyAdapterList);

        api = ConfigRetrofit.getRetrofit("http://api.kitgroup.ir/api/REST/", true, 30).create(API.class);

        if (storiesFragment != null) {
            storiesFragment.binding.tvDemo.setOnClickListener(v -> {
                binding.progressbar.setVisibility(View.GONE);
                compositeDisposable.clear();
                companySelect = companyDemo;

                api = ConfigRetrofit.getRetrofit("http://" + companyDemo.IP1 + "/api/REST/", true, 30).create(API.class);

                String nameSave = sharedPreferences.getString(companyDemo.N, "");

                if (!nameSave.equals("")) {
                    Company.deleteAll(Company.class);
                    Company.saveInTx(companyDemo);
                    Bundle bundleMainOrder = new Bundle();
                    bundleMainOrder.putString("Inv_GUID", "");
                    bundleMainOrder.putString("Tbl_GUID", "");
                    bundleMainOrder.putString("Ord_TYPE", "");
                    MainOrderFragment mainOrderFragment = new MainOrderFragment();
                    mainOrderFragment.setArguments(bundleMainOrder);

                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mainOrderFragment, "MainOrderFragment").addToBackStack("MainOrderF").commit();
                } else {
                    getInquiryAccount1(companyDemo.N, companyDemo.USER, companyDemo.PASS, account.M);
                }
            });
        }

        companyAdapterList.setOnClickItemListener((company, parent, index, delete) ->
                {
                    if (parent) {
                        api = ConfigRetrofit.getRetrofit("http://api.kitgroup.ir/api/REST/", true, 30).create(API.class);
                        getCompany(company.I, index, delete);
                        return;
                    }

                    compositeDisposable.clear();
                    companySelect = company;
                    api = ConfigRetrofit.getRetrofit("http://" + company.IP1 + "/api/REST/", true, 30).create(API.class);
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
                        if (!ParentId.equals("")) {
                            getActivity().getSupportFragmentManager().popBackStack();
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mainOrderFragment, "MainOrderFragment").addToBackStack("MainOrderF").commit();
                    } else {
                        getInquiryAccount1(company.N, company.USER, company.PASS, account.M);
                    }
                });

        //region Cast Dialog
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);
        textExitDialog = dialog.findViewById(R.id.tv_message);
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
        //endregion Cast Dialog

        account = Select.from(Account.class).first();
        binding.ivBack.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());
        try {
            ParentId = getArguments().getString("ParentId");
            if (!ParentId.equals("")) {
                binding.toolbar.setVisibility(View.VISIBLE);
                binding.mainLayout.setRotationY(0);
                getCompany(ParentId, -1, false);
            }
        } catch (Exception ignored) {
            getCompany("", 0, false);
        }
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
                                    }
                                    else {
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
                                            if (!ParentId.equals("")) {
                                                getActivity().getSupportFragmentManager().popBackStack();
                                                getActivity().getSupportFragmentManager().popBackStack();
                                            }
                                            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mainOrderFragment, "MainOrderFragment").addToBackStack("MainOrderF").commit();
                                        }

                                        //user not register
                                        else {
                                            textExitDialog.setText(" شما مشترک " + name + " نیستید.آیا مشترک میشوید؟ ");
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
    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void getCompany(String ParentId, int index, boolean delete) {
        binding.progressbar.setVisibility(View.VISIBLE);
        try {
            compositeDisposable.add(
                    api.getCompany(ParentId)
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
                                        if (ParentId.equals("")) {
                                            companies.clear();
                                            for (int i = 0; i < iDs.getCompany().size(); i++) {
                                                ArrayList<Company> companyArrayList = new ArrayList<>(iDs.getCompany());
                                                int finalI = i;
                                                CollectionUtils.filter(companyArrayList, r -> iDs.getCompany().get(finalI).I.equals(r.PI));
                                                if (companyArrayList.size() > 0)
                                                    iDs.getCompany().get(finalI).Parent = true;
                                            }

                                            CollectionUtils.filter(iDs.getCompany(), r -> r.PI.equals(""));
                                            ArrayList<Company> demo = new ArrayList<>(iDs.getCompany());
                                            CollectionUtils.filter(demo, r -> r.INSK_ID.equals("ir.kitgroup.salein"));
                                            if (demo.size() > 0 && storiesFragment != null) {
                                                companyDemo = demo.get(0);
                                                storiesFragment.binding.tvDemo.setVisibility(View.VISIBLE);
                                            }
                                            CollectionUtils.filter(iDs.getCompany(), r -> !r.INSK_ID.equals("ir.kitgroup.salein"));
                                            companies.addAll(iDs.getCompany());
                                        }
                                        else {
                                            for (int i = 0; i < iDs.getCompany().size(); i++) {
                                                if (delete) {
                                                    ArrayList<Company> list = new ArrayList<>(companies);
                                                    int finalI = i;
                                                    CollectionUtils.filter(list, l -> l.I.equals(iDs.getCompany().get(finalI).I));
                                                    if (list.size() > 0)
                                                        companies.remove(list.get(0));
                                                } else
                                                    companies.add(index + 1, iDs.getCompany().get(i));
                                            }
                                        }
                                        companyAdapterList.notifyDataSetChanged();
                                    }
                                }
                                else {
                                    Toast.makeText(getActivity(), "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
                                }
                                binding.progressbar.setVisibility(View.GONE);
                                }, throwable -> {
                                Toast.makeText(getContext(), "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
                                binding.progressbar.setVisibility(View.GONE);

                            }));
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
                    api.addAccount(userName, pass, gson1.toJson(jsonObjectAcc, typeJsonObject), "")
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
                                    if (!ParentId.equals("")) {
                                        getActivity().getSupportFragmentManager().popBackStack();
                                        getActivity().getSupportFragmentManager().popBackStack();
                                    }
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

    public void setStoriesFragment(StoriesFragment storiesFragment) {
        this.storiesFragment = storiesFragment;
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
