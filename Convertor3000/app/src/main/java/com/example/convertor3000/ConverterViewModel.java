package com.example.convertor3000;

import android.util.Log;
import android.widget.Button;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ConverterViewModel extends ViewModel implements LifecycleObserver {
    private MutableLiveData<String> converterIn;
    private MutableLiveData<String> converterOut;
    private MutableLiveData<String> converterSection;
    private MutableLiveData<String> In;
    private MutableLiveData<String> Out;
    public Converter converter;

    public ConverterViewModel() {
        converter = new Converter();
    }

    public LiveData<String> getConverterIn() {
        if (converterIn == null) {
            converterIn = new MutableLiveData<String>("seconds");
        }
        return converterIn;
    }

    public void setConverterIn(String value) {
        if (converterIn == null) {
            converterIn = new MutableLiveData<String>("seconds");
        }
        converterIn.setValue(value);
    }

    public LiveData<String> getConverterOut() {
        if (converterOut == null) {
            converterOut = new MutableLiveData<String>("seconds");
        }
        return converterOut;
    }

    public void setConverterOut(String value) {
        if (converterOut == null) {
            converterOut = new MutableLiveData<String>("seconds");
        }
        converterOut.setValue(value);
    }

    public LiveData<String> getConverterSection() {
        if (converterSection == null) {
            converterSection = new MutableLiveData<String>("time");
        }
        return converterSection;
    }

    public void setConverterSection(String value) {
        if (converterSection == null) {
            converterSection = new MutableLiveData<String>("time");
        }
        converterSection.setValue(value);
        setConverterIn(converter.GetDefaultPropOfModule(value));
        setConverterOut(converter.GetDefaultPropOfModule(value));
    }

    public LiveData<String> getIn() {
        if (In == null) {
            In = new MutableLiveData<String>("0");
        }
        return In;
    }

    public void setIn(String value) {
        if (In == null) {
            In = new MutableLiveData<String>("0");
        }
        In.setValue(value);
    }

    public LiveData<String> getOut() {
        if (Out == null) {
            Out = new MutableLiveData<String>("0");
        }
        return Out;
    }

    public void setOut(String value) {
        if (Out == null) {
            Out = new MutableLiveData<String>("0");
        }
        Out.setValue(value);
    }

    public void setButtonValue(Character buttonValue) {
        String InValue = getIn().getValue();
        String ConverterInValue = getConverterIn().getValue();
        String ConverterOutValue = getConverterOut().getValue();
        String ConverterSectionValue = getConverterSection().getValue();

        if (buttonValue != null) {
            if (buttonValue == '⌫') {
                if(InValue.length() > 1) {
                    setIn(InValue.substring(0, InValue.length() - 1));
                } else {
                    setIn("0");
                }
            } else {
                String newText = InValue;
                if (newText == null) {
                    newText = buttonValue.toString();
                } else if (newText.length() != 0 && newText.charAt(0) == '0') {
                    newText = buttonValue.toString();
                } else {
                    newText += buttonValue;
                }
                setIn(newText);
            }
        }
        // After In editing, put it into Out
        try {
            setOut(converter.Convert(Double.parseDouble(
                getIn().getValue()),
                ConverterInValue, ConverterOutValue,
                ConverterSectionValue));
        } catch (NumberFormatException nfe) {
            setOut(converter.Convert(0.0, ConverterInValue, ConverterOutValue, ConverterSectionValue));
        }

    }
}
