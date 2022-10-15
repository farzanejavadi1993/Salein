package ir.kitgroup.salein.classes.application_information;

import android.app.Activity;
import android.content.pm.PackageManager;

public class PackageName {


    public String getPackageName(Activity activity) throws PackageManager.NameNotFoundException {
        return activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).packageName;
    }
}
