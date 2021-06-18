package com.example.tufind;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elmargomez.typer.Font;
import com.elmargomez.typer.Typer;
import com.example.tufind.Login.LoginUiActivity;
import com.example.tufind.Login.ProfileActivity;
import com.example.tufind.Map.MapPojo;
import com.example.tufind.Map.MapsActivity;
import com.example.tufind.ViewPager.AllCategory;
import com.example.tufind.ViewPager.CardsViewHolderActivity;
import com.example.tufind.ViewPager.CardsViewModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Button btnbus;
    private FusedLocationProviderClient mFusedLocationClient;
    DatabaseReference mDatabase;
    DatabaseReference mDatabase1;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;
    private MapView mShowMaps;
    GoogleMap map;
    String place="";
    private String imageUri="";
    private Uri uri;
    ImageButton hamBtn;
    private ImageButton profile;
    boolean valid = true;

    //busline
    TextView namebus, detail, line;

    //mapshow
    private ArrayList<Marker> tmpRealTimeMarker = new ArrayList<>();
    private ArrayList<Marker> realTimerMarkers = new ArrayList<>();

    ///cardview
    LinearLayoutManager mLinearLayoutManager;
    RecyclerView mRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;
    FirebaseRecyclerAdapter<CardsViewModel, CardsViewHolderActivity> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<CardsViewModel> options;
    TextView allCategory,mMap;
    CollapsingToolbarLayout collapsingToolbarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getSupportActionBar().hide();

        //****set show imageuri

        place = getIntent().getStringExtra("response");
        imageUri = getIntent().getStringExtra("imageUri");
        uri = Uri.parse(imageUri);
        Log.e("MainActivity", "place: " + place);
        Log.e("MainActivity", "uri: " + imageUri);


        //***setcolor title collapsing


        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        Typeface font = Typer.set(this).getFont(Font.ROBOTO_CONDENSED_LIGHT);
        collapsingToolbarLayout.setExpandedTitleTypeface(font);
        collapsingToolbarLayout.setCollapsedTitleTypeface(font);
        int color = getResources().getColor(R.color.white);
        collapsingToolbarLayout.setExpandedTitleColor(color);
        collapsingToolbarLayout.setCollapsedTitleTextColor(color);
//****setimage collapsing

        collapsingToolbarLayout.setTitle(getFullName(place));

        ImageView imageheader = findViewById(R.id.headerimage);

        try{
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

            imageheader.setImageBitmap(bitmap);
        } catch (IOException e){
            e.printStackTrace();
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        subirLatLongFirebase();

        mShowMaps = findViewById(R.id.mapView);
        mShowMaps.onCreate(savedInstanceState);
        mShowMaps.getMapAsync(this);

        //*******menu*****//
        hamBtn = (ImageButton) findViewById(R.id.hamBtn);
        hamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(valid){
                    if(FirebaseAuth.getInstance().getCurrentUser()==null){
                        showMenu(v);
                    }else{
                        DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()&&task!=null) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.getString("isAdmin") != null) {
                                        //user is admin
                                        showMenu1(v);
                                    }
                                    if (documentSnapshot.getString("isUser") != null) {
                                        showMenu(v);

                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
        /////*****login*****///////////
        profile= (ImageButton)findViewById(R.id.btprofile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //               startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                ///////***************////////////////
                Log.d("iscomming", String.valueOf(valid));

                if(valid){
//                    startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                    if(FirebaseAuth.getInstance().getCurrentUser()==null){
                        startActivity(new Intent(ResultActivity.this, LoginUiActivity.class));
                    }else{
                        DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()&&task!=null){
                                    DocumentSnapshot documentSnapshot =task.getResult();
                                    if(documentSnapshot.exists() && documentSnapshot != null){
                                        startActivity(new Intent(ResultActivity.this, ProfileActivity.class));
//                                startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                                    }else{
                                        startActivity(new Intent(ResultActivity.this, LoginUiActivity.class));
                                    }
                                }else{
                                    startActivity(new Intent(ResultActivity.this, LoginUiActivity.class));
                                }
                            }
                        });
                    }
                }
            }
        });

        //busline
        Busline();
        //map
        mMap = findViewById(R.id.clickMap);
        mMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ResultActivity.this, MapsActivity.class);
                i.putExtra("response",place);
                startActivity(i);
            }
        });

        //cardview
        //btn connect Allcategory
        allCategory = findViewById(R.id.allCategoryImage);
        allCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent(ResultActivity.this, AllCategory.class);
