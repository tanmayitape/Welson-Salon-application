package com.example.welsonsalon;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MainPageAdapter extends RecyclerView.Adapter<MainPageAdapter.ViewHolder> {

    String[] Heading;
    String[] Info;
    int[] ImageId;
    Listener listener;
    interface Listener{
         void whenClick(int position);
    }
    MainPageAdapter(String[] Heading, String[] Info, int[] ImageId){
        this.Heading = Heading;
        this.Info = Info;
        this.ImageId = ImageId;
    }
    @NonNull
    @Override
    public MainPageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_for_mainpage,parent,false);
             return new ViewHolder(cardView);
    }


    @Override
    public void onBindViewHolder(@NonNull MainPageAdapter.ViewHolder holder, int position) {
        CardView cardView =holder.cardView;
        TextView textView =cardView.findViewById(R.id.Heading);
        textView.setText(Heading[position]);

        TextView textView1 = cardView.findViewById(R.id.info1);
        textView1.setText(Info[position]);

        ImageView imageView =cardView.findViewById(R.id.mainPage_img);
        Drawable drawable = ContextCompat.getDrawable(cardView.getContext(),ImageId[position]);
        imageView.setImageDrawable(drawable);
        imageView.setContentDescription(Heading[position]);

        //called when a cardView is Clicked
        cardView.setOnClickListener(view -> {
            if(listener!=null){
                listener.whenClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Heading.length;
    }

    //this method called in fragment
    public void setListener(Listener listener){
        this.listener = listener;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        public ViewHolder(CardView cv) {
            super(cv);
            this.cardView = cv;
        }
    }
}
