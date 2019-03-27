package com.example.fit4me;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AddActivity extends AppCompatActivity {

    //TODO way of remembering what checkboxes were already checked

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        String name = getIntent().getStringExtra("username");
        TextView text = findViewById(R.id.activity_text);
        text.setText(String.format("%s's Activities Today", name));
    }
}
