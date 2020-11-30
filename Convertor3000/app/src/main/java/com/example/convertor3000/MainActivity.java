package com.example.convertor3000;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.convertor3000.Converter;
import com.example.convertor3000.ConverterViewModel;
import com.example.convertor3000.R;
import com.example.convertor3000.ConverterFragment;
import com.example.convertor3000.KeyboardFragment;

public class MainActivity extends AppCompatActivity implements ConverterFragment.OnToggleKeyboardListener {

    KeyboardFragment keyboardFragment;
    ConverterFragment converterFragment;
    FragmentManager fragmentManager;
    ConverterViewModel converterViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        converterViewModel = new ViewModelProvider(this).get(ConverterViewModel.class);

        setContentView(R.layout.activity_main);

        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);

        keyboardFragment = (KeyboardFragment) getSupportFragmentManager().findFragmentById(R.id.keyboardFragment);
        converterFragment = (ConverterFragment) getSupportFragmentManager().findFragmentById(R.id.converterFragment);

        FragmentTransaction ft =
                getSupportFragmentManager()
                .beginTransaction()
                .hide((Fragment) keyboardFragment);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        for(String section : converterViewModel.converter.GetModulesNames()){
            menu.add(section);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        converterViewModel.setConverterSection(item.getTitle().toString());

        return true;
    }

    @Override
    public void onBackPressed() {
        if (keyboardFragment.isHidden()) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager()
                .beginTransaction()
                .hide(keyboardFragment)
                .commit();
        }
    }

    @Override
    public void OnToggleKeyboard(Boolean b) {
        FragmentTransaction ft =
                getSupportFragmentManager()
                .beginTransaction();
        if (b) {
            ft.show(keyboardFragment);
        } else {
            ft.hide(keyboardFragment);
        }
        ft.commit();
    }
}