package pt.aulasicm.touralbum.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import pt.aulasicm.touralbum.R;
import pt.aulasicm.touralbum.classes.GalleryItem;
import pt.aulasicm.touralbum.classes.User;

public class Fragment_Register extends Fragment {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();

    public Fragment_Register() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment__register, container, false);

        //GetViews
        EditText emailView=view.findViewById(R.id.email_box);
        EditText usernameView=view.findViewById(R.id.username_box);
        EditText pwView= view.findViewById(R.id.pw_box);
        EditText confirmPwView=view.findViewById(R.id.confirm_pw_box);

        //REGISTER -- Onclick Listener
        Button register =view.findViewById(R.id.RegisterButton);
        register.setOnClickListener(v -> {
            String email=emailView.getText().toString();
            String username=usernameView.getText().toString();
            String pw=pwView.getText().toString();
            String pw2=confirmPwView.getText().toString();
            ValidateFields(v,email,username,pw,pw2);
        });
        return view;
    }

    public void ValidateFields(View v, String email, String username, String pw, String pw2){
        // Validate: There are no empty fields
        if(!email.equals("") && !username.equals("") && !pw.equals("") && !pw2.equals("") ){
            //Validate: password
            if(pw.equals(pw2)) {
                AuthCreateUser(v,email,username,pw);
            }
            else{
                Toast toast = Toast.makeText(v.getContext(),"The passwords do not match.", Toast.LENGTH_LONG);
                toast.show();
            }
        }
        else {
            Toast toast = Toast.makeText(v.getContext(), "Make sure there are no empty fields.", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void AuthCreateUser(View v, String email,String username, String password) {
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener((Activity) v.getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FEEDBACK SIGN IN ", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            AddUserToRealtimeDB(email,username);
                            updateUI(v);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FEEDBACK SIGN IN ", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(v.getContext(), "Authentication failed.\n Password must have atleast 6 charaters." +
                                            "\n If that's not the case then check your connection. \n In the last case, the email you are trying to use might already in use",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void AddUserToRealtimeDB(String email,String username) {
        DatabaseReference myRef = database.getReference("/users");
        User user = new User(username, md5(email));
        GalleryItem im1=new GalleryItem("LocationTest","DescriptionTest","DateTest","batataFrita");
        user.Album.add(im1);

        myRef.child(md5(email)).setValue(user, (databaseError, databaseReference) -> {
            if (databaseError != null)
                System.out.println("Data could not be saved " + databaseError.getMessage());
            else
                System.out.println("Added successfully to REALTIME DB");
        });
    }

    private void updateUI(View v) {
        Navigation.findNavController(v).navigate(R.id.action_fragment_Register_to_fragment_Login);
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