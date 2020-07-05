package com.omarproject1.shashah;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadActivity extends AppCompatActivity {

    private ImageView backButton;
    VideoView videoView;
    Button addVideoBtn;
    ProgressBar progressBar;
    TextInputEditText videoName, videoHashTag;
    FloatingActionButton uploadBtn;
    TextView textCounter;
    EditText description;
    Spinner categories;
    ArrayAdapter<String> videosTypes;
    private Uri videoUri;
    MediaController mediaController;
    private int STORAGE_PERMISSION_CODE =1;
    private int PICK_VIDEO =1;

    VideoItem videoItem;
    UploadTask uploadTask;

    //Firebase Utils
    StorageReference storageReference;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_activity);

        initiation();
        mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.start();

        //Category Spinner
        videosTypes = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.video_categories));
        videosTypes.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categories.setAdapter(videosTypes);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });

        //Description text counter
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String descriptionTxt = description.getText().toString();
                int symbols = descriptionTxt.length();
                textCounter.setText(""+symbols+"/150");
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        addVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(UploadActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED){
//                    Toast.makeText(getApplicationContext(),"You have already granted this permission",Toast.LENGTH_SHORT).show();
                }else {
                    requestPermission();
                }
                addVideo();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadVideo();
            }
        });

    }

    private void initiation(){

        progressBar = findViewById(R.id.progressBar);
        addVideoBtn = findViewById(R.id.add_video);
        videoView = findViewById(R.id.videoView_upload);
        videoName = findViewById(R.id.video_title);
        videoHashTag = findViewById(R.id.video_hashTag);
        description = findViewById(R.id.description_txt);
        uploadBtn = findViewById(R.id.upload_video);
        textCounter = findViewById(R.id.description_length);
        backButton = findViewById(R.id.back_button);
        categories = findViewById(R.id.categories_spinner);
        videoItem = new VideoItem();
        storageReference = FirebaseStorage.getInstance().getReference("video");
        databaseReference = FirebaseDatabase.getInstance().getReference("video");

    }

    private void requestPermission() {

        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this)
                    .setTitle("Sorry permission needed")
                    .setMessage("This permission is needed to complete uploading videos")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) getApplicationContext(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);

                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create();
        }else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==STORAGE_PERMISSION_CODE){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(),"Permission Granted",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_VIDEO  || requestCode == STORAGE_PERMISSION_CODE || resultCode == RESULT_OK
                || data != null || data.getData() != null){
            videoUri = data.getData();
            videoView.setVideoURI(videoUri);
        }
    }

    private void addVideo(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_VIDEO);
    }

    private void uploadVideo() {
        final String videoNameFb = videoName.getText().toString().trim() ;
        final String videoDescriptionFb = description.getText().toString().trim();
        final String videoCategoryFb = categories.getSelectedItem().toString();
        final String videoSearchFb = videoName.getText().toString().toLowerCase().trim();
        final String videoHashTagFb = videoHashTag.getText().toString().trim();
        if(videoUri != null || !TextUtils.isEmpty(videoNameFb)){
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getExtra(videoUri));
            uploadTask = reference.putFile(videoUri);
            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();

                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUrl = task.getResult();
                        Log.e("SSSsssssssssss",""+downloadUrl.toString());
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),"Data Uploaded",Toast.LENGTH_SHORT).show();
                        videoItem.setVideoTitle(videoNameFb);
                        videoItem.setVideoDescription(videoDescriptionFb);
                        videoItem.setHashTags(videoHashTagFb);
                        videoItem.setCategories(videoCategoryFb);
                        videoItem.setSearch(videoSearchFb);
                        videoItem.setVideoUrl(downloadUrl.toString());
                        String key = databaseReference.push().getKey();
                        databaseReference.child(key).setValue(videoItem);
                    }else {
                        Toast.makeText(getApplicationContext(),"Failed on uploading",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    Log.e("Upload Error",""+e.getMessage());
                }
            });
        }else {
            Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();

        }
    }

    private String getExtra(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }


}