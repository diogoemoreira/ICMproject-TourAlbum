package pt.aulasicm.touralbum.classes;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class User {

    public String username;
    public String email;
    public ArrayList<GalleryItem> Album = new ArrayList<GalleryItem>();

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

}