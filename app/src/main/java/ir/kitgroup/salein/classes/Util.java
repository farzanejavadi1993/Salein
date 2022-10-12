package ir.kitgroup.salein.classes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import com.airbnb.lottie.LottieAnimationView;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ir.kitgroup.salein.DataBase.Account;


public class Util {


    public static double width = 0.0;
    public static double height = 0.0;
    public static double screenSize = 0.0;
  public static String DEVELOPMENT_BASE_URL="http://api.kitgroup.ir/api/REST/";
   public static String DEVELOPMENT_BASE_URL_Img="http://api.kitgroup.ir";

//  public static String DEVELOPMENT_BASE_URL="http://192.168.20.33:90/api/REST/";
//    public static String DEVELOPMENT_BASE_URL_Img="http://192.168.20.33:90";
    public static String APPLICATION_CODE="12345678";
    public static String APPLICATION_ID="";
    public static String PRODUCTION_BASE_URL="";
    public static double latitude =  0;
    public static double longitude = 0;
    public static final String MARKERS_SOURCE = "markers-source";
    public static final String MARKERS_LAYER = "markers-layer";
    public static final String MARKER_ICON_ID = "marker-icon-id";
    public static  String address = "";
    public static String nameUser = "";




    public static void ScreenSize(Activity activity) {
        if (width == 0.0) {
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

            width = dm.widthPixels;
            height = dm.heightPixels;
            double x = Math.pow(width / dm.xdpi, 2);
            double y = Math.pow(height / dm.ydpi, 2);
            screenSize=Math.sqrt(x + y);
        }


    }

    public static final int VIEW_TYPE_ITEM = 0;
    public static final int VIEW_TYPE_LOADING = 1;

    public static boolean isValid(String s) {

        Pattern p = Pattern.compile("(0/9)?[0-9]{11}");
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }

    public static boolean isValidCode(String s) {

        Pattern p = Pattern.compile("(0/9)?[0-9]{5}");
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }

    public static boolean isValidRegister(Boolean name, Boolean phone, Boolean address, boolean plaque) {

        boolean active = false;
        if (name && phone && address && plaque)
            active = true;
        return active;
    }

    public static void playLottieAnimation(String json, LottieAnimationView animationView) {

        if (animationView.isAnimating()) {
            animationView.pauseAnimation();
        }

        animationView.setAnimation(json);
        animationView.loop(true);
        animationView.playAnimation();
    }

    public static String getPrice(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString("priceProduct", "");
    }

    public static void hideKeyBoard(Context context, View view) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    public static final LatLng VANAK_SQUARE = new LatLng(36.310699, 59.599457);


    public static String toEnglishNumber(String input) {

        String[] persian = new String[]{"۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹"};
        String[] arabic = new String[]{"٠", "١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩"};

        for (int j = 0; j < persian.length; j++) {
            if (input.contains(persian[j]))
                input = input.replace(persian[j], String.valueOf(j));
        }

        for (int j = 0; j < arabic.length; j++) {
            if (input.contains(arabic[j]))
                input = input.replace(arabic[j], String.valueOf(j));
        }

        return input;
    }


    public static Date deleteDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    public  static  String getPackageName(Activity activity) {
        String packageName = "";
        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            packageName = pInfo.packageName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageName;

    }

    public static String getAndroidID(Activity activity) {

        String id;

        @SuppressLint("HardwareIds")
        String androidId = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);

        if (androidId == null || androidId.equals("")) {
            try {
                androidId = "35" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                        Build.DEVICE.length() % 10 + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                        Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                        Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                        Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                        Build.USER.length() % 10;
            } catch (Exception ignored) {
                androidId = Build.ID + Build.HOST;
            }
        }
        id = androidId;


        return id;
    }

    public static class JsonObjectAccount {
        public List<ir.kitgroup.salein.DataBase.Account> Account;
    }
}