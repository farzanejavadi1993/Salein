package ir.kitgroup.salein1.Util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import com.airbnb.lottie.LottieAnimationView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static void playLottieAnimation(String json, LottieAnimationView animationView) {

        if (animationView.isAnimating()) {
            animationView.pauseAnimation();
        }

        animationView.setAnimation(json);

        animationView.loop(true);
        animationView.playAnimation();
    }




}
