package com.naim.testml;

import android.app.Application;
import android.content.Context;

//import com.google.firebase.FirebaseApp;

/**
 * Created by Naim on 3/18/2018.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
//            FirebaseApp.initializeApp(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
