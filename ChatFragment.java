package com.example.buzzchat;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class ChatFragment extends Fragment {
    private View chatScreenView;
    private ViewPager chat_screen_view_pager;       private TabLayout chat_screen_tabs;
    private TabItem chat_screen_chat_tab,chat_screen_Group_tab,chat_screen_contact_tab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        chatScreenView = inflater.inflate(R.layout.fragment_chat, container, false);
        FieldsInitializations();

        // Ensure the initial fragment is loaded only once
        if (savedInstanceState == null) {
            replaceScreen(new PersonalChatFragment(),0);
        }

        chat_screen_tabs.addTab(chat_screen_tabs.newTab().setText("Chats"));
        chat_screen_tabs.addTab(chat_screen_tabs.newTab().setText("Contacts"));

        chat_screen_tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        selectedFragment = new PersonalChatFragment();
                        replaceScreen(selectedFragment,0);
                        break;
                    case 1:
                        selectedFragment = new ContactsFragment();
                        replaceScreen(selectedFragment,1);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do nothing
//                Fragment selectedFragment = null;
//                switch (tab.getPosition()) {
//                    case 0:
//                        selectedFragment = new PersonalChatFragment();
//                        replaceScreen(selectedFragment, 0);
//                        break;
//                    case 1:
//                        selectedFragment = new ContactsFragment();
//                        replaceScreen(selectedFragment, 1);
//                        break;
//                }
            }
        });

        return chatScreenView;
    }
    private void FieldsInitializations() {
        chat_screen_tabs = chatScreenView.findViewById(R.id.chat_screen_tabs);
    }
    private void replaceScreen(Fragment fragment,int flag) {
        if (flag==0){
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.chat_screen_frame_layout,fragment).commit();
        }
        else {
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.chat_screen_frame_layout,fragment).commit();
        }
    }
}