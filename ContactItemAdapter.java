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

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactItemAdapter extends RecyclerView.Adapter<ContactItemAdapter.ContactItemViewHolder> {

    private ArrayList<ContactInfoDataModel> userList = new ArrayList<>();       private Context context;

    private ContactItemClickListener contactItemClickListener;

    public interface ContactItemClickListener{
        void ContactItemClick(Bundle bundle);
    }
    public ContactItemAdapter(ArrayList<ContactInfoDataModel> userList, Context context,ContactItemClickListener callBack1) {
        this.userList = userList;
        this.context = context;
        this.contactItemClickListener = callBack1;
    }

    @NonNull
    @Override
    public ContactItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_card,parent,false);
        ContactItemViewHolder holder = new ContactItemViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContactItemViewHolder holder, int position) {

        ContactInfoDataModel obj = userList.get(position);

        holder.contact_name.setText(obj.getName());
        holder.contact_about.setText(obj.getAbout());
        holder.contact_nickname.setText(obj.getNickname());
        Glide.with(context).load(obj.getUserProfile()).placeholder(R.drawable.my_profile).into(holder.contact_image);

        Log.d("ContactItemAdapter", "onBindViewHolder: "+obj.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (contactItemClickListener!=null){
                    Bundle bundle = new Bundle();
                    bundle.putString("Identify","FindFrndToUserProfile");
                    bundle.putString("userId", obj.getUserId());
                    contactItemClickListener.ContactItemClick(bundle);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class ContactItemViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView contact_image,contact_state;
        private TextView contact_name,contact_nickname,contact_about;
        public ContactItemViewHolder(@NonNull View itemView) {
            super(itemView);

            contact_image = itemView.findViewById(R.id.contact_image);
            contact_state = itemView.findViewById(R.id.contact_state);
            contact_name = itemView.findViewById(R.id.contact_name);
            contact_nickname = itemView.findViewById(R.id.contact_nickname);
            contact_about = itemView.findViewById(R.id.contact_about);
        }
    }
}
