package ir.kitgroup.salein.classes;

import android.app.Application;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import ir.kitgroup.salein.R;
import ir.kitgroup.salein.models.Company;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfigRetrofit {





    public Retrofit getRetrofit(String baseUrl) {

        Retrofit retrofit=null;
        try {


        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);



        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.readTimeout(30, TimeUnit.SECONDS);
        client.connectTimeout(30, TimeUnit.SECONDS);


        retrofit=new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .client(client.build())
                .build();

        }catch (Exception e){

        }
        return retrofit;
    }

    public Company getCompany(String name) {
        Company company = new Company();
        try {


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


        switch (name) {

            case "سالین دمو":
                Service = "ارائه  خدمات نرم افزاری در مشهد فعالیت میکند.";
                imageLogo = R.drawable.salein;
                imageDialog = R.drawable.saleinicon128;
                nameCompany = "سالین دمو";
                namePackage = "ir.kitgroup.salein";
                title = "سالین دمو";
                messageWelcome = "به سالین دمو خوش آمدید";
                description = "عرضه کننده بهترین محصولات نر افزاری";
                ipLocal = "2.180.28.6:3333";
                paymentLink = "";
                userName = "administrator";
                passWord = "123";
                numberPhone = "05137638311";
                lat = 36.326805522660464;
                lng = 59.56450551053102;
                break;


            case "زعفران بهرامن":
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

            case "تاپ کباب":
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


            case "گوشت دنیوی":
                imageLogo = R.drawable.meat_icon;
                imageDialog = R.drawable.meat_png;
                nameCompany = "گوشت دنیوی";
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

            case "کافه نون":
                imageLogo = R.drawable.noon;
                imageDialog = R.drawable.noon;
                nameCompany = "کافه نون";
                Service = " ارائه انواع نوشیدنی های گرم و سرد در مشهد فعالیت میکند.";
                namePackage = "ir.kitgroup.saleinnoon";
                messageWelcome = "به کافه نون دنیوی خوش آمدید";
                title = "کافه نون";
                description = "متنوع ترین محصولات";
                break;


        }



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
        }catch (Exception exception){}
        return company;

    }

}
