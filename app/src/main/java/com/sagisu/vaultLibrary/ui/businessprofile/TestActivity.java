package com.sagisu.vaultLibrary.ui.businessprofile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sagisu.vaultLibrary.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        FloatingActionButton fb = findViewById(R.id.fabMenu);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fb.setExpanded(true);
            }
        });
    }
}