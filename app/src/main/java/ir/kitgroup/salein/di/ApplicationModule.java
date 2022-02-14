package ir.kitgroup.salein.di;




import android.app.Application;
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

import ir.kitgroup.salein.Connect.API;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.classes.BuildConfig1;
import ir.kitgroup.salein.classes.HostSelectionInterceptor;
import ir.kitgroup.salein.models.Config;
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
    Config getConfig(@ApplicationContext Context context) {
        int mode = 2;
        int imageLogo = 0;
        int imgProduct = 0;
        String packageName = "";
        String N = "";
        String DESC = "";
        String watsApp = "";
        String instagram = "";
        String aboutUs = "";
        String website = "";


        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            switch (pInfo.packageName) {

                case "ir.kitgroup.salein":
                    imageLogo = R.drawable.saleinicon128;
                    packageName = "ir.kitgroup.salein";
                    imgProduct = R.drawable.salein;
                    N = "سالین";
                    DESC = "بهترین ها رو ببین و بخر";

                    break;


                case "ir.kitgroup.saleinkonj":
                    imageLogo = R.drawable.konj_png;
                    packageName = "ir.kitgroup.saleinkonj";
                    imgProduct = R.drawable.konj_icon;
                    N = "پیتزا کنج";
                    DESC = "یک فست فود عالی";

                    break;

                case "ir.kitgroup.saleinjam":
                    imageLogo = R.drawable.jam_png;
                    packageName = "ir.kitgroup.saleinjam";
                    imgProduct = R.drawable.jam_icon;
                    N = " بازرگانی جم پارت قطعه";
                    DESC = "لوازم یدکی";
                    watsApp = "https://chat.whatsapp.com/IOK5rOpFt37CBcrLBVhifa";
                    instagram = "https://instagram.com/jampartgheteh?utm_medium=copy_link";




                    break;

                case "ir.kitgroup.saleindemo":
                    imageLogo = R.drawable.saleinicon128;
                    packageName = "ir.kitgroup.saleindemo";
                    imgProduct = R.drawable.salein;
                    N = "سالین دمو";
                    DESC = "محصولات نرم افزاری";

                    break;

                case "ir.kitgroup.saleinbahraman":
                    imageLogo = R.drawable.bahraman_png;
                    packageName = "ir.kitgroup.saleinbahraman";
                    N = "زعفران بهرامن";
                    imgProduct = R.drawable.bahraman_icon;
                    DESC = "زعفران و انواع ادویه";

                    break;

                case "ir.kitgroup.saleintop":
                    imageLogo = R.drawable.top_png;
                    packageName = "ir.kitgroup.saleintop";
                    imgProduct = R.drawable.top_icon;
                    N = "تاپ کباب";
                    DESC = "بهترین غذاها";
                    break;


                case "ir.kitgroup.saleinmeat":
                    imageLogo = R.drawable.meat_png;
                    packageName = "ir.kitgroup.saleinmeat";
                    N = "گوشت دنیوی";
                    imgProduct = R.drawable.meat_icon;
                    DESC = "پروتئین و گوشت";
                    watsApp = "";
                    instagram = "https://instagram.com/donyavi_meat?utm_medium=copy_link";
                    website = "https://donyavi.com/";
                    break;

                default:
                    imageLogo = R.drawable.saleinorder_png;
                    mode = 1;
                    imgProduct = R.drawable.saleinorder_icon;
                    break;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Config config = new Config();
        config.imageLogo = imageLogo;
        config.mode = mode;
        config.watsApp = watsApp;
        config.Instagram = instagram;
        config.website = website;

        config.N = N;
        config.DESC = DESC;
        config.INSKU_ID = packageName;
        config.imageIcon = imgProduct;
        config.Aboutus = aboutUs;

        return config;

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
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
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
                .baseUrl(BuildConfig1.DEVELOPMENT_BASE_URL)
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
