package pt.aulasicm.touralbum.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import pt.aulasicm.touralbum.R;
import pt.aulasicm.touralbum.classes.GPSTracker;

public class Fragment_Map extends Fragment {
    FloatingActionButton fabCamera;
    private GPSTracker gpsTracker;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment__map, null);

        if((ContextCompat.checkSelfPermission(getContext(),"android.permission.ACCESS_FINE_LOCATION" ) !=PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(getContext(),"android.permission.ACCESS_COARSE_LOCATION" ) !=PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{"android.permission.ACCESS_FINE_LOCATION","android.permission.ACCESS_COARSE_LOCATION"}, 1001);
        }

        //call this method to check gps enable or not
        setLocation();

        fabCamera = rootView.findViewById(R.id.cameraButton);

        fabCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_fragment_Map_to_fragment_Camera);
            }
        });

        return rootView;
    }

    public void setLocation() {
        gpsTracker = new GPSTracker(getContext());

        if (gpsTracker.canGetLocation()) {
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            //position found, show in map
            setMap(latitude, longitude);
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }
    }

    //this method is to show map
    public void setMap(final double latitude, final double longitude) {
        MapView mapView = (MapView) rootView.findViewById(R.id.map_view);
        mapView.onCreate(null);
        mapView.onResume();
        mapView.getMapAsync(
                new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googlemap) {
                        final GoogleMap map = googlemap;

                        MapsInitializer.initialize(getContext());
                        //change map type as your requirements
                        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        //user will see a blue dot in the map at his location
                        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.ACCESS_FINE_LOCATION","android.permission.ACCESS_COARSE_LOCATION"}, 1001);
                        }

                        map.setMyLocationEnabled(true);
                        LatLng marker =new LatLng(latitude, longitude);

                        //move the camera default animation
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 2));

                        //add a default marker in the position
                        //map.addMarker(new MarkerOptions().position(marker));

                    }
                }
        );
    }

}