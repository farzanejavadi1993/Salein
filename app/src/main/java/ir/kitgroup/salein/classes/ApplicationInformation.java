package ir.kitgroup.salein.classes;

import ir.kitgroup.salein.DataBase.AppInfo;

public class ApplicationInformation {

    public AppInfo getInformation(String appName) {

        AppInfo appInfo = new AppInfo();
        switch (appName) {
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
