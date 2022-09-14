package com.debuggers.apnatutor;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.debuggers.apnatutor.Models.User;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class App extends Application {
    public static final String NOTIFICATION_CHANNEL_ID = "ProgressNotification";
    public static RequestQueue QUEUE;
    public static User ME;
    public static SharedPreferences PREFERENCES;
    NotificationChannel notificationChannel;

    @Override
    public void onCreate() {
        super.onCreate();
        QUEUE = Volley.newRequestQueue(this);
        PREFERENCES = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(notificationChannel);
    }

    public static String timeStamp(long ms) {
        if (TimeUnit.MILLISECONDS.toHours(ms) > 0) {
            return String.format(Locale.getDefault(), "%d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(ms), TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)), TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
        } else {
            return String.format(Locale.getDefault(), "%d:%02d", TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)), TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
        }
    }
}
