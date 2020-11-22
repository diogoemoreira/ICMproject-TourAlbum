package pt.aulasicm.touralbum.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import pt.aulasicm.touralbum.R;
import pt.aulasicm.touralbum.classes.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Profile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Profile extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference ref;

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
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment__profile, container, false);
        LoadUserData(v);
        return v;
    }

    private void LoadUserData( View v) {
        ref = FirebaseDatabase.getInstance().getReference();
        String email=mAuth.getInstance().getCurrentUser().getEmail();
        DatabaseReference userNameRef = ref.child("users").child(md5(email));

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    EditText userView=v.findViewById(R.id.username);
                    userView.setText(user.username);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("CONNECTION ERROR", databaseError.getMessage());
            }
        };
        userNameRef.addListenerForSingleValueEvent(eventListener);
    }


    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}