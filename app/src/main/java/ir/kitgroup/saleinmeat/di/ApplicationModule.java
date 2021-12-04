package ir.kitgroup.saleinmeat.di;




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

import ir.kitgroup.saleinmeat.Connect.API;
import ir.kitgroup.saleinmeat.R;
import ir.kitgroup.saleinmeat.models.Config;
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
        String packageName = "";
        String IP1 = "";
        String N = "";
        String DESC = "";


        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            switch (pInfo.packageName) {

                case "ir.kitgroup.salein":
                    imageLogo = R.drawable.saleinicon128;
                    packageName = "ir.kitgroup.salein";
                    IP1 = "2.180.28.6:3333";
                    N = "سالین";
                    DESC = "محصولات نرم افزاری";

                    break;


                case "ir.kitgroup.saleindemo":
                    imageLogo = R.drawable.saleinicon128;
                    packageName = "ir.kitgroup.saleindemo";
                    N = "سالین دمو";
                    DESC = "محصولات نرم افزاری";

                    break;

                case "ir.kitgroup.saleinbahraman":
                    imageLogo = R.drawable.bahraman_png;
                    packageName = "ir.kitgroup.saleinbahraman";
                    N = "زعفران بهرامن";
                    DESC = "زعفران و انواع ادویه";

                    break;

                case "ir.kitgroup.saleintop":
                    imageLogo = R.drawable.top_png;
                    packageName = "ir.kitgroup.saleintop";
                    N = "تاپ کباب";
                    DESC = "بهترین غذاها";
                    break;


                case "ir.kitgroup.saleinmeat":
                    imageLogo = R.drawable.meat_png;
                    packageName = "ir.kitgroup.saleinmeat";
                    N = "گوشت دنیوی";
                    DESC = "پروتئین و گوشت";
                    break;

                default:
                    imageLogo = R.drawable.saleinorder_png;
                    mode = 1;
                    break;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Config config = new Config();
        config.imageLogo = imageLogo;
        config.mode = mode;
        config.IP1 = IP1;
        config.INSKU_ID = packageName;

        return config;

    }