//                text = getIntent().getStringExtra("place");
                ii.putExtra("response",place);
                startActivity(ii);
            }
        });
        mRecyclerView = findViewById(R.id.recyclerView);
        showData();
    }

    ///*******busline
    private void Busline(){
        //     namebus = findViewById(R.id.textname);
        detail = findViewById(R.id.buslinedetail);
        line = findViewById(R.id.Buslinetext);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);


        DatabaseReference databaseRef=FirebaseDatabase.getInstance().getReference();

        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Bustext").child(place);

        mDatabase1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String data2 = dataSnapshot.child("busline").getValue(String.class);
                line.setText(data2);
                String data3 = dataSnapshot.child("detailbus").getValue(String.class);
                detail.setText(data3);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //cardview
    private void showData(){
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        Query query = FirebaseDatabase.getInstance().getReference().child("Nearplace").child(place);
        options = new FirebaseRecyclerOptions.Builder<CardsViewModel>().setQuery(query, CardsViewModel.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CardsViewModel, CardsViewHolderActivity>(options){

            @NonNull
            @Override
            public CardsViewHolderActivity onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_cardsview_pager,parent,false);

                CardsViewHolderActivity viewHolder = new CardsViewHolderActivity(itemView);
                viewHolder.setOnClickLickListener(new CardsViewHolderActivity.ClickListner() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(ResultActivity.this,"Click",Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onItemLongCivk(View view, int position) {
                        Toast.makeText(ResultActivity.this,"Long Click",Toast.LENGTH_SHORT);
                    }
                });
                return viewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull CardsViewHolderActivity test_viewHolder, int i, @NonNull CardsViewModel testModel) {
                test_viewHolder.setDetails(getApplicationContext(),testModel.getTitle(),testModel.getImage(),testModel.getDistance());
            }

        };
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        firebaseRecyclerAdapter.startListening();
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (firebaseRecyclerAdapter != null){
//            firebaseRecyclerAdapter.startListening();
//        }
//    }
//////////*****end cards*****//////////////


//permission locstion
private void subirLatLongFirebase() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

        ActivityCompat.requestPermissions(ResultActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        return;
    }

}

