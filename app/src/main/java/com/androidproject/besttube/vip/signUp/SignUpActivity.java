package com.androidproject.besttube.vip.signUp;

import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.androidproject.besttube.vip.R;
import com.androidproject.besttube.vip.connectivity.ConnectivityReceiver;
import com.androidproject.besttube.vip.connectivity.MyApp;
import com.google.android.material.snackbar.Snackbar;

public class SignUpActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListner {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        checkInternetConnectionState();
    }


    @Override
    public void onResume() {
        super.onResume();
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);
        unregisterReceiver(connectivityReceiver);

        //register connection status
        MyApp.getInstance().setConnectivityListener(this);
    }

    private void checkInternetConnectionState() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnackBar(isConnected);
    }

    public void showSnackBar(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = getResources().getString(R.string.connected);
            color = Color.BLACK;
        } else {
            message = getResources().getString(R.string.not_connected);
            color = Color.RED;
        } ////////////////////
        Snackbar snackbar = Snackbar.make(findViewById(R.id.sign_up_activity), message, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        snackbar.setBackgroundTint(getResources().getColor(R.color.colorPrimaryDark));
        TextView textView = view.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnackBar(isConnected);
    }
}