//    @Provides
//    @Singleton
//    Company getUser(@ApplicationContext Context context) {
//
//        String N = "";
//        String DESC = "";
//        String IP1 = "";
//        String USER = "";
//        String PASS = "";
//        String SaleinId = "";
//        String T1 = "";
//        double LAT = 0.0;
//        double LONG = 0.0;
//        String city="";
//        int mode = 2;
//        int imageLogo = 0;
//        int imageDialog = 0;
//
//
//        try {
//            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//
//            switch (pInfo.packageName) {
//
//                case "ir.kitgroup.salein":
//                    N = "سالین";
//                    DESC = "محصولات نرم افزاری";
//                    city="ایران";
//                    imageLogo = R.drawable.salein;
//                    imageDialog = R.drawable.saleinicon128;
//                    IP1 = "2.180.28.6:3333";
//                    USER = "administrator";
//                    PASS = "123";
//
//
////                   IP1 = "2.180.28.6:3333";
////                    USER = "administrator";
////                    PASS = "123";
//
//
//                    SaleinId = "ir.kitgroup.salein";
//                    LAT = 36.326805522660464;
//                    LONG = 59.56450551053102;
//
//
//
//                    T1 = "05137638311";
//
//                    break;
//
//
//                case "ir.kitgroup.saleindemo":
//                    N = "سالین دمو";
//                    DESC = "محصولات نرم افزاری";
//                    city="ایران";
//                    imageLogo = R.drawable.salein;
//                    imageDialog = R.drawable.saleinicon128;
//                    IP1 = "2.180.28.6:3333";
//                    USER = "administrator";
//                    PASS = "123";
//                    SaleinId = "ir.kitgroup.salein";
//                    LAT = 36.326805522660464;
//                    LONG = 59.56450551053102;
//
//
//
//                    T1 = "05137638311";
//
//                    break;
//
//                case "ir.kitgroup.saleinbahraman":
//
//
//                    N = "زعفران بهرامن";
//                    DESC = "زعفران و انواع ادویه";
//                    city="ایران";
//                    imageLogo = R.drawable.bahraman_icon;
//                    imageDialog = R.drawable.bahraman_png;
//                    IP1 = "89.165.69.94:8085";
//                    USER = "admin";
//                    PASS = "123";
//                    SaleinId = "ir.kitgroup.saleinbahraman";
//                    LAT = 36.27928293493623;
//                    LONG = 59.611608491098615;
//
//
//
//                    break;
//
//                case "ir.kitgroup.saleintop":
//
//                    N = "تاپ کباب";
//                    DESC = "بهترین غذاها";
//                    city="مشهد";
//                    imageLogo = R.drawable.top_icon;
//                    imageDialog = R.drawable.top_png;
//                    SaleinId = "ir.kitgroup.saleintop";
//                    IP1 = "188.158.121.253:9999";
//                    USER = "topkabab";
//                    PASS = "9929";
//                    LAT = 36.318805483696735;
//                    LONG = 59.555196457006296;
//                    // paymentLink = "http://185.201.49.204:4008/";
//                    T1 = "05137638311";
//                    break;
//
//
//                case "ir.kitgroup.saleinmeat":
//
//                    N = "گوشت دنیوی";
//                    SaleinId = "ir.kitgroup.saleinmeat";
//                    DESC = "پروتئین و گوشت";
//                    city="مشهد";
//                    imageLogo = R.drawable.meat_icon;
//                    imageDialog = R.drawable.meat_png;
//                    IP1 = "109.125.133.149:9999";
//                    USER = "admin";
//                    PASS = "0123";
//                    LAT = 36.31947320471888;
//                    LONG = 59.605469293071884;
//
//                    T1 = "05137335985";
//
//
//                    break;
//
//
//                default:
//
//                    imageLogo = R.drawable.saleinorder_icon;
//                    imageDialog = R.drawable.saleinorder_png;
//                    N = "سالین سفارش گیر";
//                    SaleinId = "ir.kitgroup.saleinOrder";
//
//                    DESC = "اپلیکیشن سفارش گیر مشتریان سالین";
//                    mode = 1;
//                    IP1 = "";
//                    if (Select.from(User.class).list().size() > 0) {
//                        USER = Select.from(User.class).first().userName;
//                        PASS = Select.from(User.class).first().passWord;
//                        IP1 = Select.from(User.class).first().ipLocal;
//                    }
//
//
//                    LAT = 36.318805483696735;
//                    LONG = 59.555196457006296;
//
//                    break;
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//
//
//        Company company = new Company();
//        company.N = N;
//        company.DESC = DESC;
//        company.IP1 = IP1 ;
//        company.USER = USER;
//        company.PASS = PASS;
//        company.LAT = String.valueOf(LAT);
//        company.LONG = String.valueOf(LONG);
//        company.CITY = city;
//        company.imageLogo = imageLogo;
//        company.imageDialog = imageDialog;
//        company.mode = mode;
//        company.INSK_ID = SaleinId;
//        company.T1 = T1;
//
//
//
//
//        return company;
//
//    }


    @Provides
    @Singleton
    Typeface provideTypeFace(@ApplicationContext Context context) {
        return Typeface.createFromAsset(context.getAssets(), "iransans.ttf");

    }

    @Provides
    @Singleton
    Cache provideOkHttpCache(Application application) {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        return new Cache(application.getCacheDir(), cacheSize);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Cache cache) {

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.cache(cache);
        client.readTimeout(30, TimeUnit.SECONDS);
        client.connectTimeout(30, TimeUnit.SECONDS);
        return client.build();
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
    Retrofit provideRetrofit(Gson gson, OkHttpClient okHttpClient, @ApplicationContext Context context) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl( "http://2.180.28.6:3333/api/REST/")
                .client(okHttpClient)
                .build();
    }


    @Provides
    @Singleton
    API api(Retrofit retrofit) {
        return retrofit.create(API.class);
    }
}
