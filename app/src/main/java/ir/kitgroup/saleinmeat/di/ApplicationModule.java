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
import com.orm.query.Select;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;

import dagger.hilt.android.qualifiers.ApplicationContext;

import dagger.hilt.components.SingletonComponent;
import ir.kitgroup.saleinmeat.Connect.API;

import ir.kitgroup.saleinmeat.DataBase.User;
import ir.kitgroup.saleinmeat.R;

import ir.kitgroup.saleinmeat.models.Company;
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


        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            switch (pInfo.packageName) {

                case "ir.kitgroup.saleinbahraman":
                    imageLogo = R.drawable.bahraman_png;
                    break;

                case "ir.kitgroup.saleintop":
                    imageLogo = R.drawable.top_png;
                    break;


                case "ir.kitgroup.saleinmeat":
                    imageLogo = R.drawable.meat_png;
                    break;

                case "ir.kitgroup.saleinnoon":
                    imageLogo = R.drawable.noon;
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
        String description = "";
        String messageWelcome = "";
        String ipLocal = "";
        int mode = 2;
        int imageLogo = 0;
        int imageDialog = 0;
        String userName = "";
        String passWord = "";
        Double lat = 0.0;
        Double lng = 0.0;
        String numberPhone ="";

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            switch (pInfo.packageName) {

                case "ir.kitgroup.saleinbahraman":
                    imageLogo = R.drawable.bahraman_icon;
                    imageDialog = R.drawable.bahraman_png;
                    nameCompany = "زعفران بهرامن";
                    namePackage = "ir.kitgroup.saleinbahraman";
                    title = "زعفران بهرامن";
                    messageWelcome = "به زعفران بهرامن خوش آمدید";
                    description = "عرضه کننده بهترین محصولات";
                   ipLocal = "188.158.105.106:8085";
                   paymentLink = "";
                   userName = "admin";
                   passWord = "123";

                    lat = 36.27928293493623;
                    lng = 59.611608491098615;
                    break;

                case "ir.kitgroup.saleintop":
                    imageLogo = R.drawable.top_icon;
                    imageDialog = R.drawable.top_png;
                    nameCompany = "تاپ کباب";
                    namePackage = "ir.kitgroup.saleintop";
                    title = "رستوران تاپ کباب";
                    messageWelcome = "به رستوران تاپ کباب خوش آمدید";
                    description = "عرضه کننده بهترین غذاها";
//                    ipLocal = "188.158.121.253:9999";
//                    userName = "topkabab";
//                    passWord = "9929";

                    ipLocal = "192.168.20.8:96";
                    userName = "admin";
                    passWord = "123";
                    paymentLink = "http://185.201.49.204:4008/";
                    lat = 36.318805483696735;
                    lng = 59.555196457006296;
                    numberPhone="05137638311";
                    break;


                case "ir.kitgroup.saleinmeat":
                    imageLogo = R.drawable.meat_icon;
                    imageDialog = R.drawable.meat_png;
                    nameCompany = "گوشت دنیوی";
                    namePackage = "ir.kitgroup.saleinmeat";
                    messageWelcome = "به هایپر گوشت دنیوی خوش آمدید";
                    namePackage = "ir.kitgroup.saleinmeat";
                    title = " هایپر گوشت دنیوی";
                    description = "عرضه کننده انواع گوشت";
                    ipLocal = "109.125.133.149:9999";
                    userName = "admin";
                    passWord = "0123";

                    numberPhone="05137335985";

                    lat = 36.31947320471888;
                    lng = 59.605469293071884;

                    break;

                case "ir.kitgroup.saleinnoon":
                    imageLogo = R.drawable.noon;
                    imageDialog = R.drawable.noon;
                    nameCompany = "کافه نون";
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
        company.ipLocal = ipLocal;
        company.imageLogo = imageLogo;
        company.imageDialog = imageDialog;
        company.title = title;
        company.Description = description;
        company.nameCompany = nameCompany;
        company.mode = mode;
        company.messageWelcome = messageWelcome;
        company.namePackage = namePackage;
        company.userName = userName;
        company.passWord = passWord;
        company.linkUpdate = linkUpdate;
        company.paymentLink = paymentLink;
        company.lat = lat;
        company.lng = lng;
        company.baseUrl = "http://" + ipLocal + "/api/REST/";
        company.numberPhone=numberPhone;

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
                .baseUrl(getUser(context).baseUrl)
                .client(okHttpClient)
                .build();
    }


    @Provides
    @Singleton
    API api(Retrofit retrofit) {
        return retrofit.create(API.class);
    }
}
