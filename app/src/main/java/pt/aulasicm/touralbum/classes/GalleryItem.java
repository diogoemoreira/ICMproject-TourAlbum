package pt.aulasicm.touralbum.classes;

import android.net.Uri;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class GalleryItem {

    public Uri uri;
    public String name;
    public String location;
    public String description;
    public String date;
    public String myid;
    public String stref;

    public GalleryItem() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public GalleryItem(String location, String description, String date, String name, String myid, String stref) {
        this.location=location;
        this.description=description;
        this.date=date;
        this.name=name;
        this.myid=myid;
        this.stref=stref;
    }

    //GETTERS
    public Uri getUri() { return uri; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getMyid() {return myid; }
    public String getStref() {return stref; }
}