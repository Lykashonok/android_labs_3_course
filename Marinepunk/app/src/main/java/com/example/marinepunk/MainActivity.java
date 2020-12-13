package com.example.marinepunk;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.marinepunk.help.AccountInfo;
import com.example.marinepunk.viewmodel.ApplicationViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ApplicationViewModel applicationViewModel;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_account)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        applicationViewModel = new ViewModelProvider(this).get(ApplicationViewModel.class);
        applicationViewModel.setActivityContext(this);
        configureNavHeader();
    }

    private void configureNavHeader() {
        View headerView = navigationView.getHeaderView(0);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        TextView accountEmail = (TextView) headerView.findViewById(R.id.accountEmail);
        TextView accountName = (TextView) headerView.findViewById(R.id.accountName);
        ImageView accountImage = (ImageView) headerView.findViewById(R.id.accountImage);

        applicationViewModel.getAccountInfo().observe(this, accountInfo -> {
            accountEmail.setText(auth.getCurrentUser().getEmail());
            accountName.setText(accountInfo.accountName);
            Picasso.get().load(accountInfo.accountImage).into(accountImage);
        });
        String cuid = auth.getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("profiles").child(cuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AccountInfo ai = applicationViewModel.getAccountInfo().getValue();
                String name = snapshot.child("nickname").getValue(String.class);
                accountName.setText(name);
                ai.accountName = name;
                accountEmail.setText(auth.getCurrentUser().getEmail());
                Picasso.get().load(snapshot.child("ImagePath").getValue(String.class)).into(accountImage);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(240, 240);
                accountImage.setLayoutParams(layoutParams);
                applicationViewModel.setAccountInfo(ai);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}