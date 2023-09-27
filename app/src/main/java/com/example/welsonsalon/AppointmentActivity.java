package com.example.welsonsalon;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class AppointmentActivity extends AppCompatActivity {

    TextView Price;
    Spinner spinner_selectDate;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference ref = FirebaseDatabase.getInstance("https://welson-salon-backend-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Appointment");

    TextView Selected_service,Selected_barber,Selected_date,Selected_timeSlot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        Selected_service = findViewById(R.id.selected_service);
        Selected_barber = findViewById(R.id.selected_pro);
        Selected_date = findViewById(R.id.selected_date);
        Selected_timeSlot = findViewById(R.id.selected_time);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int Today = calendar.get(Calendar.DAY_OF_MONTH);
        int Tomorrow = Today+1;

        String Current_date = (Today+"-"+month+"-"+year);
        String Tomorrow_date = (Tomorrow+"-"+month+"-"+year);
        String[] date = {"Select Date","Today","Tomorrow"};

        //setting data to spinner
        spinner_selectDate = findViewById(R.id.selectDate);
        ArrayAdapter<String> Date_adapter =new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,date);
        spinner_selectDate.setAdapter(Date_adapter);


        Price = findViewById(R.id.Price_textView);

        String User = auth.getCurrentUser().getUid();
        spinner_selectDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position ==1){
                    if(!isNetworkAvailable()){
                        showNoInternetSnackbar();
                    }else {
                        String dates =date[position];
                        String setDate = Current_date;
                        getDataOfAppointmnet(setDate,dates);
                    }
                }
                if(position ==2){
                    if(!isNetworkAvailable()){
                        showNoInternetSnackbar();
                    }else {
                        String dates =date[position];
                        String setDate = Tomorrow_date;
                        getDataOfAppointmnet(setDate,dates);
                    }

                }
                if(position ==0){
                    Selected_service.setText("");
                    Selected_barber.setText("");
                    Selected_date.setText("");
                    Selected_timeSlot.setText("");
                    Price.setText("");
                    Toast.makeText(AppointmentActivity.this, "Select date to see your appointment", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });










    }

    private void getDataOfAppointmnet(String  dateSelected,String dates) {
        ref.child(dateSelected).child(auth.getCurrentUser().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String service_selected = snapshot.child(dateSelected).child(auth.getCurrentUser().getUid()).child("Service Selected").getValue(String.class);
                    String barber_selected = snapshot.child(dateSelected).child(auth.getCurrentUser().getUid()).child("Barber Selected").getValue(String.class);
                    String time_selected = snapshot.child(dateSelected).child(auth.getCurrentUser().getUid()).child("Time Selected").getValue(String.class);
                    String date_selected = snapshot.child(dateSelected).child(auth.getCurrentUser().getUid()).child("Date Selected").getValue(String.class);
                    String price = snapshot.child(dateSelected).child(auth.getCurrentUser().getUid()).child("Price").getValue(String.class);

                    Selected_service.setText(service_selected);
                    Selected_barber.setText(barber_selected);
                    Selected_date.setText(date_selected);
                    Selected_timeSlot.setText(time_selected);
                    Price.setText(price);
                    if(Price.getText().equals("")){
                        Toast.makeText(AppointmentActivity.this, "You don't have appointment for "+dates, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void showNoInternetSnackbar() {
        if (!isNetworkAvailable()) {
            Snackbar snackbar = Snackbar.make(
                    findViewById(android.R.id.content),
                    "No internet Connection" ,
                    durationForNoInternet()
            );
            snackbar.setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Retry logic goes here
                    showNoInternetSnackbar();
                }
            });
            snackbar.show();
        }
    }
    private int durationForNoInternet() {
        if(!isNetworkAvailable()){
            return 4000;
        }else
        {
            return 0;
        }
    }
    //this code will check the internet connectivity
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}