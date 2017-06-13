package com.max.app.contextlearning.utilities;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Constants {

    public static final String TAG = "max.app.learning";
    public static final int READ_TIME = 30000;
    public static final int DETECT_TIME = 60000;

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
        SENSORS_VALUES.put("light", new String[] {
                "0,1", "1,10", "10,100", "100,1000", "1000,5000", "5000,100000"
        });
        SENSORS_VALUES.put("proximity", new String[] {
                "Near", "Far", "None"
        });
        SENSORS_VALUES.put("noise", new String[] {
                "0,10", "10,20", "20,30", "30,40", "40,50", "50,60",
                "60,70", "70,80", "80,90", "90,100", "100,100000"
        });
        SENSORS_VALUES.put("gravity", new String[] {
                "FaceUp", "FaceDown", "Vertical", "Horizontal", "None"
        });
        SENSORS_VALUES.put("acceleration", new String[] {
                "Still", "Moving"
        });
        SENSORS_VALUES.put("device_tmp", new String[] {
                "0,30", "30,35", "35,100"
        });
    }

    public final static LinkedHashMap<String, String> ACTIONS = new LinkedHashMap<>();
    static {
        ACTIONS.put("Light", "com.max.app.contextlearning.LIGHT_SENSOR");
        ACTIONS.put("Proximity", "com.max.app.contextlearning.PROXIMITY_SENSOR");
        ACTIONS.put("Noise", "com.max.app.contextlearning.NOISE_SENSOR");
        ACTIONS.put("Gravity", "com.max.app.contextlearning.GRAVITY_SENSOR");
        ACTIONS.put("Acceleration", "com.max.app.contextlearning.ACCELERATION_SENSOR");
        ACTIONS.put("Location", "com.max.app.contextlearning.LOCATION_SENSOR");
//        ACTIONS.put("Current", "com.max.app.contextlearning.CURRENT_ACTIVITY");
    }

    public static String codeData(ArrayList<String> data) {
        String code = "";
        for (String s : data)
            code += s + " ";
        return code.substring(0, code.length() - 1);
    }

    public static String beautify(int type, String value) {
        String res = "";

        if (value.equals("None"))
            return "None";

        switch (type) {
            case 0:
                float light = Float.parseFloat(value);
                for (String s : SENSORS_VALUES.get("light")) {
                    int from = Integer.parseInt(s.split(",")[0]);
                    int to = Integer.parseInt(s.split(",")[1]);
                    if (light >= from && light < to) {
                        res = s;
                        break;
                    }
                }
                break;
            case 1:
                if (value.equals("None"))
                    res = "Far";
                else {
                    float dist = Float.parseFloat(value);
                    if (dist == 0)
                        res = "Near";
                    else
                        res = "Far";
                }
                break;
            case 2:
                double db = Double.parseDouble(value);
                for (String s : SENSORS_VALUES.get("noise")) {
                    int from = Integer.parseInt(s.split(",")[0]);
                    int to = Integer.parseInt(s.split(",")[1]);
                    if (db >= from && db < to) {
                        res = s;
                        break;
                    }
                }
                break;
            case 3:
                String [] vals1 = value.split("  ");
                float x1 = Float.parseFloat(vals1[0].split(":")[1]);
                float y1 = Float.parseFloat(vals1[1].split(":")[1]);
                float z1 = Float.parseFloat(vals1[2].split(":")[1]);
                if (z1 > 0 && Math.abs(z1) > Math.abs(x1) && Math.abs(z1) > Math.abs(y1))
                    res = "FaceUp";
                else if (z1 < 0 && Math.abs(z1) > Math.abs(x1) && Math.abs(z1) > Math.abs(y1))
                    res = "FaceDown";
                else if (Math.abs(y1) > Math.abs(x1) && Math.abs(y1) > Math.abs(z1))
                    res = "Vertical";
                else if (Math.abs(x1) > Math.abs(y1) && Math.abs(x1) > Math.abs(z1))
                    res = "Horizontal";
                else
                    res = "Unknown";
                break;
            case 4:
                String [] vals2 = value.split("  ");
                float x = Float.parseFloat(vals2[0].split(":")[1]);
                float y = Float.parseFloat(vals2[1].split(":")[1]);
                float z = Float.parseFloat(vals2[2].split(":")[1]);
                if (Math.abs(x) < 0.1 && Math.abs(y) < 0.1 && Math.abs(z) < 0.1)
                    res = "Still";
                else
                    res = "Moving";
                break;
            case 5:
                float temp = Float.parseFloat(value);
                for (String s : SENSORS_VALUES.get("device_tmp")) {
                    int from = Integer.parseInt(s.split(",")[0]);
                    int to = Integer.parseInt(s.split(",")[1]);
                    if (temp >= from && temp < to) {
                        res = s;
                        break;
                    }
                }
                break;
        }

        return res;
    }
}
