package com.omarproject1.shashah.download;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.omarproject1.shashah.R;

import java.util.Objects;

public class DownloadActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "internet_state";
    private TextView videoTitle;
    private Button downloadBtn;
    private String downloadTitle, downloadUrl;
    private Dialog connectionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        videoTitle = findViewById(R.id.download_video_title);
        downloadBtn = findViewById(R.id.download_btn);
        downloadTitle = getIntent().getStringExtra("goDownloadTitle");
        downloadUrl = getIntent().getStringExtra("goDownloadUrl");
        videoTitle.setText(downloadTitle);

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkInternetConnection()) {
                    Toast.makeText(view.getContext(), getResources().getString(R.string.start_downloading), Toast.LENGTH_SHORT).show();

                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                    request.setTitle("Download");
                    request.setDescription("Downloading Video..");
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "" + System.currentTimeMillis());
                    DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    downloadManager.enqueue(request);

                } else {
                    showConnectionDialog();
//                    Toast.makeText(view.getContext(), "Network Connection Error", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    public boolean checkInternetConnection() {

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isWifiConn = false;
        boolean isMobileConn = false;
        for (Network network : connMgr.getAllNetworks()) {
            NetworkCapabilities capabilities = connMgr.getNetworkCapabilities(network);

            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    isWifiConn = true;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    isMobileConn = true;
                }
            }
        }
        Log.d(DEBUG_TAG, "Wifi connected: " + isWifiConn);
        Log.d(DEBUG_TAG, "Mobile connected: " + isMobileConn);
        return isWifiConn || isMobileConn;
    }

    private void showConnectionDialog() {
        connectionDialog = new Dialog(this);
        connectionDialog.setContentView(R.layout.internet_connection_dialog);
        connectionDialog.setCanceledOnTouchOutside(true);
        Objects.requireNonNull(connectionDialog.getWindow()).setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        connectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        connectionDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        connectionDialog.show();
    }

}