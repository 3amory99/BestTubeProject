package com.omarproject1.shashah.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.omarproject1.shashah.R;
import com.omarproject1.shashah.connectivity.ConnectivityReceiver;
import com.omarproject1.shashah.connectivity.MyApp;
import com.omarproject1.shashah.connectivity.OfflineActivity;
import com.omarproject1.shashah.video.MainActivity2;
import com.omarproject1.shashah.upload.UploadActivity;
import com.omarproject1.shashah.main.fragments.ChildrenFragment;
import com.omarproject1.shashah.main.fragments.GamesFragment;
import com.omarproject1.shashah.main.fragments.HomeFragment;
import com.omarproject1.shashah.main.fragments.LoveFragment;
import com.omarproject1.shashah.main.fragments.ReligiousFragment;

import java.util.Locale;


public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListner {

    private Button languageBtn, shareAppBtn, privacyBtn, arabicBtn, englishBtn;
    private FloatingActionButton mainFab, firstFab, secondFab;
    float translationFabs = 100f;
    Boolean ifFabOpen = false;
    OvershootInterpolator interpolator = new OvershootInterpolator();
    private Dialog dialog, languageDialog;
    private SharedPreferences sharedPreferences;
    Intent shareIntent, chooser;
    private String strAppLink = "";
    private String shareBody = "Hello ! you can download our app service for free and enjoy.";
    ImageView optionMenu;

    TextView searchEditText;
    Fragment fragment;
    BottomNavigationView navView;
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
                        case R.id.navigation_love: {
                            fragment = new LoveFragment();
                            break;
                        }
                        case R.id.navigation_games: {
                            fragment = new GamesFragment();
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
        setContentView(R.layout.activity_main);
        checkInternetConnection();
        initiation();
        loadLocale();
        showFabsMenu();
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
    }

    private void showFabsMenu() {

        mainFab = findViewById(R.id.fab_main);
        firstFab = findViewById(R.id.fab_first);
        secondFab = findViewById(R.id.fab_second);
        firstFab.setAlpha(0f);
        secondFab.setAlpha(0f);

        firstFab.setTranslationY(translationFabs);
        secondFab.setTranslationY(translationFabs);
        mainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ifFabOpen) {
                    closeFabsMenu();
                } else {
                    openFabsMenu();
                }
            }
        });

        firstFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "Upload a new Video", Toast.LENGTH_SHORT).show();
                closeFabsMenu();
                startActivity(new Intent(MainActivity.this, UploadActivity.class));
            }
        });
        secondFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MainActivity2.class));
            }
        });

    }

    private void openFabsMenu() {

        ifFabOpen = !ifFabOpen;
        mainFab.setImageResource(R.drawable.ic_collabse);
        firstFab.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        secondFab.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();

    }

    private void closeFabsMenu() {
        ifFabOpen = !ifFabOpen;
        mainFab.setImageResource(R.drawable.ic_expand_list);
        firstFab.animate().translationY(translationFabs).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        secondFab.animate().translationY(translationFabs).alpha(0f).setInterpolator(interpolator).setDuration(300).start();


    }

    private void initiation() {
        navView = findViewById(R.id.main_nav_view);
        searchEditText = findViewById(R.id.search_edit_text);
        optionMenu = findViewById(R.id.option_menu);

    }

    private void showOptionDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
            @Override
            public void onClick(View view) {
                shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setData(Uri.parse("mailto:"));
                final String appPackageName = getApplicationContext().getPackageName();
                strAppLink = "https://play.google.com/store/apps/details?id=" + appPackageName;
                shareIntent.setType("text/link");
                shareBody = shareBody + "/n" + " " + strAppLink;
                String shareSub = "Shashah App";
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                chooser = Intent.createChooser(shareIntent, "Share Our Application Link");

                startActivity(chooser);
            }
        });
        dialog.dismiss();
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
        languageDialog.dismiss();
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
//        if (!isConnected)
        changeActivity();
        showSnackBar(isConnected);
    }
}