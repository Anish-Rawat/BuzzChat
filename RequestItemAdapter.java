package com.example.buzzchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestItemAdapter extends RecyclerView.Adapter<RequestItemAdapter.RequestItemViewHolder> {

    private ArrayList<ContactInfoDataModel> requestList =  new ArrayList<>();
    private Context context;


    private ButtonClickListener buttonClickListener;

    public interface ButtonClickListener{
        void acceptButtonClick(String reqSenderId);
        void cancelButtonClick(String reqSenderId);
    }

    public RequestItemAdapter(ArrayList<ContactInfoDataModel> requestList, Context context, ButtonClickListener callBack) {
        this.requestList = requestList;
        this.context = context;
        this.buttonClickListener = callBack;
    }
    @NonNull
    @Override
    public RequestItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item_card,parent,false);
        RequestItemViewHolder holder = new RequestItemViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RequestItemViewHolder holder, int position) {

        ContactInfoDataModel obj = requestList.get(position);

        holder.contact_name.setText(obj.getName());
        holder.contact_about.setText(obj.getAbout());
        holder.contact_nickname.setText(obj.getNickname());
        Glide.with(context).load(obj.getUserProfile()).placeholder(R.drawable.my_profile).into(holder.contact_image);

        holder.contact_item_accept_btn.setVisibility(View.VISIBLE);                     holder.contact_item_cancel_btn.setVisibility(View.VISIBLE);

        holder.contact_item_accept_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (buttonClickListener!=null){
                    buttonClickListener.acceptButtonClick(obj.getUserId());
                }
            }
        });

        holder.contact_item_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (buttonClickListener!=null){
                    buttonClickListener.cancelButtonClick(obj.getUserId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    class RequestItemViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView contact_image;
        private TextView contact_name,contact_nickname,contact_about;       private AppCompatButton contact_item_accept_btn,contact_item_cancel_btn;
        public RequestItemViewHolder(@NonNull View itemView) {
            super(itemView);

            contact_image = itemView.findViewById(R.id.contact_image);
            contact_name = itemView.findViewById(R.id.contact_name);
            contact_nickname = itemView.findViewById(R.id.contact_nickname);
            contact_about = itemView.findViewById(R.id.contact_about);

            contact_item_accept_btn = itemView.findViewById(R.id.contact_item_accept_btn);
            contact_item_cancel_btn = itemView.findViewById(R.id.contact_item_cancel_btn);
        }
    }
}
