package ir.kitgroup.salein.di;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;


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

import ir.kitgroup.salein.models.Company;
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
        return context.getSharedPreferences("preferences_name", Context.MODE_PRIVATE);

    }


    @Provides
    @Singleton
    Context provideContext(@ApplicationContext Context context) {
        return context;

    }



    @Provides
    @Singleton
    Double provideSize() {
//        DisplayMetrics dm = new DisplayMetrics();
//        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
//        double width = dm.widthPixels;
//        double height = dm.heightPixels;
//        double x = Math.pow(width / dm.xdpi, 2);
//        double y = Math.pow(height / dm.ydpi, 2);
//        return Math.sqrt(x + y);

        return 6.0;

    }
    @Provides
    @Singleton
      Company getUser(@ApplicationContext Context context) {
        String name = "";
        String namePackage = "";
        String title = "";
        String description = "";
        String messageWelcome="";
        int  mode = 2;
        int imageLogo = 0;
        int imageDialog = 0;

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            switch (pInfo.packageName) {
                case "ir.kitgroup.salein":
                    imageLogo = R.drawable.salein;
                    imageDialog = R.drawable.saleinicon128;
                    name = "ir.kitgroup.salein";
                    namePackage = "ir.kitgroup.salein";
                    title = "سالین دمو";
                    messageWelcome = "به سالین دمو خوش آمدید";
                    description = "سالین دمو ، راهنمای استفاده از اپلیکیشن";


                    break;

                case "ir.kitgroup.saleintop":
                    imageLogo = R.drawable.top_icon;
                    imageDialog = R.drawable.top_png;
                    name = "ir.kitgroup.saleintop";
                    namePackage = "ir.kitgroup.salein";
                    title = "تاپ کباب";
                    messageWelcome = "به رستوران تاپ کباب خوش آمدید";
                    description="عرضه کننده بهترین غذاها";
                    break;


                case "ir.kitgroup.saleinmeat":
                    imageLogo = R.drawable.meat_icon;
                    imageDialog = R.drawable.meat_png;
                    name = "ir.kitgroup.saleinmeat";
                    messageWelcome = "به هایپر گوشت دنیوی خوش آمدید";
                    namePackage = "ir.kitgroup.salein";
                    title = "گوشت دنیوی";
                    description="عرضه کننده انواع گوشت";

                    break;

                case "ir.kitgroup.saleinnoon":
                    imageLogo = R.drawable.noon;
                    imageDialog = R.drawable.noon;
                    name = "ir.kitgroup.saleinnoon";
                    namePackage = "ir.kitgroup.salein";
                    messageWelcome = "به کافه نون دنیوی خوش آمدید";
                    title = "کافه نون";
                    description="متنوع ترین محصولات";
                    break;

                default:
                    imageLogo = R.drawable.saleinorder_icon;
                    imageDialog = R.drawable.saleinorder_png;
                    name = "ir.kitgroup.saleiOrder";
                    namePackage = "ir.kitgroup.saleinOrder";
                    title = "SaleIn Order";
                    description = "اپلیکیشن سفارش گیر مشتریان سالین";
                    mode=1;
                    break;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        Company company = new Company();
        if (name.equals("ir.kitgroup.salein")) {

           company.ipLocal = "192.168.20.8:96";
           company.imageLogo = imageLogo;
           company.imageDialog =imageDialog;
           company.title=title;
           company.Description= description;
           company.nameCompany = name;
           company.mode = mode;
           company.messageWelcome = messageWelcome;
           company.namePackage = namePackage;
           company.userName = "admin";
           company.passWord = "123";
        }

        return company;

    }


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
                .baseUrl("http://" + getUser(context).ipLocal + "/api/REST/")
                .client(okHttpClient)
                .build();
    }


    @Provides
    @Singleton
    API api(Retrofit retrofit) {
        return retrofit.create(API.class);
    }
}
