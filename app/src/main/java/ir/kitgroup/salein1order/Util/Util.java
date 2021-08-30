package ir.kitgroup.salein1order.Util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.airbnb.lottie.LottieAnimationView;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ir.kitgroup.salein1order.classes.App;
import ir.kitgroup.salein1order.DataBase.Product;

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

    public static void hideKeyBoard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    public static final LatLng VANAK_SQUARE = new LatLng(36.310699,  59.599457);

}
