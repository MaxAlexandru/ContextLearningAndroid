package com.example.max.contextlearning;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Max on 4/21/2017.
 */

public class Constants {

    public static final String [] SENSORS = {
            "Proximity",
            "Light"
    };

    public final static LinkedHashMap<String, String []> SENSORS_VALUES = new LinkedHashMap<>();
    static {
        SENSORS_VALUES.put("Proximity", new String[] {"Near", "Far"});
        SENSORS_VALUES.put("Light", new String[] {"Outside", "Inside", "Dark"});
    }

    public final static LinkedHashMap<String, String> ACTIONS = new LinkedHashMap<>();
    static {
        ACTIONS.put("Light", "com.example.max.contextlearning.LIGHT_SENSOR");
        ACTIONS.put("Proximity", "com.example.max.contextlearning.PROXIMITY_SENSOR");
        ACTIONS.put("Noise", "com.example.max.contextlearning.NOISE_SENSOR");
    }
}
