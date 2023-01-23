package ir.kitgroup.saleinPakhshKourosh.classes.application_information;

import android.app.Activity;
import android.content.pm.PackageManager;

public class PackageName {

    public String getPackageName(Activity activity) {
      String packageName ="";
        try {
           packageName = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageName;
    }
}
