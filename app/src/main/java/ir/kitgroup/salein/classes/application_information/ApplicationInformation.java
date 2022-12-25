package ir.kitgroup.salein.classes.application_information;

import android.app.Activity;

import ir.kitgroup.salein.DataBase.SaleinShop;


public class ApplicationInformation {
    public SaleinShop getInformation(PackageName packageName , Activity activity) {

        SaleinShop appInfo = new SaleinShop();
            switch (packageName.getPackageName(activity)) {
                case "ir.kitgroup.salein":
                    appInfo.setApplication_code("1111");
                    appInfo.setGif_url("file:///android_asset/donyavi.gif");
                    appInfo.setSaleinApp(true);
                    break;
                case "ir.kitgroup.saleinmeat":
                    appInfo.setApplication_code("1113");
                    appInfo.setGif_url("file:///android_asset/donyavi.gif");
                    break;
                case "ir.kitgroup.saleinjam":
                    appInfo.setApplication_code("1114");
                    appInfo.setGif_url("file:///android_asset/donyavi.gif");
                    break;

                case "ir.kitgroup.saleinfingilkabab":
                    appInfo.setApplication_code("1115");
                    appInfo.setGif_url("file:///android_asset/loadingfood.gif");
                    break;
            }
            return appInfo;

    }
}
