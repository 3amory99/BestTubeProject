package com.omarproject1.shashah.connectivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.omarproject1.shashah.R;
import com.omarproject1.shashah.main.MainActivity;

public class OfflineActivity extends AppCompatActivity {

    Button tryAgainBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        tryAgainBtn = findViewById(R.id.try_againBtn);
        tryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(OfflineActivity.this, MainActivity.class));
                finish();
            }
        });


    }
}