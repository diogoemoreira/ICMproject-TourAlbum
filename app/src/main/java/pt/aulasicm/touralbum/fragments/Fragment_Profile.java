package pt.aulasicm.touralbum.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.util.*;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import pt.aulasicm.touralbum.R;
import pt.aulasicm.touralbum.activities.MainActivity;
import pt.aulasicm.touralbum.classes.User;
import pt.aulasicm.touralbum.classes.Hash;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Profile extends Fragment {
    // Firebase instances Authentication/RealtimeDB/Storage
    private FirebaseUser AuthUser=FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference ref;
    FirebaseStorage storage=FirebaseStorage.getInstance();
    StorageReference storageRef=storage.getReference();

    //Views
    private ImageView imageView;
    EditText userView;
    Button changePic;

    // Uri indicates, where the image will be picked from
    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;



    public Fragment_Profile() {
        // Required empty public constructor
    }


    public static Fragment_Profile newInstance() {
        Fragment_Profile fragment = new Fragment_Profile();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment__profile, container, false);

        imageView=v.findViewById(R.id.profilepic);
        changePic=v.findViewById(R.id.changePic);

        imageView.setImageResource(R.drawable.ic_launcher_foreground);
        LoadUserData(v);

        // on pressing btnSelect SelectImage() is called
        changePic.setOnClickListener(v1 -> SelectImage());
        return v;
    }

    // Select Image method
    private void SelectImage()
    {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }


    // Override onActivityResult method
    @Override
    public void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {
        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == Activity.RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            imageView.setImageURI(filePath);
            uploadImage();
        }
    }

    // UploadImage method
    private void uploadImage() {
        if (filePath != null) {
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageRef.child("images/users/"+userView.getText() + "/profilePic");

            // adding listeners on upload or failure of image
            // Progress Listener for loading
            // percentage on the dialog box
            ref.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                // Image uploaded successfully
                // Dismiss dialog
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Image Uploaded!!", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                // Error, Image not uploaded
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
            })
            .addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int)progress + "%");
            });
        }
    }

    private void LoadUserData( View v) {

        ref = FirebaseDatabase.getInstance().getReference();
        String email=AuthUser.getEmail();
        DatabaseReference usernameRef = ref.child("users").child(Hash.md5(email));

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    userView=v.findViewById(R.id.username);
                    userView.setText(user.username);

                    // Load ProfilePic from Firebase Storage
                    StorageReference ref = storageRef.child("images/users/"+userView.getText() + "/profilePic");
                    if(ref!=null) {
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            Picasso.with(getContext()).load(uri).into(imageView);
                        }).addOnFailureListener(exception -> {
                            System.out.println("Didn't find this ref.");
                        });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("CONNECTION ERROR", databaseError.getMessage());
            }
        };
        usernameRef.addListenerForSingleValueEvent(eventListener);
    }



}