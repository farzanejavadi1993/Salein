package ir.kitgroup.saleinpakhshyab.classes;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import ir.kitgroup.saleinpakhshyab.R;

public class CustomDialog {

    //region Interface Negative Button
    public interface ClickNegativeButton {
        void onClick();
    }

    private ClickNegativeButton clickNegativeButton;

    public void setOnClickNegativeButton(ClickNegativeButton clickNegativeButton) {
        this.clickNegativeButton = clickNegativeButton;
    }
    //endregion Interface Negative Button


    //region Interface Positive Button
    public interface ClickPositiveButton {
        void onClick();
    }

    private ClickPositiveButton clickPositiveButton;

    public void setOnClickPositiveButton(ClickPositiveButton clickPositiveButton) {
        this.clickPositiveButton = clickPositiveButton;
    }
    //endregion Interface Positive Button


    private static CustomDialog customDialog = null;
    private Dialog mDialog;

    public static CustomDialog getInstance() {
        if (customDialog == null) {
            customDialog = new CustomDialog();
        }
        return customDialog;
    }

    public void showDialog(Context context, String message, boolean cancelable, String textNegative, String textPositive, boolean showPositiveButton , boolean showNegativeButton) {

        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.custom_dialog);
        TextView tvMessage = mDialog.findViewById(R.id.tv_message);
        MaterialButton btnCancel = mDialog.findViewById(R.id.btn_cancel);
        MaterialButton btnOk = mDialog.findViewById(R.id.btn_ok);


        if (!showNegativeButton)
            btnCancel.setVisibility(View.GONE);

        if (!showPositiveButton)
            btnOk.setVisibility(View.GONE);



        tvMessage.setText(message);
        btnOk.setText(textPositive);
        btnCancel.setText(textNegative);

        mDialog.setCancelable(cancelable);
        mDialog.setCanceledOnTouchOutside(cancelable);




        btnOk.setOnClickListener(view -> clickPositiveButton.onClick());
        btnCancel.setOnClickListener(v12 ->
                clickNegativeButton.onClick()
        );
        mDialog.show();
    }



    public void hideProgress() {
        if (mDialog != null) {
            try {
                mDialog.dismiss();
            }catch (Exception ignored){}
            mDialog = null;
        }
    }

}
