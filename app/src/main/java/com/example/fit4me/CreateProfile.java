package com.example.fit4me;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;


public class CreateProfile extends AppCompatActivity {

    private Button button;
    private String goal;
    public static final String BRONZE_GOAL = "4000";
    public static final String SILVER_GOAL = "8000";
    public static final String GOLD_GOAL = "12000";

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
                database.createAccount(Integer.parseInt(goal), "");
                Intent intent = new Intent(CreateProfile.this, HomePage.class);
                startActivity(intent);
            }
        });
    }

    public void onRadioButtonClicked(View view)
    {
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.bronze:
                if (checked)
                {
                    this.goal = BRONZE_GOAL;
                }
                break;
            case R.id.silver:
                if (checked)
                {
                    this.goal = SILVER_GOAL;
                }
                break;
            case R.id.gold:
                if(checked)
                {
                    this.goal = GOLD_GOAL;
                }
        }
    }

}
