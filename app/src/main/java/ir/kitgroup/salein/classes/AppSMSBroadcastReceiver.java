package ir.kitgroup.salein.classes;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class AppSMSBroadcastReceiver extends BroadcastReceiver {

    public static OnSmsReceiveListener onSmsReceiveListener;

    public void setOnSmsReceiveListener(OnSmsReceiveListener onSmsReceiveListener) {

        AppSMSBroadcastReceiver.onSmsReceiveListener = onSmsReceiveListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {

                Bundle extras = intent.getExtras();
                Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                switch (status.getStatusCode()) {

                    case CommonStatusCodes.SUCCESS:

                        String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                        onSmsReceiveListener.onReceive(
                                ToEnglishNumbers(message.split("\\n")[1].split(":")[1].trim()));
                        break;
                    case CommonStatusCodes.TIMEOUT:

                        break;
                }
            }
        } catch (Exception ignored) {
        }
    }

    public interface OnSmsReceiveListener {

        void onReceive(String code);
    }

    private String ToEnglishNumbers(String numbers) {

        String[] arabic = {
                "٠", "١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩"
        };

        String[] persian = {
                "۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹"
        };

        for (int j = 0; j < persian.length; j++)
            numbers = numbers.replaceAll(persian[j], String.valueOf(j)).replaceAll(arabic[j], String.valueOf(j));

        return numbers;
    }
}