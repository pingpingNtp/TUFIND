package com.example.tufind.Map;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.tufind.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    ArrayList<Marker> tmpRealTimeMarker = new ArrayList<>();
    ArrayList<Marker> realTimerMarkers = new ArrayList<>();
    String text="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        text = getIntent().getStringExtra("response");
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;




        mDatabase.child("map_locations").child(text).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (Marker marker : realTimerMarkers) {
                    marker.remove();
                }
                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                    MapPojo mp = snapshot1.getValue(MapPojo.class);
                    double latitud = mp.getLatitud();
                    double longitud = mp.getLongitud();
                    LatLng location = new LatLng(latitud, longitud);
                    MarkerOptions markerOptions = new MarkerOptions();
                    mMap.addMarker(new MarkerOptions().position(location).title("Marker in "+text));

                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(location)
                            .zoom(17).build();
                    //Zoom in and animate the camera.
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                }
               realTimerMarkers.clear();
                realTimerMarkers.addAll(tmpRealTimeMarker);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}