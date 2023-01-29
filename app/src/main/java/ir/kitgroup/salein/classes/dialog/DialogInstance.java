package ir.kitgroup.salein.classes.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;

import ir.kitgroup.salein.R;


public class DialogInstance implements Dialogs {

    Dialog mDialog;

    private static DialogInstance dialogInstance;

    public static DialogInstance getInstance() {
        if (dialogInstance == null)
            dialogInstance = new DialogInstance();

        return dialogInstance;
    }




    @Override
    public Dialog dialog(Activity activity, boolean cancelable, @LayoutRes int res) {
        mDialog = new android.app.Dialog(activity);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mDialog.setContentView(res);
        mDialog.setCancelable(cancelable);


        return mDialog;
    }


}
