package ir.kitgroup.saleinmeat.di;




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

import ir.kitgroup.saleinmeat.R;
import ir.kitgroup.saleinmeat.models.Config;

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
        String N = "";
        String DESC = "";


        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            switch (pInfo.packageName) {

                case "ir.kitgroup.salein":
                    imageLogo = R.drawable.saleinicon128;
                    packageName = "ir.kitgroup.salein";
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

        config.N = N;
        config.DESC = DESC;
        config.INSKU_ID = packageName;

        return config;

    }




    @Provides
    @Singleton
    Typeface provideTypeFace(@ApplicationContext Context context) {
        return Typeface.createFromAsset(context.getAssets(), "iransans.ttf");

    }


}
