package pt.aulasicm.touralbum.classes;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;

import pt.aulasicm.touralbum.R;

public class GalleryItemAdapter   extends RecyclerView.Adapter<GalleryItemAdapter.PictureViewHolder>  {
    private ArrayList<GalleryItem> mPicsList;
    private LayoutInflater mInflater;

    public GalleryItemAdapter(Context context,
                              ArrayList<GalleryItem> mPicsList) {
        mInflater = LayoutInflater.from(context);
        this.mPicsList = mPicsList;
    }


    @NonNull
    @Override
    public PictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.gallerypic_item, parent, false);
        View desc = mItemView.findViewById(R.id.cardview);

        desc.setOnLongClickListener(arg0 -> createMenu(mItemView,desc));

        return new PictureViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryItemAdapter.PictureViewHolder holder, int position) {
        GalleryItem mCurrent = mPicsList.get(position);
        String picInfo="Location: " + mCurrent.getLocation()
                + "\nDate: " + mCurrent.getDate()
                + "\nDescription: " + mCurrent.getDescription();



        //NOTA: escrever .setImage para ver outras possibilidades no autocomplete alem de URI. Exemplo: BITMAP
        //holder.pic.setImageURI(mCurrent.getUri());
        Picasso.with(holder.itemView.getContext()).load(mCurrent.getUri()).fit().into(holder.pic);
        holder.picInfo.setText(picInfo);
        holder.storageRef.setText(mCurrent.uri.toString());
        holder.pvId.setText(mCurrent.myid);
        holder.email.setText(mCurrent.email);
    }
    @Override
    public int getItemCount() {
        return mPicsList.size();
    }







    private boolean createMenu(View mItemView, View desc) {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(mItemView.getContext(), desc);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(item -> {
            Toast.makeText(mItemView.getContext(),"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();

            switch(item.getTitle().toString()){
                case "Edit":
                    //pop up for editing description
                    final AlertDialog dialogBuilder = new AlertDialog.Builder(mItemView.getContext()).create();

                    View dialogView = mInflater.inflate(R.layout.popup_change_desc, null);

                    final EditText editText =  dialogView.findViewById(R.id.edt_comment);
                    Button buttonSubmit =  dialogView.findViewById(R.id.buttonSubmit);
                    Button buttonCancel =  dialogView.findViewById(R.id.buttonCancel);

                    buttonCancel.setOnClickListener(view -> dialogBuilder.dismiss());
                    buttonSubmit.setOnClickListener(view -> {
                        // Commit new Description
                        String newDescription=editText.getText().toString();
                        EditDescription(mItemView,newDescription);
                        dialogBuilder.dismiss();
                    });

                    dialogBuilder.setView(dialogView);
                    dialogBuilder.show();break;

                case "Delete":
                    DeletePicture(mItemView);
                    break;
                case "Download":break;
                default: Toast.makeText(mItemView.getContext(),"Oops something went wrong ", Toast.LENGTH_SHORT).show();
            }

            return true;
        });
        popup.show();//showing popup menu
        return true;
    }

    private void DeletePicture(View mItemView) {
        //GET REFS
        DatabaseReference ref;
        FirebaseStorage storage=FirebaseStorage.getInstance();


        //GET VIEWS
        TextView emailView=mItemView.findViewById(R.id.userEmail);
        String email=emailView.getText().toString();
        TextView myid=mItemView.findViewById(R.id.myId);
        String myidd=myid.getText().toString();
        TextView uriView=mItemView.findViewById(R.id.storageRef);
        String uri=uriView.getText().toString();

        //REMOVE FROM REALTIME DB
        ref = FirebaseDatabase.getInstance().getReference();
        Query applesQuery = ref.child("users").child(email).child("Album").child(myidd);

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot Snapshot: dataSnapshot.getChildren()) {
                    Snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FAILED TO DELETE PIC", "onCancelled", databaseError.toException());
            }
        });

        //REMOVE FROM STORAGE
        StorageReference photoRef = storage.getReferenceFromUrl(uri);

        photoRef.delete().addOnSuccessListener(aVoid -> {
            // File deleted successfully
            Log.d("DELETED SUCCESSFULLY", "onSuccess: deleted file");
        }).addOnFailureListener(exception -> {
            // Uh-oh, an error occurred!
            Log.d("FAILED", "onFailure: did not delete file");
        });
    }


    private void EditDescription(View mItemView, String newDescription) {
        //GET REFS
        DatabaseReference ref;

        //GET VIEWS
        TextView emailView=mItemView.findViewById(R.id.userEmail);
        String email=emailView.getText().toString();
        TextView myid=mItemView.findViewById(R.id.myId);
        String myidd=myid.getText().toString();


       ref = FirebaseDatabase.getInstance().getReference();
       ref.child("users").child(email).child("Album").child(myidd).child("description").setValue(newDescription);
    }




    //VIEW HOLDER CLASS
    class PictureViewHolder extends RecyclerView.ViewHolder {
        public final ImageView pic;
        public final TextView picInfo;
        public final TextView storageRef;
        public final TextView pvId;
        public final TextView email;
        final GalleryItemAdapter mAdapter;

        public PictureViewHolder(View itemView, GalleryItemAdapter adapter) {
            super(itemView);
            pic= itemView.findViewById(R.id.pic);
            picInfo=itemView.findViewById(R.id.picInfo);
            storageRef=itemView.findViewById(R.id.storageRef);
            pvId=itemView.findViewById(R.id.myId);
            email=itemView.findViewById(R.id.userEmail);
            this.mAdapter = adapter;
        }
    }


}

