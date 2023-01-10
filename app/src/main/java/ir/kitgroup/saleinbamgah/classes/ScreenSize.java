package ir.kitgroup.saleinbamgah.classes;


import android.app.Activity;
import android.util.DisplayMetrics;


public class ScreenSize {

    private final DisplayMetrics dm;


    public ScreenSize(Activity activity){
         dm = new DisplayMetrics();;
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
    }


    public int getWidth() {
       return  dm.widthPixels;
    }


    public int getHeight() {
        return  dm.heightPixels;
    }

    public double getScreenSize() {
        double x = Math.pow(getWidth() / dm.xdpi, 2);
        double y = Math.pow(getHeight() / dm.ydpi, 2);
        return Math.sqrt(x + y);
    }





}
