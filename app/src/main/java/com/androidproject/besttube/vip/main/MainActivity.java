package com.androidproject.besttube.vip.main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.androidproject.besttube.vip.R;
import com.androidproject.besttube.vip.connectivity.ConnectivityReceiver;
import com.androidproject.besttube.vip.connectivity.MyApp;
import com.androidproject.besttube.vip.connectivity.OfflineActivity;
import com.androidproject.besttube.vip.main.fragments.ChildrenFragment;
import com.androidproject.besttube.vip.main.fragments.EducationFragment;
import com.androidproject.besttube.vip.main.fragments.HomeFragment;
import com.androidproject.besttube.vip.main.fragments.PrivacyFragment;
import com.androidproject.besttube.vip.main.fragments.ReligiousFragment;
import com.androidproject.besttube.vip.main.fragments.SportsFragment;
import com.androidproject.besttube.vip.profile.ProfileActivity;
import com.androidproject.besttube.vip.search.SearchActivity;
import com.androidproject.besttube.vip.splash.SplashActivity;
import com.androidproject.besttube.vip.upload.UploadActivity;
import com.androidproject.besttube.vip.users.UsersActivity;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;


public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListner, PopupMenu.OnMenuItemClickListener {

    private AdView mAdView;
    private Button languageBtn, shareAppBtn, privacyBtn, arabicBtn, englishBtn,profileBtn, logOutBtn;
    private Dialog dialog, languageDialog, downloadDialog;
    private SharedPreferences sharedPreferences;
    private Intent shareIntent, chooser;
    private String strAppLink = "";
    private String shareBody = "Hello ! you can download our app service for free and enjoy.";
    private ImageView optionMenu, moreMenu, closeOption, closeLangDialog, closeDownloadDialog;
    private TextView searchEditText;
    Fragment fragment;
    BottomNavigationView navView;

    FirebaseAuth auth;
    FirebaseUser currentUser;
    BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    int id = menuItem.getItemId();
                    switch (id) {

                        case R.id.navigation_home: {
                            fragment = new HomeFragment();
                            break;
                        }
                        case R.id.navigation_children: {
                            fragment = new ChildrenFragment();
                            break;
                        }
                        case R.id.navigation_sports: {
                            fragment = new SportsFragment();
                            break;
                        }
                        case R.id.navigation_education: {
                            fragment = new EducationFragment();
                            break;
                        }
                        case R.id.navigation_religious: {
                            fragment = new ReligiousFragment();
                            break;
                        }
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, fragment).commit();
                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        checkInternetConnection();
        initiation();

//        Test intro id
//        ca-app-pub-3940256099942544/1044960115
        AdLoader.Builder builder = new AdLoader.Builder(MainActivity.this,"ca-app-pub-1338896604510686/5143409681")
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(final UnifiedNativeAd unifiedNativeAd) {
                        // Assumes you have a placeholder FrameLayout in your View layout
                        // (with id fl_adplaceholder) where the ad is to be placed.

                        final UnifiedNativeAdView adView = (UnifiedNativeAdView) LayoutInflater.from(MainActivity.this)
                                .inflate(R.layout.ad_unified, null);

                        ImageView adClose = adView.findViewById(R.id.close_native_ads);
                        adClose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                unifiedNativeAd.destroy();
                                adView.destroy();
                            }
                        });
                        // This method sets the text, images and the native ad, etc into the ad
                        // view.
                        populateNativeAdView(unifiedNativeAd, adView);
                        FrameLayout frameLayout = findViewById(R.id.frame_layout_slider);
                        frameLayout.removeAllViews();
                        frameLayout.addView(adView);

                    }
                });
        loadAdMobBanner();

        new Handler().postDelayed(new Runnable() {
            @SuppressLint("WrongConstant")
            @Override
            public void run() {

                    builder.build().loadAd(new AdRequest.Builder().build());
            }
        }, 3500);

        showDownloadDialog();
        loadLocale();
        fragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, fragment).commit();
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        optionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptionDialog();
            }
        });

        searchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        moreMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(moreMenu);
            }
        });
    }

    private void loadAdMobBanner() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void showDownloadDialog() {
        boolean isFirstRun = getSharedPreferences("DownloadSupportDialog", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun) {
            downloadDialog = new Dialog(this);
            downloadDialog.setContentView(R.layout.download_dialog);
            closeDownloadDialog = downloadDialog.findViewById(R.id.close_download_dialog);
            closeDownloadDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    downloadDialog.dismiss();
                }
            });
            downloadDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            downloadDialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
            getSharedPreferences("DownloadSupportDialog", MODE_PRIVATE).edit().putBoolean("isFirstRun", false).apply();
            downloadDialog.show();
        }
    }

    private void showMenu(ImageView moreMenu) {
        PopupMenu popup = new PopupMenu(this, moreMenu);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.more_menu);
        popup.show();
    }
    private void initiation() {
        navView = findViewById(R.id.main_nav_view);
        searchEditText = findViewById(R.id.search_edit_text);
        optionMenu = findViewById(R.id.option_menu);
        moreMenu = findViewById(R.id.more_btn);

    }
    private void showOptionDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        profileBtn = dialog.findViewById(R.id.profile);
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                dialog.dismiss();
            }
        });
        languageBtn = dialog.findViewById(R.id.language);
        languageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLanguageDialog();
                dialog.dismiss();
            }
        });
        shareAppBtn = dialog.findViewById(R.id.share_app);
        shareAppBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(View view) {
                shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setData(Uri.parse("mailto:"));
                final String appPackageName = getApplicationContext().getPackageName();
                strAppLink = "https://play.google.com/store/apps/details?id=" + appPackageName;
                shareIntent.setType("text/link");
                shareBody = shareBody + "/n" + " " + strAppLink;
                String shareSub = "Best Tube App";
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                chooser = Intent.createChooser(shareIntent, "Share Our Application Link");

                startActivity(chooser);
            }
        });
        privacyBtn = dialog.findViewById(R.id.privacy);
        PrivacyFragment privacyFragment = new PrivacyFragment();
        privacyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction().replace(R.id.home_fragment, privacyFragment).commit();
                dialog.dismiss();
            }
        });
        logOutBtn = dialog.findViewById(R.id.log_out);
        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, SplashActivity.class));
                Toast.makeText(MainActivity.this, getResources().getString(R.string.log_out_successfully), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        closeOption = dialog.findViewById(R.id.close_option_dialog);
        closeOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void showLanguageDialog() {
        languageDialog = new Dialog(this);
        languageDialog.setContentView(R.layout.language_dialog);
        languageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        arabicBtn = languageDialog.findViewById(R.id.arabic_lang);
        arabicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("ar");
                recreate();
                languageDialog.dismiss();
                Toast.makeText(MainActivity.this, "Arabic", Toast.LENGTH_SHORT).show();

            }
        });
        englishBtn = languageDialog.findViewById(R.id.english_lang);
        englishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("");
                recreate();
                languageDialog.dismiss();
                Toast.makeText(MainActivity.this, "English", Toast.LENGTH_SHORT).show();
            }
        });
        closeLangDialog = languageDialog.findViewById(R.id.close_language_dialog);
        closeLangDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                languageDialog.dismiss();
            }
        });
        languageDialog.show();
    }
    private void setLocale(String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.setLocale(locale);
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("myLanguage", lang);
        editor.apply();


    }
    private void loadLocale() {

        SharedPreferences sharedPreferences2 = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String Language = sharedPreferences2.getString("myLanguage", "");
        setLocale(Language);

    }
    private void checkInternetConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnackBar(isConnected);
        if (!isConnected)
            changeActivity();
    }
    private void changeActivity() {

        Intent intent = new Intent(this, OfflineActivity.class);
        intent.putExtra("currentActivity", "" + getApplicationContext().getPackageName());
        startActivity(intent);
        finish();


    }
    public void showSnackBar(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = getResources().getString(R.string.connected);
            color = Color.WHITE;
        } else {
            message = getResources().getString(R.string.not_connected);
            color = Color.RED;
        } ////////////////////
        Snackbar snackbar = Snackbar.make(findViewById(R.id.main_activity), message, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        TextView textView = view.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //Register intent filter
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
        registerReceiver(connectivityReceiver, intentFilter);
        unregisterReceiver(connectivityReceiver);

        //register connection status
        MyApp.getInstance().setConnectivityListener(this);
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        changeActivity();
        showSnackBar(isConnected);
    }
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.upload_item:
                startActivity(new Intent(MainActivity.this, UploadActivity.class));
                return true;
            case R.id.users_item:
                startActivity(new Intent(MainActivity.this, UsersActivity.class));
                return true;
            default:
                return false;
        }
    }

    private void populateNativeAdView(final UnifiedNativeAd unifiedNativeAd, final UnifiedNativeAdView adView) {
        TextView adTitle = adView.findViewById(R.id.ad_headline);
        TextView adBody = adView.findViewById(R.id.ad_body);
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        TextView adAdvertiser = adView.findViewById(R.id.ad_advertiser);
        TextView adPrice = adView.findViewById(R.id.ad_price);
        TextView adStore = adView.findViewById(R.id.ad_store);
        RatingBar adRating = adView.findViewById(R.id.ad_stars);
        Button adStoreBtn = adView.findViewById(R.id.ad_call_to_action);

        NativeAd.Image icon = unifiedNativeAd.getIcon();



        adView.setMediaView(mediaView);
        adView.setBodyView(adBody);
        adView.setHeadlineView(adTitle);

        adView.setAdvertiserView(adAdvertiser);
        adView.setPriceView(adPrice);
        adView.setStarRatingView(adRating);
        adView.setStoreView(adStore);
        adView.setCallToActionView(adStoreBtn);
//        adView.setIconView(icon);

//        if (icon == null) {
//            adView.getIconView().setVisibility(View.INVISIBLE);
//        } else {
//            ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
//            adView.getIconView().setVisibility(View.VISIBLE);
//        }

        if(unifiedNativeAd.getHeadline()!=null)
            ((TextView)adView.getHeadlineView()).setText(unifiedNativeAd.getHeadline());

        if(unifiedNativeAd.getBody()!=null)
            ((TextView)adView.getBodyView()).setText(unifiedNativeAd.getBody());

        if(unifiedNativeAd.getMediaContent()!=null)
            ((MediaView)adView.getMediaView()).setMediaContent(unifiedNativeAd.getMediaContent());


        if (unifiedNativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(unifiedNativeAd.getPrice());
        }

        if (unifiedNativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(unifiedNativeAd.getStore());
        }

        if (unifiedNativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(unifiedNativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (unifiedNativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(unifiedNativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }



        ImageView adClose = adView.findViewById(R.id.close_native_ads);
        adClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unifiedNativeAd.destroy();
                adView.destroy();
            }
        });


        adView.setNativeAd(unifiedNativeAd);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, SplashActivity.class));
            finish();
        }

    }
}