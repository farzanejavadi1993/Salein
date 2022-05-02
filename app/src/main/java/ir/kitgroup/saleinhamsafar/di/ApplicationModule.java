package ir.kitgroup.saleinhamsafar.di;




import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.preference.PreferenceManager;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;

import dagger.hilt.android.qualifiers.ApplicationContext;

import dagger.hilt.components.SingletonComponent;

import ir.kitgroup.saleinhamsafar.Connect.API;


import ir.kitgroup.saleinhamsafar.classes.HostSelectionInterceptor;
import ir.kitgroup.saleinhamsafar.classes.Util;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class ApplicationModule {

    @Provides
    @Singleton
    SharedPreferences provideSharedPreference(@ApplicationContext Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    Context provideContext(@ApplicationContext Context context) {
        return context;
    }








    @Provides
    @Singleton
    public HostSelectionInterceptor provideHostSelectionInterceptor(SharedPreferences preferenceHelper) {
        return new HostSelectionInterceptor(preferenceHelper);
    }


    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(@ApplicationContext Context context,HostSelectionInterceptor  hostSelectionInterceptor) {

        long cacheSize = 5 * 1024 * 1024;
        Cache mCache = new Cache(context.getCacheDir(), cacheSize);

        return new OkHttpClient().newBuilder()
                .cache(mCache)
                .retryOnConnectionFailure(true)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(hostSelectionInterceptor)
                .followRedirects(true)
                .followSslRedirects(true)
                .build();


    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(Gson gson, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Util.DEVELOPMENT_BASE_URL)
                .client(okHttpClient)
                .build();


    }

    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create();
    }





    @Provides
    @Singleton
    Typeface provideTypeFace(@ApplicationContext Context context) {
        return Typeface.createFromAsset(context.getAssets(), "iransans.ttf");

    }

    @Provides
    @Singleton
    public API provideAPI(Retrofit ret) {

        return ret.create(API.class);

    }
}
