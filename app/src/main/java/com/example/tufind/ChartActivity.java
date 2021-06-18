package com.example.tufind;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.example.tufind.Login.LoginUiActivity;
import com.example.tufind.Login.ProfileActivity;
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

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ChartActivity extends AppCompatActivity {

    private ArrayList<building> list = new ArrayList<>();
    private AnyChartView anyChartView;
    private List<DataEntry> data;
    private int year=0;
    private ImageButton hamBtn;
    boolean valid = true;
    private ImageButton profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        getSupportActionBar().hide();

        hamBtn = (ImageButton) findViewById(R.id.hamBtn);
        hamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });

        list.add(new building("LAWS",0)); list.add(new building("TBS",0)); list.add(new building("ECON",0)); list.add(new building("SW",0));
        list.add(new building("SA",0)); list.add(new building("ARTS",0)); list.add(new building("JC",0)); list.add(new building("TSE",0));
        list.add(new building("SIIT",0)); list.add(new building("TDS",0)); list.add(new building("FINEART",0)); list.add(new building("KNK",0));
        list.add(new building("PYC",0)); list.add(new building("LSED",0)); list.add(new building("PYC2",0)); list.add(new building("LITU",0));
        list.add(new building("SC1",0)); list.add(new building("SC2",0)); list.add(new building("SC3",0)); list.add(new building("LC1",0));
        list.add(new building("LC2",0)); list.add(new building("LC3",0)); list.add(new building("LC4",0)); list.add(new building("LC5",0));

        anyChartView = findViewById(R.id.any_chart_view);
        final Cartesian cartesian = AnyChart.column();
        data = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("result_finding");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> snapshotIterator = snapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                while (iterator.hasNext()) {
                    DataSnapshot next = (DataSnapshot) iterator.next();
                    String place = next.child("place").getValue().toString();
                    String date = next.child("date").getValue().toString();
//                    Log.e("MainActivity", "place=" +place+ " date=" + date);

                    try {
                        SimpleDateFormat mm = new SimpleDateFormat("MMMM", Locale.ENGLISH); //month
                        SimpleDateFormat yy = new SimpleDateFormat("yyyy", Locale.ENGLISH); //year
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        LocalDate myObj = LocalDate.now(); //today's date
                        Date dd = sdf.parse(String.valueOf(myObj)); //date from today
                        String month_now = mm.format(dd);
                        String year_now = yy.format(dd);
                        Date dddd = sdf.parse(date); //date from database
                        String month = mm.format(dddd);
                        String year = yy.format(dddd);
//                        Log.e("MainActivity", "now :" + month_now + " " + year_now);
//                        Log.e("MainActivity", "db :" + month + " " + year);
                        if(month.equalsIgnoreCase(month_now) &&  year.equalsIgnoreCase(year_now)){
                            calCount(place);
                        }
                    }
                    catch(Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        final Button showBtn = (Button) findViewById(R.id.showBtn);
        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0; i<list.size(); i++) {
                    for(int j = i+1; j<list.size(); j++) {
                        if(list.get(i).getCount() < list.get(j).getCount()) {
                            building tmp = list.get(i);
                            list.set(i,list.get(j));
                            list.set(j,tmp);
                        }
                    }
                }
                for(int i=0; i<10; i++){
                    data.add(new ValueDataEntry(list.get(i).getPlace(), list.get(i).getCount()));
                }
                Column column = cartesian.column(data);
                column.tooltip()
                        .titleFormat("{%X}")
                        .position(Position.CENTER_BOTTOM)
                        .anchor(Anchor.CENTER_BOTTOM)
                        .offsetX(0d)
                        .offsetY(5d)
                        .format("{%Value}{groupsSeparator: }");

                cartesian.animation(true);
                cartesian.yScale().minimum(0d);
                cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");
                cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                cartesian.interactivity().hoverMode(HoverMode.BY_X);
                cartesian.xAxis(0).title("");
                cartesian.yAxis(0).title("Finding Numbers");
                anyChartView.setChart(cartesian);
            }
        });

        final TextView showMoreBtn = (TextView) findViewById(R.id.showMoreBtn);
        showMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder data = new StringBuilder();
                data.append(String.format("%s,%s", getResources().getString(R.string.building), getResources().getString(R.string.total)));
                for(int i=0; i<list.size(); i++){
                    String line = String.format("\n%s,%s", getFullName(list.get(i).getPlace()), String.valueOf(list.get(i).getCount()));
                    data.append(line);
                }
                try{
                    //saving the file into device
                    FileOutputStream out = openFileOutput("summary.csv", Context.MODE_PRIVATE);
                    out.write((data.toString()).getBytes());
                    out.close();

                    //exporting
                    Context context = getApplicationContext();
                    File fileLocation = new File(getFilesDir(), "summary.csv");
                    Uri path = FileProvider.getUriForFile(context, "com.example.exportcsv.fileprovider", fileLocation);
                    Intent fileIntent = new Intent(Intent.ACTION_SEND);
                    fileIntent.setType("text/csv");
                    fileIntent.putExtra(Intent.EXTRA_SUBJECT, "summary");
                    fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    fileIntent.putExtra(Intent.EXTRA_STREAM, path);
                    startActivity(Intent.createChooser(fileIntent, "Send mail"));
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        /////*****login*****///////////
        profile = (ImageButton)findViewById(R.id.btprofile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //               startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                ///////***************////////////////
                Log.d("iscomming", String.valueOf(valid));

                if(valid){
//                    startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                    if(FirebaseAuth.getInstance().getCurrentUser()==null){
                        startActivity(new Intent(ChartActivity.this, LoginUiActivity.class));
                    }else{
                        DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()&&task!=null){
                                    DocumentSnapshot documentSnapshot =task.getResult();
                                    if(documentSnapshot.exists() && documentSnapshot != null){
                                        startActivity(new Intent(ChartActivity.this, ProfileActivity.class));
//                                startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                                    }else{
                                        startActivity(new Intent(ChartActivity.this, LoginUiActivity.class));
                                    }
                                }else{
                                    startActivity(new Intent(ChartActivity.this, LoginUiActivity.class));
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    //////ต้องเพิ่มเงื่อนไข
    private void showMenu(View v){
        PopupMenu popupMenu = new PopupMenu(ChartActivity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.find){
                    startActivity(new Intent(ChartActivity.this, MainActivity.class));
                }
                if(item.getItemId() == R.id.statistic){
                    Log.e("MainActivity", "statistic");
//                    itemStr="history";
//                    Intent intent1 = new Intent(ResultActivity.this, HistoryActivity.class);
//                    startActivity(intent1);
                    if(valid){
//                    startActivity(new Intent(MainActivity.this, LoginUiActivity.class));
                        if(FirebaseAuth.getInstance().getCurrentUser()==null){
                            startActivity(new Intent(ChartActivity.this, LoginUiActivity.class));
                        }else{
                            DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()&&task!=null){
                                        DocumentSnapshot documentSnapshot =task.getResult();
                                        if(documentSnapshot.exists() && documentSnapshot != null){
                                            Intent intent1 = new Intent(ChartActivity.this, ChartActivity.class);
                                            startActivity(intent1);
                                        }else{
                                            startActivity(new Intent(ChartActivity.this, LoginUiActivity.class));
                                        }
                                    }else{
                                        startActivity(new Intent(ChartActivity.this, LoginUiActivity.class));
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
                            startActivity(new Intent(ChartActivity.this, LoginUiActivity.class));
                        }else{
                            DocumentReference df = FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()&&task!=null){
                                        DocumentSnapshot documentSnapshot =task.getResult();
                                        if(documentSnapshot.exists() && documentSnapshot != null){
                                            Intent intent1 = new Intent(ChartActivity.this, UploadActivity.class);
                                            startActivity(intent1);
                                        }else{
                                            startActivity(new Intent(ChartActivity.this, LoginUiActivity.class));
                                        }
                                    }else{
                                        startActivity(new Intent(ChartActivity.this, LoginUiActivity.class));
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

    public void calCount(String place){
        for(int i=0; i<list.size(); i++){
            if(place.equalsIgnoreCase(list.get(i).getPlace())){
                list.get(i).getNewCount();
            }
        }
    }
    class building {
        private String place;
        private int count;
        public building(String place, int count){
            this.place = place;
            this.count = count;
        }
        public building(){
        }
        public int getNewCount(){
            count = count+1;
            return count;
        }
        public void setPlace(String place) {
            this.place = place;
        }
        public void setCount(int count) {
            this.count = count;
        }
        public int getCount() {
            return count;
        }
        public String getPlace() {
            return place;
        }
    }



}