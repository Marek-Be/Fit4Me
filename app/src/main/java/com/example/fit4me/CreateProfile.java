package com.example.fit4me;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class CreateProfile extends AppCompatActivity {

    private Button button;
    private EditText nameInput;
    private EditText goalInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        //button functionality to HomePage activity
        button = findViewById(R.id.button2);
        nameInput = findViewById(R.id.nameInput);
        goalInput = findViewById(R.id.goalInput);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateProfile.this, HomePage.class);
                String [] arguments = {nameInput.getText().toString(), goalInput.getText().toString()};
                intent.putExtra("arguments", arguments);
                startActivity(intent);
            }
        });

    }

    //way of creating user account based on goal information inputted

}
