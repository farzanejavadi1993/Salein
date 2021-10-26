package ir.kitgroup.saleinmeat.Fragments.MobileView;

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

import ir.kitgroup.saleinmeat.Activities.Classes.LauncherActivity;
import ir.kitgroup.saleinmeat.Adapters.CompanyAdapterList;
import ir.kitgroup.saleinmeat.R;
import ir.kitgroup.saleinmeat.databinding.FragmentCompanyBinding;
import ir.kitgroup.saleinmeat.models.ModelCompany;

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
        ArrayList<ModelCompany> list = new ArrayList<>(fullList());
        for (int i = 0; i < list.size(); i++) {
            boolean check = sharedPreferences.getBoolean(list.get(i).PACKAGEName, false);
            if (check)
                list.get(i).Check = true;
        }


        CompanyAdapterList companyAdapterList = new CompanyAdapterList(getActivity(), list, 2);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(companyAdapterList);

        companyAdapterList.setOnClickItemListener((company, check) ->
                sharedPreferences.edit().putBoolean(company.PACKAGEName, check).apply()
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

    private ArrayList<ModelCompany> fullList() {
        ArrayList<ModelCompany> companyList = new ArrayList<>();
        ModelCompany company_SaleIn = new ModelCompany();

        company_SaleIn.NameCompany = "سالین دمو";
        company_SaleIn.DESC = "سالین دمو ، راهنمای استفاده از اپلیکیشن";
        company_SaleIn.PACKAGEName = "ir.kitgroup.salein";
        company_SaleIn.Check = false;
        company_SaleIn.IP = "2.180.28.6:3333";
        company_SaleIn.USER = "administrator";
        company_SaleIn.PASS = "123";
        company_SaleIn.LAT = 36.326805522660464;
        company_SaleIn.LNG = 59.56450551053102;
        company_SaleIn.ICON = R.drawable.salein;


        ModelCompany company_top = new ModelCompany();
        company_top.IP = "188.158.121.253:9999";
        company_top.NameCompany = "رستوران تاپ کباب";
        company_top.DESC = "عرضه کننده بهترین غذاها";
        company_top.PACKAGEName = "ir.kitgroup.saleintop";
        company_top.Check = false;
        company_top.USER = "topkabab";
        company_top.PASS = "9929";
        company_top.LAT = 36.318805483696735;
        company_top.LNG = 59.555196457006296;
        company_top.ICON = R.drawable.top_png;


        ModelCompany company_meat = new ModelCompany();
        company_meat.IP = "109.125.133.149:9999";

        company_meat.PACKAGEName = "ir.kitgroup.saleinmeat";
        company_meat.NameCompany = "هایپر گوشت دنیوی";
        company_meat.DESC = "عرضه کننده انواع گوشت";
        company_meat.Check = false;
        company_meat.USER = "admin";
        company_meat.PASS = "0123";
        company_meat.LAT = 36.31947320471888;
        company_meat.LNG = 59.605469293071884;
        company_meat.ICON = R.drawable.meat_png;


        ModelCompany company_noon = new ModelCompany();
        company_noon.IP = "91.243.168.57:8080";
        company_noon.PACKAGEName = "ir.kitgroup.saleinnoon";
        company_noon.Check = false;
        company_noon.NameCompany = "کافه نون";
        company_noon.DESC = "بهترین کافه ، متنوع ترین محصولات";
        company_noon.USER = "admin";
        company_noon.PASS = "123";
        company_noon.LAT = 36.32650550170935;
        company_noon.LNG = 59.54865109150493;
        company_noon.ICON = R.drawable.noon;



        ModelCompany company_bahraman = new ModelCompany();
       company_bahraman.IP = "22";
       company_bahraman.PACKAGEName = "ir.kitgroup.saleinbahraman";
       company_bahraman.Check = false;
       company_bahraman.NameCompany = "زعفران بهرامن";
       company_bahraman.DESC = "تولید وصادرکننده بهترین زعفران خاورمیانه";
       company_bahraman.USER = "";
       company_bahraman.PASS = "";
       company_bahraman.LAT = 0.0;
       company_bahraman.LNG = 0.0;
       company_bahraman.ICON = R.drawable.bahraman;



        ModelCompany company_shaparak = new ModelCompany();
        company_shaparak.IP = "22";
        company_shaparak.PACKAGEName = "ir.kitgroup.saleinshaparak";
        company_shaparak.Check = false;
        company_shaparak.NameCompany = "کلینیک شاپرک";
        company_shaparak.DESC = "کلینیک تخصصی زیبایی";
        company_shaparak.USER = "";
        company_shaparak.PASS = "";
        company_shaparak.LAT = 0.0;
        company_shaparak.LNG = 0.0;
        company_shaparak.ICON = R.drawable.shaparak;


        ModelCompany company_ajil = new ModelCompany();
        company_ajil.IP = "22";
        company_ajil.PACKAGEName = "ir.kitgroup.saleinajil";
        company_ajil.Check = false;
        company_ajil.NameCompany = "آجیل حسینی";
        company_ajil.DESC = "محصولات خشکبار وآجیل";
        company_ajil.USER = "";
        company_ajil.PASS = "";
        company_ajil.LAT = 0.0;
        company_ajil.LNG = 0.0;
        company_ajil.ICON = R.drawable.ajil;


        ModelCompany company_tek = new ModelCompany();
        company_tek.IP = "22";
        company_tek.PACKAGEName = "ir.kitgroup.saleinatek";
        company_tek.Check = false;
        company_tek.NameCompany = "ایران تک";
        company_tek.DESC = "پخش تلفن همراه و تجهیزات";
        company_tek.USER = "";
        company_tek.PASS = "";
        company_tek.LAT = 0.0;
        company_tek.LNG = 0.0;
        company_tek.ICON = R.drawable.tek;



        ModelCompany company_andishe = new ModelCompany();
        company_andishe.IP = "22";
        company_andishe.PACKAGEName = "ir.kitgroup.saleinandishe";
        company_andishe.Check = false;
        company_andishe.NameCompany = "کلینیک اندیشه و رفتار";
        company_andishe.DESC = "کلینیک تخصصی مشاوره و روانشناسی";
        company_andishe.USER = "";
        company_andishe.PASS = "";
        company_andishe.LAT = 0.0;
        company_andishe.LNG = 0.0;
        company_andishe.ICON = R.drawable.white;

        ModelCompany company_ashreza = new ModelCompany();
        company_ashreza.IP = "22";
        company_ashreza.PACKAGEName = "ir.kitgroup.saleinashreza";
        company_ashreza.Check = false;
        company_ashreza.NameCompany = "آش رضا";
        company_ashreza.DESC = "غذاهای محلی مشهد";
        company_ashreza.USER = "";
        company_ashreza.PASS = "";
        company_ashreza.LAT = 0.0;
        company_ashreza.LNG = 0.0;
        company_ashreza.ICON = R.drawable.white;



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