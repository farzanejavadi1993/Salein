package ir.kitgroup.saleinpakhshyab.ui.companies.test;

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
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.button.MaterialButton;
import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;
import ir.kitgroup.saleinpakhshyab.Connect.CompanyViewModel;
import ir.kitgroup.saleinpakhshyab.Connect.MainViewModel;
import ir.kitgroup.saleinpakhshyab.DataBase.Account;
import ir.kitgroup.saleinpakhshyab.DataBase.Company;
import ir.kitgroup.saleinpakhshyab.R;
import ir.kitgroup.saleinpakhshyab.classes.ConnectToServer;
import ir.kitgroup.saleinpakhshyab.classes.HostSelectionInterceptor;
import ir.kitgroup.saleinpakhshyab.classes.Util;
import ir.kitgroup.saleinpakhshyab.databinding.CompanyFragmentTestBinding;
import ir.kitgroup.saleinpakhshyab.models.Advertise;
import ir.kitgroup.saleinpakhshyab.models.ProductLevel1;
import ir.kitgroup.saleinpakhshyab.models.ProductLevel2;
import ir.kitgroup.saleinpakhshyab.models.Setting;

@AndroidEntryPoint
public class CompanyFragmentTest extends Fragment {

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    HostSelectionInterceptor hostSelectionInterceptor;

    private CompanyFragmentTestBinding binding;
    private MainViewModel mainViewModel;
    private CompanyViewModel companyViewModel;


    private Account account;
    private ConnectToServer connectToServer;

    private ArrayList<Company> allCompanies = new ArrayList<>();
    private CompanyAdapterTest companyAdapter;
    private Company companySelect;
    private boolean ACCSTP = false;

    private String idAccountServer;

    private Dialog dialog;
    private TextView textExitDialog;
    private String error = "";


    private int pageMain = 1;


    private SliderAdapter adapterBanner;
    private final ArrayList<Advertise> bannerList = new ArrayList<>();
    ArrayList<String> companiesId = new ArrayList<>();

    private ProductLevel1TestAdapter adapterProductLevel1;
    private ArrayList<ProductLevel1> productLevel1s = new ArrayList<>();


    private ProductLevel2TestAdapter adapterProductLevel2;
    private ArrayList<ProductLevel2> productLevel2s = new ArrayList<>();

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

