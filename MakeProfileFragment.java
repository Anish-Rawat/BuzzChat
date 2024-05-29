package com.example.buzzchat;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MakeProfileFragment extends Fragment {
    private static final int PICK_PHOTO = 101;      private String downloadUrl = "";     private Uri imageUri;       private Boolean isEditProfileBtnClicked = false;
    private View makeProfileScreen;         private String PageRequest="";
    private ImageView make_profile_back_btn,make_profile_calender_btn;        private AppCompatButton make_profile_save_btn;      private CircleImageView make_profile_image,make_profile_btn;
    private EditText make_profile_name,make_profile_about,make_profile_nickname,make_profile_DOB,make_profile_gender,
            make_profile_country,make_profile_contact_number,make_profile_address;

    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;     private DatabaseReference rootRef,userRef;      private String currentUserId;
    private StorageReference storageRef,profileImageRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        makeProfileScreen = inflater.inflate(R.layout.fragment_make_profile, container, false);
        FieldsInitializations();

        make_profile_save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveUserInfo();
            }
        });

        make_profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isEditProfileBtnClicked = true;

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,PICK_PHOTO);
            }
        });

        make_profile_calender_btn.setOnClickListener(new View.OnClickListener() {
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
                                make_profile_DOB.setText(dayOfMonth+" - "+(month+1)+" - "+year);
                            }
                        },year,month,date);
                datePickerDialog.show();
            }
        });


        return makeProfileScreen;
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
            make_profile_image.setImageURI(imageUri);

            saveUserProfileImage();
        }
    }
    private void FieldsInitializations() {

        make_profile_back_btn = makeProfileScreen.findViewById(R.id.make_profile_back_btn);
        make_profile_calender_btn = makeProfileScreen.findViewById(R.id.make_profile_calender_btn);

        make_profile_save_btn = makeProfileScreen.findViewById(R.id.make_profile_save_btn);

        make_profile_image = makeProfileScreen.findViewById(R.id.make_profile_image);
        make_profile_btn = makeProfileScreen.findViewById(R.id.make_profile_btn);

        make_profile_name = makeProfileScreen.findViewById(R.id.make_profile_name);
        make_profile_about = makeProfileScreen.findViewById(R.id.make_profile_about);
        make_profile_nickname = makeProfileScreen.findViewById(R.id.make_profile_nickname);
        make_profile_DOB = makeProfileScreen.findViewById(R.id.make_profile_DOB);
        make_profile_gender = makeProfileScreen.findViewById(R.id.make_profile_gender);
        make_profile_country = makeProfileScreen.findViewById(R.id.make_profile_country);
        make_profile_contact_number = makeProfileScreen.findViewById(R.id.make_profile_contact_number);
        make_profile_address = makeProfileScreen.findViewById(R.id.make_profile_address);

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
    private void SaveUserInfo() {

        String userName = make_profile_name.getText().toString();
        String userAbout = make_profile_about.getText().toString();
        String userNickname = make_profile_nickname.getText().toString();
        String userDob = make_profile_DOB.getText().toString();
        String userGender = make_profile_gender.getText().toString();
        String userCountry = make_profile_country.getText().toString();
        String userContactNumber = make_profile_contact_number.getText().toString();
        String userAddress = make_profile_address.getText().toString();

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
                        SendUserToHomePage();
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
                        SendUserToHomePage();
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
    private void SendUserToHomePage() {
//        MainFragment mainFragment = new MainFragment();
//        ((MainActivity) requireActivity()).replaceFragment(mainFragment,0);

        Intent intent = new Intent(getActivity(),DashboardActivity.class);
        startActivity(intent);
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