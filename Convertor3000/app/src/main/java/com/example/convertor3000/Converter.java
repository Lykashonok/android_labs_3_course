package com.example.convertor3000;

import android.animation.TypeConverter;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Converter {
    public Map<String, ConverterModule> modules;

    public String Convert(Double value, String In, String Out, String Module) {
        if (In == Out) return value.toString();
        Double inPrimaryProp = modules.get(Module).convertFunctions.get(In).apply(value, false);
        Double result = modules.get(Module).convertFunctions.get(Out).apply(inPrimaryProp, true);
        return result.toString();
    }

    public ArrayList<String> GetModulesNames() {
        return new ArrayList<>(modules.keySet());
    }

    public ArrayList<String> GetModulePropsNames(String Module) {
        return new ArrayList<String>(modules.get(Module).convertFunctions.keySet());
    }

    public String GetDefaultPropOfModule(String Module) {
        return modules.get(Module).primaryProp;
    }

    public Converter () {
        Initialize();
    }

    private void Initialize() {
        modules = new HashMap<String, ConverterModule>() {{
            ConverterModule timeModule = new ConverterModule("time","seconds");
            timeModule.convertFunctions.put("microseconds", (value, reverse) -> !reverse ? value * 0.000001 : value / 0.000001);
            timeModule.convertFunctions.put("miliseconds", (value, reverse) -> !reverse ? value * 0.001 : value / 0.001);
            timeModule.convertFunctions.put("minutes", (value, reverse) -> !reverse ? value * 60 : value / 60);
            timeModule.convertFunctions.put("hours", (value, reverse) -> !reverse ? value * 3600 : value / 3600);
            timeModule.convertFunctions.put("days", (value, reverse) -> !reverse ? value * 86400 : value / 86400);
            timeModule.convertFunctions.put("weeks", (value, reverse) -> !reverse ? value * 604800 : value / 604800);
            timeModule.convertFunctions.put("months", (value, reverse) -> !reverse ? value * 2592000 : value / 2592000);

            ConverterModule speedModule = new ConverterModule("speed","m/s");
            speedModule.convertFunctions.put("km/h", (value, reverse) -> !reverse ? value * 3.6 : value / 3.6);
            speedModule.convertFunctions.put("mile/h", (value, reverse) -> !reverse ? value * 0.44704 : value / 0.44704);

            ConverterModule areaModule = new ConverterModule("area","s. meters");
            areaModule.convertFunctions.put("s. kilometers", (value, reverse) -> !reverse ? value * 1000000 : value / 1000000);
            areaModule.convertFunctions.put("s. decimeters", (value, reverse) -> !reverse ? value * 0.01 : value / 0.01);
            areaModule.convertFunctions.put("s. santimeters", (value, reverse) -> !reverse ? value * 0.0001 : value / 0.0001);
            areaModule.convertFunctions.put("s. milimeters", (value, reverse) -> !reverse ? value * 0.000001 : value / 0.000001);

            ConverterModule volumeModule = new ConverterModule("volume","c. meters");
            volumeModule.convertFunctions.put("c. kilometers", (value, reverse) -> !reverse ? value * 1000000000 : value / 1000000000);
            volumeModule.convertFunctions.put("c. decimeters", (value, reverse) -> !reverse ? value * 0.001 : value / 0.001);
            volumeModule.convertFunctions.put("c. santimeters", (value, reverse) -> !reverse ? value * 0.000001 : value / 0.000001);
            volumeModule.convertFunctions.put("c. milimeters", (value, reverse) -> !reverse ? value * 0.000000001 : value / 0.000000001);
            volumeModule.convertFunctions.put("liters", (value, reverse) -> !reverse ? value * 0.001 : value / 0.001);
            volumeModule.convertFunctions.put("mililiters", (value, reverse) -> !reverse ? value * 0.000001 : value / 0.000001);

            put("time", timeModule);
            put("area", areaModule);
            put("volume", volumeModule);
            put("speed", speedModule);
        }};
    }

    public class ConverterModule {
        public String section;
        public String primaryProp;
        public Map<String, BiFunction<Double, Boolean, Double>> convertFunctions;

        public ConverterModule(String section, String prop) {
            this.section = section;
            this.primaryProp = prop;
            convertFunctions = new HashMap<String, BiFunction<Double, Boolean, Double>>();
            this.convertFunctions.put(this.primaryProp, (value, reverse) -> value);
        }
    }
}
