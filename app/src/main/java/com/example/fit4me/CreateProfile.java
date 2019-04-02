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
        final DatabaseHandler database = new DatabaseHandler(this,null);

        //button functionality to HomePage activity
        button = findViewById(R.id.button2);
        nameInput = findViewById(R.id.nameInput);
        goalInput = findViewById(R.id.goalInput);
        nameInput.bringToFront();
        goalInput.bringToFront();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nameInput.getText() != null && goalInput.getText() != null) {
                    String username = nameInput.getText().toString();
                    int goal = Integer.parseInt(goalInput.getText().toString());
                    if (goal != 0 && username != "") {
                        database.createAccount(goal, username);   //TODO write goal to database here
                        Intent intent = new Intent(CreateProfile.this, HomePage.class);
                        String[] arguments = {username, goalInput.getText().toString()};
                        intent.putExtra("Source", "createprofile");
                        intent.putExtra("arguments", arguments);
                        startActivity(intent);
                    }
                }
            }
        });
    }

}
