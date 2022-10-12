package ir.kitgroup.salein.classes;

import android.app.Activity;
import android.content.pm.PackageManager;

public interface PackageName {
    String packageName(Activity activity) throws PackageManager.NameNotFoundException;
}
