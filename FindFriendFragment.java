package com.example.buzzchat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindFriendFragment extends Fragment implements ContactItemAdapter.ContactItemClickListener{

    private View FindFriendScreenView;      private ProgressBar find_friend_screen_progress_bar;
    private RecyclerView find_friend_screen_recycler_view;      private View find_friend_screen_search_layout;
    private ArrayList<ContactInfoDataModel> userList = new ArrayList<>();       private ContactItemAdapter contactItemAdapter;

    private FirebaseAuth mAuth;     private DatabaseReference rootRef;      private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FindFriendScreenView = inflater.inflate(R.layout.fragment_find_friend, container, false);

        FieldsInitializations();

        find_friend_screen_progress_bar.setVisibility(View.VISIBLE);

        fetchUsers();

        contactItemAdapter = new ContactItemAdapter(userList,getContext(),this);
        find_friend_screen_recycler_view.setAdapter(contactItemAdapter);
        find_friend_screen_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.d("FindFriendsFragment", "onCreateView: "+userList);

        return FindFriendScreenView;
    }
    private void FieldsInitializations() {

        find_friend_screen_recycler_view = FindFriendScreenView.findViewById(R.id.find_friend_screen_recycler_view);
        find_friend_screen_progress_bar = FindFriendScreenView.findViewById(R.id.find_friend_screen_progress_bar);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
    }
    private void fetchUsers() {

        rootRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    if (!dataSnapshot.getKey().equals(currentUserId)){
                        if (dataSnapshot.hasChild("userProfile")){
                            userList.add(new ContactInfoDataModel(dataSnapshot.child("userProfile").getValue().toString(), dataSnapshot.child("name").getValue().toString(),
                                    dataSnapshot.child("nickname").getValue().toString(),dataSnapshot.child("about").getValue().toString(),
                                    dataSnapshot.child("userId").getValue().toString()));
                        }
                        else{
                            Log.d("FindFriendFrag", "onDataChange: "+dataSnapshot.getKey());

                            userList.add(new ContactInfoDataModel(dataSnapshot.child("name").getValue().toString(),
                                    dataSnapshot.child("nickname").getValue().toString(),dataSnapshot.child("about").getValue().toString(),
                                    dataSnapshot.child("userId").getValue().toString()));
                        }
                    }
                }
                find_friend_screen_progress_bar.setVisibility(View.GONE);
                contactItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void ContactItemClick(Bundle bundle) {

        UserProfileFragment userProfileScreen = new UserProfileFragment();
        userProfileScreen.setArguments(bundle);

        ((DashboardActivity) requireActivity()).replaceScreen(userProfileScreen,1);
    }
}