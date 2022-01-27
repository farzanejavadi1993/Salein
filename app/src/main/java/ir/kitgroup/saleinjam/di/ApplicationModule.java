package ir.kitgroup.saleinjam.di;




import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.preference.PreferenceManager;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;

import dagger.hilt.android.qualifiers.ApplicationContext;

import dagger.hilt.components.SingletonComponent;

import ir.kitgroup.saleinjam.R;
import ir.kitgroup.saleinjam.models.Config;

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

        config.N = N;
        config.DESC = DESC;
        config.INSKU_ID = packageName;
        config.imageIcon = imgProduct;
        config.Aboutus = aboutUs;

        return config;

    }




    @Provides
    @Singleton
    Typeface provideTypeFace(@ApplicationContext Context context) {
        return Typeface.createFromAsset(context.getAssets(), "iransans.ttf");

    }


}
