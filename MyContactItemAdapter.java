package com.example.buzzchat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyContactItemAdapter extends RecyclerView.Adapter<MyContactItemAdapter.MyContactViewHolder> {
    private ArrayList<UserInfoDataModel> myContactList = new ArrayList<>();     private Context context;

    private MyContactItemClickListener myContactItemClickListener;
    private DatabaseReference userRef;

    public interface MyContactItemClickListener{
        void MyContactItemClick(Bundle bundle);
    }
    public MyContactItemAdapter(ArrayList<UserInfoDataModel> myContactList, Context context,MyContactItemClickListener callBack) {
        this.myContactList = myContactList;
        this.context = context;
        this.myContactItemClickListener = callBack;
    }

    @NonNull
    @Override
    public MyContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_card,parent,false);
        MyContactViewHolder holder = new MyContactViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyContactViewHolder holder, int position) {

        UserInfoDataModel obj = myContactList.get(position);

        holder.contact_name.setText(obj.getName());
        holder.contact_about.setText(obj.getAbout());
        holder.contact_nickname.setText(obj.getNickname());

        userRef.child(obj.getUserId()).child("userState").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("state")){
                    if (snapshot.child("state").getValue().toString().equals("online")){
                        holder.contact_state.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.contact_state.setVisibility(View.INVISIBLE);
                    }
                }
                else {
                    Log.d("Personal Msg frag", "Online State not updated");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d("Personal Msg frag", "message := "+error.getMessage().toString());
            }
        });

        Glide.with(context).load(obj.getUserProfile()).placeholder(R.drawable.my_profile).into(holder.contact_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myContactItemClickListener!=null){

                    Bundle bundle = new Bundle();
                    bundle.putString("Identify","MyContactToPersonalMessage");
                    bundle.putString("userId",obj.getUserId());
                    bundle.putString("name",obj.getName());
                    bundle.putString("userProfile",obj.getUserProfile());
                    myContactItemClickListener.MyContactItemClick(bundle);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return myContactList.size();
    }

    class MyContactViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView contact_image,contact_state;
        private TextView contact_name,contact_nickname,contact_about;
        public MyContactViewHolder(@NonNull View itemView) {
            super(itemView);

            contact_image = itemView.findViewById(R.id.contact_image);
            contact_state = itemView.findViewById(R.id.contact_state);
            contact_name = itemView.findViewById(R.id.contact_name);
            contact_nickname = itemView.findViewById(R.id.contact_nickname);
            contact_about = itemView.findViewById(R.id.contact_about);
        }
    }
}
