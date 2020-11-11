package pt.aulasicm.touralbum;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class Fragment_Login extends Fragment {

    public Fragment_Login() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__login, container, false);
        View register = view.findViewById(R.id.buttonLoginRegister);

        register.setOnClickListener(v ->{
            Navigation.findNavController(v).navigate(R.id.action_fragment_Login_to_fragment_Register);
        });

        return view;
    }
}