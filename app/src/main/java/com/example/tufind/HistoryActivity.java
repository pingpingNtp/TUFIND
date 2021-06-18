package com.example.tufind;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.tufind.Login.LoginUiActivity;
import com.example.tufind.Login.ProfileActivity;
import com.example.tufind.ViewPager.AllCategory;
import com.example.tufind.ViewPager.CardsDetailsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Iterator;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private ArrayList<historyTable> list;
    private ImageButton hamBtn,profile;
    boolean valid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().hide();

        list = new ArrayList<>();
        recyclerView = findViewById(R.id.userList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(list, this);
        recyclerView.setAdapter(adapter);

        hamBtn = (ImageButton) findViewById(R.id.hamBtn);
        hamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("result_finding");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> snapshotIterable = snapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterable.iterator();
                int count=0;
                while (iterator.hasNext()) {
                    DataSnapshot next = (DataSnapshot) iterator.next();
                    String str1 = next.child("user").getValue().toString();
                    String str2 = next.child("place").getValue().toString();
                    String str3 = next.child("date").getValue().toString();
                    Log.e("MainActivity", "count=" +count+ " str1 " +str2 +" " +str3);
//                    historyTable htr = new historyTable(str1,str2,str3);
//                    list.add(htr);
                    if(str1.equalsIgnoreCase("user1")){
                        historyTable htr = new historyTable(str1,str2,str3);
                        list.add(htr);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

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
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
        ArrayList<historyTable> list;
        Context context;
        public MyAdapter(ArrayList<historyTable> list, Context context){
            this.list = list;
            this.context = context;
        }


        @NonNull
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.item, parent, false);
            MyAdapter.ViewHolder viewHolder = new MyAdapter.ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {

//        holder.place_txt.setText("miyawaki");
//        holder.date_txt.setText("sakura");
            historyTable htr = list.get(position);
            holder.place_txt.setText(htr.getPlace());
            holder.date_txt.setText(htr.getDate());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView place_txt, date_txt;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                place_txt = itemView.findViewById(R.id.place_txt);
                date_txt = itemView.findViewById(R.id.date_txt);
            }
        }
    }

    private void loginProfile(){
        if(valid){
//                    startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
            if(FirebaseAuth.getInstance().getCurrentUser()==null){
                startActivity(new Intent(HistoryActivity.this, LoginUiActivity.class));
            }else{
                DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()&&task!=null){
                            DocumentSnapshot documentSnapshot =task.getResult();
                            if(documentSnapshot.exists() && documentSnapshot != null){
                                startActivity(new Intent(HistoryActivity.this, ProfileActivity.class));
//                                startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                            }else{
                                startActivity(new Intent(HistoryActivity.this, LoginUiActivity.class));
                            }
                        }else{
                            startActivity(new Intent(HistoryActivity.this, LoginUiActivity.class));
                        }
                    }
                });
            }
        }
    }

    private void showMenu(View v){
        PopupMenu popupMenu = new PopupMenu(HistoryActivity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu_userogin, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.find){
                    startActivity(new Intent(HistoryActivity.this, MainActivity.class));
                }
                if(item.getItemId() == R.id.history){
                    Log.e("MainActivity", "history");
//                    itemStr="history";
                    Intent intent1 = new Intent(HistoryActivity.this, HistoryActivity.class);
                    startActivity(intent1);
                }
                return true;
            }
        });
        popupMenu.show();
    }

    class historyTable {
        String user;
        String place;
        String date;
        public historyTable(String user, String place, String date){
            this.user = user;
            this.place = place;
            this.date = date;
        }
        public historyTable(){

        }
        public void setUser(String user) {
            this.user = user;
        }
        public void setPlace(String place) {
            this.place = place;
        }
        public void setDate(String date) {
            this.date = date;
        }
        public String getUser() {
            return user;
        }
        public String getPlace() {
            String newPLace="";
            if(place.equalsIgnoreCase("LAWS")){
                newPLace=getResources().getString(R.string.LAWS);
            } else if(place.equalsIgnoreCase("TBS")){
                newPLace=getResources().getString(R.string.TBS);
            } else if(place.equalsIgnoreCase("ECON")){
                newPLace=getResources().getString(R.string.ECON);
            } else if(place.equalsIgnoreCase("SW")){
                newPLace=getResources().getString(R.string.SW);
            } else if(place.equalsIgnoreCase("SA")){
                newPLace=getResources().getString(R.string.SA);
            } else if(place.equalsIgnoreCase("ARTS")){
                newPLace=getResources().getString(R.string.ARTS);
            } else if(place.equalsIgnoreCase("JC")){
                newPLace=getResources().getString(R.string.JC);
            } else if(place.equalsIgnoreCase("TSE")){
                newPLace=getResources().getString(R.string.TSE);
            } else if(place.equalsIgnoreCase("SIIT")){
                newPLace=getResources().getString(R.string.SIIT);
            } else if(place.equalsIgnoreCase("TDS")){
                newPLace=getResources().getString(R.string.TDS);
            } else if(place.equalsIgnoreCase("FINEART")){
                newPLace=getResources().getString(R.string.FINEART);
            } else if(place.equalsIgnoreCase("KNK")){
                newPLace=getResources().getString(R.string.KNK);
            } else if(place.equalsIgnoreCase("PYC")){
                newPLace=getResources().getString(R.string.PYC);
            } else if(place.equalsIgnoreCase("LSED")){
                newPLace=getResources().getString(R.string.LSED);
            } else if(place.equalsIgnoreCase("PYC2")){
                newPLace=getResources().getString(R.string.PYC2);
            } else if(place.equalsIgnoreCase("LITU")){
                newPLace=getResources().getString(R.string.LITU);
            } else if(place.equalsIgnoreCase("SC1")){
                newPLace=getResources().getString(R.string.SC1);
            } else if(place.equalsIgnoreCase("SC2")){
                newPLace=getResources().getString(R.string.SC2);
            } else if(place.equalsIgnoreCase("SC3")){
                newPLace=getResources().getString(R.string.SC3);
            } else if(place.equalsIgnoreCase("LC1")){
                newPLace=getResources().getString(R.string.LC1);
            } else if(place.equalsIgnoreCase("LC2")){
                newPLace=getResources().getString(R.string.LC2);
            } else if(place.equalsIgnoreCase("LC3")){
                newPLace=getResources().getString(R.string.LC3);
            } else if(place.equalsIgnoreCase("LC4")){
                newPLace=getResources().getString(R.string.LC4);
            } else if(place.equalsIgnoreCase("LC5")){
                newPLace=getResources().getString(R.string.LC5);
            }
            return newPLace;
        }
        public String getDate() {
            return date;
        }
    }

}