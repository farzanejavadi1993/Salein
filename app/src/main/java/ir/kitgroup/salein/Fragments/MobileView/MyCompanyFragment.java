package ir.kitgroup.salein.Fragments.MobileView;

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
import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import ir.kitgroup.salein.Activities.Classes.LauncherActivity;
import ir.kitgroup.salein.Adapters.CompanyAdapterList;
import ir.kitgroup.salein.Connect.API;
import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.DataBase.User;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.Util.Util;
import ir.kitgroup.salein.classes.App;
import ir.kitgroup.salein.classes.CustomProgress;
import ir.kitgroup.salein.databinding.FragmentMyCompanyBinding;
import ir.kitgroup.salein.models.ModelAccount;
import ir.kitgroup.salein.models.ModelCompany;
import ir.kitgroup.salein.models.ModelLog;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyCompanyFragment extends Fragment {

    private FragmentMyCompanyBinding binding;
    private SharedPreferences sharedPreferences;
    private CustomProgress customProgress;

    private Account account;


    private Dialog dialog;
    private  TextView textExit;
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

        customProgress=CustomProgress.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        ArrayList<ModelCompany> list = new ArrayList<>(fullList());
        ArrayList<ModelCompany> listCompany = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            boolean check = sharedPreferences.getBoolean(list.get(i).PACKAGEName, false);
            if (check)
                listCompany.add(list.get(i));
        }


        CompanyAdapterList companyAdapterList = new CompanyAdapterList(getActivity(), listCompany, 1);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(companyAdapterList);

        companyAdapterList.setOnClickItemListener((company, check) ->
                {
                    User.deleteAll(User.class);
                    User user = new User();
                    user.ipLocal =company.IP;
                    user.userName =company.USER;
                    user.passWord = company.PASS;
                    user.lat = company.LAT;
                    user.lng = company.LNG;
                    user.save();
                    userName = user.userName;
                    password = user.passWord;
                    name=company.NameCompany;
                    LauncherActivity.name = company.PACKAGEName;
                    configRetrofit(userName, password, account.M);
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


    private void configRetrofit(String user, String pass, String mobile) {
        String baseUrl = "http://" + Util.toEnglishNumber(Select.from(User.class).first().ipLocal) + "/api/REST/";
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
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
                    .client(client)
                    .build();

            App.api = App.retrofit.create(API.class);

            getInquiryAccount(user, pass, mobile);

        } catch (NetworkOnMainThreadException ex) {


        }
    }

    private void getInquiryAccount(String userName, String passWord, String mobile) {


        customProgress.showProgress(getActivity(),"لطفا منتظر بمانید",false);
        try {
            Call<String> call = App.api.getInquiryAccount(userName, passWord, mobile, "", "", 1);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelAccount>() {
                    }.getType();
                    ModelAccount iDs;
                    try {
                        iDs = gson.fromJson(response.body(), typeIDs);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "مدل دریافت شده از مشتریان نامعتبر است.", Toast.LENGTH_SHORT).show();
                        if (customProgress.isShow) {
                            customProgress.hideProgress();
                        }
                        customProgress.hideProgress();
                        return;
                    }


                    assert iDs != null;
                    if (iDs.getAccountList() == null) {
                        Type typeIDs0 = new TypeToken<ModelLog>() {
                        }.getType();
                        ModelLog iDs0 = gson.fromJson(response.body(), typeIDs0);

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
                            getFragmentManager().popBackStack();
                            Bundle bundle = new Bundle();
                            bundle.putString("Ord_TYPE", "");
                            bundle.putString("Tbl_GUID", "");
                            bundle.putString("Inv_GUID", "");

                            MainOrderMobileFragment mainOrderMobileFragment = new MainOrderMobileFragment();
                            mainOrderMobileFragment.setArguments(bundle);
                            FragmentTransaction replaceFragment = Objects.requireNonNull(getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment")).addToBackStack("MainOrderMobileF");
                            replaceFragment.commit();

                            if (customProgress.isShow) {
                                customProgress.hideProgress();
                            }
                            customProgress.hideProgress();
                        }

                        //user not register
                        else {
                            textExit.setText(" شما مشترک " + name + " نیستید.آیا مشترک میشوید؟ ");
                            dialog.show();
                            if (customProgress.isShow) {
                                customProgress.hideProgress();
                            }
                            customProgress.hideProgress();
                        }




                    }


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    Toast.makeText(getContext(), "خطای تایم اوت در دریافت اطلاعات مشتریان." + t.toString(), Toast.LENGTH_SHORT).show();
                    if (customProgress.isShow) {
                        customProgress.hideProgress();
                    }
                    customProgress.hideProgress();

                }
            });


        } catch (NetworkOnMainThreadException ex) {
            Toast.makeText(getContext(), "خطا در دریافت اطلاعات مشتریان." + ex.toString(), Toast.LENGTH_SHORT).show();

            if (customProgress.isShow) {
                customProgress.hideProgress();
            }
            customProgress.hideProgress();
        }


    }


    private static class JsonObjectAccount {

        public List<Account> Account;

    }

    private void addAccount(String userName, String pass, List<Account> accounts) {


        customProgress.showProgress(getActivity(),"لطفا منتظر بمانید",false);
        try {
            JsonObjectAccount jsonObjectAcc = new JsonObjectAccount();
            jsonObjectAcc.Account = accounts;



            Gson gson = new Gson();
            Type typeJsonObject = new TypeToken<JsonObjectAccount>() {
            }.getType();

            Call<String> call = App.api.addAccount(userName, pass, gson.toJson(jsonObjectAcc, typeJsonObject), "");


            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelLog>() {
                    }.getType();
                    ModelLog iDs = gson.fromJson(response.body(), typeIDs);

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
                        FragmentTransaction replaceFragment = Objects.requireNonNull(getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment")).addToBackStack("MainOrderMobileF");
                        replaceFragment.commit();

                        if (customProgress.isShow) {
                            customProgress.hideProgress();
                        }
                        customProgress.hideProgress();

                    } else {

                        Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                        if (customProgress.isShow) {
                            customProgress.hideProgress();
                        }
                        customProgress.hideProgress();
                    }



                }


                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getContext(), "خطای تایم اوت در ثبت مشتری" + t.toString(), Toast.LENGTH_SHORT).show();

                    if (customProgress.isShow) {
                        customProgress.hideProgress();
                    }
                    customProgress.hideProgress();

                }
            });


        } catch (NetworkOnMainThreadException ex) {

            Toast.makeText(getContext(), "خطا در ثبت مشتری" + ex.toString(), Toast.LENGTH_SHORT).show();
            if (customProgress.isShow) {
                customProgress.hideProgress();
            }
            customProgress.hideProgress();


        }


    }


    private ArrayList<ModelCompany> fullList() {
        ArrayList<ModelCompany> companyList = new ArrayList<>();
        ModelCompany company_SaleIn = new ModelCompany();
        company_SaleIn.IP = "192.168.20.8:96";
        company_SaleIn.NameCompany = "سالین دمو";
        company_SaleIn.DESC = "سالین دمو ، راهنمای استفاده از اپلیکیشن";
        company_SaleIn.PACKAGEName = "ir.kitgroup.salein";
        company_SaleIn.Check = false;
        company_SaleIn.USER = "admin";
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


        companyList.clear();
        companyList.add(company_SaleIn);
        companyList.add(company_top);
        companyList.add(company_meat);
        companyList.add(company_noon);

        return companyList;
    }

}
