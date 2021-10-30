package ir.kitgroup.salein.Fragments.MobileView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.preference.PreferenceManager;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.button.MaterialButton;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.salein.Adapters.CompanyAdapterList;
import ir.kitgroup.salein.Connect.API;
import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.DataBase.User;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.Util.Util;
import ir.kitgroup.salein.classes.App;
import ir.kitgroup.salein.databinding.FragmentMyCompanyBinding;
import ir.kitgroup.salein.models.Company;
import ir.kitgroup.salein.models.ModelAccount;

import ir.kitgroup.salein.models.ModelLog;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
@AndroidEntryPoint
public class MyCompanyFragment extends Fragment {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FragmentMyCompanyBinding binding;
    private SharedPreferences sharedPreferences;


    @Inject
    Company company;

    private Account account;


    private Dialog dialog;
    private TextView textExit;
    private int imageIconDialog;
    private String name;
    private String userName = "";
    private String password = "";


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


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        ArrayList<Company> list = new ArrayList<>(fullList());
        ArrayList<Company> listCompany = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            boolean check = sharedPreferences.getBoolean(list.get(i).namePackage, false);
            if (check)
                listCompany.add(list.get(i));
        }

        if (listCompany.size()==0)
            binding.txtError.setText("لیست فروشگاه های انتخابی شما خالی می باشد با مراجعه به تب فروشگاه ها ،فروشگاه  مورد نظر خود را انتخاب کنید");

        else
            binding.txtError.setText("");
        CompanyAdapterList companyAdapterList = new CompanyAdapterList(getActivity(), listCompany, 1);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(companyAdapterList);

        companyAdapterList.setOnClickItemListener((company, check) ->
                {


                    User.deleteAll(User.class);
                    User user = new User();
                    user.ipLocal = company.ipLocal;
                    user.userName = company.userName;
                    user.passWord = company.passWord;
                    user.lat = company.lat;
                    user.lng = company.lng;
                    user.save();
                    userName = user.userName;
                    password = user.passWord;
                    name = company.nameCompany;
                    company.nameCompany= company.namePackage;


                    String nameSave = sharedPreferences.getString("CN", "");
                    if (nameSave.equals(company.nameCompany)) {
                        Bundle bundle = new Bundle();
                        bundle.putString("Ord_TYPE", "");
                        bundle.putString("Tbl_GUID", "");
                        bundle.putString("Inv_GUID", "");

                        sharedPreferences.edit().putString("CN", name).apply();
                        MainOrderMobileFragment mainOrderMobileFragment = new MainOrderMobileFragment();
                        mainOrderMobileFragment.setArguments(bundle);
                        FragmentTransaction replaceFragment = requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment").addToBackStack("MainOrderMobileF");
                        replaceFragment.commit();
                    } else
                        configRetrofit(name, userName, password, account.M);
                }
        );


        imageIconDialog = R.drawable.saleinicon128;


        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(false);

        textExit = dialog.findViewById(R.id.tv_message);
        ImageView ivIcon = dialog.findViewById(R.id.iv_icon);
        ivIcon.setImageResource(imageIconDialog);


        MaterialButton btnOk = dialog.findViewById(R.id.btn_ok);
        MaterialButton btnNo = dialog.findViewById(R.id.btn_cancel);
        btnNo.setOnClickListener(v -> {
            dialog.dismiss();

        });
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            ArrayList<Account> AccList = new ArrayList<>();
            AccList.clear();
            AccList.add(account);
            addAccount(userName, password, AccList);


        });


        account = Select.from(Account.class).first();


    }


    private void configRetrofit(String name, String user, String pass, String mobile) {
        String baseUrl = "http://" + Util.toEnglishNumber(Select.from(User.class).first().ipLocal) + "/api/REST/";
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .setDateFormat(DateFormat.LONG)
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .setVersion(1.0)
                .create();


        try {
            App.retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();

            App.api = App.retrofit.create(API.class);

            getInquiryAccount1(name, user, pass, mobile);

        } catch (NetworkOnMainThreadException ex) {


        }
    }



    @SuppressLint("SetTextI18n")
    private void getInquiryAccount1(String name,String userName, String passWord, String mobile) {
     binding.progressbar.setVisibility(View.VISIBLE);
        try {
            compositeDisposable.add(
                    App.api.getInquiryAccount1(userName, passWord, mobile, "", "", 1, 1)
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
                                            getFragmentManager().popBackStack();
                                            Bundle bundle = new Bundle();
                                            bundle.putString("Ord_TYPE", "");
                                            bundle.putString("Tbl_GUID", "");
                                            bundle.putString("Inv_GUID", "");

                                            sharedPreferences.edit().putString("CN", name).apply();
                                            MainOrderMobileFragment mainOrderMobileFragment = new MainOrderMobileFragment();
                                            mainOrderMobileFragment.setArguments(bundle);
                                            FragmentTransaction replaceFragment = requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment").addToBackStack("MainOrderMobileF");
                                            replaceFragment.commit();

                                            binding.progressbar.setVisibility(View.GONE);
                                        }

                                        //user not register
                                        else {
                                            textExit.setText(" شما مشترک " + name + " نیستید.آیا مشترک میشوید؟ ");
                                            dialog.show();
                                            binding.progressbar.setVisibility(View.GONE);
                                        }


                                    }
                                } else {
                                    Toast.makeText(getActivity(), "خطا در ارتباط با سرور", Toast.LENGTH_SHORT).show();
                                    binding.progressbar.setVisibility(View.GONE);
                                }


                            }, throwable -> {

                                Toast.makeText(getContext(), "فروشگاه تعطیل می باشد." , Toast.LENGTH_SHORT).show();
                                binding.progressbar.setVisibility(View.GONE);

                            })
            );
        } catch (Exception e) {
            Toast.makeText(getContext(), "خطا در دریافت اطلاعات مشتریان." , Toast.LENGTH_SHORT).show();

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
                    App.api.addAccount1(userName, pass, gson1.toJson(jsonObjectAcc, typeJsonObject), "")
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
                                if (message == 1) {
                                    Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();

                                    Bundle bundle = new Bundle();
                                    bundle.putString("Ord_TYPE", "");
                                    bundle.putString("Tbl_GUID", "");
                                    bundle.putString("Inv_GUID", "");
                                    MainOrderMobileFragment mainOrderMobileFragment = new MainOrderMobileFragment();
                                    mainOrderMobileFragment.setArguments(bundle);
                                    FragmentTransaction replaceFragment = requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment").addToBackStack("MainOrderMobileF");
                                    replaceFragment.commit();

                             binding.progressbar.setVisibility(View.GONE);

                                } else {

                                    Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                                    binding.progressbar.setVisibility(View.GONE);
                                }

                            }, throwable -> {

                                Toast.makeText(getContext(), "خطای تایم اوت در ثبت اطلاعات مشتری." , Toast.LENGTH_SHORT).show();
                                binding.progressbar.setVisibility(View.GONE);

                            })
            );
        } catch (Exception e) {
            Toast.makeText(getContext(), "خطا در ثبت اطلاعات مشتری." , Toast.LENGTH_SHORT).show();
            binding.progressbar.setVisibility(View.GONE);
        }


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
        company_top.imageLogo = R.drawable.top_icon;


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
        company_meat.imageLogo = R.drawable.meat_icon;


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
