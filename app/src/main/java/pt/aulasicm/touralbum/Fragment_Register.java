package pt.aulasicm.touralbum;

import android.content.Context;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class Fragment_Register extends Fragment {

    public Fragment_Register() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // GET DB INSTANCE
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment__register, container, false);

        //GetViews
        EditText emailView=(EditText) view.findViewById(R.id.email_box);
        EditText usernameView=(EditText) view.findViewById(R.id.username_box);
        EditText pwView=(EditText) view.findViewById(R.id.pw_box);
        EditText confirmPwView=(EditText) view.findViewById(R.id.confirm_pw_box);


        //REGISTER -- Onclick Listener
        Button register = (Button) view.findViewById(R.id.RegisterButton);
        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                String email=emailView.getText().toString();
                String username=usernameView.getText().toString();
                String pw=pwView.getText().toString();
                String pw2=confirmPwView.getText().toString();
                if(ValidateFields(email,username,pw,pw2)){
                    /*
                    HashMap<String,String> userInfo=new HashMap<>();
                    userInfo.put("email",email);
                    userInfo.put("username",username);
                    userInfo.put("pw",pw);
                     */
                    DatabaseReference myRef = database.getReference("/users");
                    User user = new User(username, email,pw);
                    myRef.setValue(user, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                System.out.println("Data could not be saved " + databaseError.getMessage());
                            } else {
                                System.out.println("Data saved successfully.");
                            }
                        }
                    });

                    System.out.println("------------ADICIONOU À BD-----------");

                    //Go back to login
                    Navigation.findNavController(v).navigate(R.id.action_fragment_Register_to_fragment_Login);

                }
                else{
                    Toast toast = Toast.makeText(context,"The passwords do not match.", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        return view;
    }

    public boolean ValidateFields(String email, String username, String pw, String pw2){
        if(pw.equals(pw2)){
            return true;
        }
        return false;
    }



}