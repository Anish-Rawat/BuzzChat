package com.example.buzzchat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment {

    private View HomeScreenView;        private ProgressBar home_screen_progress_bar;
    private CircleImageView home_screen_my_profile;     private ImageView home_screen_notifications;        private TextView home_screen_my_name,home_screen_app_about_link;
    private RecyclerView home_Screen_recycler_view_stories,home_screen_recycler_view_popular_users;
    private ArrayList<UserInfoDataModel> popularUsersList = new ArrayList<>();      private PopularUsersAdapter popularUsersAdapter;

    private FirebaseAuth mAuth;     private DatabaseReference rootRef,userRef;      private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        HomeScreenView = inflater.inflate(R.layout.fragment_home, container, false);

        FieldsInitializations();

        FetchMyProfileData();
        home_screen_progress_bar.setVisibility(View.VISIBLE);
        FetchPopularUsersData();

        popularUsersAdapter = new PopularUsersAdapter(popularUsersList,getContext());
        home_screen_recycler_view_popular_users.setAdapter(popularUsersAdapter);
        home_screen_recycler_view_popular_users.setLayoutManager(new GridLayoutManager(getContext(),2,RecyclerView.HORIZONTAL,false));

        home_screen_app_about_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AboutAppFragment aboutAppFragment = new AboutAppFragment();

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_frame_layout, aboutAppFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return HomeScreenView;
    }
    private void FieldsInitializations() {

        home_screen_my_profile = HomeScreenView.findViewById(R.id.home_screen_my_profile);
        home_screen_notifications = HomeScreenView.findViewById(R.id.home_screen_notifications);
        home_screen_my_name = HomeScreenView.findViewById(R.id.home_screen_my_name);
        home_screen_app_about_link = HomeScreenView.findViewById(R.id.home_screen_app_about_link);
        home_Screen_recycler_view_stories = HomeScreenView.findViewById(R.id.home_Screen_recycler_view_stories);
        home_screen_recycler_view_popular_users = HomeScreenView.findViewById(R.id.home_screen_recycler_view_popular_users);
        home_screen_progress_bar = HomeScreenView.findViewById(R.id.home_screen_progress_bar);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null){
            currentUserId = mAuth.getCurrentUser().getUid();
        }
        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Users");
    }
    private void FetchMyProfileData() {

        if(currentUserId!=null){
            userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.hasChild("userProfile")){
                        home_screen_my_name.setText(snapshot.child("name").getValue().toString());
                        if (isAdded() && getActivity() != null) {       // Before calling Glide, ensure that the fragment is added and getActivity() is not null. This prevents the NullPointerException if the fragment is not attached to an activity.
                            Glide.with(getActivity())
                                    .load(snapshot.child("userProfile").getValue().toString())
                                    .placeholder(R.drawable.my_profile)
                                    .into(home_screen_my_profile);
                        }
                    } else if (!snapshot.hasChild("userProfile")) {
                        home_screen_my_name.setText(snapshot.child("name").getValue().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                    Log.d("Home Screen", "onCancelled: "+error.getMessage());
                }
            });
        }

    }

    private void FetchPopularUsersData() {

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                popularUsersList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    if (dataSnapshot.hasChild("userProfile")){
                        if (!dataSnapshot.child("userId").getValue().toString().equals(currentUserId)){
                            popularUsersList.add(new UserInfoDataModel(dataSnapshot.child("userProfile").getValue().toString(),dataSnapshot.child("userId").getValue().toString(),
                                    dataSnapshot.child("name").getValue().toString(),dataSnapshot.child("nickname").getValue().toString(),dataSnapshot.child("about").getValue().toString(),
                                    dataSnapshot.child("dob").getValue().toString(),dataSnapshot.child("gender").getValue().toString(),dataSnapshot.child("country").getValue().toString(),dataSnapshot.child("contactNumber").getValue().toString(),
                                    dataSnapshot.child("address").getValue().toString()));
                        }
                    }
                }
                popularUsersAdapter.notifyDataSetChanged();
                home_screen_progress_bar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}