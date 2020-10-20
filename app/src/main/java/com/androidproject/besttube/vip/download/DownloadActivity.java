package com.androidproject.besttube.vip.download;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.androidproject.besttube.vip.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.File;
import java.util.Objects;

public class DownloadActivity extends AppCompatActivity {

    private static final int PERMISSION_STORAGE_CODE = 1000;
    private static final String DEBUG_TAG = "internet_state";
    private TextView videoTitle;
    private Button downloadBtn;
    private String downloadTitle, downloadUrl;
    private ImageView closeInternetDialog;
    private Dialog connectionDialog;
    MKLoader downloader;
    //
    private RewardedAd rewardedAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        onRequestAd();
        downloader = findViewById(R.id.download_video_loader);
        videoTitle = findViewById(R.id.download_video_title);
        downloadBtn = findViewById(R.id.download_btn);
        downloadTitle = getIntent().getStringExtra("goDownloadTitle");
        downloadUrl = getIntent().getStringExtra("goDownloadUrl");
        videoTitle.setText(downloadTitle);

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkInternetConnection()) {
                    if (ContextCompat.checkSelfPermission(view.getContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                            onShowRewardAd();
                    } else {
                        requestPermission(DownloadActivity.this);
                    }
                } else {
                    showConnectionDialog();
//                    Toast.makeText(view.getContext(), "Network Connection Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    BroadcastReceiver attachmentDownloadCompleteReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                long downloadId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                openDownloadedAttachment(context, downloadId);
            }
        }
    };

    private void onRequestAd(){
//        Uid = ca-app-pub-1338896604510686/9817605143
        rewardedAd = new RewardedAd(this,"ca-app-pub-1338896604510686/9817605143");
        RewardedAdLoadCallback rewardedAdLoadCallback = new RewardedAdLoadCallback(){
            @Override
            public void onRewardedAdLoaded() {
                super.onRewardedAdLoaded();
//                Toast.makeText(DownloadActivity.this, "OnRewarded Loaded", Toast.LENGTH_SHORT).show();
                downloadBtn.setEnabled(true);
                downloader.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError loadAdError) {
                super.onRewardedAdFailedToLoad(loadAdError);
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(),rewardedAdLoadCallback);
    }
    private void onShowRewardAd(){
        if(!rewardedAd.isLoaded())return;
        rewardedAd.getRewardItem().getAmount();
        RewardedAdCallback rewardedAdCallback = new RewardedAdCallback() {
            @Override
            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                Toast.makeText(DownloadActivity.this, "Earned", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedAdOpened() {
                super.onRewardedAdOpened();
            }

            @Override
            public void onRewardedAdClosed() {
                super.onRewardedAdClosed();
                onRequestAd();
                startDownloading(DownloadActivity.this, downloadUrl);

            }

            @Override
            public void onRewardedAdFailedToShow(AdError adError) {
                super.onRewardedAdFailedToShow(adError);
            }
        };
        rewardedAd.show(DownloadActivity.this,rewardedAdCallback);
    }

    private void openDownloadedAttachment(Context context, long downloadId) {

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            String downloadLocalUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            String downloadMimeType = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
            if ((downloadStatus == DownloadManager.STATUS_SUCCESSFUL) && downloadLocalUri != null) {
                openDownloadedAttachment(context, Uri.parse(downloadLocalUri), downloadMimeType);
            }
        }
        cursor.close();

    }

    private void openDownloadedAttachment(Context context, Uri parse, String downloadMimeType) {
        if (parse != null) {
            // Get Content Uri.
            if (ContentResolver.SCHEME_FILE.equals(parse.getScheme())) {
                // FileUri - Convert it to contentUri.
                File file = new File(parse.getPath());
                parse = FileProvider.getUriForFile(context, "com.androidproject.besttube.vip.provider", file);
                ;
            }

            Intent openAttachmentIntent = new Intent(Intent.ACTION_VIEW);
            openAttachmentIntent.setDataAndType(parse, downloadMimeType);
            openAttachmentIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                context.startActivity(openAttachmentIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, context.getString(R.string.unable_to_open_file), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startDownloading(final Activity activity, final String url) {

        try {
            if (downloadUrl != null && !downloadUrl.isEmpty()) {

                activity.registerReceiver(attachmentDownloadCompleteReceive, new IntentFilter(
                        DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                Toast.makeText(DownloadActivity.this, getResources().getString(R.string.start_downloading), Toast.LENGTH_SHORT).show();
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                request.setTitle("Download");
                request.setDescription("Downloading Video..");
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "" + System.currentTimeMillis());
                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                downloadManager.enqueue(request);
            }
        } catch (IllegalStateException e) {
            Toast.makeText(activity, "Please insert an SD card to download file", Toast.LENGTH_SHORT).show();
        }
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


    private String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private void showConnectionDialog() {
        connectionDialog = new Dialog(this);
        connectionDialog.setContentView(R.layout.internet_connection_dialog);
        connectionDialog.setCanceledOnTouchOutside(true);
        closeInternetDialog = connectionDialog.findViewById(R.id.close_internet_dialog);
        Objects.requireNonNull(connectionDialog.getWindow()).setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        connectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        connectionDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        closeInternetDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectionDialog.dismiss();
            }
        });
        connectionDialog.show();
    }


    public void requestPermission(Activity context) {

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(context)
                    .setTitle("Sorry permission needed")
                    .setMessage("Write storage permission is needed to complete uploading videos")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE_CODE);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(DownloadActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                startDownloading(DownloadActivity.this, downloadUrl);
            } else {
                Toast.makeText(DownloadActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                requestPermission(DownloadActivity.this);

            }
        }
    }

}