package com.example.fit4me;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;


public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    //implement Google account sign in to access CreateProfile activity

    //changing activity functions
    public void toCreateProfile(View view)
    {
        Intent intent = new Intent(this, CreateProfile.class);
        startActivity(intent);
    }
    public void toHomePage(View view)
    {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }

}
