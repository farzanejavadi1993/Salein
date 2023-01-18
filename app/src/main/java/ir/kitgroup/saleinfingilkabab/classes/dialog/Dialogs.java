package ir.kitgroup.saleinfingilkabab.classes.dialog;

import android.app.Activity;
import android.app.Dialog;

import androidx.annotation.LayoutRes;

public interface Dialogs {
    Dialog dialog(Activity activity,boolean cancelable,@LayoutRes int res);
}
