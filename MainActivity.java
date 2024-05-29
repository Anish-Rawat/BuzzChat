package com.example.buzzchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity{

    private FrameLayout main_activity_frame_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if there's an action in the intent
        Intent intent = getIntent();
        if (intent != null && "signout".equals(intent.getStringExtra("action"))) {
            replaceFragment(new SignInFragment(), 0);
        } else{
            FieldsInitializations();
            // Load the initial fragment (SplashFragment)
            if (savedInstanceState == null) {
                openSplashScreen();
            }
        }
    }
    private void FieldsInitializations() {

        main_activity_frame_layout = findViewById(R.id.main_activity_frame_layout);
    }
    private void openSplashScreen() {
        SplashFragment splashScreen = new SplashFragment();
        replaceFragment(splashScreen,0);
//        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_frame_layout,splashScreen).commit();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
    public void replaceFragment(Fragment fragment,int flag){

        if (flag == 0){
            getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_frame_layout,fragment).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_frame_layout,fragment).commit();
        }
    }
}