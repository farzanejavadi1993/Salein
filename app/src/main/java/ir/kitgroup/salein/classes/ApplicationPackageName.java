package ir.kitgroup.salein.classes;

import android.app.Activity;
import android.content.pm.PackageManager;

public class ApplicationPackageName implements PackageName{
    @Override
    public String packageName(Activity activity) throws PackageManager.NameNotFoundException {
        return activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).packageName;
    }
}
