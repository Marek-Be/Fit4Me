package com.example.fit4me;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;


public class CreateProfile extends AppCompatActivity {

    private static final int BRONZE_GOAL = 4000;
    private static final int SILVER_GOAL = 8000;
    private static final int GOLD_GOAL = 12000;

    private Button button;
    private int goal = SILVER_GOAL;     //Default value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        final DatabaseHandler database = new DatabaseHandler(this,null);
        //button functionality to HomePage activity
        button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.createAccount(goal);
                Intent intent = new Intent(CreateProfile.this, HomePage.class);
                startActivity(intent);
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.bronze:
                if (checked)
                    this.goal = BRONZE_GOAL;
                break;
            case R.id.silver:
                if (checked)
                    this.goal = SILVER_GOAL;
                break;
            case R.id.gold:
                if(checked)
                    this.goal = GOLD_GOAL;
        }
    }

}
