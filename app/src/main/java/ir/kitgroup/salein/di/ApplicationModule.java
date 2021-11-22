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
import com.orm.query.Select;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;

import dagger.hilt.android.qualifiers.ApplicationContext;

import dagger.hilt.components.SingletonComponent;
import ir.kitgroup.salein.Connect.API;

import ir.kitgroup.salein.DataBase.User;
import ir.kitgroup.salein.R;

import ir.kitgroup.salein.DataBase.Company;
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
        String name = "";
        String packageName = "";


        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            switch (pInfo.packageName) {

                case "ir.kitgroup.salein":
                    imageLogo = R.drawable.saleinicon128;
                    name = "سالین دمو";
                    packageName = "ir.kitgroup.salein";

                    break;


                case "ir.kitgroup.saleinbahraman":
                    imageLogo = R.drawable.bahraman_png;
                    name = "زعفران بهرامن";
                    packageName = "ir.kitgroup.saleinbahraman";

                    break;

                case "ir.kitgroup.saleintop":
                    imageLogo = R.drawable.top_png;
                    name = "تاپ کباب";
                    packageName = "ir.kitgroup.saleintop";
                    break;


                case "ir.kitgroup.saleinmeat":
                    imageLogo = R.drawable.meat_png;
                    name = "گوشت دنیوی";
                    packageName = "ir.kitgroup.saleinmeat";
                    break;

                case "ir.kitgroup.saleinnoon":
                    imageLogo = R.drawable.noon;
                    packageName = "ir.kitgroup.saleinnoon";
                    name = "کافه نون";
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
        config.name = name;
        config.packageName = packageName;

        return config;

    }


    @Provides
    @Singleton
    Company getUser(@ApplicationContext Context context) {

        String nameCompany = "";
        String linkUpdate = "";
        String paymentLink = "";
        String namePackage = "";
        String title = "";
        String Service = "";
        String description = "";
        String messageWelcome = "";
        String ipLocal = "";
        int mode = 2;
        int imageLogo = 0;
        int imageDialog = 0;
        String userName = "";
        String passWord = "";
        double lat = 0.0;
        double lng = 0.0;
        String numberPhone = "";

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            switch (pInfo.packageName) {

                case "ir.kitgroup.salein":
                    Service = "ارائه  خدمات نرم افزاری در مشهد فعالیت میکند.";
                    imageLogo = R.drawable.salein;
                    imageDialog = R.drawable.saleinicon128;
                    nameCompany = "سالین دمو";
                    namePackage = "ir.kitgroup.salein";
                    title = "سالین دمو";
                    messageWelcome = "به سالین دمو خوش آمدید";
                    description = "عرضه کننده بهترین محصولات نر افزاری";
                    paymentLink = "";

/*
                    ipLocal = "2.180.28.6:3333";
                    userName = "administrator";
                    passWord = "123";*/

                   ipLocal = "192.168.20.8:96";
                    userName = "admin";
                    passWord = "123";


                    numberPhone = "05137638311";
                    lat = 36.326805522660464;
                    lng = 59.56450551053102;
                    break;


                case "ir.kitgroup.saleinbahraman":
                    Service = " ارائه  زعفران و انواع ادویه در مشهد فعالیت میکند.";
                    imageLogo = R.drawable.bahraman_icon;
                    imageDialog = R.drawable.bahraman_png;
                    nameCompany = "زعفران بهرامن";
                    namePackage = "ir.kitgroup.saleinbahraman";
                    title = "زعفران بهرامن";
                    messageWelcome = "به زعفران بهرامن خوش آمدید";
                    description = "عرضه کننده بهترین محصولات";
                    ipLocal = "89.165.69.94:8085";
                    paymentLink = "";
                    userName = "admin";
                    passWord = "123";
                    numberPhone = "";
                    lat = 36.27928293493623;
                    lng = 59.611608491098615;
                    break;

                case "ir.kitgroup.saleintop":
                    imageLogo = R.drawable.top_icon;
                    imageDialog = R.drawable.top_png;
                    nameCompany = "تاپ کباب";
                    namePackage = "ir.kitgroup.saleintop";
                    Service = " ارائه انواع غذا در مشهد فعالیت میکند.";
                    title = "رستوران تاپ کباب";
                    messageWelcome = "به رستوران تاپ کباب خوش آمدید";
                    description = "عرضه کننده بهترین غذاها";
                    ipLocal = "188.158.121.253:9999";
                    userName = "topkabab";
                    passWord = "9929";
                    paymentLink = "http://185.201.49.204:4008/";
                    lat = 36.318805483696735;
                    lng = 59.555196457006296;
                    numberPhone = "05137638311";
                    break;


                case "ir.kitgroup.saleinmeat":
                    imageLogo = R.drawable.meat_icon;
                    imageDialog = R.drawable.meat_png;
                    nameCompany = "گوشت دنیوی";
                    namePackage = "ir.kitgroup.saleinmeat";
                    messageWelcome = "به هایپر گوشت دنیوی خوش آمدید";
                    Service = " ارائه پروتئین و گوشت در مشهد فعالیت میکند.";
                    namePackage = "ir.kitgroup.saleinmeat";
                    title = " هایپر گوشت دنیوی";
                    description = "عرضه کننده انواع گوشت";

                    ipLocal = "109.125.133.149:9999";
                    userName = "admin";
                    passWord = "0123";

                    linkUpdate = "https://cafebazaar.ir/app/ir.kitgroup.saleinmeat";


                    numberPhone = "05137335985";
                    lat = 36.31947320471888;
                    lng = 59.605469293071884;

                    break;

                case "ir.kitgroup.saleinnoon":
                    imageLogo = R.drawable.noon;
                    imageDialog = R.drawable.noon;
                    nameCompany = "کافه نون";
                    Service = " ارائه انواع نوشیدنی های گرم و سرد در مشهد فعالیت میکند.";
                    namePackage = "ir.kitgroup.saleinnoon";
                    messageWelcome = "به کافه نون دنیوی خوش آمدید";
                    title = "کافه نون";
                    description = "متنوع ترین محصولات";
                    break;

                default:

                    imageLogo = R.drawable.saleinorder_icon;
                    imageDialog = R.drawable.saleinorder_png;
                    nameCompany = "سالین سفارش گیر";
                    namePackage = "ir.kitgroup.saleinOrder";
                    title = "SaleIn Order";
                    description = "اپلیکیشن سفارش گیر مشتریان سالین";
                    mode = 1;
                    ipLocal = "";
                    if (Select.from(User.class).list().size() > 0) {
                        userName = Select.from(User.class).first().userName;
                        passWord = Select.from(User.class).first().passWord;
                        ipLocal = Select.from(User.class).first().ipLocal;
                    }


                    lat = 36.318805483696735;
                    lng = 59.555196457006296;

                    break;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        Company company = new Company();
        company.services = Service;
        company.IP1 = ipLocal;
        company.imageLogo = imageLogo;
        company.imageDialog = imageDialog;
        company.title = title;
        company.Description = description;
        company.N = nameCompany;
        company.mode = mode;
        company.messageWelcome = messageWelcome;
        company.namePackage = namePackage;
        company.userName = userName;
        company.passWord = passWord;
        company.linkUpdate = linkUpdate;
        company.paymentLink = paymentLink;
        company.LAT = String.valueOf(lat);
        company.LONG =String.valueOf(lng);
        company.IP1 = "http://" + ipLocal + "/api/REST/";
        company.T1 = numberPhone;

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
                .baseUrl(getUser(context).IP1)
                .client(okHttpClient)
                .build();
    }


    @Provides
    @Singleton
    API api(Retrofit retrofit) {
        return retrofit.create(API.class);
    }
}
