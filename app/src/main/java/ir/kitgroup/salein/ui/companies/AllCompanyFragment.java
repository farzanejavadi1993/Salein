package ir.kitgroup.salein.ui.companies;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
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
import ir.kitgroup.salein.Connect.CompanyViewModel;
import ir.kitgroup.salein.Connect.MainViewModel;
import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.classes.ConnectToServer;


import ir.kitgroup.salein.DataBase.Company;
import ir.kitgroup.salein.R;

import ir.kitgroup.salein.classes.HostSelectionInterceptor;

import ir.kitgroup.salein.databinding.FragmentAllCompanyBinding;
import ir.kitgroup.salein.models.Setting;

@AndroidEntryPoint
public class AllCompanyFragment extends Fragment {

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    HostSelectionInterceptor hostSelectionInterceptor;
    private FragmentAllCompanyBinding binding;

    private MainViewModel mainViewModel;
    private CompanyViewModel companyViewModel;

    private ConnectToServer connectToServer;

    private Company companyDemo;
    private Company companySelect;
    private CompanyFragment companyFragment;

    private ArrayList<Company> parentCompanies = new ArrayList<>();
    private ArrayList<Company> childCompany;

    private CompanyAdapterList companyAdapterList;

    private Account account;
    private String ParentId = "";
    private Dialog dialog;
    private TextView textExitDialog;

    private String NAME;


    private boolean ACCSTP = false;
    private int pageMain = 1;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentAllCompanyBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        init();
        initRecyclerView();
        navigateToSaleinDemo();
        castDialog();
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        companyViewModel = new ViewModelProvider(getActivity()).get(CompanyViewModel.class);
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        try {
            ParentId = AllCompanyFragmentArgs.fromBundle(getArguments()).getParentId();
            binding.toolbar.setVisibility(View.VISIBLE);
            binding.mainLayout.setRotationY(0);
            binding.progressbar.setVisibility(View.VISIBLE);
            mainViewModel.getAllCompany(ParentId, pageMain);
        } catch (Exception ignored) {
            binding.progressbar.setVisibility(View.VISIBLE);
            mainViewModel.getAllCompany("", pageMain);
        }


        companyViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            binding.progressbar.setVisibility(View.GONE);
            companyViewModel.getResultMessage().setValue(null);
            Toasty.warning(getActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
        });

        mainViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {

            if (result == null) return;
            binding.progressbar.setVisibility(View.GONE);
            mainViewModel.getResultMessage().setValue(null);
            Toasty.warning(getActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
        });

        mainViewModel.getResultAllCompany().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;
            binding.progressbar.setVisibility(View.GONE);
            mainViewModel.getResultAllCompany().setValue(null);


            if (result.size() > 0) {
                parentCompanies.clear();

                if (!ParentId.equals("")) {
                    parentCompanies.addAll(result);
                    companyAdapterList.notifyDataSetChanged();
                    return;
                }


                //region Find Parent Items Of All Company And Set Parent Variable True
                for (int i = 0; i < result.size(); i++) {
                    ArrayList<Company> companyArrayList = new ArrayList<>(result);
                    int finalI = i;
                    CollectionUtils.filter(companyArrayList, r ->
                            result.get(finalI).getI().equals(r.getPi())
                    );
                    if (companyArrayList.size() > 0) {
                        result.get(finalI).Parent = true;
                        childCompany.addAll(companyArrayList);

                    }
                }

                //endregion Find Parent Items Of All Company And Set Parent Variable True

                //region Filter Demo Company
                ArrayList<Company> demo = new ArrayList<>(result);
                CollectionUtils.filter(demo, r -> r.getInskId().equals("ir.kitgroup.saleindemo"));
                if (demo.size() > 0 && companyFragment != null) {
                    companyDemo = demo.get(0);
                    companyFragment.binding.tvDemo.setVisibility(View.VISIBLE);
                }
                //endregion Filter Demo Company

                //region Filter Companies That Is Not Child Or Companies That Is  Parent
                CollectionUtils.filter(result, r -> r.getPi().equals("") && !r.getInskId().equals("ir.kitgroup.saleindemo") && !r.getInskId().equals("ir.kitgroup.salein"));
                //endregion Filter Companies That Is Not Child Or Companies That Is  Parent

                //region Filter list according to companyId That come From Pakhshyab
                String companyId = sharedPreferences.getString("companyId", "");
                if (!companyId.equals("")) {
                    CollectionUtils.filter(result, r -> r.getI().contains(companyId));
                    sharedPreferences.edit().putString("companyId", "").apply();
                }
                //endregion Filter list according to companyId That come From Pakhshyab

                parentCompanies.addAll(result);
                companyAdapterList.notifyDataSetChanged();
            }
        });


        companyViewModel.getResultInquiryAccount().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;

            companyViewModel.getResultInquiryAccount().setValue(null);

            //user is register
            if (result.size() > 0) {
                Account.deleteAll(Account.class);
                Account.saveInTx(result);

                Company.deleteAll(Company.class);
                Company.saveInTx(companySelect);

                sharedPreferences.edit().putString(NAME, "login").apply();
                NavDirections action = CompanyFragmentDirections.actionGoToHomeFragment("");
                Navigation.findNavController(binding.getRoot()).navigate(action);
            }
            //user not register
            else {
                textExitDialog.setText(" شما مشترک " + NAME + " نیستید.آیا مشترک میشوید؟ ");
                dialog.show();
            }
        });

        companyViewModel.getResultSetting().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            companyViewModel.getResultSetting().setValue(null);
            List<Setting> settingsList = new ArrayList<>(result);
            if (settingsList.size() > 0) {
                String accStp = settingsList.get(0).ACC_STATUS_APP;
                ACCSTP = !accStp.equals("0");
                account.STAPP = ACCSTP;
                ArrayList<Account> AccList = new ArrayList<>();
                AccList.add(account);
                companyViewModel.addAccount(companySelect.getUser(), companySelect.getPass(), AccList);
            }
        });

        companyViewModel.getResultAddAccount().observe(getViewLifecycleOwner(), result -> {
            binding.progressbar.setVisibility(View.GONE);
            if (result == null)
                return;
            companyViewModel.getResultAddAccount().setValue(null);
            Company.deleteAll(Company.class);
            Company.saveInTx(companySelect);
            NavDirections action = CompanyFragmentDirections.actionGoToHomeFragment("");
            Navigation.findNavController(binding.getRoot()).navigate(action);
        });
    }


    public void setCompanyFragment(CompanyFragment companyFragment) {
        this.companyFragment = companyFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }


    private void init() {
        childCompany = new ArrayList<>();
        connectToServer = new ConnectToServer();
        account = Select.from(Account.class).first();

        binding.ivBack.setOnClickListener(v ->
                Navigation.findNavController(binding.getRoot()).popBackStack());
    }


    private void initRecyclerView() {
        companyAdapterList = new CompanyAdapterList(parentCompanies, 2);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        binding.recyclerView.setAdapter(companyAdapterList);

        companyAdapterList.setOnClickItemListener((company, parent, index, delete) ->
        {
            account.STAPP = false;

            //region Get Child From This Parent
            if (parent) {
                ArrayList<Company> childs = new ArrayList<>(childCompany);
                CollectionUtils.filter(childs, ch -> ch.getPi().equals(company.getI()));

                if (childs.size() > 0  ) {
                   if (parentCompanies.containsAll(childs)){
                       parentCompanies.removeAll(childs);
                       companyAdapterList.notifyItemRangeRemoved(index + 1, childs.size());
                    }
                    else{
                        parentCompanies.addAll(index + 1, childs);
                        companyAdapterList.notifyItemRangeInserted(index + 1, childs.size());
                    }

                }
                return;
            }
            //endregion Get Child From This Parent


            //region Enter To Home Fragment
            String url = "http://" + company.getIp1() + "/api/REST/";
            connectToServer(url);
            companySelect = company;


            //region If User Login To Company Selected
            String nameSave = sharedPreferences.getString(company.getN(), "");
            if (!nameSave.equals("")) {
                Company.deleteAll(Company.class);
                Company.saveInTx(company);
                NavDirections action;
                if (!ParentId.equals("")) {
                    action = AllCompanyFragmentDirections.actionGoToHomeFragment("");
                } else
                    action = CompanyFragmentDirections.actionGoToHomeFragment("");

//
//                if (!parent)
//                    ParentId = "";

                Navigation.findNavController(binding.getRoot()).navigate(action);
            }
            //endregion If User Login To Company Selected


            //region If User Is Not Login To Company Selected
            else {
//                if (!parent)
//                    ParentId = "";
                binding.progressbar.setVisibility(View.VISIBLE);
                NAME = companySelect.getN();
                companyViewModel.getInquiryAccount(companySelect.getUser(), companySelect.getPass(), account.getM());
            }
            //endregion If User Is Not Login To Company Selected


            //endregion Enter To Home Fragment
        });

    }


    private void navigateToSaleinDemo() {
        if (companyFragment != null) {
            companyFragment.binding.tvDemo.setOnClickListener(v -> {
                binding.progressbar.setVisibility(View.VISIBLE);
                companySelect = companyDemo;
                String url = "http://" + companyDemo.getIp1() + "/api/REST/";

                connectToServer(url);

                String nameSave = sharedPreferences.getString(companyDemo.getN(), "");

                if (!nameSave.equals("")) {
                    binding.progressbar.setVisibility(View.GONE);
                    Company.deleteAll(Company.class);
                    Company.saveInTx(companyDemo);
                    NavDirections action = CompanyFragmentDirections.actionGoToHomeFragment("");
                    Navigation.findNavController(binding.getRoot()).navigate(action);
                } else {
                    NAME = companyDemo.getN();
                    companyViewModel.getInquiryAccount(companyDemo.getUser(), companyDemo.getPass(), account.getM());
                }
            });
        }
    }


    private void castDialog() {

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

            binding.progressbar.setVisibility(View.VISIBLE);
            companyViewModel.getSetting(companySelect.getUser(), companySelect.getPass());
        });

    }


    private void connectToServer(String url) {
        connectToServer.connect(sharedPreferences, hostSelectionInterceptor, true, url);
    }

}
