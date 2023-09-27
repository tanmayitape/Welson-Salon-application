package com.example.welsonsalon;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Profile_Fragment extends Fragment {
    StorageReference downloadImageRef = FirebaseStorage.getInstance().getReference().child("Users_image");

    TextView user_Name,user_email;
    String current_user_email,current_Lastname,current_username;

    Button EditProfile,Appoint_btn,Schedule_btn;
    ImageView user_image;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference ref = FirebaseDatabase.getInstance("https://welson-salon-backend-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_, container, false);

        //TextView
        user_Name = view.findViewById(R.id.user_Name);
        user_email = view.findViewById(R.id.user_email);
        user_image = view.findViewById(R.id.user_image);

        //setting refresh option
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if(isNetworkNotAvailable()){
               // showNoInternetSnackbar(view);
                Toast.makeText(getContext(), "NO INTERNET!", Toast.LENGTH_SHORT).show();
            //    ProgressDialog pd = new ProgressDialog(getContext());
                swipeRefreshLayout.setRefreshing(false);
             //   pd.dismiss();
                user_Name.setText(current_username+" "+current_Lastname);
                user_email.setText(current_user_email);
            }else{
                ProgressDialog pd = new ProgressDialog(getContext());
                GetImageFromStorage();
                getUserDataFromFirebase();
                swipeRefreshLayout.setRefreshing(false);
                pd.dismiss();

            }

        });


        if(isNetworkNotAvailable()){
            user_Name.setText(current_username+" "+current_Lastname);
            user_email.setText(current_user_email);
        }else{
            //Get image from firebase
            GetImageFromStorage();
            //get user data
            getUserDataFromFirebase();
        }



        EditProfile = view.findViewById(R.id.linkToEditprofile);
        EditProfile.setOnClickListener(v -> startActivity(new Intent(getContext(),EditProfileActivity.class)));

        Appoint_btn = view.findViewById(R.id.appoint_button);
        Schedule_btn = view.findViewById(R.id.schedule_button);

        Appoint_btn.setOnClickListener(v -> startActivity(new Intent(getContext(),AppointmentActivity.class)));

        Schedule_btn.setOnClickListener(v -> startActivity(new Intent(getContext(),Schedule_activity.class)));
        return view;
    }

    private void getUserDataFromFirebase() {
        //authentication of user
        ref.child(auth.getCurrentUser().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    current_username = snapshot.child(auth.getCurrentUser().getUid()).child("FirstName").getValue(String.class);
                    current_Lastname = snapshot.child(auth.getCurrentUser().getUid()).child("LastName").getValue(String.class);
                    current_user_email = snapshot.child(auth.getCurrentUser().getUid()).child("Email").getValue(String.class);

                    user_Name.setText(current_username+" "+current_Lastname);
                    user_email.setText(current_user_email);
                }
                if(user_Name.getText().toString().isEmpty()){
                    getDialogInfo("Warning","You Will need to Update your profile to get values!",new Intent(getContext(),EditProfileActivity.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error","Error = "+error);
            }
        });
    }

    private void GetImageFromStorage() {
        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Loading Profile");
        pd.show();
        String userId = auth.getCurrentUser().getUid();
        downloadImageRef.child(userId).getDownloadUrl().addOnSuccessListener(uri -> {
          //  Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();
            Picasso.get().load(uri).into(user_image);
            pd.dismiss();
        }).addOnFailureListener(e -> pd.dismiss());
    }
    public void getDialogInfo(String header,String des,Intent intent){
        Dialog dialog =new Dialog(getContext());
        dialog.setContentView(R.layout.custom_dialog);
        dialog.show();
        TextView dialog_header = dialog.findViewById(R.id.dialog_header);
        TextView dialog_dis = dialog.findViewById(R.id.dialog_text);
        Button dialog_btn = dialog.findViewById(R.id.dialog_button);
        ImageView dialog_close = dialog.findViewById(R.id.CloseDialog);

        dialog_header.setText(header);
        dialog_dis.setText(des);
        dialog_btn.setOnClickListener(v ->{ dialog.dismiss();
            startActivity(intent);});
        dialog_close.setOnClickListener(v -> dialog.dismiss());
    }

    //this code will check the internet connectivity
    private boolean isNetworkNotAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo == null || !networkInfo.isConnected();
    }

}

