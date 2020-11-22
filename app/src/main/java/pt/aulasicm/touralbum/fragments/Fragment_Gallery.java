package pt.aulasicm.touralbum.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import pt.aulasicm.touralbum.R;
import pt.aulasicm.touralbum.classes.GalleryItem;
import pt.aulasicm.touralbum.classes.GalleryItemAdapter;

public class Fragment_Gallery extends Fragment {
    private RecyclerView mRecyclerView;
    private GalleryItemAdapter mAdapter;
    private LinkedList<GalleryItem> mPictureList=new LinkedList<>();

    public Fragment_Gallery() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment__gallery, container, false);

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


        mPictureList=getPicturesList(v.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),mPictureList);

        mRecyclerView = v.findViewById(R.id.gallery);
        mAdapter = new GalleryItemAdapter(v.getContext(), mPictureList);
        mRecyclerView.setAdapter(mAdapter);
        // Give the RecyclerView a default layout manager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));

        return v;
    }




    //Filling the gallery with the DEVICE's pics for testing
    public static LinkedList<GalleryItem> getPicturesList(File dir, LinkedList<GalleryItem> mPictureList) {
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

    public static void loadImage(File file,LinkedList<GalleryItem> mPictureList) {
        GalleryItem newItem = new GalleryItem();
        newItem.uri = Uri.fromFile(file);
        newItem.date = getDateFromUri(newItem.uri);
        newItem.description="The world of angels";
        newItem.location="Paradise";
        mPictureList.add(newItem);
    }

}