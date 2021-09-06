package ir.kitgroup.order.classes;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.model.MapID;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orm.SugarContext;
import com.orm.query.Select;

import java.text.DateFormat;
import java.util.concurrent.TimeUnit;

import ir.kitgroup.order.Connect.API;
import ir.kitgroup.order.DataBase.User;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

    public static API api;
    public static Context context;
    private static String baseUrl="";
    public static Retrofit retrofit;
    public static int mode = 1;//1  ordOrganization  //2  OrdClient


    private static SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        SugarContext.init(getApplicationContext());
        if (App.mode==2)
        User.deleteAll(User.class);
        if (Select.from(User.class).list().size() == 0) {
            User user = new User();
            user.ipLocal = "192.168.20.8:96";
            user.userName = "admin";
            user.passWord = "123";
            user.save();
        }
        baseUrl="http://" + Select.from(User.class).first().ipLocal + "/api/REST/";





        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();

        context = getApplicationContext();


        if (mode == 1) {
            if (retrofit == null && Select.from(User.class).list().size() > 0) {

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


                String baseUrl = "http://" + Select.from(User.class).first().ipLocal + "/api/REST/";
                // String  baseUrl = "http://192.168.20.8:96/api/REST/";
                // String  baseUrl = "http://109.125.133.149:9999/api/REST/";


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


        CedarMaps.getInstance()
                .setClientID("sportapp-6594917192157661130")
                .setClientSecret("b2uejHNwb3J0YXBw4V7hZnhRDiV3fQ8aqbTay-mjSd1IXllmWRN1EezGsss=")
                .setContext(this)
                .setMapID(MapID.MIX);


        // Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        super.onCreate();
    }
}