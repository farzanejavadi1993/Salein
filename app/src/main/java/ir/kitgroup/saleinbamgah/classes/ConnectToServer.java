package ir.kitgroup.saleinbamgah.classes;

import android.content.SharedPreferences;

public class ConnectToServer {


    public void connect(SharedPreferences sharedPreferences,HostSelectionInterceptor hostSelectionInterceptor,boolean connect,String url){
        sharedPreferences.edit().putBoolean("status", connect).apply();
        Util.PRODUCTION_BASE_URL=url;
        hostSelectionInterceptor.setHostBaseUrl();
    }

}
