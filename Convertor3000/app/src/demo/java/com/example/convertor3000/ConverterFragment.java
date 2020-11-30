package com.example.convertor3000;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.convertor3000.Converter;
import com.example.convertor3000.ConverterViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class ConverterFragment extends Fragment {

    ConverterViewModel converterViewModel;

    OnToggleKeyboardListener onToggleKeyboardListener;
    ArrayAdapter<String> ModuleValues;

    Toast messageCopied;

    EditText In;
    EditText Out;
    Spinner SpinnerIn;
    Spinner SpinnerOut;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public ConverterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onToggleKeyboardListener = (OnToggleKeyboardListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " must be implemented");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        messageCopied = Toast.makeText(requireContext().getApplicationContext(), "Copied successfully", Toast.LENGTH_SHORT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_converter, container, false);

        // In, Out - EditTexts
        In          = (EditText) view.findViewById(R.id.inputValueIn);
        Out         = (EditText) view.findViewById(R.id.inputValueOut); Out.setInputType(InputType.TYPE_NULL);
        SpinnerIn   = (Spinner)  view.findViewById(R.id.propIn);
        SpinnerOut  = (Spinner)  view.findViewById(R.id.propOut);

        converterViewModel = new ViewModelProvider(requireActivity()).get(ConverterViewModel.class);

        ConfigureInputs(view);

        return view;
    }

    public void ConfigureInputs(View view) {
        // Observing viewModel values changing
        converterViewModel.getIn().observe(getViewLifecycleOwner(),  value -> { In.setText(value); });
        converterViewModel.getOut().observe(getViewLifecycleOwner(), value -> { Out.setText(value); });
        converterViewModel.getConverterSection().observe(getViewLifecycleOwner(), value -> { setSpinners(); });

        SpinnerIn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = adapterView.getItemAtPosition(i).toString();
                converterViewModel.setConverterIn(value);
                converterViewModel.setButtonValue(null);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        SpinnerOut.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = adapterView.getItemAtPosition(i).toString();
                converterViewModel.setConverterOut(value);
                converterViewModel.setButtonValue(null);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        // Setting spinners
        setSpinners();

        ArrayList<EditText> editTexts = new ArrayList<EditText>(Arrays.asList(In, Out));
        for (EditText e: editTexts) {
            e.setOnFocusChangeListener((View v, boolean b) -> {
                    if( b == true ) {
                        onToggleKeyboardListener.OnToggleKeyboard(true);
                    } else {
                        onToggleKeyboardListener.OnToggleKeyboard(false);
                    }
                }
            );
            e.setOnClickListener(v -> {
                onToggleKeyboardListener.OnToggleKeyboard(true);
            });
            e.setShowSoftInputOnFocus(false);
        }
    }

    private void setSpinners() {
        String Module = converterViewModel.getConverterSection().getValue();
        ArrayList<String> propNames = converterViewModel.converter.GetModulePropsNames(Module);
        ModuleValues = new ArrayAdapter<String>(requireActivity(),
                android.R.layout.simple_spinner_item, propNames
        );
        ModuleValues.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerIn.setAdapter(ModuleValues);
        SpinnerOut.setAdapter(ModuleValues);
        SpinnerIn.setSelection(ModuleValues.getPosition(converterViewModel.getConverterIn().getValue()));
        SpinnerOut.setSelection(ModuleValues.getPosition(converterViewModel.getConverterOut().getValue()));
    }

    public interface OnToggleKeyboardListener {
        public void OnToggleKeyboard(Boolean b);
    }
}