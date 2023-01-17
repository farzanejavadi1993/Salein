package ir.kitgroup.saleindemo.classes.application_information;

import android.app.Activity;

import ir.kitgroup.saleindemo.DataBase.SaleinShop;


public class ApplicationInformation {
    public SaleinShop getInformation(PackageName packageName , Activity activity) {

        SaleinShop appInfo = new SaleinShop();
            switch (packageName.getPackageName(activity)) {
                case "ir.kitgroup.salein":
                    appInfo.setApplication_code("1111");
                    appInfo.setGif_url("splash_screen.json");
                    appInfo.setSaleinApp(true);
                    break;
                case "ir.kitgroup.saleinmeat":
                    appInfo.setApplication_code("1113");
                    appInfo.setGif_url("file:///android_asset/donyavi.gif");
                    break;
                case "ir.kitgroup.saleinjam":
                    appInfo.setApplication_code("1114");
                    appInfo.setGif_url("splash_screen.json");
                    break;

                case "ir.kitgroup.saleinfingilkabab":
                    appInfo.setApplication_code("1115");
                    appInfo.setGif_url("file:///android_asset/fingili.gif");
                    break;

                case "ir.kitgroup.saleinkhavari":
                    appInfo.setApplication_code("1117");
                    appInfo.setGif_url("splash_screen.json");
                    break;

                case "ir.kitgroup.saleinbamgah":
                    appInfo.setApplication_code("1120");
                    appInfo.setGif_url("splash_screen.json");
                    break;
            }
            return appInfo;

    }
}
