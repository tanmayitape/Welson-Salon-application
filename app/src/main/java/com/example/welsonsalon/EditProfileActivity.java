package com.example.welsonsalon;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {


    EditText change_Name,change_Username,change_phoneNo;
    Uri imageUri;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference ref = FirebaseDatabase.getInstance("https://welson-salon-backend-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
   FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference imageRef = storage.getReference().child("Users_image");
    Button button;

    Boolean isThisImgUploaded = true;
    ImageView User_Image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        GetImageFromStorage();
        //Hooks
        change_Name = findViewById(R.id.ChangeName);
        change_Username = findViewById(R.id.ChangeUserName);
        change_phoneNo = findViewById(R.id.ChangeNumber);

        button = findViewById(R.id.update_btn);
        User_Image = findViewById(R.id.userImage);
        //getting data into textView
        ref.child(auth.getCurrentUser().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String UserId = auth.getCurrentUser().getUid();
                    String Name = snapshot.child(UserId).child("FirstName").getValue(String.class);
                    String username= snapshot.child(UserId).child("LastName").getValue(String.class);
                    String number = snapshot.child(UserId).child("PhoneNo").getValue(String.class);

                    change_Name.setText(Name);
                    change_Username.setText(username);
                    change_phoneNo.setText(number);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        button.setOnClickListener(v -> {
            if(!isNetworkAvailable()){
                showNoInternetSnackbar();
            }else {
                String FirstName = change_Name.getText().toString();
                String LastName = change_Username.getText().toString();
                String number = change_phoneNo.getText().toString();

                if (FirstName.isEmpty() && LastName.isEmpty() && number.isEmpty()) {
                    Toast.makeText(this, "Cannot be Empty!", Toast.LENGTH_SHORT).show();
                } else {
                    String UserId = auth.getCurrentUser().getUid();
                    if(isThisImgUploaded){
                        HashMap<String,Object> user = new HashMap<>();
                        user.put("FirstName",FirstName);
                        user.put("PhoneNo",number);
                        user.put("LastName",LastName);

                        ref.child(UserId).child("FirstName").setValue(FirstName);
                        ref.child(UserId).child("PhoneNo").setValue(number);
                        ref.child(UserId).child("LastName").setValue(LastName);

                        startActivity(new Intent(this, MainActivity.class));
                        finish();

                    }else {
                        HashMap<String,Object> user = new HashMap<>();
                        user.put("FirstName",FirstName);
                        user.put("PhoneNo",number);
                        user.put("LastName",LastName);

                        ref.child(UserId).child("FirstName").setValue(FirstName);
                        ref.child(UserId).child("PhoneNo").setValue(number);
                        ref.child(UserId).child("LastName").setValue(LastName);

                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                        uploadImage();
                    }

                }
            }




        });

        User_Image.setOnClickListener(v -> {
            SelectImage();
        });
    }
    private void SelectImage() {
        isThisImgUploaded =false;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image"),100);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100){

            imageUri = data.getData();
            User_Image.setImageURI(imageUri);

        }
    }

    private void uploadImage() {
        imageRef.child(auth.getCurrentUser().getUid()).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(EditProfileActivity.this, "uploaded!", Toast.LENGTH_SHORT).show();
                isThisImgUploaded =true;
            }
        });
    }
    private void GetImageFromStorage() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Please wait");
        pd.show();
        StorageReference downloadImageRef = FirebaseStorage.getInstance().getReference().child("Users_image");
        String userId = auth.getCurrentUser().getUid();
        downloadImageRef.child(userId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //Toast.makeText(EditProfileActivity.this, "success", Toast.LENGTH_SHORT).show();
                Picasso.get().load(uri).into(User_Image);
                pd.dismiss();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(EditProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            pd.dismiss();
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