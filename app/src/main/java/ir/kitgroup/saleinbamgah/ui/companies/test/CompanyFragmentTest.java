package ir.kitgroup.saleinbamgah.ui.companies.test;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;
import ir.kitgroup.saleinbamgah.Connect.CompanyViewModel;
import ir.kitgroup.saleinbamgah.Connect.MainViewModel;
import ir.kitgroup.saleinbamgah.DataBase.Company;
import ir.kitgroup.saleinbamgah.classes.HostSelectionInterceptor;
import ir.kitgroup.saleinbamgah.databinding.CompanyFragmentTestBinding;
import ir.kitgroup.saleinbamgah.models.Advertise;

@AndroidEntryPoint
public class CompanyFragmentTest extends Fragment {

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    HostSelectionInterceptor hostSelectionInterceptor;

    private CompanyFragmentTestBinding binding;
    private MainViewModel mainViewModel;
    private CompanyViewModel companyViewModel;


    private int pageMain = 1;


    private ArrayList<Company> allCompanies = new ArrayList<>();
    private CompanyAdapterTest companyAdapter;



    private SliderAdapter adapterBanner;
    private final ArrayList<Advertise> bannerList = new ArrayList<>();
    private int height;
    ArrayList<String> companiesId=new ArrayList<>();


    //region Override Method

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CompanyFragmentTestBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
        initSpecial();
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        companyViewModel = new ViewModelProvider(getActivity()).get(CompanyViewModel.class);
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);


        mainViewModel.getAllCompany("", pageMain);




        companyViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            companyViewModel.getResultMessage().setValue(null);
            Toasty.warning(getActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
        });

        mainViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {
            if (result == null) return;
            mainViewModel.getResultMessage().setValue(null);
            Toasty.warning(getActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
        });

        mainViewModel.getResultAllCompany().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;

            mainViewModel.getResultAllCompany().setValue(null);


            if (pageMain == 1)
                allCompanies.clear();

            if (result.size() > 0) {

                allCompanies.addAll(result);

              /*  //region Find Parent Items Of All Company And Set Parent Variable True
                for (int i = 0; i < result.size(); i++) {
                    ArrayList<Company> companyArrayList = new ArrayList<>(result);
                    int finalI = i;
                    CollectionUtils.filter(companyArrayList, r -> result.get(finalI).getI().equals(r.getPi()));
                    if (companyArrayList.size() > 0) {
                        result.get(finalI).Parent = true;
                        if (!allCompanies.containsAll(companyArrayList))
                            allCompanies.addAll(companyArrayList);
                    }
                }
                //endregion Find Parent Items Of All Company And Set Parent Variable True*/


                //region Filter list according to companyId That come From Pakhshyab
                String companyId = sharedPreferences.getString("companyId", "");
                if (!companyId.equals("")) {
                    CollectionUtils.filter(result, r -> r.getI().contains(companyId));
                    sharedPreferences.edit().putString("companyId", "").apply();
                    allCompanies.clear();
                    allCompanies.addAll(result);
                }
                //endregion Filter list according to companyId That come From Pakhshyab


                if (allCompanies.size()>0){
                    allCompanies.get(0).click=true;
                    companiesId.clear();
                    companiesId.add("4982DD86-2613-ED11-9B6C-000C29D9B371");

                    mainViewModel.getAdvsByCompanyId(companiesId, pageMain, "CB0F6319-5C4C-ED11-9BC9-000C29D9B371", "7883274E-0536-4A48-BF02-81F6F6A4163E");
                }

            }
            else if (result.size() == 0)
                Toasty.error(getContext(), "هیچ شرکتی یافت نشد", Toasty.LENGTH_SHORT).show();



            companyAdapter.notifyDataSetChanged();
        });


        mainViewModel.getResultCompanyAdvertisement().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            mainViewModel.getResultCompanyAdvertisement().setValue(null);


            bannerList.clear();

            if (result.size() > 0) {
                bannerList.addAll(result);
            }

            adapterBanner.notifyDataSetChanged();
            binding.slider.setSliderAdapter(adapterBanner);

        });


        /*companyViewModel.getResultInquiryAccount().observe(getViewLifecycleOwner(), result -> {

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
                resetFilter();

                // NavDirections action = CompanyFragmentDirections.actionGoToHomeFragment("");
                // Navigation.findNavController(binding.getRoot()).navigate(action);
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
            resetFilter();
            //  NavDirections action = CompanyFragmentDirections.actionGoToHomeFragment("");
            // Navigation.findNavController(binding.getRoot()).navigate(action);
        });*/

    }

    //endregion Override Method

    //region Method
    @SuppressLint("NotifyDataSetChanged")
    private void initRecyclerView() {

        companyAdapter = new CompanyAdapterTest(allCompanies,getActivity());

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        manager.setReverseLayout(true);

        binding.rvCompany.setLayoutManager(manager);
        binding.rvCompany.setAdapter(companyAdapter);

        companyAdapter.setOnClickItemListener(modelCompany -> {

            //region UnClick Old Item ProductLevel1 And Item ProductLevel2
            ArrayList<Company> resultPrdGrp1 = new ArrayList<>(allCompanies);
            CollectionUtils.filter(resultPrdGrp1, r -> r.click);
            if (resultPrdGrp1.size() > 0) {
                allCompanies.get(allCompanies.indexOf(resultPrdGrp1.get(0))).click = false;
            }
            //endregion UnClick Old Item ProductLevel1 And Item ProductLevel2


            //region Click New Item ProductLevel1
            ArrayList<Company> resultPrdGroup1_ = new ArrayList<>(allCompanies);
            CollectionUtils.filter(resultPrdGroup1_, r -> r.getI().equals(modelCompany.getI()));
            if (resultPrdGroup1_.size() > 0) {
                allCompanies.get(allCompanies.indexOf(resultPrdGroup1_.get(0))).click = true;
            }
            //endregion Click New Item ProductLevel1

            companyAdapter.notifyDataSetChanged();

            companiesId.clear();
           // companiesId.add(modelCompany.getI());

            companiesId.add("4982DD86-2613-ED11-9B6C-000C29D9B371");

            mainViewModel.getAdvsByCompanyId(companiesId, pageMain, "CB0F6319-5C4C-ED11-9BC9-000C29D9B371", "7883274E-0536-4A48-BF02-81F6F6A4163E");
        });



/*
        companyAdapter.setOnClickItemListener((company, parent, index, delete) ->
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

                resetFilter();
                NavDirections action;
                if (!ParentId.equals("")) {
                    action = AllCompanyFragmentDirections.actionGoToHomeFragment("");
                }
                //else
                //action = CompanyFragmentDirections.actionGoToHomeFragment("");


                //  Navigation.findNavController(binding.getRoot()).navigate(action);
            }
            //endregion If User Login To Company Selected


            //region If User Is Not Login To Company Selected
            else {
                binding.progressbar.setVisibility(View.VISIBLE);
                NAME = companySelect.getN();
                companyViewModel.getInquiryAccount(companySelect.getUser(), companySelect.getPass(), account.getM());
            }
            //endregion If User Is Not Login To Company Selected


            //endregion Enter To Home Fragment
        });*/

    }

    private void initSpecial() {

        adapterBanner = new SliderAdapter(getActivity(), bannerList);

      /*  adapterBanner.setOnClickListener((advertise, position) -> {
            NavDirections action = AdvertiseFragmentDirections.actionGoToDetailAdvertiseFragment(advertise.getI());
            Navigation.findNavController(binding.getRoot()).navigate(action);
        });*/

        binding.slider.setSliderAdapter(adapterBanner);
        binding.slider.setScrollTimeInSec(3);
        binding.slider.setIndicatorEnabled(true);
        binding.slider.setAutoCycle(true);
        binding.slider.startAutoCycle();


    }
    //endregion Method
}
