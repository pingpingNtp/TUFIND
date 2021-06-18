package com.example.tufind.ViewPager;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tufind.ChartActivity;
import com.example.tufind.HistoryActivity;
import com.example.tufind.Login.LoginUiActivity;
import com.example.tufind.Login.ProfileActivity;
import com.example.tufind.MainActivity;
import com.example.tufind.R;
import com.example.tufind.ResultActivity;
import com.example.tufind.UploadActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

//AllCardsMainActivity
public class AllCategory extends AppCompatActivity {

    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mRecyclerView;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerAdapter<CardsViewModel, AllCategoryAdapter> firebaseRecyclerAdapter;
    private FirebaseRecyclerOptions<CardsViewModel> options;
    private String text="";
    private ImageButton hamBtn,profile;
    private boolean valid = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_category);
        getSupportActionBar().hide();

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

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);


        mRecyclerView = findViewById(R.id.all_category);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        showData();

        ///login
        profile= (ImageButton)findViewById(R.id.btprofile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ///////***************////////////////
                loginProfile();
            }
        });
    }

    private void loginProfile(){
        if(valid){
            if(FirebaseAuth.getInstance().getCurrentUser()==null){
                startActivity(new Intent(AllCategory.this, LoginUiActivity.class));
            }else{
                DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()&&task!=null){
                            DocumentSnapshot documentSnapshot =task.getResult();
                            if(documentSnapshot.exists() && documentSnapshot != null){
                                startActivity(new Intent(AllCategory.this, ProfileActivity.class));
                            }else{
                                startActivity(new Intent(AllCategory.this, LoginUiActivity.class));
                            }
                        }else{
                            startActivity(new Intent(AllCategory.this, LoginUiActivity.class));
                        }
                    }
                });
            }
        }
    }
    private void showData(){


        text = getIntent().getStringExtra("response");
        Query query = FirebaseDatabase.getInstance().getReference().child("Nearplace").child(text);
        options = new FirebaseRecyclerOptions.Builder<CardsViewModel>().setQuery(query, CardsViewModel.class).build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CardsViewModel, AllCategoryAdapter>(options){


            @Override
            protected void onBindViewHolder(@NonNull AllCategoryAdapter allCategoryAdapter, int i, @NonNull final CardsViewModel testModel) {
                allCategoryAdapter.setDetails(getApplicationContext(),testModel.getImage(),testModel.getTitle(),testModel.getDistance());


                allCategoryAdapter.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(),CardsDetailsActivity.class);
                        i.putExtra("name",testModel.getTitle());
                        i.putExtra("image",testModel.getImage());
                        i.putExtra("detail",testModel.getDetail());
                        i.putExtra("distance",testModel.getDistance());
                        i.putExtra("place",text);
                        startActivity(i);
                    }
                });
            }

            @NonNull
            @Override
            public AllCategoryAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_cardsview_pager,parent,false);
                itemView.getLayoutParams().height = 756;
                itemView.getLayoutParams().width =564;
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
                params.leftMargin = 14;


                AllCategoryAdapter viewHolder = new AllCategoryAdapter(itemView);
                viewHolder.setOnClickLickListener(new AllCategoryAdapter.ClickListner() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(AllCategory.this,"hello",Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onItemLongCivk(View view, int position) {
                        Toast.makeText(AllCategory.this,"Long Click",Toast.LENGTH_SHORT);
                    }
                });
                return viewHolder;
            }

        };
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), true));
        firebaseRecyclerAdapter.startListening();
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }


    // now we need some item decoration class for manage spacing

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    //////
    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapter != null){
            firebaseRecyclerAdapter.startListening();
        }
    }

    private void showMenu(View v){
        PopupMenu popupMenu = new PopupMenu(AllCategory.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_userogin, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.find){
                    startActivity(new Intent(AllCategory.this, MainActivity.class));
                }
                if(item.getItemId() == R.id.history){
                    Log.e("MainActivity", "history");

                    if(valid){
                        if(FirebaseAuth.getInstance().getCurrentUser()==null){
                            startActivity(new Intent(AllCategory.this, LoginUiActivity.class));
                        }else{
                            DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()&&task!=null){
                                        DocumentSnapshot documentSnapshot =task.getResult();
                                        if(documentSnapshot.exists() && documentSnapshot != null){
                                            Intent intent1 = new Intent(AllCategory.this, HistoryActivity.class);
                                            startActivity(intent1);
                                        }else{
                                            startActivity(new Intent(AllCategory.this, LoginUiActivity.class));
                                        }
                                    }else{
                                        startActivity(new Intent(AllCategory.this, LoginUiActivity.class));
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
        PopupMenu popupMenu = new PopupMenu(AllCategory.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.find){
                    startActivity(new Intent(AllCategory.this, MainActivity.class));
                }
                if(item.getItemId() == R.id.statistic){
                    Log.e("MainActivity", "statistic");

                    if(valid){
//                    startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                        if(FirebaseAuth.getInstance().getCurrentUser()==null){
                            startActivity(new Intent(AllCategory.this, LoginUiActivity.class));
                        }else{
                            DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()&&task!=null){
                                        DocumentSnapshot documentSnapshot =task.getResult();
                                        if(documentSnapshot.exists() && documentSnapshot != null){
                                            Intent intent1 = new Intent(AllCategory.this, ChartActivity.class);
                                            startActivity(intent1);
                                        }else{
                                            startActivity(new Intent(AllCategory.this, LoginUiActivity.class));
                                        }
                                    }else{
                                        startActivity(new Intent(AllCategory.this, LoginUiActivity.class));
                                    }
                                }
                            });
                        }
                    }

                }
                if(item.getItemId() == R.id.upload){

                    if(valid){

                        if(FirebaseAuth.getInstance().getCurrentUser()==null){
                            startActivity(new Intent(AllCategory.this, LoginUiActivity.class));
                        }else{
                            DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()&&task!=null){
                                        DocumentSnapshot documentSnapshot =task.getResult();
                                        if(documentSnapshot.exists() && documentSnapshot != null){
                                            Intent intent1 = new Intent(AllCategory.this, UploadActivity.class);
                                            startActivity(intent1);
                                        }else{
                                            startActivity(new Intent(AllCategory.this, LoginUiActivity.class));
                                        }
                                    }else{
                                        startActivity(new Intent(AllCategory.this, LoginUiActivity.class));
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
}