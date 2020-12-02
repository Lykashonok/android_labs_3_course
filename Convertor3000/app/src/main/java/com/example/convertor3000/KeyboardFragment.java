package com.example.convertor3000;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.convertor3000.ConverterViewModel;
import com.example.convertor3000.R;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link KeyboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KeyboardFragment extends Fragment {
    ConverterViewModel converterViewModel;

    public KeyboardFragment() { }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();

        View view = inflater.inflate(R.layout.fragment_keyboard, container, false);
        converterViewModel = new ViewModelProvider(requireActivity()).get(ConverterViewModel.class);

        ArrayList<Button> keyboardButtons = new ArrayList<Button>(Arrays.asList(
                (Button) view.findViewById(R.id.key_0),
                (Button) view.findViewById(R.id.key_1),
                (Button) view.findViewById(R.id.key_2),
                (Button) view.findViewById(R.id.key_3),
                (Button) view.findViewById(R.id.key_4),
                (Button) view.findViewById(R.id.key_5),
                (Button) view.findViewById(R.id.key_6),
                (Button) view.findViewById(R.id.key_7),
                (Button) view.findViewById(R.id.key_8),
                (Button) view.findViewById(R.id.key_9),
                (Button) view.findViewById(R.id.key_dot),
                (Button) view.findViewById(R.id.key_erase)));
        for (Button button: keyboardButtons) {
            button.setOnClickListener(v -> {
                converterViewModel.setButtonValue(button.getText().charAt(0));
            });
        }

        return view;
    }
}