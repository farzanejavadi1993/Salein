package ir.kitgroup.saleinOrder.di;


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
import com.orm.query.Select;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;

import dagger.hilt.android.qualifiers.ApplicationContext;

import dagger.hilt.components.SingletonComponent;
import ir.kitgroup.saleinOrder.Connect.API;

import ir.kitgroup.saleinOrder.DataBase.User;
import ir.kitgroup.saleinOrder.R;

import ir.kitgroup.saleinOrder.classes.Util;
import ir.kitgroup.saleinOrder.models.Company;
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
        Boolean changeConfig=false;
        String name = "";
        String namePackage = "";
        String title = "";
        String description = "";
        String messageWelcome = "";
        String ipLocal = "";
        int mode = 2;
        int imageLogo = 0;
        int imageDialog = 0;
        String userName = "";
        String passWord = "";

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            switch (pInfo.packageName) {


                case "ir.kitgroup.saleintop":
                    imageLogo = R.drawable.top_icon;
                    imageDialog = R.drawable.top_png;
                    name = "ir.kitgroup.saleintop";
                    namePackage = "ir.kitgroup.saleintop";
                    title = "تاپ کباب";
                    messageWelcome = "به رستوران تاپ کباب خوش آمدید";
                    description = "عرضه کننده بهترین غذاها";
                    ipLocal = "188.158.121.253:9999";
                    userName = "topkabab";
                    passWord = "9929";
                    break;


                case "ir.kitgroup.saleinmeat":
                    imageLogo = R.drawable.meat_icon;
                    imageDialog = R.drawable.meat_png;
                    name = "ir.kitgroup.saleinmeat";
                    messageWelcome = "به هایپر گوشت دنیوی خوش آمدید";
                    namePackage = "ir.kitgroup.saleinmeat";
                    title = "گوشت دنیوی";
                    description = "عرضه کننده انواع گوشت";
                    ipLocal = "109.125.133.149:9999";
                    userName = "admin";
                    passWord = "0123";

                    break;

                case "ir.kitgroup.saleinnoon":
                    imageLogo = R.drawable.noon;
                    imageDialog = R.drawable.noon;
                    name = "ir.kitgroup.saleinnoon";
                    namePackage = "ir.kitgroup.saleinnoon";
                    messageWelcome = "به کافه نون دنیوی خوش آمدید";
                    title = "کافه نون";
                    description = "متنوع ترین محصولات";
                    break;

                default:

                    imageLogo = R.drawable.saleinorder_icon;
                    imageDialog = R.drawable.saleinorder_png;
                    name = "ir.kitgroup.saleiOrder";
                    namePackage = "ir.kitgroup.saleinOrder";
                    title = "SaleIn Order";
                    description = "اپلیکیشن سفارش گیر مشتریان سالین";
                    mode = 1;
                    break;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        Company company = new Company();
        company.ipLocal = ipLocal;
        company.imageLogo = imageLogo;
        company.imageDialog = imageDialog;
        company.title = title;
        company.Description = description;
        company.nameCompany = name;
        company.mode = mode;
        company.messageWelcome = messageWelcome;
        company.namePackage = namePackage;
        company.userName = userName;
        company.passWord = passWord;
        Util.Base_Url = "http://" + ipLocal + "/api/REST/";


        return company;

    }



    @Provides
    @Singleton
    Boolean getChangeConfig(@ApplicationContext Context context) {

        return getUser(context).changeConfig;
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
                .baseUrl(Select.from(User.class).first()!=null?
                        Select.from(User.class).first().ipLocal:
                        Util.Base_Url)
                .client(okHttpClient)
                .build();
    }


    @Provides
    @Singleton
    API api(Retrofit retrofit) {
        return retrofit.create(API.class);
    }
}
