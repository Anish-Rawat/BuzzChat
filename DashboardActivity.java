package com.example.buzzchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DashboardActivity extends AppCompatActivity {

    private BottomNavigationView main_fragment_bottom_navigation;
    private FirebaseAuth mAuth;     private DatabaseReference rootRef,userRef;      private String currentUserId;
    @Override
    public void onStart() {
        super.onStart();
        UpdateUserOnlineStatus("online");
    }

    @Override
    public void onStop() {
        super.onStop();
        UpdateUserOnlineStatus("offline");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        FieldsInitializations();
        if(savedInstanceState == null){
            replaceScreen(new HomeFragment(),0);
        }
        main_fragment_bottom_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if(id == R.id.home_screen){
                    replaceScreen(new HomeFragment(),0);
                }
                else if(id == R.id.search_screen){
                    replaceScreen(new FindFriendFragment(),1);
                }
                else if(id == R.id.chat_screen){
                    replaceScreen(new ChatFragment(),1);
                }
                else if(id == R.id.request_screen){
                    replaceScreen(new RequestFragment(),1);
                }
                else if(id == R.id.my_profile_screen){
                    replaceScreen(new MyProfileFragment(),1);
                }
                return true;
            }
        });

        main_fragment_bottom_navigation.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {

                int id = item.getItemId();

                if(id == R.id.home_screen){
                    replaceScreen(new HomeFragment(),0);
                }
                else if(id == R.id.search_screen){
                    replaceScreen(new FindFriendFragment(),0);
                }
                else if(id == R.id.chat_screen){
                    replaceScreen(new ChatFragment(),0);
                }
                else if(id == R.id.request_screen){
                    replaceScreen(new RequestFragment(),0);
                }
                else if(id == R.id.my_profile_screen){
                    replaceScreen(new MyProfileFragment(),0);
                }
            }
        });

       getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_frame_layout);
                if (currentFragment instanceof HomeFragment) {
                    main_fragment_bottom_navigation.setSelectedItemId(R.id.home_screen);
                } else if (currentFragment instanceof FindFriendFragment) {
                    main_fragment_bottom_navigation.setSelectedItemId(R.id.search_screen);
                }
                else if (currentFragment instanceof ChatFragment) {
                    main_fragment_bottom_navigation.setSelectedItemId(R.id.chat_screen);
                }
                else if (currentFragment instanceof RequestFragment) {
                    main_fragment_bottom_navigation.setSelectedItemId(R.id.request_screen);
                } else if (currentFragment instanceof MyProfileFragment) {
                    main_fragment_bottom_navigation.setSelectedItemId(R.id.my_profile_screen);
                }
            }
        });
    }
    private void FieldsInitializations() {
        main_fragment_bottom_navigation = findViewById(R.id.main_fragment_bottom_navigation);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null){
            currentUserId = mAuth.getCurrentUser().getUid();
        }
        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Users");
    }
    private void UpdateUserOnlineStatus(String state) {

        String currentDate , currentTime;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        currentDate = currentDateFormat.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        currentTime = currentTimeFormat.format(calForTime.getTime());

        UserInfoDataModel userInfoDataModel = new UserInfoDataModel();
        userInfoDataModel.setOnlineDate(currentDate);
        userInfoDataModel.setOnlineTiming(currentTime);
        userInfoDataModel.setState(state);

        userRef.child(currentUserId).child("userState").setValue(userInfoDataModel);
    }
    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_frame_layout);
        if (currentFragment instanceof HomeFragment) {
            finishAffinity(); // Close the app if the current fragment is HomeFragment
        } else {
            super.onBackPressed(); // Otherwise, pop the back stack
        }
    }

    public void replaceScreen(Fragment fragment, int flag) {

        if (flag == 0) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_frame_layout,fragment).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_frame_layout,fragment).addToBackStack(null).commit();
        }
    }
}