//mapview
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("map_locations").child(place);

        mDatabase.addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(Marker marker:realTimerMarkers){
                        marker.remove();
                    }
                    for (DataSnapshot snapshot1 : dataSnapshot.getChildren()){
                        MapPojo mp = snapshot1.getValue(MapPojo.class);
                        Double latitud = mp.getLatitud();
                        Double longitud = mp.getLongitud();
                        LatLng location = new LatLng(latitud,longitud);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(location);
                        tmpRealTimeMarker.add(map.addMarker(markerOptions));
                        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location,17.0f));

                    }
                    realTimerMarkers.clear();
                    realTimerMarkers.addAll(tmpRealTimeMarker);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }
    @Override
    public void onResume() {
        mShowMaps.onResume();
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mShowMaps.onPause();
    }


    private void showMenu(View v){
        PopupMenu popupMenu = new PopupMenu(ResultActivity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_userogin, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.find){
                    startActivity(new Intent(ResultActivity.this, MainActivity.class));
                }
                if(item.getItemId() == R.id.history){
                    Log.e("MainActivity", "history");
//                    itemStr="history";
//                    Intent intent1 = new Intent(ResultActivity.this, HistoryActivity.class);
//                    startActivity(intent1);
                    if(valid){
//                    startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                        if(FirebaseAuth.getInstance().getCurrentUser()==null){
                            startActivity(new Intent(ResultActivity.this, LoginUiActivity.class));
                        }else{
                            DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()&&task!=null){
                                        DocumentSnapshot documentSnapshot =task.getResult();
                                        if(documentSnapshot.exists() && documentSnapshot != null){
                                            Intent intent1 = new Intent(ResultActivity.this, HistoryActivity.class);
                                            startActivity(intent1);
                                        }else{
                                            startActivity(new Intent(ResultActivity.this, LoginUiActivity.class));
                                        }
                                    }else{
                                        startActivity(new Intent(ResultActivity.this, LoginUiActivity.class));
                                    }
                                }
                            });
                        }
                    }

                }

                return true;
            }
        });
        popupMenu.show();
    }
    //////ต้องเพิ่มเงื่อนไข
    private void showMenu1(View v){
        PopupMenu popupMenu = new PopupMenu(ResultActivity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.find){
                    startActivity(new Intent(ResultActivity.this, MainActivity.class));
                }
                if(item.getItemId() == R.id.statistic){
                    Log.e("MainActivity", "statistic");
//                    itemStr="history";
//                    Intent intent1 = new Intent(ResultActivity.this, HistoryActivity.class);
//                    startActivity(intent1);
                    if(valid){
//                    startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                        if(FirebaseAuth.getInstance().getCurrentUser()==null){
                            startActivity(new Intent(ResultActivity.this, LoginUiActivity.class));
                        }else{
                            DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()&&task!=null){
                                        DocumentSnapshot documentSnapshot =task.getResult();
                                        if(documentSnapshot.exists() && documentSnapshot != null){
                                            Intent intent1 = new Intent(ResultActivity.this, ChartActivity.class);
                                            startActivity(intent1);
                                        }else{
                                            startActivity(new Intent(ResultActivity.this, LoginUiActivity.class));
                                        }
                                    }else{
                                        startActivity(new Intent(ResultActivity.this, LoginUiActivity.class));
                                    }
                                }
                            });
                        }
                    }

                }
                if(item.getItemId() == R.id.upload){
//                    startActivity(new Intent(UploadActivity.this, UploadActivity.class));
                    if(valid){
//                    startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                        if(FirebaseAuth.getInstance().getCurrentUser()==null){
                            startActivity(new Intent(ResultActivity.this, LoginUiActivity.class));
                        }else{
                            DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()&&task!=null){
                                        DocumentSnapshot documentSnapshot =task.getResult();
                                        if(documentSnapshot.exists() && documentSnapshot != null){
                                            Intent intent1 = new Intent(ResultActivity.this, UploadActivity.class);
                                            startActivity(intent1);
                                        }else{
                                            startActivity(new Intent(ResultActivity.this, LoginUiActivity.class));
                                        }
                                    }else{
                                        startActivity(new Intent(ResultActivity.this, LoginUiActivity.class));
                                    }
                                }
                            });
                        }
                    }
                }



                return true;
            }
        });
        popupMenu.show();
    }

    public String getFullName(String place){
        String str="";
        if(place.equalsIgnoreCase("LAWS")){
            str=getResources().getString(R.string.LAWS);
        } else if(place.equalsIgnoreCase("TBS")){
            str=getResources().getString(R.string.TBS);
        } else if(place.equalsIgnoreCase("ECON")){
            str=getResources().getString(R.string.ECON);
        } else if(place.equalsIgnoreCase("SW")){
            str=getResources().getString(R.string.SW);
        } else if(place.equalsIgnoreCase("SA")){
            str=getResources().getString(R.string.SA);
        } else if(place.equalsIgnoreCase("ARTS")){
            str=getResources().getString(R.string.ARTS);
        } else if(place.equalsIgnoreCase("JC")){
            str=getResources().getString(R.string.JC);
        } else if(place.equalsIgnoreCase("TSE")){
            str=getResources().getString(R.string.TSE);
        } else if(place.equalsIgnoreCase("SIIT")){
            str=getResources().getString(R.string.SIIT);
        } else if(place.equalsIgnoreCase("TDS")){
            str=getResources().getString(R.string.TDS);
        } else if(place.equalsIgnoreCase("FINEART")){
            str=getResources().getString(R.string.FINEART);
        } else if(place.equalsIgnoreCase("KNK")){
            str=getResources().getString(R.string.KNK);
        } else if(place.equalsIgnoreCase("PYC")){
            str=getResources().getString(R.string.PYC);
        } else if(place.equalsIgnoreCase("LSED")){
            str=getResources().getString(R.string.LSED);
        } else if(place.equalsIgnoreCase("PYC2")){
            str=getResources().getString(R.string.PYC2);
        } else if(place.equalsIgnoreCase("LITU")){
            str=getResources().getString(R.string.LITU);
        } else if(place.equalsIgnoreCase("SC1")){
            str=getResources().getString(R.string.SC1);
        } else if(place.equalsIgnoreCase("SC2")){
            str=getResources().getString(R.string.SC2);
        } else if(place.equalsIgnoreCase("SC3")){
            str=getResources().getString(R.string.SC3);
        } else if(place.equalsIgnoreCase("LC1")){
            str=getResources().getString(R.string.LC1);
        } else if(place.equalsIgnoreCase("LC2")){
            str=getResources().getString(R.string.LC2);
        } else if(place.equalsIgnoreCase("LC3")){
            str=getResources().getString(R.string.LC3);
        } else if(place.equalsIgnoreCase("LC4")){
            str=getResources().getString(R.string.LC4);
        } else if(place.equalsIgnoreCase("LC5")){
            str=getResources().getString(R.string.LC5);
        }
        return str;
    }
}