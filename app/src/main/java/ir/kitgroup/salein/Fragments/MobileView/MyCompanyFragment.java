package ir.kitgroup.salein.Fragments.MobileView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import ir.kitgroup.salein.Activities.Classes.LauncherActivity;
import ir.kitgroup.salein.Connect.API;
import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.DataBase.User;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.Util.Util;
import ir.kitgroup.salein.classes.App;
import ir.kitgroup.salein.databinding.FragmentMyCompanyBinding;
import ir.kitgroup.salein.models.ModelAccount;
import ir.kitgroup.salein.models.ModelLog;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyCompanyFragment extends Fragment {

    private FragmentMyCompanyBinding binding;
    private Boolean top,meat,salein=false;
    private SharedPreferences sharedPreferences;


    private String ip="";
    private String user="";
    private String pass="";
    private String url="";




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

        top = sharedPreferences.getBoolean("top", false);
        salein = sharedPreferences.getBoolean("salein", false);
        meat = sharedPreferences.getBoolean("meat", false);


        if (top)
            binding.cardTop.setVisibility(View.VISIBLE);
        if (salein)
            binding.cardSalein.setVisibility(View.VISIBLE);
        if (meat)
            binding.cardMeat.setVisibility(View.VISIBLE);


        binding.cardSalein.setOnClickListener(v -> {

            LauncherActivity.name="ir.kitgroup.salein";
            User.deleteAll(User.class);
            User user = new User();
            user.ipLocal = "185.201.49.204:9999";
            user.userName = "administrator";
            user.passWord = "123";
            user.lat=36.326805522660464;
            user.lng=59.56450551053102;
            user.save();
            configRetrofit();

        });


        binding.cardTop.setOnClickListener(v -> {
            LauncherActivity.name= "ir.kitgroup.saleintop";

                User.deleteAll(User.class);

            User user1 = new User();
            user1.ipLocal = "188.158.121.253:9999";
            user1.userName = "topkabab";
            user1.passWord = "9929";
            user1.lat = 36.318805483696735;
            user1.lng =  59.555196457006296;
            user1.save();
            configRetrofit();
        });


        binding.cardMeat.setOnClickListener(v -> {
            LauncherActivity.name="ir.kitgroup.saleinmeat";

            User.deleteAll(User.class);

            User user2 = new User();
            user2.ipLocal = "109.125.133.149:9999";
            user2.userName = "admin";
            user2.passWord = "0123";
            user2.lat=36.31947320471888;
            user2.lng=59.605469293071884;
            user2.save();
            configRetrofit();
        });



    }


    private void configRetrofit(){
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


        } catch (NetworkOnMainThreadException ex) {


        }
    }



    private void getInquiryAccount(String userName, String passWord, String mobile) {

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

                            MainOrderMobileFragment mainOrderMobileFragment = new MainOrderMobileFragment();
                            mainOrderMobileFragment.setArguments(bundle);
                            FragmentTransaction replaceFragment = Objects.requireNonNull(getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment"));
                            replaceFragment.commit();



                        }

                        //user not register
                        else {
                            getFragmentManager().popBackStack();
                            Bundle bundle = new Bundle();
                            bundle.putString("mobile", mobile);
                            MapFragment mapFragment = new MapFragment();
                            mapFragment.setArguments(bundle);
                            FragmentTransaction addFragment = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().add(R.id.frame_main, mapFragment, "MapFragment");
                            addFragment.commit();
                        }


                    }


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    Toast.makeText(getContext(), "خطای تایم اوت در دریافت اطلاعات مشتریان." + t.toString(), Toast.LENGTH_SHORT).show();


                }
            });


        } catch (NetworkOnMainThreadException ex) {
            Toast.makeText(getContext(), "خطا در دریافت اطلاعات مشتریان." + ex.toString(), Toast.LENGTH_SHORT).show();

        }


    }
}
