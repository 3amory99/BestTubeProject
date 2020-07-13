package com.omarproject1.shashah.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityReceiver extends BroadcastReceiver {

    public static ConnectivityReceiverListner connectivityReceiverListner;

    public ConnectivityReceiver() {
        super();
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (connectivityReceiverListner != null) {
            connectivityReceiverListner.onNetworkConnectionChanged(isConnected);
        }
    }


    public static boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MyApp.getInstance()
                .getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    //Create interface
    public interface ConnectivityReceiverListner {
        void onNetworkConnectionChanged(boolean isConnected);

    }


}
