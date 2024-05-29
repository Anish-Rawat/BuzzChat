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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class RequestFragment extends Fragment implements RequestItemAdapter.ButtonClickListener{

    private View RequestScreenView;     private View request_screen_no_request_view;        private RecyclerView request_screen_recycler_view;
    private RequestItemAdapter requestItemAdapter;      private ProgressBar request_screen_progress_bar;

    private FirebaseAuth mAuth;     private DatabaseReference rootRef,contactRef,requestRef,userRef;       private String currentUserId,userId; // here userId refer to request sender id
    private ArrayList<ContactInfoDataModel> requestList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RequestScreenView = inflater.inflate(R.layout.fragment_request, container, false);

        FieldsInitializations();

        request_screen_progress_bar.setVisibility(View.VISIBLE);
        fetchRequest();

        requestItemAdapter = new RequestItemAdapter(requestList,requireContext(),this);
        request_screen_recycler_view.setAdapter(requestItemAdapter);
        request_screen_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));


        return RequestScreenView;
    }

    private void FieldsInitializations() {

        request_screen_no_request_view = RequestScreenView.findViewById(R.id.request_screen_no_request_view);
        request_screen_recycler_view = RequestScreenView.findViewById(R.id.request_screen_recycler_view);
        request_screen_progress_bar = RequestScreenView.findViewById(R.id.request_screen_progress_bar);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        contactRef = rootRef.child("Contact");
        requestRef = rootRef.child("Request");
        userRef = rootRef.child("Users");
    }
    private void fetchRequest() {

        requestRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                requestList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    if (dataSnapshot.child("request_type").getValue(String.class).equals("received")){

                        userId = dataSnapshot.getKey();
                        FetchRequestSenderData(userId);
                    }
//                    updateUI();
                }
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        updateUI();

    }

    private void FetchRequestSenderData(String userId) {

        userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild("userProfile")){
                    requestList.add(new ContactInfoDataModel(snapshot.child("userProfile").getValue().toString(), snapshot.child("name").getValue().toString(),
                            snapshot.child("nickname").getValue().toString(),snapshot.child("about").getValue().toString(),
                            snapshot.child("userId").getValue().toString()));
                }
                else{
                    Log.d("RequestFrag", "onDataChange: "+snapshot.getKey());

                    requestList.add(new ContactInfoDataModel(snapshot.child("name").getValue().toString(),
                            snapshot.child("nickname").getValue().toString(),snapshot.child("about").getValue().toString(),
                            snapshot.child("userId").getValue().toString()));
                }
                requestItemAdapter.notifyDataSetChanged();
                updateUI();
                request_screen_progress_bar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateUI() {
        if (requestList.isEmpty()) {
            request_screen_no_request_view.setVisibility(View.VISIBLE);
            request_screen_recycler_view.setVisibility(View.GONE);
            request_screen_progress_bar.setVisibility(View.GONE);
        } else {
            request_screen_no_request_view.setVisibility(View.GONE);
            request_screen_recycler_view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void acceptButtonClick(String reqSenderId) {

        contactRef.child(currentUserId).child(reqSenderId).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    contactRef.child(reqSenderId).child(currentUserId).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                requestRef.child(currentUserId).child(reqSenderId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){
                                            requestRef.child(reqSenderId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()){
                                                        Toast.makeText(requireContext(), "Accepted", Toast.LENGTH_SHORT).show();
                                                        removeRequestFromList(reqSenderId);
                                                        requestItemAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void cancelButtonClick(String reqSenderId) {

        requestRef.child(currentUserId).child(reqSenderId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    requestRef.child(reqSenderId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(requireContext(), "Accepted", Toast.LENGTH_SHORT).show();
                                removeRequestFromList(reqSenderId);
                                requestItemAdapter.notifyDataSetChanged();
                            }

                        }
                    });
                }
            }
        });
    }
    private void removeRequestFromList(String reqSenderId) {
        for (int i = 0; i < requestList.size(); i++) {
            if (requestList.get(i).getUserId().equals(reqSenderId)) {
                requestList.remove(i);
                break;
            }
        }
        requestItemAdapter.notifyDataSetChanged();
        updateUI();
    }
}