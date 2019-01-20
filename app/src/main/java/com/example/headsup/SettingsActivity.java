package com.example.headsup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_popup);

        Toast.makeText(this, "OOOOOOOOOOOOF", Toast.LENGTH_SHORT).show();
    }
}