        init();
        initRecyclerViewCompany();
        initSpecial();
        initRecyclerViewProductLevel();
        initRecyclerViewProductLeve2();
        castDialog();
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        companyViewModel = new ViewModelProvider(getActivity()).get(CompanyViewModel.class);
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);


        String companyId = sharedPreferences.getString("companyId", "");
        if (!companyId.equals(""))
            mainViewModel.getCompany(companyId);
        else
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

                CollectionUtils.filter(result, r -> !r.getInskId().equals("ir.kitgroup.salein"));

                ArrayList<Company> demo = new ArrayList<>(result);
                CollectionUtils.filter(demo, r -> r.getInskId().equals("ir.kitgroup.saleindemo"));
                if (demo.size() > 0) {
                    companySelect = demo.get(0);
                    Company.deleteAll(Company.class);
                    Company.saveInTx(demo);
                    String url = "http://" + demo.get(0).getIp1() + "/api/REST/";
                    connectToServer(url);
                    allCompanies.add(demo.get(0));

                }


                CollectionUtils.filter(result, r -> !r.getInskId().equals("ir.kitgroup.saleindemo"));
                allCompanies.addAll(1, result);
                allCompanies.get(0).click = true;

                companyAdapter.notifyDataSetChanged();
                getCompanyData();

            } else if (result.size() == 0)
                binding.txtError.setText("هیچ شرکتی یافت نشد");


            companyAdapter.notifyDataSetChanged();
        });

        mainViewModel.getResultCompanyAdvertisement().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            mainViewModel.getResultCompanyAdvertisement().setValue(null);


            bannerList.clear();

            if (result.size() > 0) {
                binding.layoutSlider.setVisibility(View.VISIBLE);
                bannerList.addAll(result);
            }

            adapterBanner.notifyDataSetChanged();
            binding.slider.setSliderAdapter(adapterBanner);

        });

        companyViewModel.getResultProductLevel1().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            companyViewModel.getResultProductLevel1().setValue(null);
            if (result.size() == 1) {
                binding.rvProductLevel1.setVisibility(View.GONE);
                companyViewModel.getProductLevel2(companySelect.getUser(), companySelect.getPass(), result.get(0).getI());
            } else {
                productLevel1s.clear();
                productLevel1s.addAll(result);
                binding.progressbar.setVisibility(View.GONE);
                adapterProductLevel1.notifyDataSetChanged();
            }
        });


        companyViewModel.getResultProductLevel2().observe(getViewLifecycleOwner(), result -> {
            if (result == null)
                return;
            companyViewModel.getResultProductLevel2().setValue(null);
            binding.progressbar.setVisibility(View.GONE);
            productLevel2s.clear();
            productLevel2s.addAll(result);
            adapterProductLevel2.notifyDataSetChanged();
        });


        companyViewModel.getResultInquiryAccount().observe(getViewLifecycleOwner(), result -> {

            if (result == null)
                return;

            companyViewModel.getResultInquiryAccount().setValue(null);

//            int message = result.getLog().getMessage();
//            String description = result.getLog().getDescription();
//
//            if (message == 2) {
//                error = " شما مشترک " + companySelect.getN() + " نیستید.آیا مشترک میشوید؟ ";
//                textExitDialog.setText(error);
//                dialog.show();
//            }
//            else if (message == 3) {
//                sharedPreferences.edit().putBoolean("disableAccount", true).apply();
//                Toasty.error(getActivity(),description,Toasty.LENGTH_SHORT).show();
//            }
//            else if (message == 4) {
//                resetFilter();
//
//                Account.deleteAll(Account.class);
//                Account.saveInTx(result.getAccountList());
//
//                Company.deleteAll(Company.class);
//                Company.saveInTx(companySelect);
//
//                sharedPreferences.edit().putString(companySelect.getN(), "").apply();
//                getCompanyData();
//
//            }
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
            Toasty.success(getActivity(), "حساب شما با موفقیت ایجاد شد.", Toasty.LENGTH_SHORT).show();
            companyViewModel.getResultAddAccount().setValue(null);
            Company.deleteAll(Company.class);
            Company.saveInTx(companySelect);

            getCompanyData();
        });

    }

    //endregion Override Method

    //region Method
    private void init() {
        connectToServer = new ConnectToServer();

        account = Select.from(Account.class).first();

        idAccountServer = sharedPreferences.getString("idServer", "");

    }

    @SuppressLint("NotifyDataSetChanged")
    private void initRecyclerViewCompany() {

        companyAdapter = new CompanyAdapterTest(allCompanies, getActivity());

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        manager.setReverseLayout(true);

        binding.rvCompany.setLayoutManager(manager);
        binding.rvCompany.setAdapter(companyAdapter);

        companyAdapter.setOnClickItemListener(company -> {
            sharedPreferences.edit().putBoolean("disableAccount", false).apply();
            binding.rvProductLevel1.setVisibility(View.VISIBLE);
            reset();
            binding.progressbar.setVisibility(View.VISIBLE);
            account.STAPP = false;
            companySelect = company;
            String url = "http://" + company.getIp1() + "/api/REST/";
            connectToServer(url);

            //region UnClick Old CompanyItem
            ArrayList<Company> resultPrdGrp1 = new ArrayList<>(allCompanies);
            CollectionUtils.filter(resultPrdGrp1, r -> r.click);
            if (resultPrdGrp1.size() > 0) {
                allCompanies.get(allCompanies.indexOf(resultPrdGrp1.get(0))).click = false;
            }
            //endregion UnClick Old CompanyItem

            //region Click New Item CompanyItem
            ArrayList<Company> resultPrdGroup1_ = new ArrayList<>(allCompanies);
            CollectionUtils.filter(resultPrdGroup1_, r -> r.getI().equals(company.getI()));
            if (resultPrdGroup1_.size() > 0) {
                allCompanies.get(allCompanies.indexOf(resultPrdGroup1_.get(0))).click = true;
            }
            //endregion Click New Item CompanyItem

            companyAdapter.notifyDataSetChanged();

            //region If User Login To Company Selected
            String nameSave = sharedPreferences.getString(company.getN(), "");
            if (nameSave.equals(""))
                companyViewModel.getInquiryAccount("1112", companySelect.getI(), account.getM());
            else
                getCompanyData();

            //endregion If User Login To Company Selected


        });


    }

    @SuppressLint("NotifyDataSetChanged")
    private void initRecyclerViewProductLevel() {
        adapterProductLevel1 = new ProductLevel1TestAdapter(getActivity(), productLevel1s);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getActivity(), 3) {
            @Override
            protected boolean isLayoutRTL() {
                return true;
            }
        };
        binding.rvProductLevel1.setLayoutManager(linearLayoutManager);
        binding.rvProductLevel1.setAdapter(adapterProductLevel1);
        adapterProductLevel1.SetOnItemClickListener(GUID -> {
            resetFilter();
            NavDirections action = CompanyFragmentTestDirections.actionGoToHomeFragment("", GUID, "");
            Navigation.findNavController(binding.getRoot()).navigate(action);
        });
    }


    @SuppressLint("NotifyDataSetChanged")
    private void initRecyclerViewProductLeve2() {
        adapterProductLevel2 = new ProductLevel2TestAdapter(getActivity(), productLevel2s);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(getActivity(), 3) {
            @Override
            protected boolean isLayoutRTL() {
                return true;
            }
        };
        binding.rvProductLevel2.setLayoutManager(linearLayoutManager);
        binding.rvProductLevel2.setAdapter(adapterProductLevel2);
        adapterProductLevel2.SetOnItemClickListener(GUID -> {
            resetFilter();
            NavDirections action = CompanyFragmentTestDirections.actionGoToHomeFragment("", "", GUID);
            Navigation.findNavController(binding.getRoot()).navigate(action);
        });
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

    private void connectToServer(String url) {
        connectToServer.connect(sharedPreferences, hostSelectionInterceptor, true, url);
    }

    private void resetFilter() {
        Company.deleteAll(Company.class);
        Company.saveInTx(companySelect);


        sharedPreferences.edit().putBoolean("vip", false).apply();
        sharedPreferences.edit().putBoolean("discount", false).apply();
    }

    private void getCompanyData() {
        companyViewModel.getProductLevel1(companySelect.getUser(), companySelect.getPass());
        companiesId.clear();
        companiesId.add(companySelect.getI());
        mainViewModel.getAdvsByCompanyId(companiesId, pageMain, Util.APPLICATION_ID, idAccountServer);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void reset() {
        error = "";
        binding.txtError.setText("");
        binding.txtError.setVisibility(View.GONE);

        binding.progressbar.setVisibility(View.VISIBLE);

        bannerList.clear();
        adapterBanner.notifyDataSetChanged();
        binding.layoutSlider.setVisibility(View.GONE);

        productLevel1s.clear();
        adapterProductLevel1.notifyDataSetChanged();

        productLevel2s.clear();
        adapterProductLevel2.notifyDataSetChanged();

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
        btnNo.setOnClickListener(v ->
                {
                    dialog.dismiss();
                    binding.txtError.setText(error);
                    binding.txtError.setVisibility(View.VISIBLE);
                    binding.progressbar.setVisibility(View.GONE);
                }

        );
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            binding.progressbar.setVisibility(View.VISIBLE);
            companyViewModel.getSetting(companySelect.getUser(), companySelect.getPass());
        });

    }
    //endregion Method
}
