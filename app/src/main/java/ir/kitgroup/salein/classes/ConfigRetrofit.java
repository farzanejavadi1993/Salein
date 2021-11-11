package ir.kitgroup.salein.classes;

import android.app.Application;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfigRetrofit {

    public void getCompany() {
    }

    public Retrofit getRetrofit(String baseUrl) {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);


        int cacheSize = 10 * 1024 * 1024; // 10 MiB

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.readTimeout(30, TimeUnit.SECONDS);
        client.connectTimeout(30, TimeUnit.SECONDS);


        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .client(client.build())
                .build();
    }
}
