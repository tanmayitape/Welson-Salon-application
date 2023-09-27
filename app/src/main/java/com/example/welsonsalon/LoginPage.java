package com.example.welsonsalon;


import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginPage extends AppCompatActivity {
    Button callSignUp,login_btn,Forget_btn;
    DatabaseReference ref = FirebaseDatabase.getInstance("https://welson-salon-backend-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ImageView image;
    TextView logoText,sloganText;
    TextInputLayout email,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);





        // Hooks
        image = findViewById(R.id.logo_image);
        login_btn = findViewById(R.id.login_btn);
        logoText = findViewById(R.id.logo_name);
        sloganText = findViewById(R.id.slogan_name);
        email = findViewById(R.id.username);
        password = findViewById(R.id.password);
        callSignUp = findViewById(R.id.signup_screen);

        login_btn.setOnClickListener(view -> {
            onLoginUser();
        });



        callSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(LoginPage.this, RegisterPage.class);

            Pair[] pairs = new Pair[7];
            pairs[0]= new Pair<View,String>(image,"logo_salon");
            pairs[1]= new Pair<View,String>(logoText,"logo_head");
            pairs[2]= new Pair<View,String>(email,"trans_username");
            pairs[3]= new Pair<View,String>(password,"button_password");
            pairs[4]= new Pair<View,String>(login_btn,"button_login");
            pairs[5]= new Pair<View,String>(callSignUp,"user_signup");
            pairs[6]= new Pair<View,String>(sloganText,"login_continue");

            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginPage.this,pairs);
            startActivity(intent,options.toBundle());


        });
        
        Forget_btn = findViewById(R.id.forget_btn);
        Forget_btn.setOnClickListener(v -> {
                startActivity(new Intent(LoginPage.this,ResetPasswordActivity.class));
        });
    }

    private void onLoginUser() {
       if(!isNetworkAvailable()){
           showNoInternetSnackbar();
       }else{
           String userEmail = email.getEditText().getText().toString();
           String userPassword = password.getEditText().getText().toString();

           ProgressDialog pd = new ProgressDialog(this);

           if (userEmail.isEmpty()) {
               email.setError("Cannot be empty!");
           } else if (userPassword.isEmpty()) {
               password.setError("Cannot be empty!");
               email.setError(null);
               password.setFocusable(true);
           } else {
               auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(task -> {
                   pd.setMessage("Please Wait!");
                   pd.show();
                   if (task.isSuccessful()) {
                       //Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
                       ref.child(auth.getCurrentUser().getUid()).child("Password").setValue(userPassword);
                       startActivity(new Intent(LoginPage.this, MainActivity.class));
                       finish();
                       pd.dismiss();
                   } else {
                       email.setError("Incorrect values!");
                       password.setError("Incorrect values!");
                       pd.dismiss();
                   }

               });
           }
       }
    }
    //double tab to exit functionality
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
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
