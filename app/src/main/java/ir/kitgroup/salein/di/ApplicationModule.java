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

import java.util.ArrayList;
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

import ir.kitgroup.salein.models.Company;
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
        String packageName="";


        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            switch (pInfo.packageName) {

                case "ir.kitgroup.salein":
                    imageLogo = R.drawable.saleinicon128;
                    name = "سالین دمو";
                    packageName="ir.kitgroup.salein";

                    break;


                case "ir.kitgroup.saleinbahraman":
                    imageLogo = R.drawable.bahraman_png;
                    name = "زعفران بهرامن";
                    packageName="ir.kitgroup.saleinbahraman";

                    break;

                case "ir.kitgroup.saleintop":
                    imageLogo = R.drawable.top_png;
                    name = "تاپ کباب";
                    packageName="ir.kitgroup.saleintop";
                    break;


                case "ir.kitgroup.saleinmeat":
                    imageLogo = R.drawable.meat_png;
                    name = "گوشت دنیوی";
                    packageName="ir.kitgroup.saleinmeat";
                    break;

                case "ir.kitgroup.saleinnoon":
                    imageLogo = R.drawable.noon;
                    packageName="ir.kitgroup.saleinnoon";
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
        Double lat = 0.0;
        Double lng = 0.0;
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
                  ipLocal = "2.180.28.6:3333";
                  userName = "administrator";
                  passWord = "123";
                   // ipLocal="192.168.20.8:96";
                   // userName="admin";
                   // passWord="123";
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
                    ipLocal = "188.158.105.106:8085";
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
        company.numberPhone = numberPhone;

        return company;

    }










    @Provides
    @Singleton
    ArrayList<Company> ListCompany() {
        ArrayList<Company> arrayList = new ArrayList<>();
        Company companySalein = new Company();
        Company companySaleinMeat = new Company();
        Company companySaleinTop = new Company();
        Company companySaleinBahraman = new Company();


        companySalein.services = "ارائه  خدمات نرم افزاری در مشهد فعالیت میکند.";
        companySalein.imageLogo = R.drawable.salein;
        companySalein.imageDialog = R.drawable.saleinicon128;
        companySalein.nameCompany = "سالین دمو";
        companySalein.namePackage = "ir.kitgroup.salein";
        companySalein.title = "سالین دمو";
        companySalein.messageWelcome = "به سالین دمو خوش آمدید";
        companySalein.Description = "عرضه کننده بهترین محصولات نر افزاری";
        companySalein.ipLocal = "2.180.28.6:3333";
        companySalein.paymentLink = "";
        companySalein.userName = "administrator";
        companySalein.passWord = "123";
        companySalein.numberPhone = "05137638311";
        companySalein.lat = 36.326805522660464;
        companySalein.lng = 59.56450551053102;
        companySalein.baseUrl = "http://" + companySalein.ipLocal + "/api/REST/";


        companySaleinBahraman.services = " ارائه  زعفران و انواع ادویه در مشهد فعالیت میکند.";
        companySaleinBahraman.imageLogo = R.drawable.bahraman_icon;
        companySaleinBahraman.imageDialog = R.drawable.bahraman_png;
        companySaleinBahraman.nameCompany = "زعفران بهرامن";
        companySaleinBahraman.namePackage = "ir.kitgroup.saleinbahraman";
        companySaleinBahraman.title = "زعفران بهرامن";
        companySaleinBahraman.messageWelcome = "به زعفران بهرامن خوش آمدید";
        companySaleinBahraman.Description = "عرضه کننده بهترین محصولات";
        companySaleinBahraman.ipLocal = "188.158.105.106:8085";
        companySaleinBahraman.paymentLink = "";
        companySaleinBahraman.userName = "admin";
        companySaleinBahraman.passWord = "123";
        companySaleinBahraman.numberPhone = "";
        companySaleinBahraman.lat = 36.27928293493623;
        companySaleinBahraman.lng = 59.611608491098615;
        companySaleinBahraman.baseUrl = "http://" + companySaleinBahraman.ipLocal + "/api/REST/";


        companySaleinTop.imageLogo = R.drawable.top_icon;
        companySaleinTop.imageDialog = R.drawable.top_png;
        companySaleinTop.nameCompany = "تاپ کباب";
        companySaleinTop.namePackage = "ir.kitgroup.saleintop";
        companySaleinTop.services = " ارائه انواع غذا در مشهد فعالیت میکند.";
        companySaleinTop.title = "رستوران تاپ کباب";
        companySaleinTop.messageWelcome = "به رستوران تاپ کباب خوش آمدید";
        companySaleinTop.Description = "عرضه کننده بهترین غذاها";
        companySaleinTop.ipLocal = "188.158.121.253:9999";
        companySaleinTop.userName = "topkabab";
        companySaleinTop.passWord = "9929";
        companySaleinTop.paymentLink = "http://185.201.49.204:4008/";
        companySaleinTop.lat = 36.318805483696735;
        companySaleinTop.lng = 59.555196457006296;
        companySaleinTop.numberPhone = "05137638311";
        companySaleinTop.baseUrl = "http://" + companySaleinTop.ipLocal + "/api/REST/";


        companySaleinMeat.imageLogo = R.drawable.meat_icon;
        companySaleinMeat.imageDialog = R.drawable.meat_png;
        companySaleinMeat.nameCompany = "گوشت دنیوی";
        companySaleinMeat.namePackage = "ir.kitgroup.saleinmeat";
        companySaleinMeat.messageWelcome = "به هایپر گوشت دنیوی خوش آمدید";
        companySaleinMeat.services = " ارائه پروتئین و گوشت در مشهد فعالیت میکند.";
        companySaleinMeat.namePackage = "ir.kitgroup.saleinmeat";
        companySaleinMeat.title = " هایپر گوشت دنیوی";
        companySaleinMeat.Description = "عرضه کننده انواع گوشت";
        companySaleinMeat.ipLocal = "109.125.133.149:9999";
        companySaleinMeat.userName = "admin";
        companySaleinMeat.passWord = "0123";
        companySaleinMeat.numberPhone = "05137335985";
        companySaleinMeat.lat = 36.31947320471888;
        companySaleinMeat.lng = 59.605469293071884;
        companySaleinMeat.baseUrl = "http://" + companySaleinMeat.ipLocal + "/api/REST/";


        arrayList.add(companySalein);
        arrayList.add(companySaleinTop);
        arrayList.add(companySaleinMeat);
        arrayList.add(companySaleinBahraman);

        return arrayList;


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
