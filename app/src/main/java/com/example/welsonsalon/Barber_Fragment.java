package com.example.welsonsalon;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Barber_Fragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_barber_, container, false);


        //getting data for cardView
        String[] Heading =new String[MainPageData.data.length];
        for(int i=0;i<MainPageData.data.length;i++){
            Heading[i] =MainPageData.data[i].getHeading();
        }

        String[] info =new String[MainPageData.data.length];
        for(int i=0;i<MainPageData.data.length;i++){
            info[i] =MainPageData.data[i].getInfo();
        }

        int[] ImageId =new int[MainPageData.data.length];
        for(int i=0;i<MainPageData.data.length;i++)
            {
                ImageId[i] =MainPageData.data[i].getImageResourceId();
            }

        //RecyclerView's Adapter
        MainPageAdapter adapter = new MainPageAdapter(Heading,info,ImageId);
       recyclerView.setAdapter(adapter);

       //adds click listener
       adapter.setListener(position ->{
           if(position==0){
              // Toast.makeText(getContext(), "Hey It's Working! ", Toast.LENGTH_SHORT).show();
               Intent intention =new Intent(recyclerView.getContext(),About_us_activity.class);
               startActivity(intention);
           } else if (position==1) {
               Intent intent =new Intent(recyclerView.getContext(),Schedule_activity.class);
               startActivity(intent);
           } else if (position==2) {
               Toast.makeText(getContext(), "Our Location", Toast.LENGTH_SHORT).show();
               Intent intent = new Intent(getContext(),LocateUs.class);
               startActivity(intent);
           }
           else {
               Toast.makeText(getContext(), "IN PROGRESS 2!", Toast.LENGTH_SHORT).show();
           }
       }


       );
        //defines the cardView structure Style
        LinearLayoutManager Manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(Manager);

        return recyclerView;
    }
}