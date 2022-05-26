package com.sagisu.vaultLibrary.ui.kyc;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.sagisu.vaultLibrary.R;
import com.sagisu.vaultLibrary.utils.Util;

public class VerifyIdIntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veify_id_intro);
        setActionBarTitle("Verify ID");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.showSnackBar("Coming soon",VerifyIdIntroActivity.this);
                /*Intent intent = new Intent(VerifyIdIntroActivity.this, DocumentVerificationActivity.class);
                startActivity(intent);*/
            }
        });

        findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}