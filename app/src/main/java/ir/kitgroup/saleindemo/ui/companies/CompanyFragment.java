package ir.kitgroup.saleindemo.ui.companies;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.button.MaterialButton;
import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;
import ir.kitgroup.saleindemo.classes.Util;
import ir.kitgroup.saleindemo.ui.launcher.homeItem.MainOrderFragment;
import ir.kitgroup.saleindemo.Connect.MyViewModel;
import ir.kitgroup.saleindemo.DataBase.Users;
import ir.kitgroup.saleindemo.DataBase.Company;
import ir.kitgroup.saleindemo.R;

import ir.kitgroup.saleindemo.classes.HostSelectionInterceptor;
import ir.kitgroup.saleindemo.databinding.FragmentCompanyBinding;

@AndroidEntryPoint
public class CompanyFragment extends Fragment {
    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    HostSelectionInterceptor hostSelectionInterceptor;

    private MyViewModel myViewModel;

    private FragmentCompanyBinding binding;
    private Company companyDemo;
    private Company companySelect;
    private StoriesFragment storiesFragment;
    private ArrayList<Company> companies;
    private CompanyAdapterList companyAdapterList;
    private Users account;

    private String ParentId = "";
    private Dialog dialog;
    private TextView textExitDialog;
    private Boolean DLT;
    private int INDX;
    private String NAME;

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


        sharedPreferences.edit().putBoolean("status", false).apply();
        hostSelectionInterceptor.setHostBaseUrl();

      //  ((LauncherActivity) getActivity()).getVisibilityBottomBar(false);

        companies = new ArrayList<>();
        companyAdapterList = new CompanyAdapterList(companies, 2);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(companyAdapterList);


        //region Go To SaleinDemo
        if (storiesFragment != null) {
            storiesFragment.binding.tvDemo.setOnClickListener(v -> {

                binding.progressbar.setVisibility(View.VISIBLE);
                companySelect = companyDemo;

                sharedPreferences.edit().putBoolean("status", false).apply();
                hostSelectionInterceptor.setHostBaseUrl();

                String nameSave = sharedPreferences.getString(companyDemo.N, "");

                if (!nameSave.equals("")) {
                    binding.progressbar.setVisibility(View.GONE);
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
                    NAME = companyDemo.N;
                    myViewModel.getInquiryAccount(companyDemo.USER,companyDemo.PASS,account.M);
                }
            });
        }
        //endregion Go To SaleinDemo


