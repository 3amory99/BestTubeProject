package com.omarproject1.shashah.connectivity;

import android.app.Application;

public class MyApp extends Application {

    private static MyApp mInstence;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstence = this;
    }

    public static synchronized MyApp getInstance() {
        return mInstence;
    }



    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListner listner) {
        ConnectivityReceiver.connectivityReceiverListner = listner;
    }
}
