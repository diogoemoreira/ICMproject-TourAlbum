package pt.aulasicm.touralbum.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


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
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import pt.aulasicm.touralbum.R;
import pt.aulasicm.touralbum.activities.MainActivity;
import pt.aulasicm.touralbum.classes.GPSTracker;
import pt.aulasicm.touralbum.classes.GalleryItem;
import pt.aulasicm.touralbum.classes.Hash;
import pt.aulasicm.touralbum.classes.User;

public class Fragment_Camera extends Fragment {
    // Firebase instances Authentication/RealtimeDB/Storage
    private FirebaseUser AuthUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference ref;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    private int REQUEST_CODE_PERMISSIONS = 101;
    private String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    TextureView textureView;
    View imageCapture;
    

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment__camera, container, false);

        textureView = (TextureView) v.findViewById(R.id.view_finder);

        imageCapture = v.findViewById(R.id.imgCapture);

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this.getActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        return v;
    }

    private void startCamera() {
        CameraX.unbindAll();

        Rational aspectRatio = new Rational(textureView.getWidth(), textureView.getHeight());
        Size screen = new Size(textureView.getWidth(), textureView.getHeight());

        PreviewConfig pConfig = new PreviewConfig.Builder().setTargetAspectRatio(aspectRatio).setTargetResolution(screen).build();
        Preview preview = new Preview(pConfig);

        preview.setOnPreviewOutputUpdateListener(
                new Preview.OnPreviewOutputUpdateListener() {
                    @Override
                    public void onUpdated(Preview.PreviewOutput output) {
                        ViewGroup parent = (ViewGroup) textureView.getParent();
                        parent.removeView(textureView);
                        parent.addView(textureView);

                        textureView.setSurfaceTexture(output.getSurfaceTexture());
                        updateTransform();
                    }
                }
        );

        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder().setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetRotation(this.getActivity().getWindowManager().getDefaultDisplay().getRotation()).build();
        final ImageCapture imgCap = new ImageCapture(imageCaptureConfig);

        imageCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "img" + System.currentTimeMillis() + ".png");
                imgCap.takePicture(file, new ImageCapture.OnImageSavedListener() {
                    @Override
                    public void onImageSaved(@NonNull File file) {

                        String msg = "Pic captured at " + file.getAbsolutePath();
                        uploadImage(Uri.fromFile(file));
                        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(@NonNull ImageCapture.UseCaseError useCaseError, @NonNull String message, @Nullable Throwable cause) {
                        String msg = "Pic capture failed " + message;
                        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

                        if (cause != null) {
                            cause.printStackTrace();
                        }
                    }
                });
            }
        });

        CameraX.bindToLifecycle(this, preview, imgCap);
    }

    private void updateTransform() {
        Matrix mx = new Matrix();
        float w = textureView.getMeasuredWidth();
        float h = textureView.getMeasuredHeight();

        float cx = w / 2f;
        float cy = h / 2f;

        int rotationDgr;
        int rotation = (int) textureView.getRotation();

        switch (rotation) {
            case Surface.ROTATION_0:
                rotationDgr = 0;
                break;
            case Surface.ROTATION_90:
                rotationDgr = 90;
                break;
            case Surface.ROTATION_180:
                rotationDgr = 180;
                break;
            case Surface.ROTATION_270:
                rotationDgr = 270;
                break;
            default:
                return;
        }

        mx.postRotate((float) rotationDgr, cx, cy);
        textureView.setTransform(mx);

    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    // UploadImage method
    private void uploadImage(Uri filePath) {
        ref = FirebaseDatabase.getInstance().getReference();
        String email = AuthUser.getEmail();
        DatabaseReference usernameRef = ref.child("users").child(Hash.md5(email));

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    String username = user.username;
                    System.out.println(filePath);
                    if (filePath != null) {
                        // Code for showing progressDialog while uploading
                        ProgressDialog progressDialog = new ProgressDialog(getContext());
                        progressDialog.setTitle("Uploading...");
                        progressDialog.show();
                        String filename = getFileName(filePath);
                        StorageReference ref = storageRef.child("images/users/" + username + "/gallery/" + filename);
                        ref.putFile(filePath).addOnSuccessListener(taskSnapshot -> {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                            try {
                                AddToRealTimeDB(user, usernameRef, filename);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }).addOnProgressListener(taskSnapshot -> {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()
                                    / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
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

    private void AddToRealTimeDB(User user, DatabaseReference myRef, String filename) throws IOException {
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        String date = simpleDateFormat.format(new Date());
        System.out.println(date);

        GalleryItem im1 = new GalleryItem(this.getAddress(), "Empty", date, filename);
        user.Album.add(im1);

        myRef.setValue(user, (databaseError, databaseReference) -> {
            if (databaseError != null)
                System.out.println("Data could not be saved " + databaseError.getMessage());
            else
                System.out.println("Added successfully to REALTIME DB");
        });
    }


    public String getFileName(Uri uri) {
        String fileName = null;
        String scheme = uri.getScheme();
        if (scheme.equals("file")) {
            fileName = uri.getLastPathSegment();
        } else if (scheme.equals("content")) {
            String[] proj = {MediaStore.Images.Media.TITLE};
            Cursor cursor = getContext().getContentResolver().query(uri, proj, null, null, null);
            if (cursor != null && cursor.getCount() != 0) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
                cursor.moveToFirst();
                fileName = cursor.getString(columnIndex);
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        System.out.println("nome do ficheiro" + fileName);
        return fileName;
    }

    public String getAddress() throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this.getContext(), Locale.getDefault());

        LocationManager lm = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double latitude=location.getLatitude();
        double longitude=location.getLongitude();

        System.out.println("lat: "+latitude+" long: "+longitude);
        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        if (addresses.size() <= 0) {
            System.out.println("AAAAAAAAAAAAAA"+addresses.toString());
            return "Not Found!";
        }

        String address = addresses.get(0).getLocality();

        return address;
    }
}