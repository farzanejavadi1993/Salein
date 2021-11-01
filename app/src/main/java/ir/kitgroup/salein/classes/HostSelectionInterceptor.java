package ir.kitgroup.salein.classes;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.salein.BuildConfig;
import ir.kitgroup.salein.Connect.API;
import ir.kitgroup.salein.DataBase.User;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.Util.Util;
import ir.kitgroup.salein.models.Company;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@AndroidEntryPoint
public class HostSelectionInterceptor extends Fragment {

    @Inject
    Gson gson;


    @Inject
    OkHttpClient okHttpClient;

    @Inject
    API apiInjection;


    @Inject
    Company companyInjection;


    public API getApi(Boolean change) {
        API api = apiInjection;
        if (change) {

            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(Select.from(User.class).first() != null ?
                            Select.from(User.class).first().ipLocal :
                            Util.Base_Url)
                    .client(okHttpClient)
                    .build();


            api = retrofit.create(API.class);


        }
        return api;
    }


    public Company getCompany(Boolean change, String PackageName, Context context) {
        Company company1 = companyInjection;
        if (change) {
            try {
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

                PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

                switch (pInfo.packageName) {
                    case "ir.kitgroup.salein":
                        changeConfig=true;
                        imageLogo = R.drawable.salein;
                        imageDialog = R.drawable.saleinicon128;
                        name = "ir.kitgroup.salein";
                        namePackage = "ir.kitgroup.salein";
                        title = "سالین دمو";
                        ipLocal = "192.168.20.8:96";
                        userName = "admin";
                        passWord = "123";
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
                        namePackage = "ir.kitgroup.salein";
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
                        namePackage = "ir.kitgroup.salein";
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

                company1.ipLocal = ipLocal;
                company1.imageLogo = imageLogo;
                company1.imageDialog = imageDialog;
                company1.title = title;
                company1.Description = description;
                company1.nameCompany = name;
                company1.mode = mode;
                company1.messageWelcome = messageWelcome;
                company1.namePackage = namePackage;
                company1.userName = userName;
                company1.passWord = passWord;
                Util.Base_Url = "http://" + ipLocal + "/api/REST/";



            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }


        }
        return company1;


    }
}