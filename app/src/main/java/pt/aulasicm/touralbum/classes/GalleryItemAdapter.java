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
    public GalleryItemAdapter.PictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.gallerypic_item, parent, false);

        View desc = mItemView.findViewById(R.id.picInfo);

        desc.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(mItemView.getContext(), desc);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(mItemView.getContext(),"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                        switch(item.getTitle().toString()){
                            case "Edit":
                                //pop up for editing description
                                final AlertDialog dialogBuilder = new AlertDialog.Builder(mItemView.getContext()).create();

                                View dialogView = mInflater.inflate(R.layout.popup_change_desc, null);

                                final EditText editText = (EditText) dialogView.findViewById(R.id.edt_comment);
                                Button buttonSubmit = (Button) dialogView.findViewById(R.id.buttonSubmit);
                                Button buttonCancel = (Button) dialogView.findViewById(R.id.buttonCancel);

                                buttonCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialogBuilder.dismiss();
                                    }
                                });
                                buttonSubmit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // Commit new Description
                                        dialogBuilder.dismiss();
                                    }
                                });

                                dialogBuilder.setView(dialogView);
                                dialogBuilder.show();break;
                            case "Delete":break;
                            case "Download":break;
                            default: Toast.makeText(mItemView.getContext(),"Oops something went wrong ", Toast.LENGTH_SHORT).show();
                        }

                        return true;
                    }
                });
                popup.show();//showing popup menu
                return true;
            }
        });
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

    }
    @Override
    public int getItemCount() {
        return mPicsList.size();
    }

    //VIEW HOLDER CLASS
    class PictureViewHolder extends RecyclerView.ViewHolder {
        public final ImageView pic;
        public final TextView picInfo;
        final GalleryItemAdapter mAdapter;

        public PictureViewHolder(View itemView, GalleryItemAdapter adapter) {
            super(itemView);
            pic= itemView.findViewById(R.id.pic);
            picInfo=itemView.findViewById(R.id.picInfo);
            this.mAdapter = adapter;
        }
    }


}

