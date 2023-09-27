package com.example.welsonsalon;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class Schedule_activity extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference ref = FirebaseDatabase.getInstance("https://welson-salon-backend-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();

    String[] service = {"Select service","Haircut","Senior Cut","Face Massage","Head Massage","Children Cut","Beard Trim","Save And Haircut"};
    String[] Price = {"","150 rupees","100 rupees","100 rupees","100 rupees","150 rupees","100 rupees","200 rupees"};
    String[] Employee = {"Select Barber","Imran","Aman","Rohit"};

    String[] Time_slot = {"Select Time","8:00-8.30","8:30-9:00","9:00-9.30","9:30-10:00","10:00-10.30","10:30-11:00","11:00-11.30","11:30-12:00","12:00-12.30","12:30-13:00"};


    Spinner spin_service,spin_barber,spin_time,spin_date;
    TextView price;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar =findViewById(R.id.schedule_toolbar);
        setSupportActionBar(toolbar);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int Today = calendar.get(Calendar.DAY_OF_MONTH);
        int Tomorrow = Today+1;

        int hours = calendar.get(Calendar.HOUR);
       // Toast.makeText(this, hours, Toast.LENGTH_SHORT).show();
        Log.d("checking", "onCreate: "+hours);

        String Current_date = (Today+"-"+month+"-"+year);
        String Tomorrow_date = (Tomorrow+"-"+month+"-"+year);
        String[] date = {"Select Date",Current_date,Tomorrow_date};


        spin_service = findViewById(R.id.spinner_for_service);
        spin_barber = findViewById(R.id.spinner_for_barber);
        spin_time = findViewById(R.id.spinner_for_time);
        spin_date = findViewById(R.id.spinner_for_date);
        price = findViewById(R.id.Price_textView);


        ArrayAdapter<String> adapter =new ArrayAdapter<>(Schedule_activity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,service);
        spin_service.setAdapter(adapter);

        ArrayAdapter<String> Date_adapter =new ArrayAdapter<>(Schedule_activity.this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,date);
        spin_date.setAdapter(Date_adapter);

        ArrayAdapter<String> barber_adapter =new ArrayAdapter<>(Schedule_activity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,Employee);
        spin_barber.setAdapter(barber_adapter);

        ArrayAdapter<String> Time_adapter =new
                ArrayAdapter<>(Schedule_activity.this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                Time_slot);
        spin_time.setAdapter(Time_adapter);



        //back action
        ActionBar actionBar =getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        Button button = findViewById(R.id.add_appointment);
        button.setOnClickListener(v -> {

            String service_selected = spin_service.getSelectedItem().toString();
            String barber_selected = spin_barber.getSelectedItem().toString();
            String date_selected = spin_date.getSelectedItem().toString();
            String time_selected = spin_time.getSelectedItem().toString();
            String Price_dis = price.getText().toString();

            HashMap<String,Object> data = new HashMap<>();
            data.put("Service Selected",service_selected);
            data.put("Barber Selected",barber_selected);
            data.put("Date Selected",date_selected);
            data.put("Time Selected",time_selected);
            data.put("Price",Price_dis);

            ref.child("Appointment").child(date_selected).child(auth.getCurrentUser().getUid()).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    String service_selected = spin_service.getSelectedItem().toString();
                    String barber_selected = spin_barber.getSelectedItem().toString();
                    String date_selected = spin_date.getSelectedItem().toString();
                    String time_selected = spin_time.getSelectedItem().toString();
                    String Price_dis = price.getText().toString();

                    getDialogInfo("Booked","You have successfully booked an appointment",new Intent(Schedule_activity.this,AppointmentActivity.class),date_selected);

                }
            });
        });

        //setting price
        spin_service.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                price.setText(Price[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void getDialogInfo(String header, String des, Intent intent, String date_selected){
        Dialog dialog =new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.show();
        TextView dialog_header = dialog.findViewById(R.id.dialog_header);
        TextView dialog_dis = dialog.findViewById(R.id.dialog_text);
        Button dialog_btn = dialog.findViewById(R.id.dialog_button);
        ImageView dialog_close = dialog.findViewById(R.id.CloseDialog);

        dialog_header.setText(header);
        dialog_dis.setText(des);
        dialog_btn.setOnClickListener(v ->{ dialog.dismiss();
            intent.putExtra("date_selected",date_selected);
            startActivity(intent);
        finish();});
        dialog_close.setOnClickListener(v -> dialog.dismiss());




    }
}