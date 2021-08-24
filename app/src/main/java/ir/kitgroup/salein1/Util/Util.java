package ir.kitgroup.salein1.Util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ir.kitgroup.salein1.Classes.App;
import ir.kitgroup.salein1.DataBase.Product;

public class Util {

    public static ArrayList<Product> AllProduct = new ArrayList<>();
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
        return sharedPreferences.getString("priceProduct","");
    }

}
