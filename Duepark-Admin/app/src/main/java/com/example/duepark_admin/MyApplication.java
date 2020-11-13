package com.example.duepark_admin;

import android.app.Application;

import com.example.duepark_admin.Network.ConnectivityReceiver;

public class MyApplication extends Application {
    private static MyApplication mInstance;

    @Override
    public void onCreate(){
        super.onCreate();

        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

}
