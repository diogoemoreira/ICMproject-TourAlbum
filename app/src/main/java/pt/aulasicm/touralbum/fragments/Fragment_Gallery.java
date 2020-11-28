package pt.aulasicm.touralbum.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import pt.aulasicm.touralbum.R;
import pt.aulasicm.touralbum.classes.GalleryItem;
import pt.aulasicm.touralbum.classes.GalleryItemAdapter;
import pt.aulasicm.touralbum.classes.User;
import pt.aulasicm.touralbum.classes.Hash;

public class Fragment_Gallery extends Fragment {
    // Firebase instances Authentication/RealtimeDB/Storage
    private FirebaseUser AuthUser= FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference ref;
    FirebaseStorage storage=FirebaseStorage.getInstance();
    StorageReference storageRef=storage.getReference();

    //Recycler View
    private RecyclerView mRecyclerView;
    private GalleryItemAdapter mAdapter;
    private ArrayList<GalleryItem> mPictureList;

    public Fragment_Gallery() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment__gallery, container, false);
       // getPermissionReadStorage();

        mRecyclerView = v.findViewById(R.id.gallery);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));  // Give the RecyclerView a default layout manager.
        mRecyclerView.setHasFixedSize(true);

        mPictureList=new ArrayList<>();
        GetPicturesFromDB();

        return v;
    }




    private void GetPicturesFromDB() {
        // Get pics Uri
        ref = FirebaseDatabase.getInstance().getReference();
        String email=AuthUser.getEmail();
        DatabaseReference usernameRef = ref.child("users").child(Hash.md5(email));

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    ClearRecycler();
                    User user = dataSnapshot.getValue(User.class);
                    String username=user.username;
                    System.out.println(user.username);
                    for (GalleryItem im: user.Album){
                        // Get this pic from storage
                        StorageReference ref = storageRef.child("images/users/"+ username + "/gallery/" + im.name);

                        System.out.println(ref);
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                           im.uri=uri;
                            System.out.println(uri);
                            mPictureList.add(im);
                            mAdapter = new GalleryItemAdapter(getContext(), mPictureList);
                            mRecyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();

                        }).addOnFailureListener(exception -> {
                            System.out.println("Failed to retrieve Uri of this image");
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("CONNECTION ERROR", databaseError.getMessage());
            }
        };
        usernameRef.addListenerForSingleValueEvent(eventListener);
    }




    //Filling the gallery with the DEVICE's pics for testing
    public static ArrayList<GalleryItem> getPicturesList(File dir, ArrayList<GalleryItem> mPictureList) {
        mPictureList.clear();
        if (dir.exists()) {
            System.out.println("I FOUND THE DIR !!");
            File[] files = dir.listFiles();
            for (File file : files) {
                System.out.println("I FOUND A PIC HELL YEAHHH");
                String absolutePath = file.getAbsolutePath();
                String extension = absolutePath.substring(absolutePath.lastIndexOf("."));
                if (extension.equals(".jpg")) {
                    loadImage(file,mPictureList);
                }
            }
        }
        else{
            System.out.println("FUCK... NO DIR.. WHYYYYY!???");
        }
        if(mPictureList.size()==0)
            System.out.println("DAMNNNNNNNN YOUUUUU ESTA VAZIA ~~~~ FFS!!");
        return mPictureList;
    }

    private static String getDateFromUri(Uri uri){
        String[] split = uri.getPath().split("/");
        String fileName = split[split.length - 1];
        String fileNameNoExt = fileName.split("\\.")[0];
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = format.format(new Date(Long.parseLong(fileNameNoExt)));
        return dateString;
    }

    public static void loadImage(File file,ArrayList<GalleryItem> mPictureList) {
        GalleryItem newItem = new GalleryItem();
        newItem.uri = Uri.fromFile(file);
        newItem.date = getDateFromUri(newItem.uri);
        newItem.description="The world of angels";
        newItem.location="Paradise";
        mPictureList.add(newItem);
    }

/*
    private void getPermissionReadStorage() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
*/
    private void ClearRecycler(){
        if(mPictureList !=null){
            mPictureList.clear();
            if(mAdapter != null){
                mAdapter.notifyDataSetChanged();
            }
            mPictureList=new ArrayList<>();
        }
    }



}