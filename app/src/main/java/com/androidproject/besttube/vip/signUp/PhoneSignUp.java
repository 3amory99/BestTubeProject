package com.androidproject.besttube.vip.signUp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.androidproject.besttube.vip.R;

import java.util.Objects;


public class PhoneSignUp extends Fragment {

    private static final String DEBUG_TAG = "internet_state";
    Button verifyBtn;
    Animation logoAnim;
    private ImageView logo, closeInternetDialog;
    private Dialog connectionDialog;
    AutoCompleteTextView phoneNumberET;
    String phoneNumber;


    public PhoneSignUp() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        logo = view.findViewById(R.id.login_logo);
        logoAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.from_top);
        logo.startAnimation(logoAnim);
        verifyBtn = view.findViewById(R.id.verify);
        phoneNumberET = view.findViewById(R.id.phone_num);
        final NavController navController = Navigation.findNavController(view);
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber = phoneNumberET.getText().toString().trim();
                if (checkInternetConnection()) {
                    if (TextUtils.isEmpty(phoneNumber)) {
                        Toast.makeText(view.getContext(), getResources().getString(R.string.enter_phone_number), Toast.LENGTH_SHORT).show();
                        phoneNumberET.setError(getResources().getString(R.string.enter_phone_number));
                    } else if (phoneNumber.length() <= 11) {
                        Toast.makeText(view.getContext(), getResources().getString(R.string.enter_valid_number), Toast.LENGTH_SHORT).show();
                        phoneNumberET.setError(getResources().getString(R.string.enter_valid_number));
                    } else {


                        PhoneSignUpDirections.ActionPhoneSignUpToVerification toVerification = PhoneSignUpDirections.actionPhoneSignUpToVerification();
                        toVerification.setPhone(phoneNumber);
                        navController.navigate(toVerification);

                    }
                } else {
                    showConnectionDialog();
                }
            }
        });

    }


    public boolean checkInternetConnection() {

        ConnectivityManager connMgr = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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
        connectionDialog = new Dialog(getContext());
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
}