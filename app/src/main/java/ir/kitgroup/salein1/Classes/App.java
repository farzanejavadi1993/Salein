package ir.kitgroup.salein1.Classes;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orm.SugarContext;

import java.text.DateFormat;
import java.util.concurrent.TimeUnit;

import ir.kitgroup.salein1.Connect.API;
import ir.kitgroup.salein1.DataBase.User;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

    public static API api;
    public static Retrofit retrofit;
    public static int mode =1;//1  ordOrganization  //2  OrdClient
    private String baseUrl = "http://185.201.49.204:9696/api/REST/";

    private static SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;



    @Override
    public void onCreate() {


        SugarContext.init(getApplicationContext());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();



        if (mode == 1) {
            if (retrofit == null && User.count(User.class) > 0) {

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
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

                String  baseUrl = "http://" + User.first(User.class).ipLocal + "/api/REST/";


                retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build();

                api = retrofit.create(API.class);
            }
        } else {
            if (retrofit == null) {

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
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





                retrofit = new Retrofit.Builder()
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .client(client)
                        .build();

                api = retrofit.create(API.class);
            }
        }


        super.onCreate();
    }
}