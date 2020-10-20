package com.androidproject.besttube.vip.signUp;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.androidproject.besttube.vip.R;
import com.androidproject.besttube.vip.main.MainActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class SettingsActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "InternetState";
    private ConstraintLayout nameLayout, phoneLayout, aboutLayout;
    private TextView nameTxt, phoneTxt, aboutTxt;
    private ImageView profileImage, closeInternetDialog;
    private MKLoader imageLoader;
    private EditText nameBottomSheetTxt;
    private Dialog connectionDialog;
    private Button finishSettings, saveBottomSheet, cancelBottomSheet;
    Uri file = null;
    Boolean state = false;
    Bitmap thumbnail = null;
    public int GALLERY = 1, CAMERA = 2;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference mProfileStorage;
    FloatingActionButton cameraBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initiation();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
        Query query = databaseReference.orderByChild("phone").equalTo(user.getPhoneNumber());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String name = "" + dataSnapshot.child("name").getValue();
                        String image = "" + dataSnapshot.child("image").getValue();
                        String phone = "" + dataSnapshot.child("phone").getValue();
                        String about = "" + dataSnapshot.child("about").getValue();

                        nameTxt.setText(name);
                        phoneTxt.setText(phone);
                        aboutTxt.setText(about);

                        try {
                            if (image == "") {
                                profileImage.setImageResource(R.drawable.user);
                            } else {
                                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                                        .placeholder(R.drawable.user)
                                        .into(profileImage, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                Picasso.get().load(image).placeholder(R.drawable.user).into(profileImage);
                                            }
                                        });
                            }
                        } catch (Exception e) {
//                        Picasso.get().load(R.drawable.contact).into(profileImage);
                            profileImage.setImageResource(R.drawable.user);
                        }
                    }
                } else {
                    Toast.makeText(SettingsActivity.this, "snapshot equal null ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("img could not be loaded", error.getMessage());
            }
        });
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               launchPictureDialogs();
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchPictureDialogs();
            }
        });
        nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(SettingsActivity.this,
                        R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.bottom_sheet_layout, (ConstraintLayout) findViewById(R.id.bottom_sheet));
                bottomSheetDialog.setContentView(bottomSheetView);
                nameBottomSheetTxt = bottomSheetDialog.findViewById(R.id.bottom_edit_text);
                saveBottomSheet = bottomSheetDialog.findViewById(R.id.save_btn);
                saveBottomSheet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String nameEditTxt = nameBottomSheetTxt.getText().toString().trim();
                        String searchName = nameBottomSheetTxt.getText().toString().toLowerCase().trim();
                        if (!TextUtils.isEmpty(nameEditTxt)) {

                            HashMap<String, Object> result = new HashMap<>();
                            result.put("search",searchName);
                            result.put("name", nameEditTxt);
                            databaseReference.child(user.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getApplicationContext(), "Your username is updated successfully", Toast.LENGTH_SHORT).show();
                                    nameBottomSheetTxt.setText("");
                                    bottomSheetDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Please enter your new username", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
                cancelBottomSheet = bottomSheetDialog.findViewById(R.id.cancel_bottom_btn);
                cancelBottomSheet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.show();
            }
        });
        phoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseAuth != null) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(SettingsActivity.this, SignUpActivity.class));
                    finish();
                }
            }
        });
        aboutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAboutUpdateDialog();
            }
        });
        finishSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(nameTxt.getText())) {
                    startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                    finish();
                } else {
                    nameTxt.setError(getResources().getString(R.string.enter_your_name));
                }
            }
        });
    }

    private void launchPictureDialogs() {
        if (checkInternetConnection()) {

            if (ContextCompat.checkSelfPermission(SettingsActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                showPictureDialog();
            }else {
                requestMultiplePermissions();
            }
        } else {
            showConnectionDialog();
        }
    }


    private void initiation() {
        nameLayout = findViewById(R.id.name_layout);
        phoneLayout = findViewById(R.id.phone_layout);
        aboutLayout = findViewById(R.id.about_layout);
        cameraBtn = findViewById(R.id.fab_btn);
        profileImage = findViewById(R.id.profile_image);
        nameTxt = findViewById(R.id.name);
        phoneTxt = findViewById(R.id.phone);
        aboutTxt = findViewById(R.id.about);
        imageLoader = findViewById(R.id.image_loader);
        imageLoader.setVisibility(View.GONE);
        finishSettings = findViewById(R.id.finish_settings);

    }


    private void showAboutUpdateDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update About ");
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);
        final EditText editText = new EditText(this);
        editText.setHint("Enter you new bio");
        editText.setTextSize(15);
        linearLayout.addView(editText);

        builder.setView(linearLayout);

        builder.setPositiveButton(getResources().getString(R.string.update), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(value)) {

                    HashMap<String, Object> result = new HashMap<>();
                    result.put("about", value);
                    databaseReference.child(user.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Your bio is updated successfully", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter your new bio", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Insert a new Image");
        String[] pictureDialogItems = {
                "Open Gallery ",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // mImageUrl=android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        startActivityForResult(galleryIntent, GALLERY);
    }

    public void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                file = data.getData();
                profileImage.setImageURI(data.getData());
                Uri contentUri = file;
                imageLoader.setVisibility(View.VISIBLE);
                uploadImage(file);

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentUri);
//                    String path = saveImage(bitmap);
//                    Toast.makeText(getApplicationContext(), "Image Saved in" + " " + path, Toast.LENGTH_SHORT).show();

                    profileImage.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    //    Toast.makeText(WorkerProfile.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            profileImage.setImageBitmap(thumbnail);
            imageLoader.setVisibility(View.VISIBLE);
            uploadImage(getImageUri(this, thumbnail));
//            saveImage(thumbnail);
//              Toast.makeText(SettingsActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            state = true;
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
//                            openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();

                    }
/*
                    @Override
                  /  public void onPermissionRationaleShouldBeShown(List&lt;PermissionRequest&gt;permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }*/
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }


    void uploadImage(Uri uri) {

        Uri file = uri;
        mProfileStorage = FirebaseStorage.getInstance().getReference();
        final StorageReference filePath = mProfileStorage.child("profile_images/image" + (int) (Math.random() * 100) + "" + System.currentTimeMillis() + ".jpg");
        filePath.putFile(file).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return filePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downUri = task.getResult();
                    Log.d("Image uri link", "onComplete: Url: " + downUri.toString());
                    String downloaded_url = downUri.toString();
                    databaseReference.child(user.getUid()).child("image").setValue(downloaded_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                imageLoader.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            imageLoader.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Can not upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
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