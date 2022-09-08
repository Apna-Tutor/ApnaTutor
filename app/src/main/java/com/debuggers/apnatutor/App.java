package com.debuggers.apnatutor;

import android.app.Application;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.debuggers.apnatutor.Models.User;

public class App extends Application {
    public static RequestQueue QUEUE;
    public static User ME;
    public static SharedPreferences PREFERENCES;

    @Override
    public void onCreate() {
        super.onCreate();
        QUEUE = Volley.newRequestQueue(this);
        PREFERENCES = getSharedPreferences(getPackageName(), MODE_PRIVATE);
    }
}
