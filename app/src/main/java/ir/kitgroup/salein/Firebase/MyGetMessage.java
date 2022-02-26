/*
package ir.kitgroup.salein.Firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import ir.kitgroup.salein.Activities.LauncherActivity;
import ir.kitgroup.salein.R;


public class MyGetMessage extends FirebaseMessagingService {

    public static String staticAppURL;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            staticAppURL = "";
            PendingIntent pendingIntent = null;
            boolean isNot = true;

            String message = remoteMessage.getData().get("message");
            String title = remoteMessage.getData().get("title");
            Boolean isUpdate = Boolean.valueOf(remoteMessage.getData().get("isUpdate"));
            String version = remoteMessage.getData().get("version");
            String appURL = remoteMessage.getData().get("appURL");

            if (isUpdate) {
                if (!TextUtils.equals(version, getAppVersion(this)) && version != null) {
                    title = "ورژن جدید در دسترس است!";
                    message = "لطفا برای کارکرد صحیح نرم افزار را با لمس اینجا آپدیت کنید.";

                    Intent intent2 = new Intent(this, LauncherActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("UpdateAppURL", appURL);
                    intent2.putExtras(bundle);
                    pendingIntent = PendingIntent.getActivity(this, 0,
                            intent2, PendingIntent.FLAG_UPDATE_CURRENT);
                } else {
                    isNot = false;
                }
            } else {
                final Intent emptyIntent = new Intent(this, LauncherActivity.class);
                pendingIntent = PendingIntent.getActivity(this, 0,
                        emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            long[] pattern = {1000, 600, 1000, 600};
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_salein))
                            .setContentTitle(title)
                            .setContentText(message)
                            .setContentIntent(pendingIntent)
                            .setLights(Color.RED, 500, 500)
                            .setVibrate(pattern)
                            .setFullScreenIntent(pendingIntent, true)
                            .setSound(alarmSound)
//                            .setBadgeIconType(R.mipmap.ic_salein)
                            .setSmallIcon(R.mipmap.ic_salein);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null && isNot)
                notificationManager.notify(88, mBuilder.build());

            super.onMessageReceived(remoteMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getAppVersion(Context context) {
        String result = "";
        try {
            result = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;

            result = result.replaceAll("[a-zA-Z]|-", "");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }
}*/
