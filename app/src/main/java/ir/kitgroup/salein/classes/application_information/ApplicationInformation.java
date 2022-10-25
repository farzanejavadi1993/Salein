package ir.kitgroup.salein.classes.application_information;

import android.app.Activity;

import ir.kitgroup.salein.DataBase.Salein;


public class ApplicationInformation {
    public Salein getInformation(PackageName packageName , Activity activity) {

        Salein appInfo = new Salein();
            switch (packageName.getPackageName(activity)) {
                case "ir.kitgroup.salein":
                    appInfo.setApplication_code("12345678");
                    appInfo.setSaleinApp(true);
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
