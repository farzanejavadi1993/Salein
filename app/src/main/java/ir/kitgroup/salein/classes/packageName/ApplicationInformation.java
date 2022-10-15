package ir.kitgroup.salein.classes.packageName;

import android.app.Activity;
import android.content.pm.PackageManager;

import ir.kitgroup.salein.DataBase.AppInformation;


public class ApplicationInformation {


    public AppInformation getInformation(PackageName packageName , Activity activity) throws PackageManager.NameNotFoundException {

        AppInformation appInfo = new AppInformation();
            switch (packageName.getPackageName(activity)) {
                case "ir.kitgroup.salein":
                    appInfo.setApplication_code("12345678");
                    appInfo.setSalein_main(true);
                    break;
                case "ir.kitgroup.saleinmeat":
                    appInfo.setApplication_code("987654");
                    appInfo.setGif_url("file:///android_asset/donyavi.gif");
                    break;
                case "ir.kitgroup.saleinjam":
                    appInfo.setApplication_code("963258");
                    break;
            }
            return appInfo;

    }
}
