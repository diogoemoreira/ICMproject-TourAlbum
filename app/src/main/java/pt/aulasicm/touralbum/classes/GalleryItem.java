package pt.aulasicm.touralbum.classes;

import android.net.Uri;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class GalleryItem {

    public Uri uri;
    public String location;
    public String description;
    public String date;

    public GalleryItem() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public GalleryItem(String location,String description,String date) {
        this.location=location;
        this.description=description;
        this.date=date;
    }

    //GETTERS
    public Uri getUri() { return uri; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
}