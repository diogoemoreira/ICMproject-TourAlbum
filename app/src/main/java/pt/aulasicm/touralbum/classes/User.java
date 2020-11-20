package pt.aulasicm.touralbum.classes;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public String pw;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email,String pw) {
        this.username = username;
        this.email = email;
        this.pw=pw;
    }
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

}