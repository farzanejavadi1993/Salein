package ir.kitgroup.salein.Util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


import com.airbnb.lottie.LottieAnimationView;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dagger.hilt.android.qualifiers.ApplicationContext;
import ir.kitgroup.salein.DataBase.User;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.classes.App;


public class Util {


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

    public static String getPrice() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.context);
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


    public static User getUser(@ApplicationContext Context context) {
        String name = "";
        String namePackage = "";
        int image = 0;

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            switch (pInfo.packageName) {
                case "ir.kitgroup.salein":
                    image = R.drawable.saleinicon128;
                    name = "ir.kitgroup.salein";
                    namePackage = "ir.kitgroup.salein";

                    break;

                case "ir.kitgroup.saleintop":
                    image = R.drawable.top_png;
                    name = "ir.kitgroup.saleintop";
                    namePackage = "ir.kitgroup.salein";
                    break;


                case "ir.kitgroup.saleinmeat":
                    image = R.drawable.meat_png;
                    name = "ir.kitgroup.saleinmeat";
                    namePackage = "ir.kitgroup.salein";

                    break;

                case "ir.kitgroup.saleinnoon":
                    image = R.drawable.noon;
                    name = "ir.kitgroup.saleinnoon";
                    namePackage = "ir.kitgroup.salein";
                    break;

                default:
                    image = R.drawable.saleinorder_png;
                    name = "ir.kitgroup.saleiOrder";
                    namePackage = "ir.kitgroup.saleinOrder";
                    break;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        User user = new User();
        if (name.equals("ir.kitgroup.salein")) {

            user.ipLocal = "192.168.20.8:96";
            user.image = image;
            user.name = name;
            user.namePackage = namePackage;
            user.userName = "admin";
            user.passWord = "123";
        }

        return user;

    }


    public static ArrayList<Double> getSizeMobile(Activity context) {

        ArrayList<Double> SizeApp = new ArrayList<>();
        double width = 0;
        double height = 0;
        double screenInches = 0.0;
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        double x = Math.pow(width / dm.xdpi, 2);
        double y = Math.pow(height / dm.ydpi, 2);
        screenInches = Math.sqrt(x + y);
        SizeApp.add(screenInches);
        SizeApp.add(width);
        SizeApp.add(height);
        return SizeApp;
    }


}
