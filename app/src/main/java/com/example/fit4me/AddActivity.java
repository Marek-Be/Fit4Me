package com.example.fit4me;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        TextView text = findViewById(R.id.activity_text);
        text.setText("Activities Today");

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
                for(int i = 0; i < activityArray.length; i++)
                    if(activityArray[i].isChecked())
                        checkedArray[i] = true;
                Intent data = getIntent();
                data.putExtra("Activities", checkedArray);
                setResult(RESULT_OK, data);
                Log.i("Activities", "Returning to the home page");
                finish();
            }
        });
    }
}