        companyAdapterList.setOnClickItemListener((company, parent, index, delete) ->
        {



            if (parent) {
                sharedPreferences.edit().putBoolean("status", false).apply();
                hostSelectionInterceptor.setHostBaseUrl();
                this.INDX = index;
                this.DLT = delete;
                binding.progressbar.setVisibility(View.VISIBLE);
                binding.progressbar.setVisibility(View.VISIBLE);
                myViewModel.getCompany(company.I);
                ParentId=company.I;

                return;
            }else {
                ParentId="";
            }
            Util.PRODUCTION_BASE_URL = "http://" + company.IP1 + "/api/REST/";
            sharedPreferences.edit().putBoolean("status", true).apply();
            hostSelectionInterceptor.setHostBaseUrl();
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
                binding.progressbar.setVisibility(View.VISIBLE);
                NAME = companySelect.N;
                myViewModel.getInquiryAccount(companySelect.USER,companySelect.PASS,account.M);
            }
        });

        //region Cast Dialog
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);
        textExitDialog = dialog.findViewById(R.id.tv_message);
        MaterialButton btnOk = dialog.findViewById(R.id.btn_ok);
        MaterialButton btnNo = dialog.findViewById(R.id.btn_cancel);
        btnNo.setOnClickListener(v -> dialog.dismiss());
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            ArrayList<Users> AccList = new ArrayList<>();
            AccList.add(account);
            binding.progressbar.setVisibility(View.VISIBLE);
            myViewModel.addAccount(companySelect.USER,companySelect.PASS,AccList);
        });
        //endregion Cast Dialog

        account = Select.from(Users.class).first();
        binding.ivBack.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());

    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        try {
            ParentId = getArguments().getString("ParentId");
            if (!ParentId.equals("")) {
                binding.toolbar.setVisibility(View.VISIBLE);
                binding.mainLayout.setRotationY(0);

                this.INDX = -1;
                this.DLT = false;
                binding.progressbar.setVisibility(View.VISIBLE);
                myViewModel.getCompany(ParentId);

            }
        }
        catch (Exception ignored) {

            this.INDX = 0;
            this.DLT = false;
            binding.progressbar.setVisibility(View.VISIBLE);
            myViewModel.getCompany("");
        }

        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {
            binding.progressbar.setVisibility(View.GONE);
            if (result == null) return;
            Toasty.warning(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
           // myViewModel.getResultMessage().setValue(null);
        });

        myViewModel.getResultCompany().observe(getViewLifecycleOwner(), result -> {

            binding.progressbar.setVisibility(View.GONE);
            if (result == null)
                return;


            myViewModel.getResultCompany().setValue(null);
            if (result.size() > 0) {

                Util.PRODUCTION_BASE_URL = "http://" + Select.from(Company.class).first().IP1 + "/api/REST/";
                sharedPreferences.edit().putBoolean("status", true).apply();
                hostSelectionInterceptor.setHostBaseUrl();


                if (ParentId.equals("")) {
                    companies.clear();
                    for (int i = 0; i < result.size(); i++) {
                        ArrayList<Company> companyArrayList = new ArrayList<>(result);
                        int finalI = i;
                        CollectionUtils.filter(companyArrayList, r -> result.get(finalI).I.equals(r.PI));
                        if (companyArrayList.size() > 0)
                            result.get(finalI).Parent = true;
                    }

                    CollectionUtils.filter(result, r -> r.PI.equals(""));
                    ArrayList<Company> demo = new ArrayList<>(result);
                    CollectionUtils.filter(demo, r -> r.INSK_ID.equals("ir.kitgroup.salein"));
                    if (demo.size() > 0 && storiesFragment != null) {
                        companyDemo = demo.get(0);
                        storiesFragment.binding.tvDemo.setVisibility(View.VISIBLE);
                    }
                    CollectionUtils.filter(result, r -> !r.INSK_ID.equals("ir.kitgroup.salein") && r.INSK_ID.contains("ir.kitgroup.salein"));
                    companies.addAll(result);
                } else {
                    for (int i = 0; i < result.size(); i++) {
                        if (DLT) {
                            ArrayList<Company> list = new ArrayList<>(companies);
                            int finalI = i;
                            CollectionUtils.filter(list, l -> l.I.equals(result.get(finalI).I));
                            if (list.size() > 0)
                                companies.remove(list.get(0));
                        } else
                            companies.add(INDX + 1, result.get(i));
                    }
                }
                companyAdapterList.notifyDataSetChanged();

            }
        });

        myViewModel.getResultInquiryAccount().observe(getViewLifecycleOwner(), result -> {
            binding.progressbar.setVisibility(View.GONE);
            if (result == null)
                return;
            //user is register
            if (result.size() > 0) {

                Users.deleteAll(Users.class);
                Users.saveInTx(result);

                Company.deleteAll(Company.class);
                Company.saveInTx(companySelect);


                sharedPreferences.edit().putString(NAME, "login").apply();

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
                textExitDialog.setText(" شما مشترک " + NAME + " نیستید.آیا مشترک میشوید؟ ");

                dialog.show();
            }
        });

        myViewModel.getResultAddAccount().observe(getViewLifecycleOwner(), result -> {
            binding.progressbar.setVisibility(View.GONE);
            if (result == null)
                return;
            Company.deleteAll(Company.class);
            Company.saveInTx(companySelect);

            Bundle bundleMainOrder = new Bundle();
            bundleMainOrder.putString("Inv_GUID", "");
            bundleMainOrder.putString("Tbl_GUID", "");
            bundleMainOrder.putString("Ord_TYPE", "");
            MainOrderFragment mainOrderFragment = new MainOrderFragment();
            mainOrderFragment.setArguments(bundleMainOrder);

            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mainOrderFragment, "MainOrderFragment").addToBackStack("MainOrderF").commit();
        });
    }


    public static class JsonObjectAccount {
        public List<Users> Account;
    }

    public void setStoriesFragment(StoriesFragment storiesFragment) {
        this.storiesFragment = storiesFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
