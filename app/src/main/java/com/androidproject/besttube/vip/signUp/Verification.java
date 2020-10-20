package com.androidproject.besttube.vip.signUp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.androidproject.besttube.vip.R;
import com.androidproject.besttube.vip.model.User;
import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tuyenmonkey.mkloader.MKLoader;

import java.util.concurrent.TimeUnit;

public class Verification extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private View v;
    private TextView phoneNumTxt, resend;
    private PinView pinView;
    private MKLoader loader;
    private Button verifyCodeBtn;
    private String verificationCodeBySystem;
    public static String codeSend;
    String phoneNum;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {

                    Log.d("", "onVerificationCompleted:" + credential);
                    String code = credential.getSmsCode();
                    if (code != null) {
                        pinView.setText(code);
//                        verifyCode(code);
                        signInWithPhoneAuthCredential(credential);
                    }
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.
                    Log.w("", "onVerificationFailed", e);

                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        // Invalid request
                        // ...
                    } else if (e instanceof FirebaseTooManyRequestsException) {
                        // The SMS quota for the project has been exceeded
                        // ...
                    }
                    Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    // Show a message and update the UI
                    // ...
                }

                @Override
                public void onCodeAutoRetrievalTimeOut(String s) {
                    super.onCodeAutoRetrievalTimeOut(s);
                }

                @Override
                public void onCodeSent(String verificationId,
                                       PhoneAuthProvider.ForceResendingToken token) {
                    super.onCodeSent(verificationId, token);
                    Log.d("", "onCodeSent:" + verificationId);
                    verificationCodeBySystem = verificationId;

                    // Save verification ID and resending token so we can use them later
                    codeSend = verificationId;
                    //mResendToken = token;

                    // ...
                }
            };

    private void sendVerificationCodeToUser(String phoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                "+20" + phoneNumber,        // Phone number to verify
                 phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) v.getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loader.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Log.d("", "signInWithCredential:success");
                            currentUser = firebaseAuth.getCurrentUser();
                            if (currentUser != null) {
                                String storedNumber = "+2" + phoneNum;
                                String uid = currentUser.getUid();
                                Intent intent = new Intent(v.getContext(), SettingsActivity.class);
                                addUser("", storedNumber, "", "", uid);
                                Toast.makeText(v.getContext(), getResources().getString(R.string.register_successfully), Toast.LENGTH_SHORT).show();
                                startActivity(intent);
                                getActivity().finish();
                            }

                        } else {
                            Log.w("", "signInWithCredential:failure", task.getException());
                            Toast.makeText(v.getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("ErrorInSignIN", e.getMessage() + "");
                loader.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_verfication, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        firebaseAuth = FirebaseAuth.getInstance();
        loader = view.findViewById(R.id.load_code);
        phoneNumTxt = view.findViewById(R.id.phone_number);
        pinView = view.findViewById(R.id.pin_view);
        resend = view.findViewById(R.id.resend);
        verifyCodeBtn = view.findViewById(R.id.verify_code);

        if (getArguments() != null) {
            VerificationArgs args = VerificationArgs.fromBundle(getArguments());
            phoneNum = args.getPhone();
            if (phoneNum != null) {
                phoneNumTxt.setText(getResources().getString(R.string.code) + phoneNum);
                sendVerificationCodeToUser(phoneNum);
            }
        }
        verifyCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codeWritten = pinView.getText().toString().trim();

                if (codeWritten.isEmpty()) {
                    pinView.setError("Wrong Code...");
                    pinView.requestFocus();
                    return;
                } else {
                    verifyCode(codeWritten);
                }

            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCodeToUser(phoneNum);
            }
        });


    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, code);
        signInWithPhoneAuthCredential(credential);
    }


    public void addUser(String name, String phone, String image, String about, String userUid) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("users");

        myRef.child(currentUser.getUid()).setValue(new User(name, phone, image, about, userUid));

    }

}