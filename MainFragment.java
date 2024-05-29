package com.example.buzzchat;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainFragment extends Fragment {

    private View mainScreenView;
    private BottomNavigationView main_fragment_bottom_navigation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainScreenView = inflater.inflate(R.layout.fragment_main, container, false);
        FieldsInitializations();

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

        if(savedInstanceState == null){
            replaceScreen(new HomeFragment(),0);
        }

        return mainScreenView;
    }
    private void FieldsInitializations() {
        main_fragment_bottom_navigation = mainScreenView.findViewById(R.id.main_fragment_bottom_navigation);
    }
    public void replaceScreen(Fragment fragment,int flag) {
//        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if (flag == 0) {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_frame_layout,fragment).commit();
        }
        else {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_frame_layout,fragment).addToBackStack(null).commit();
        }
    }

}