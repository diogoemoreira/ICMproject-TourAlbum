package pt.aulasicm.touralbum.classes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pt.aulasicm.touralbum.R;

public class Gallery_Holder extends Fragment {

    public Gallery_Holder() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.gallery_holder, container, false);
    }
}