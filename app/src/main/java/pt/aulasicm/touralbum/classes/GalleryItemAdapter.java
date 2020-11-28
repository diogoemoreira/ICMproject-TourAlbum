package pt.aulasicm.touralbum.classes;

import android.app.AlertDialog;
import android.content.Context;
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

        desc.setOnLongClickListener((View.OnLongClickListener) arg0 -> createMenu(mItemView,desc));

        return new PictureViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryItemAdapter.PictureViewHolder holder, int position) {
        GalleryItem mCurrent = mPicsList.get(position);
        String picInfo="Location: " + mCurrent.getLocation()
                + "\nDate: " + mCurrent.getDate()
                + "\nDescription: " + mCurrent.getDescription();
        String storageRef=mCurrent.getStref();
        String id=mCurrent.getMyid();

        //NOTA: escrever .setImage para ver outras possibilidades no autocomplete alem de URI. Exemplo: BITMAP
        //holder.pic.setImageURI(mCurrent.getUri());
        Picasso.with(holder.itemView.getContext()).load(mCurrent.getUri()).fit().into(holder.pic);
        holder.picInfo.setText(picInfo);
        holder.storageRef.setText(mCurrent.stref);
        holder.pvId.setText(mCurrent.myid);
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
        popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) item -> {
            Toast.makeText(mItemView.getContext(),"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();

            switch(item.getTitle().toString()){
                case "Edit":
                    //pop up for editing description
                    final AlertDialog dialogBuilder = new AlertDialog.Builder(mItemView.getContext()).create();

                    View dialogView = mInflater.inflate(R.layout.popup_change_desc, null);

                    final EditText editText = (EditText) dialogView.findViewById(R.id.edt_comment);
                    Button buttonSubmit = (Button) dialogView.findViewById(R.id.buttonSubmit);
                    Button buttonCancel = (Button) dialogView.findViewById(R.id.buttonCancel);

                    buttonCancel.setOnClickListener(view -> dialogBuilder.dismiss());
                    buttonSubmit.setOnClickListener(view -> {
                        // Commit new Description
                        EditDescription();
                        dialogBuilder.dismiss();
                    });

                    dialogBuilder.setView(dialogView);
                    dialogBuilder.show();break;

                case "Delete":break;
                case "Download":break;
                default: Toast.makeText(mItemView.getContext(),"Oops something went wrong ", Toast.LENGTH_SHORT).show();
            }

            return true;
        });
        popup.show();//showing popup menu
        return true;
    }


    private void EditDescription() {
    }




    //VIEW HOLDER CLASS
    class PictureViewHolder extends RecyclerView.ViewHolder {
        public final ImageView pic;
        public final TextView picInfo;
        public final TextView storageRef;
        public final TextView pvId;
        final GalleryItemAdapter mAdapter;

        public PictureViewHolder(View itemView, GalleryItemAdapter adapter) {
            super(itemView);
            pic= itemView.findViewById(R.id.pic);
            picInfo=itemView.findViewById(R.id.picInfo);
            storageRef=itemView.findViewById(R.id.storageRef);
            pvId=itemView.findViewById(R.id.myId);
            this.mAdapter = adapter;
        }
    }


}

