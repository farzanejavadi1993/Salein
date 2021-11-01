package ir.kitgroup.saleinOrder.classes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import com.airbnb.lottie.LottieAnimationView;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dagger.hilt.android.qualifiers.ApplicationContext;
import ir.kitgroup.saleinOrder.DataBase.User;
import ir.kitgroup.saleinOrder.R;


public class Util {

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




    public static String Base_Url = "";
}