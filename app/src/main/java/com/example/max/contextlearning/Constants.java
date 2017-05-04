package com.example.max.contextlearning;

import java.util.LinkedHashMap;

/**
 * Created by Max on 4/21/2017.
 */

public class Constants {

    public static final String [] SENSORS = {
            "Light",
            "Proximity",
            "Noise",
            "Gravity",
            "Acceleration",
            "Device Temperature"
    };

    public static final String [] TASKS = {
            "Silent Volume",
            "Quite Volume",
            "Normal Volume",
            "Loud Volume",
            "Smart Notifications"
    };


    public final static LinkedHashMap<String, String []> SENSORS_VALUES = new LinkedHashMap<>();
    static {
        SENSORS_VALUES.put("Light", new String[] {"Dark", "LightBulb", "Daylight"});
        SENSORS_VALUES.put("Proximity", new String[] {"Near", "Far"});
        SENSORS_VALUES.put("Noise", new String[] {"Silence", "Normal", "Noisy"});
        SENSORS_VALUES.put("Gravity", new String[] {"FaceUp", "FaceDown", "Unknown"});
        SENSORS_VALUES.put("Acceleration", new String[] {"Still", "Moving"});
        SENSORS_VALUES.put("Device Temperature", new String[] {"Cool", "Normal", "Hot"});
    }

    public final static LinkedHashMap<String, String> ACTIONS = new LinkedHashMap<>();
    static {
        ACTIONS.put("Light", "com.example.max.contextlearning.LIGHT_SENSOR");
        ACTIONS.put("Proximity", "com.example.max.contextlearning.PROXIMITY_SENSOR");
        ACTIONS.put("Noise", "com.example.max.contextlearning.NOISE_SENSOR");
        ACTIONS.put("Gravity", "com.example.max.contextlearning.GRAVITY_SENSOR");
        ACTIONS.put("Acceleration", "com.example.max.contextlearning.ACCELERATION_SENSOR");
    }

    public static String beautify(int type, String value) {
        String res = "";

        switch (type) {
            case 0:
                float light = Float.parseFloat(value);
                if (light < 10)
                    res = "Dark";
                else if (light < 1000)
                    res = "LightBulb";
                else
                    res = "Daylight";
                break;
            case 1:
                float dist = Float.parseFloat(value);
                if (dist == 0)
                    res = "Near";
                else
                    res = "Far";
                break;
            case 2:
                double db = Double.parseDouble(value);
                if (db < 20)
                    res = "Silence";
                else if (db < 40)
                    res = "Normal";
                else
                    res = "Noisy";
                break;
            case 3:
                String [] vals1 = value.split(" ");
                float z1 = Float.parseFloat(vals1[2]);
                if (z1 >= 9.5)
                    res = "FaceUp";
                else if (z1 <= -9.5)
                    res = "FaceDown";
                else
                    res = "Unknown";
                break;
            case 4:
                String [] vals2 = value.split(" ");
                float x = Float.parseFloat(vals2[0]);
                float y = Float.parseFloat(vals2[0]);
                float z = Float.parseFloat(vals2[0]);
                if (x < 0.3 && y < 0.3 && z < 0.3)
                    res = "Still";
                else
                    res = "Moving";
                break;
            case 5:
                float temp = Float.parseFloat(value);
                if (temp < 25.0)
                    res = "Cool";
                else if (temp < 40.0)
                    res = "Normal";
                else
                    res = "Hot";
                break;
        }

        return res;
    }
}
