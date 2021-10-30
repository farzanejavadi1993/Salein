package ir.kitgroup.salein.Fragments.MobileView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ir.kitgroup.salein.Activities.Classes.LauncherActivity;
import ir.kitgroup.salein.Adapters.CompanyAdapterList;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.databinding.FragmentCompanyBinding;
import ir.kitgroup.salein.models.Company;


public class CompanyFragment extends Fragment {

    private FragmentCompanyBinding binding;
    private SharedPreferences sharedPreferences;

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


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ArrayList<Company> list = new ArrayList<>(fullList());
        for (int i = 0; i < list.size(); i++) {
            boolean check = sharedPreferences.getBoolean(list.get(i).namePackage, false);
            if (check)
                list.get(i).CheckUser = true;
        }


        CompanyAdapterList companyAdapterList = new CompanyAdapterList(getActivity(), list, 2);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(companyAdapterList);

        companyAdapterList.setOnClickItemListener((company, check) ->
                sharedPreferences.edit().putBoolean(company.namePackage, check).apply()
        );

        binding.btnRegisterCompany.setOnClickListener(v -> {

            LauncherActivity mainActivity = (LauncherActivity) v.getContext();
            Fragment fragment = mainActivity.getSupportFragmentManager().findFragmentByTag("StoriesFragment");
            if (fragment instanceof StoriesFragment) {
                StoriesFragment fgf = (StoriesFragment) fragment;
                fgf.setMyCompany();
            }
        });
    }

    private ArrayList<Company> fullList() {
        ArrayList<Company> companyList = new ArrayList<>();
        Company company_SaleIn = new Company();
        company_SaleIn.nameCompany = "سالین دمو";
        company_SaleIn.Description = "سالین دمو ، راهنمای استفاده از اپلیکیشن";
        company_SaleIn.namePackage = "ir.kitgroup.salein";
        company_SaleIn.CheckUser = false;
        company_SaleIn.ipLocal = "2.180.28.6:3333";
        company_SaleIn.userName = "administrator";
        company_SaleIn.passWord = "123";
        company_SaleIn.lat = 36.326805522660464;
        company_SaleIn.lng = 59.56450551053102;
        company_SaleIn.imageLogo = R.drawable.salein;


        Company company_top = new Company();
        company_top.ipLocal = "188.158.121.253:9999";
        company_top.nameCompany = "رستوران تاپ کباب";
        company_top.Description = "عرضه کننده بهترین غذاها";
        company_top.namePackage = "ir.kitgroup.saleintop";
        company_top.CheckUser = false;
        company_top.userName = "topkabab";
        company_top.passWord = "9929";
        company_top.lat = 36.318805483696735;
        company_top.lng = 59.555196457006296;
        company_top.imageLogo = R.drawable.top_png;


        Company company_meat = new Company();
        company_meat.ipLocal = "109.125.133.149:9999";

        company_meat.namePackage = "ir.kitgroup.saleinmeat";
        company_meat.nameCompany = "هایپر گوشت دنیوی";
        company_meat.Description = "عرضه کننده انواع گوشت";
        company_meat.CheckUser = false;
        company_meat.userName = "admin";
        company_meat.passWord = "0123";
        company_meat.lat = 36.31947320471888;
        company_meat.lng = 59.605469293071884;
        company_meat.imageLogo = R.drawable.meat_png;


        Company company_noon = new Company();
        company_noon.ipLocal = "91.243.168.57:8080";
        company_noon.namePackage = "ir.kitgroup.saleinnoon";
        company_noon.CheckUser = false;
        company_noon.nameCompany = "کافه نون";
        company_noon.Description = "بهترین کافه ، متنوع ترین محصولات";
        company_noon.userName = "admin";
        company_noon.passWord = "123";
        company_noon.lat = 36.32650550170935;
        company_noon.lng = 59.54865109150493;
        company_noon.imageLogo = R.drawable.noon;



        Company company_bahraman = new Company();
       company_bahraman.ipLocal = "22";
       company_bahraman.namePackage = "ir.kitgroup.saleinbahraman";
       company_bahraman.CheckUser = false;
       company_bahraman.nameCompany = "زعفران بهرامن";
       company_bahraman.Description = "تولید وصادرکننده بهترین زعفران خاورمیانه";
       company_bahraman.userName = "";
       company_bahraman.passWord = "";
       company_bahraman.lat = 0.0;
       company_bahraman.lng = 0.0;
       company_bahraman.imageLogo = R.drawable.bahraman;



        Company company_shaparak = new Company();
        company_shaparak.ipLocal = "22";
        company_shaparak.namePackage = "ir.kitgroup.saleinshaparak";
        company_shaparak.CheckUser = false;
        company_shaparak.nameCompany = "کلینیک شاپرک";
        company_shaparak.Description = "کلینیک تخصصی زیبایی";
        company_shaparak.userName = "";
        company_shaparak.passWord = "";
        company_shaparak.lat = 0.0;
        company_shaparak.lng = 0.0;
        company_shaparak.imageLogo = R.drawable.shaparak;


        Company company_ajil = new Company();
        company_ajil.ipLocal = "22";
        company_ajil.namePackage = "ir.kitgroup.saleinajil";
        company_ajil.CheckUser = false;
        company_ajil.nameCompany = "آجیل حسینی";
        company_ajil.Description = "محصولات خشکبار وآجیل";
        company_ajil.userName = "";
        company_ajil.passWord = "";
        company_ajil.lat = 0.0;
        company_ajil.lng = 0.0;
        company_ajil.imageLogo = R.drawable.ajil;


        Company company_tek = new Company();
        company_tek.ipLocal = "22";
        company_tek.namePackage = "ir.kitgroup.saleinatek";
        company_tek.CheckUser = false;
        company_tek.nameCompany = "ایران تک";
        company_tek.Description = "پخش تلفن همراه و تجهیزات";
        company_tek.userName = "";
        company_tek.passWord = "";
        company_tek.lat = 0.0;
        company_tek.lng = 0.0;
        company_tek.imageLogo = R.drawable.tek;



        Company company_andishe = new Company();
        company_andishe.ipLocal = "22";
        company_andishe.namePackage = "ir.kitgroup.saleinandishe";
        company_andishe.CheckUser = false;
        company_andishe.nameCompany = "کلینیک اندیشه و رفتار";
        company_andishe.Description = "کلینیک تخصصی مشاوره و روانشناسی";
        company_andishe.userName = "";
        company_andishe.passWord = "";
        company_andishe.lat = 0.0;
        company_andishe.lng = 0.0;
        company_andishe.imageLogo = R.drawable.white;

        Company company_ashreza = new Company();
        company_ashreza.ipLocal = "22";
        company_ashreza.namePackage = "ir.kitgroup.saleinashreza";
        company_ashreza.CheckUser = false;
        company_ashreza.nameCompany = "آش رضا";
        company_ashreza.Description = "غذاهای محلی مشهد";
        company_ashreza.userName = "";
        company_ashreza.passWord = "";
        company_ashreza.lat = 0.0;
        company_ashreza.lng = 0.0;
        company_ashreza.imageLogo = R.drawable.white;



        companyList.clear();
        companyList.add(company_SaleIn);
        companyList.add(company_top);
        companyList.add(company_meat);
        companyList.add(company_noon);
        companyList.add(company_bahraman);
        companyList.add(company_ajil);
        companyList.add(company_shaparak);
        companyList.add(company_tek);
        companyList.add(company_andishe);
        companyList.add(company_ashreza);


        return companyList;
    }

}
