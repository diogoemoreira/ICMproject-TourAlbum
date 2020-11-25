package pt.aulasicm.touralbum.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
        return new PictureViewHolder(mItemView, this);
    }
    @Override
    public void onBindViewHolder(@NonNull GalleryItemAdapter.PictureViewHolder holder, int position) {
        GalleryItem mCurrent = mPicsList.get(position);
        String picInfo="Location: " + mCurrent.getLocation()
                 + "\nDate: " + mCurrent.getDate()
                 + "\nDescription: " + mCurrent.getDescription();

        //NOTA: escrever .setImage para ver outras possibilidades no autocomplete alem de URI. Exemplo: BITMAP
        System.out.println("kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
        //holder.pic.setImageURI(mCurrent.getUri());
        Picasso.with(holder.itemView.getContext()).load(mCurrent.getUri()).into(holder.pic);
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
            System.out.println("AHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
            this.mAdapter = adapter;
        }
    }
}

