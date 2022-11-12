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
import androidx.lifecycle.ViewModelStoreOwner;
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

    private ConnectToServer connectToServer;
    private CompanyViewModel companyViewModel;
    private MainViewModel mainViewModel;
    private FragmentAllCompanyBinding binding;

    private Company companyDemo;
    private Company companySelect;
    private CompanyFragment storiesFragment;
    private ArrayList<Company> companies;
    private CompanyAdapterList companyAdapterList;
    private Account account;

    private String ParentId = "";
    private Dialog dialog;
    private TextView textExitDialog;
    private Boolean DLT;
    private int INDX;
    private String NAME;

    private int pageMain = 1;

    private boolean ACCSTP = false;

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


        connectToServer = new ConnectToServer();

        //region Config RecyclerView
        companies = new ArrayList<>();
        companyAdapterList = new CompanyAdapterList(companies, 2);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(companyAdapterList);

        companyAdapterList.setOnClickItemListener((company, parent, index, delete) ->
        {

            account.STAPP = false;
            //region Get Child From This Parent
            if (parent) {
                this.INDX = index;
                this.DLT = delete;
                binding.progressbar.setVisibility(View.VISIBLE);
                binding.progressbar.setVisibility(View.VISIBLE);
                mainViewModel.getAllCompany(company.getI(), pageMain);
                ParentId = company.getI();
                return;
            }
            //endregion Get Child From This Parent



            //region Enter To Home Fragment
            String url = "http://" + company.getIp1() + "/api/REST/";
            connectToServer.connect(sharedPreferences, hostSelectionInterceptor, true, url);
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


                if (!parent)
                    ParentId = "";

                Navigation.findNavController(binding.getRoot()).navigate(action);
            }
            //endregion If User Login To Company Selected


            //region If User Is Not Login To Company Selected
            else {
                if (!parent)
                    ParentId = "";
                binding.progressbar.setVisibility(View.VISIBLE);
                NAME = companySelect.getN();
                companyViewModel.getInquiryAccount(companySelect.getUser(), companySelect.getPass(), account.getM());
            }
            //endregion If User Is Not Login To Company Selected


            //endregion Enter To Home Fragment
        });
        //endregion Config RecyclerView


        //region Go To SaleinDemo
        if (storiesFragment != null) {
            storiesFragment.binding.tvDemo.setOnClickListener(v -> {
                binding.progressbar.setVisibility(View.VISIBLE);
                companySelect = companyDemo;


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


        //endregion Go To SaleinDemo

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

            binding.progressbar.setVisibility(View.VISIBLE);
            companyViewModel.getSetting(companySelect.getUser(), companySelect.getPass());
        });
        //endregion Cast Dialog

        account = Select.from(Account.class).first();

        binding.ivBack.setOnClickListener(v ->
                Navigation.findNavController(binding.getRoot()).popBackStack());

    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        companyViewModel = new ViewModelProvider(getActivity()).get(CompanyViewModel.class);
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        try {

            ParentId = AllCompanyFragmentArgs.fromBundle(getArguments()).getParentId();

            if (!ParentId.equals("")) {
                binding.toolbar.setVisibility(View.VISIBLE);
                binding.mainLayout.setRotationY(0);
                this.INDX = -1;
                this.DLT = false;
                binding.progressbar.setVisibility(View.VISIBLE);
                mainViewModel.getAllCompany(ParentId, pageMain);

            }

        } catch (Exception ignored) {
            this.INDX = 0;
            this.DLT = false;
            binding.progressbar.setVisibility(View.VISIBLE);
            mainViewModel.getAllCompany("", pageMain);
        }


        companyViewModel.getResultMessage().observe(storiesFragment.getViewLifecycleOwner(), result -> {
            binding.progressbar.setVisibility(View.GONE);
            if (result == null) return;
            companyViewModel.getResultMessage().setValue(null);
            Toasty.warning(getActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
        });

        mainViewModel.getResultMessage().observe(storiesFragment.getViewLifecycleOwner(), result -> {
            binding.progressbar.setVisibility(View.GONE);
            if (result == null) return;
            mainViewModel.getResultMessage().setValue(null);
            Toasty.warning(getActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
        });

        mainViewModel.getResultAllCompany().observe(storiesFragment.getViewLifecycleOwner(), result -> {
            binding.progressbar.setVisibility(View.GONE);
            if (result == null)
                return;
            mainViewModel.getResultAllCompany().setValue(null);


            if (result.size() > 0) {


                //regionGet All Company
                if (ParentId.equals("")) {
                    companies.clear();

                    //region Find Parent Items Of All Company And Set Parent Variable True
                    for (int i = 0; i < result.size(); i++) {
                        ArrayList<Company> companyArrayList = new ArrayList<>(result);
                        int finalI = i;
                        CollectionUtils.filter(companyArrayList, r ->
                                result.get(finalI).getI().equals(r.getPi())
                        );
                        if (companyArrayList.size() > 0)
                            result.get(finalI).Parent = true;
                    }
                    //endregion Find Parent Items Of All Company And Set Parent Variable True

                    //region Filter Companies That Is Not Child Or Companies That Is  Parent
                    CollectionUtils.filter(result, r -> r.getPi().equals(""));
                    //endregion Filter Companies That Is Not Child Or Companies That Is  Parent

                    //region Filter Demo Company

                    ArrayList<Company> demo = new ArrayList<>(result);
                    CollectionUtils.filter(demo, r -> r.getInskId().equals("ir.kitgroup.salein"));
                    if (demo.size() > 0 && storiesFragment != null) {
                        companyDemo = demo.get(0);
                        storiesFragment.binding.tvDemo.setVisibility(View.VISIBLE);
                    }
                    CollectionUtils.filter(result,
                            r ->
                                    !r.getInskId().equals("ir.kitgroup.salein") &&
                                            !r.getInskId().equals("ir.kitgroup.saleindemo") &&
                                            r.getInskId().contains("ir.kitgroup.salein")
                    );

                    //endregion Filter Demo Company

                    String companyId = sharedPreferences.getString("companyId", "");
                    if (!companyId.equals("")) {
                        CollectionUtils.filter(result, r -> r.getI().contains(companyId));
                        sharedPreferences.edit().putString("companyId", "").apply();
                    }
                    companies.addAll(result);
                }
                //endregionGet All Company


                //region Get Child Company
                else {
                    for (int i = 0; i < result.size(); i++) {
                        if (DLT) {
                            ArrayList<Company> list = new ArrayList<>(companies);
                            int finalI = i;
                            CollectionUtils.filter(list, l -> l.getI().equals(result.get(finalI).getI()));
                            if (list.size() > 0)
                                companies.remove(list.get(0));
                        } else
                            companies.add(INDX + 1, result.get(i));
                    }
                }
                //endregion Get Child Company

                companyAdapterList.notifyDataSetChanged();

            }
        });

        companyViewModel.getResultInquiryAccount().observe(storiesFragment.getViewLifecycleOwner(), result -> {
            binding.progressbar.setVisibility(View.GONE);
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

        companyViewModel.getResultSetting().observe(storiesFragment.getViewLifecycleOwner(), result -> {
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

        companyViewModel.getResultAddAccount().observe(storiesFragment.getViewLifecycleOwner(), result -> {
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


    public void setStoriesFragment(CompanyFragment storiesFragment) {
        this.storiesFragment = storiesFragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }


}
