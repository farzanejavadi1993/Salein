package ir.kitgroup.salein.classes;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;


import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.model.MapID;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.orm.SugarContext;
import com.orm.query.Select;

import java.text.DateFormat;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;
import ir.kitgroup.salein.Connect.API;
import ir.kitgroup.salein.DataBase.User;
import ir.kitgroup.salein.Util.Util;
import ir.kitgroup.salein.models.Company;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@HiltAndroidApp
public class App extends Application {

    @Inject
    Company company;

    @Inject
    SharedPreferences sharedPreferences;

    public static API api;
    public static Retrofit retrofit;
    private int mode=2;






    @Override
    public void onCreate() {
        SugarContext.init(getApplicationContext());


        CedarMaps.getInstance()
                .setClientID("sportapp-6594917192157661130")
                .setClientSecret("b2uejHNwb3J0YXBw4V7hZnhRDiV3fQ8aqbTay-mjSd1IXllmWRN1EezGsss=")
                .setContext(this)
                .setMapID(MapID.MIX);



        super.onCreate();
    }
}