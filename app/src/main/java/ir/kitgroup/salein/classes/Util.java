package ir.kitgroup.salein.classes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Util {


    public static String Main_URL = "http://api.kitgroup.ir/api/REST/";
    public static String Main_Url_IMAGE = "http://api.kitgroup.ir";

   /* public static String Main_URL = "http://2.180.28.6:1993/api/REST/";
    public static String Main_Url_IMAGE = "2.180.28.6:1993";*/


    public static String APPLICATION_ID = "";
    public static String PRODUCTION_BASE_URL = "";

    public static boolean isValid(String s) {

        Pattern p = Pattern.compile("(0/9)?[0-9]{11}");
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }

    public static String getPrice(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString("priceProduct", "");
    }

    public static void hideKeyBoard(Context context, View view) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

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