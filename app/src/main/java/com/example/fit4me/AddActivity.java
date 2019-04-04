package com.example.fit4me;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;


public class AddActivity extends AppCompatActivity {

    //TODO way of remembering what checkboxes were already checked
    private Button applyButton;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHandler database = new DatabaseHandler(this,null);

        setContentView(R.layout.activity_add);

        String name = getIntent().getStringExtra("username");
        TextView text = findViewById(R.id.activity_text);
        text.setText(String.format("%s's Activities Today", name));

        applyButton = findViewById(R.id.apply);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox football = findViewById(R.id.football);
                CheckBox swimming = findViewById(R.id.swimming);
                CheckBox basketball = findViewById(R.id.basketball);
                CheckBox cycling = findViewById(R.id.cycling);
                CheckBox [] activityArray = {football, swimming, basketball, cycling};
                boolean [] checkedArray = {false, false, false, false};
                for(int i = 0; i < activityArray.length; i++) {
                    if(activityArray[i].isChecked()) {
                        checkedArray[i] = true;
                        Log.i("Activities", "i = true");
                    }
                }
                Intent data = getIntent();
                data.putExtra("Activities", checkedArray);
                setResult(RESULT_OK, data);
                Log.i("Activities", "Returning to the home page");
                finish();
            }
        });
    }
}

