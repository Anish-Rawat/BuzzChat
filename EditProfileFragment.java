package com.example.buzzchat;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {

    private static final int PICK_PHOTO = 101;      private String downloadUrl = "";     private Uri imageUri;       private Boolean isEditProfileBtnClicked = false;
    private View editProfileScreen;
//    private String PageRequest="";
    private ImageView edit_profile_back_btn,edit_profile_calender_btn;        private AppCompatButton edit_profile_save_btn;      private CircleImageView edit_profile_image,edit_profile_btn;
    private EditText edit_profile_name,edit_profile_about,edit_profile_nickname,edit_profile_DOB,edit_profile_gender,
            edit_profile_country,edit_profile_contact_number,edit_profile_address;

    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;     private DatabaseReference rootRef,userRef;      private String currentUserId;
    private StorageReference storageRef,profileImageRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        editProfileScreen = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        FieldsInitializations();
        fetchUserInformations();
//        if (PageRequest.equals("MyProfilePage")){
//            fetchUserInformations();
//        }else if (PageRequest.equals("SignUpPage") || PageRequest.equals("")){
//
//        }

        edit_profile_save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveUserInfo();
            }
        });

        edit_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isEditProfileBtnClicked = true;

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,PICK_PHOTO);
            }
        });

        edit_profile_calender_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int date = calendar.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                edit_profile_DOB.setText(dayOfMonth+" - "+(month+1)+" - "+year);
                            }
                        },year,month,date);
                datePickerDialog.show();
            }
        });

        return editProfileScreen;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PHOTO && resultCode == RESULT_OK && data!=null ){

            loadingBar.setTitle("Upload Profile Image");
            loadingBar.setMessage("Please wait your profile image is updating....");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            imageUri = data.getData();
            edit_profile_image.setImageURI(imageUri);

            saveUserProfileImage();
        }
    }
    private void FieldsInitializations() {

        edit_profile_back_btn = editProfileScreen.findViewById(R.id.edit_profile_back_btn);
        edit_profile_calender_btn = editProfileScreen.findViewById(R.id.edit_profile_calender_btn);

        edit_profile_save_btn = editProfileScreen.findViewById(R.id.edit_profile_save_btn);

        edit_profile_image = editProfileScreen.findViewById(R.id.edit_profile_image);
        edit_profile_btn = editProfileScreen.findViewById(R.id.edit_profile_btn);

        edit_profile_name = editProfileScreen.findViewById(R.id.edit_profile_name);
        edit_profile_about = editProfileScreen.findViewById(R.id.edit_profile_about);
        edit_profile_nickname = editProfileScreen.findViewById(R.id.edit_profile_nickname);
        edit_profile_DOB = editProfileScreen.findViewById(R.id.edit_profile_DOB);
        edit_profile_gender = editProfileScreen.findViewById(R.id.edit_profile_gender);
        edit_profile_country = editProfileScreen.findViewById(R.id.edit_profile_country);
        edit_profile_contact_number = editProfileScreen.findViewById(R.id.edit_profile_contact_number);
        edit_profile_address = editProfileScreen.findViewById(R.id.edit_profile_address);

        loadingBar = new ProgressDialog(getContext());

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null){
            currentUserId = mAuth.getCurrentUser().getUid();
        }
        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Users");
        storageRef = FirebaseStorage.getInstance().getReference();
        profileImageRef = storageRef.child("Profile Image");
    }
    private void fetchUserInformations() {

        String userName,userAbout,userNickname,userDOB,userGender,userCountry,userContactNumber,userAddress;

        rootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    if (snapshot.hasChild("userProfile") && snapshot.hasChild("name")){

                        downloadUrl = snapshot.child("userProfile").getValue().toString();

                        Glide.with(getContext()).load(snapshot.child("userProfile").getValue().toString()).placeholder(R.drawable.my_profile).into(edit_profile_image);
                        edit_profile_name.setText(snapshot.child("name").getValue().toString());
                        edit_profile_about.setText(snapshot.child("about").getValue().toString());
                        edit_profile_nickname.setText(snapshot.child("nickname").getValue().toString());
                        edit_profile_DOB.setText(snapshot.child("dob").getValue().toString());
                        edit_profile_gender.setText(snapshot.child("gender").getValue().toString());
                        edit_profile_country.setText(snapshot.child("country").getValue().toString());
                        edit_profile_contact_number.setText(snapshot.child("contactNumber").getValue().toString());
                        edit_profile_address.setText(snapshot.child("address").getValue().toString());
                    }
                    else if (!snapshot.hasChild("userProfile") && snapshot.hasChild("name")){
                        edit_profile_name.setText(snapshot.child("name").getValue().toString());
                        edit_profile_about.setText(snapshot.child("about").getValue().toString());
                        edit_profile_nickname.setText(snapshot.child("nickname").getValue().toString());
                        edit_profile_DOB.setText(snapshot.child("dob").getValue().toString());
                        edit_profile_gender.setText(snapshot.child("gender").getValue().toString());
                        edit_profile_country.setText(snapshot.child("country").getValue().toString());
                        edit_profile_contact_number.setText(snapshot.child("contactNumber").getValue().toString());
                        edit_profile_address.setText(snapshot.child("address").getValue().toString());
                    }
                    else if (snapshot.hasChild("userProfile") && !snapshot.hasChild("name")){
                        Glide.with(getContext()).load(snapshot.child("userProfile").getValue().toString()).placeholder(R.drawable.my_profile).into(edit_profile_image);
                    }
                }
                else {
//                    downloadUrl = "";
                    Log.d("EditProfileFrag", "Fetch User Info onDataChange: "+downloadUrl);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void SaveUserInfo() {

        String userName = edit_profile_name.getText().toString();
        String userAbout = edit_profile_about.getText().toString();
        String userNickname = edit_profile_nickname.getText().toString();
        String userDob = edit_profile_DOB.getText().toString();
        String userGender = edit_profile_gender.getText().toString();
        String userCountry = edit_profile_country.getText().toString();
        String userContactNumber = edit_profile_contact_number.getText().toString();
        String userAddress = edit_profile_address.getText().toString();

        if (!validateDate(userDob)) {
            Toast.makeText(requireContext(), "DOB cannot be later than current date", Toast.LENGTH_SHORT).show();
        } else if (!validateContactNumber(userContactNumber)) {
            Toast.makeText(requireContext(), "Contact number should be 10 digit without special character.", Toast.LENGTH_SHORT).show();
        } else if (!validateGender(userGender)) {
            Toast.makeText(requireContext(), "Gender must be Male, Female, or Transgender", Toast.LENGTH_SHORT).show();
        }
        else if(userName.isEmpty() || userAbout.isEmpty() || userNickname.isEmpty() || userDob.isEmpty() || userGender.isEmpty() || userCountry.isEmpty() || userContactNumber.isEmpty() || userAddress.isEmpty()){
            Toast.makeText(requireContext(), "Enter required fields", Toast.LENGTH_SHORT).show();
        }
        else if (!downloadUrl.isEmpty()){
            UserInfoDataModel userInfoDataModel = new UserInfoDataModel(downloadUrl,currentUserId,userName,userNickname,userAbout,userDob,userGender,userCountry,userContactNumber,userAddress);
            userRef.child(currentUserId).setValue(userInfoDataModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(requireContext(), "UserInfo Saved", Toast.LENGTH_SHORT).show();
                        SendUserToMyProfilePage();
                    }
                    else {
                        Log.d("EditProfile", "onComplete: "+currentUserId);
                        Log.d("EditProfile", "onComplete: "+task.getException().getMessage());
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            UserInfoDataModel userInfoDataModel = new UserInfoDataModel(currentUserId,userName,userNickname,userAbout,userDob,userGender,userCountry,userContactNumber,userAddress);
            userRef.child(currentUserId).setValue(userInfoDataModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(requireContext(), "UserInfo Saved", Toast.LENGTH_SHORT).show();
                        Log.d("***********", "onComplete: ");
                        SendUserToMyProfilePage();
                    }
                    else {
                        Log.d("EditProfile", "onComplete: "+currentUserId);
                        Log.d("EditProfile", "onComplete: "+task.getException().getMessage());
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean validateDate(String dob) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd - MM -yyyy");
        dateFormat.setLenient(false);
        try {
            Date date = dateFormat.parse(dob);
            return !date.after(new Date());
        } catch (ParseException e) {
            Log.d("EditProfile", "validateDate: "+e.getMessage());
            return false;
        }
    }
    private boolean validateContactNumber(String contactNumber) {
        // Define the regex pattern
        String regex = "^[0-9]{10}$";

        return contactNumber.matches(regex);
    }
    private boolean validateGender(String gender) {
        return gender.equalsIgnoreCase("Male") || gender.equalsIgnoreCase("Female") || gender.equalsIgnoreCase("Transgender");
    }
    private void SendUserToMyProfilePage() {

        MyProfileFragment myProfileScreen = new MyProfileFragment();

        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_frame_layout,myProfileScreen).commit();
    }
    private void saveUserProfileImage() {

        StorageReference filePath = profileImageRef.child(currentUserId+".jpg");

        filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()){

                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            downloadUrl = uri.toString();
                            saveProfileImageIntoDataBase();
                        }
                    });
                }
            }
        });
    }

    private void saveProfileImageIntoDataBase() {

        userRef.child(currentUserId).child("userProfile").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    loadingBar.dismiss();
                    Log.d("EditProfileFrag", "saveProfileImageIntoDataBase: Saved");
                }
                else {
                    Log.d("EditProfileFrag", "saveProfileImageIntoDataBase: Not Saved");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}