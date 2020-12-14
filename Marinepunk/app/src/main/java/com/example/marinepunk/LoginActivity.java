package com.example.marinepunk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        toMainIfAuthenticated(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public static void toMainIfAuthenticated(Activity activity) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent i = new Intent(activity, MainActivity.class);
            // Go back to login screen from main screen - not enough good decision
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            activity.startActivity(i);
        }
    }
}