package com.justimagine.imagine;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {

    // To initialize Firebase at the start of the application. Set in gradle.build
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
