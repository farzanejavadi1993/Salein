package ir.kitgroup.saleinBahraman.classes;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import ir.kitgroup.saleinBahraman.R;


public class CustomProgress {

    public static CustomProgress customProgress = null;
    private Dialog mDialog;
    private ProgressBar spinKitView;

    public  Boolean isShow=false;

    public static CustomProgress getInstance() {
        if (customProgress == null) {
            customProgress = new CustomProgress();
        }
        return customProgress;
    }

    public void showProgress(Context context, String message, boolean cancelable) {
        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
       // mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.custom_progressbar);
        spinKitView =  mDialog.findViewById(R.id.spin_kit);
        TextView progressText = mDialog.findViewById(R.id.progress_text);
        progressText.setText("" + message);
        progressText.setVisibility(View.VISIBLE);
        spinKitView.setVisibility(View.VISIBLE);
        // you can change or add this line according to your need
        spinKitView.setIndeterminate(true);
        mDialog.setCancelable(cancelable);
        mDialog.setCanceledOnTouchOutside(cancelable);
        isShow=true;
        mDialog.show();
    }

    public void hideProgress() {
        if (mDialog != null) {
            isShow=false;
            mDialog.dismiss();
            mDialog = null;

        }
    }
}