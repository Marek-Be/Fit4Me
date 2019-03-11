package com.example.fit4me;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class CreateProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
    }

    public void toHomePage(View view)
    {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }

    //way of creating user account based on goal information inputted

}
