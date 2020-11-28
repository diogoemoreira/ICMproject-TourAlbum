package pt.aulasicm.touralbum.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pt.aulasicm.touralbum.R;
import pt.aulasicm.touralbum.activities.MainActivity;


public class Fragment_Login extends Fragment {
    private FirebaseAuth mAuth;

    public Fragment_Login() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null)
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser,View v) {
        Intent intent = new Intent(v.getContext(), MainActivity.class);
        System.out.println(currentUser);
        intent.putExtra("user", currentUser);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__login, container, false);
        View register = view.findViewById(R.id.buttonLoginRegister);
        View login= view.findViewById(R.id.login_btn);
        Context context = view.getContext();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(v ->{
            Navigation.findNavController(v).navigate(R.id.action_fragment_Login_to_fragment_Register);
        });

        login.setOnClickListener(v-> {
            EditText emailView=(EditText) view.findViewById(R.id.login_email);
            EditText pwView=(EditText) view.findViewById(R.id.login_pw);

            //TODO: APAGAR ISTO QUE È APENAS PARA NAO TER TRABALHO A ESCREVER SEMPRE O MAIL
            //emailView.setText("fabio@gmail.com");
            //pwView.setText("a123456");
            emailView.setText("diogo123@mail.com");
            pwView.setText("123456");
            //TODO APAGAR ISTO QUE È APENAS PARA NAO TER TRABALHO A ESCREVER SEMPRE O MAIL

            String email=emailView.getText().toString();
            String pw=pwView.getText().toString();
            if(email!=null && pw!=null  && !email.equals("") & !pw.equals("")){
                ValidateCredentials(view,email,pw);
            }

        });
        return view;
    }

    private  void ValidateCredentials(View v, String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) v.getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LOGIN FEEDBACK", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user,v); // Should go directly to the map if he is already logged in.
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LOGIN FEEDBACK", "signInWithEmail:failure", task.getException());
                            Toast.makeText(v.getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}