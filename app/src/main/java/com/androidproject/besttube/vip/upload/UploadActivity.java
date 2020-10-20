package com.androidproject.besttube.vip.upload;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androidproject.besttube.vip.R;
import com.androidproject.besttube.vip.main.MainActivity;
import com.androidproject.besttube.vip.model.VideoItem;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class UploadActivity extends AppCompatActivity {

    private static final String DEBUG_TAG = "NetworkStatus";
    private ImageView homeButton, thumbnail, closeInternetDialog;
    Dialog connectionDialog;
    VideoView videoView;
    Button addVideoBtn;
    TextInputEditText videoName;
    FloatingActionButton uploadBtn;
    Spinner categories;
    ArrayAdapter<String> videosTypes;
    private Uri videoUri, thumbnailUri;
    String downloaded_url;
    MediaController mediaController;
    private int STORAGE_PERMISSION_CODE = 1;
    private int PICK_VIDEO = 1;

    VideoItem videoItem;
    UploadTask uploadTask;

    Dialog loaderDialog;
    //Firebase Utils
    FirebaseAuth auth;
    FirebaseUser currentUser;
    StorageReference storageReference, thumbnailStorageReference;
    DatabaseReference userReference, mono3atReference, sportsReference, childrenReference, religiousReference, educationReference;
    public Bitmap bitmapThumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_activity);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        initiation();
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.start();

        //Category Spinner
        videosTypes = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.video_categories));
        videosTypes.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categories.setAdapter(videosTypes);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadActivity.this, MainActivity.class));
                finish();
            }
        });
        addVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(UploadActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED) {
                    addVideo();
//                    Toast.makeText(getApplicationContext(),"You have already granted this permission",Toast.LENGTH_SHORT).show();
                } else {
                    requestPermission(UploadActivity.this);
                }
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loaderDialog = new Dialog(UploadActivity.this);
                loaderDialog.setContentView(R.layout.loader_dialog);
                Objects.requireNonNull(loaderDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                uploadVideo();
            }
        });

    }

    private void initiation() {
        addVideoBtn = findViewById(R.id.add_video);
        videoView = findViewById(R.id.videoView_upload);
        videoName = findViewById(R.id.video_title);
        uploadBtn = findViewById(R.id.upload_video);
        homeButton = findViewById(R.id.home_button);
        categories = findViewById(R.id.categories_spinner);
        thumbnail = findViewById(R.id.upload_thumbnail);
        videoItem = new VideoItem();


        storageReference = FirebaseStorage.getInstance().getReference("video");
        thumbnailStorageReference = FirebaseStorage.getInstance().getReference("thumbnail");

        userReference = FirebaseDatabase.getInstance().getReference("users");
        mono3atReference = FirebaseDatabase.getInstance().getReference("video").child("mono3at");
        religiousReference = FirebaseDatabase.getInstance().getReference("video").child("religious");
        sportsReference = FirebaseDatabase.getInstance().getReference("video").child("sports");
        childrenReference = FirebaseDatabase.getInstance().getReference("video").child("children");
        educationReference = FirebaseDatabase.getInstance().getReference("video").child("education");
    }

    private void requestPermission(Activity activity) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Sorry permission needed")
                    .setMessage("This permission is needed to complete uploading videos")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                addVideo();
            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                requestPermission(this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO || requestCode == STORAGE_PERMISSION_CODE || resultCode == RESULT_OK
                || data != null || data.getData() != null) {
            if (data != null) {

                videoUri = data.getData();
                if (videoUri != null) {
                    videoView.setVideoURI(videoUri);
                    String selectedPathVideo = "";
                    selectedPathVideo = ImageFilePath.getPath(getApplicationContext(), videoUri);
                    Log.e("Image File Path", "" + selectedPathVideo);

                    try {
//                        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(selectedPathVideo, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
//                        if (thumb != null) {
//                            Log.e("ThumbnailAfterCreation", "Not null");
//                        }
//                        thumbnailUri = getImageUri(UploadActivity.this, thumb);
//                        Log.e("URI", "" + thumbnailUri.toString());
//                        thumbnail.setImageURI(thumbnailUri);


                        Size mSize = new Size(96, 96);
                        CancellationSignal ca = new CancellationSignal();
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            File file = new File(videoUri.getPath());
                            bitmapThumbnail = ThumbnailUtils.createVideoThumbnail(file, mSize, ca);
                            Toast.makeText(this, "From 10", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(this, "From 9", Toast.LENGTH_SHORT).show();
                            bitmapThumbnail = ThumbnailUtils.createVideoThumbnail(selectedPathVideo, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                        }
                        if (bitmapThumbnail != null) {
                            Log.e("ThumbnailAfterCreation", "Not null");
                            Bitmap bitmap = null;
                            thumbnailUri = getImageUri(UploadActivity.this, bitmapThumbnail);
                            if (thumbnailUri != null) {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                    bitmap = getContentResolver().loadThumbnail(thumbnailUri, mSize, ca);
                                    thumbnail.setImageBitmap(bitmap);
                                } else {
                                    thumbnail.setImageURI(thumbnailUri);
                                }
                                Log.e("URI", "" + thumbnailUri.toString());
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.no_selected_videos), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.something_go_wrong_on_finding_any_videos), Toast.LENGTH_SHORT).show();
        }
    }


    private void addVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void uploadVideo() {


        final String videoNameFb = videoName.getText().toString().trim();
        final String videoCategoryFb = categories.getSelectedItem().toString();
        final String videoSearchFb = videoName.getText().toString().toLowerCase().trim();

        if (videoUri != null || !TextUtils.isEmpty(videoNameFb)) {
            uploadBtn.setEnabled(false);
            loaderDialog.show();
            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getExtra(videoUri));
            uploadTask = reference.putFile(videoUri);
            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();

                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        videoItem.setVideoTitle(videoNameFb);
                        videoItem.setCategories(videoCategoryFb);
                        videoItem.setSearch(videoSearchFb);
                        videoItem.setThumbnail("");
                        videoItem.setVideoOwner(currentUser.getUid());
                        if (downloadUrl != null) {
                            videoItem.setVideoUrl(downloadUrl.toString());
                        }

                        if (checkInternetConnection()) {
                            if (videoUri == null || videoName.getText().toString().isEmpty()) {
                                videoName.setError(getResources().getString(R.string.enter_video_title));
                                uploadBtn.setEnabled(true);
                                return;
                            } else {
                                if (videoCategoryFb != null) {
                                    Calendar calendar = Calendar.getInstance();
                                    videoItem.setVideoDate(calendar.getTime());
                                    if (categories.getSelectedItemPosition() == 0) {
                                        String key = mono3atReference.push().getKey();
                                        videoItem.setVideoId(key);
                                        if (key != null) {
                                            mono3atReference.child(key).setValue(videoItem);
                                            uploadThumbnail(key, thumbnailUri, mono3atReference);
                                        }
                                    } else if (categories.getSelectedItemPosition() == 1) {
                                        String key = sportsReference.push().getKey();
                                        videoItem.setVideoId(key);

                                        if (key != null) {
                                            mono3atReference.child(key).setValue(videoItem);
                                            sportsReference.child(key).setValue(videoItem);
                                            uploadThumbnail(key, thumbnailUri, sportsReference);
                                        }
                                    } else if (categories.getSelectedItemPosition() == 2) {
                                        String key = educationReference.push().getKey();
                                        videoItem.setVideoId(key);

                                        if (key != null) {
                                            mono3atReference.child(key).setValue(videoItem);
                                            educationReference.child(key).setValue(videoItem);
                                            uploadThumbnail(key, thumbnailUri, educationReference);

                                        }
                                    } else if (categories.getSelectedItemPosition() == 3) {
                                        String key = religiousReference.push().getKey();
                                        videoItem.setVideoId(key);

                                        if (key != null) {
                                            mono3atReference.child(key).setValue(videoItem);
                                            religiousReference.child(key).setValue(videoItem);
                                            uploadThumbnail(key, thumbnailUri, religiousReference);
                                        }
                                    } else if (categories.getSelectedItemPosition() == 4) {
                                        String key = childrenReference.push().getKey();
                                        videoItem.setVideoId(key);
                                        if (key != null) {
                                            mono3atReference.child(key).setValue(videoItem);
                                            childrenReference.child(key).setValue(videoItem);
                                            uploadThumbnail(key, thumbnailUri, childrenReference);
                                        }
                                    }
                                }
                            }
                        } else {
                            loaderDialog.dismiss();
                            uploadBtn.setEnabled(true);
                            Toast.makeText(getApplicationContext(), "Error in Database reference", Toast.LENGTH_SHORT).show();
                        }
                        Log.e("VideoLink", "" + downloadUrl.toString());
                        uploadBtn.setEnabled(true);
                        loaderDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "" + getResources().getString(R.string.video_uploaded), Toast.LENGTH_SHORT).show();
                        clearData();

                    } else {
                        loaderDialog.dismiss();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_on_uploading), Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loaderDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Upload Error", "" + e.getMessage());
                }
            });
        } else {

            if (checkInternetConnection()) {
                Toast.makeText(getApplicationContext(), "Try again later", Toast.LENGTH_SHORT).show();
            } else {
                showConnectionDialog();
            }
        }
    }

    private void uploadThumbnail(String key, Uri uri, DatabaseReference reference) {
        if (uri != null) {
            final StorageReference filePath = thumbnailStorageReference.child("thumbnail" + (int) (Math.random() * 100) + "" + System.currentTimeMillis() + ".jpg");
            filePath.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri thumbnailDownUri = task.getResult();
                        Log.d("Image uri link", "onComplete: Url: " + thumbnailDownUri.toString());
                        downloaded_url = thumbnailDownUri.toString();
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("thumbnail", downloaded_url);
                        if (categories.getSelectedItemPosition() == 0) {
                            uploadMono3atThumbnail(key, map);
                        } else {
                            HashMap<String, Object> map2 = new HashMap<>();
                            map2.put("thumbnail", downloaded_url);

                            reference.child(key).updateChildren(map2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    uploadMono3atThumbnail(key, map2);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });


                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("CanNotUploadThumbnail", e.getMessage());
                }
            });
        }
    }

    private void uploadMono3atThumbnail(String key, HashMap<String, Object> map) {
        mono3atReference.child(key).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void clearData() {

        videoView.setMediaController(null);
        videoName.setText("");
        categories.setSelection(0);

    }

    private String getExtra(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

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
