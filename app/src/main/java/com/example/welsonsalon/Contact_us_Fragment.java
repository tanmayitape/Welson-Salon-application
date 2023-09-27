package com.example.welsonsalon;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Contact_us_Fragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_us_, container, false);
        Button message_send_btn =view.findViewById(R.id.message_send_btn);

        Spinner spinner = view.findViewById(R.id.spinner);
        EditText message = view.findViewById(R.id.customer_massage_for_us);
        EditText name = view.findViewById(R.id.userName);
        EditText email = view.findViewById(R.id.email);

        message_send_btn.setOnClickListener(view1 -> {

            if(name.getText().toString().length() == 0){
                name.setError("Please enter your name");
            }
            if(email.getText().toString().length() == 0){
                email.setError("Please enter your name");
            }
            if( message.getText().toString().length() == 0) {
                 message.setError("Please add some info first!");
            }
            else {
                message.setText(null);
                name.setText(null);
                email.setText(null);

                if (spinner.getSelectedItemId() > 0) {
                    String info = spinner.getSelectedItem().toString();
                    Toast.makeText(inflater.getContext(), "Your "+info+" has been sent!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}