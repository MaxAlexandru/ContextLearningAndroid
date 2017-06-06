package com.max.app.contextlearning.algorithms;

import android.util.Log;

import com.max.app.contextlearning.utilities.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class DecisionTreeHelper {

    private String label;

    public DecisionTreeHelper(String label) {
        this.label = label;
    }

    public DecisionTree id3(ArrayList<HashMap<String, String>> data, ArrayList<String> attributes) {
        DecisionTree node;
        if (allYes(data)) {
            node = new DecisionTree("Yes");
        } else if (allNo(data)) {
            node = new DecisionTree("No");
        } else if (attributes.isEmpty()) {
            node = new DecisionTree(getMostCommonClass(data));
        } else {
            String bestAttr = "";
            float bestGain = -9999;
            for (String attr : attributes) {
                float newGain = gain(data, attr);
                if (newGain > bestGain) {
                    bestGain = newGain;
                    bestAttr = attr;
                }
            }
            node = new DecisionTree(bestAttr);
            for (String value : Constants.SENSORS_VALUES.get(bestAttr)) {
                ArrayList<HashMap<String, String>> newData = new ArrayList<>();
                if (bestAttr.equals("proximity") || bestAttr.equals("gravity") || bestAttr.equals("acceleration")) {
                    newData = getAttrValData(data, bestAttr, value);
                } else if (bestAttr.equals("light") || bestAttr.equals("noise") || bestAttr.equals("device_tmp")) {
                    int low = Integer.parseInt(value.split(",")[0]);
                    int high = Integer.parseInt(value.split(",")[1]);
                    newData = getAttrContValData(data, bestAttr, low, high);
                }
                if (newData.isEmpty())
                    node.addNode(value, new DecisionTree(getMostCommonClass(data)));
                else {
                    attributes.remove(bestAttr);
                    node.addNode(value, id3(newData, attributes));
                }
            }
        }
        return node;
    }

    /* Entropy function */
    private float entropy(ArrayList<HashMap<String, String>> data) {
        float ent = 0;
        float x = data.size();

        /* Yes class */
        float xc = 0;
        for (HashMap<String, String> d : data)
            if (label.equals(d.get("label")))
                xc += 1;
        if (xc > 0)
            ent += xc / x * Math.log(xc / x);

        /* No class */
        xc = 0;
        for (HashMap<String, String> d : data)
            if (!label.equals(d.get("label")))
                xc += 1;
        if (xc > 0)
            ent += xc / x * Math.log(xc / x);

        return -ent;
    }

    /* Gain function */
    public float gain(ArrayList<HashMap<String, String>> data, String attr) {
        float e = entropy(data);
        float ev = 0;
        float x = data.size();

        if (attr.equals("proximity") || attr.equals("gravity") || attr.equals("acceleration")) {
            for (String val : Constants.SENSORS_VALUES.get(attr)) {
                ArrayList<HashMap<String, String>> newData = getAttrValData(data, attr, val);
                float newEntropy = entropy(newData);
                float xv = newData.size();
                ev += xv / x * newEntropy;
            }
        } else if (attr.equals("light") || attr.equals("noise") || attr.equals("device_tmp")) {
            for (String val : Constants.SENSORS_VALUES.get(attr)) {
                int low = Integer.parseInt(val.split(",")[0]);
                int high = Integer.parseInt(val.split(",")[1]);
                ArrayList<HashMap<String, String>> newData = getAttrContValData(data, attr, low, high);
                float newEntropy = entropy(newData);
                float xv = newData.size();
                ev += xv / x * newEntropy;
            }
        }

        return e - ev;
    }

    /* Get examples with attributes based on discreet value */
    private ArrayList<HashMap<String, String>> getAttrValData(
            ArrayList<HashMap<String, String>> data, String attr, String val) {
        ArrayList<HashMap<String, String>> newData = new ArrayList<>();

        for (HashMap<String, String> s : data)
            if (s.get(attr).equals(val))
                newData.add(s);

        return newData;
    }

    /* Get examples with attributes based on continuous value */
    private ArrayList<HashMap<String, String>> getAttrContValData(
            ArrayList<HashMap<String, String>> data, String attr, int low, int high) {
        ArrayList<HashMap<String, String>> newData = new ArrayList<>();

        for (HashMap<String, String> s : data) {
            float floatVal = Float.parseFloat(s.get(attr));
            if (floatVal >= low && floatVal < high)
                newData.add(s);
        }

        return newData;
    }

    /* Checks if all examples are yes */
    private boolean allYes(ArrayList<HashMap<String, String>> data) {
        for (HashMap<String, String> d : data)
            if (!d.get("label").equals(label))
                return false;
        return true;
    }

    /* Checks if all examples are no */
    private boolean allNo(ArrayList<HashMap<String, String>> data) {
        for (HashMap<String, String> d : data)
            if (d.get("label").equals(label))
                return false;
        return true;
    }

    /* Most common class */
    private String getMostCommonClass(ArrayList<HashMap<String, String>> data) {
        int yes = 0;
        for (HashMap<String, String> d : data)
            if (d.get("label").equals(label))
                yes += 1;
        if (yes >= data.size())
            return "Yes";
        return "No";
    }

    private static boolean isClass(DecisionTree tree, ArrayList<String> input) {
        while (!tree.attr.equals("Yes") && !tree.attr.equals("No")) {
            if (tree.attr.equals("proximity")) {
                tree = tree.children.get(Constants.beautify(1, input.get(1)));
            } else if (tree.attr.equals("gravity")) {
                tree = tree.children.get(Constants.beautify(3, input.get(3)));
            } else if (tree.attr.equals("acceleration")) {
                tree = tree.children.get(Constants.beautify(4, input.get(4)));
            } else if (tree.attr.equals("light")) {
                String value = "";
                for (String val : Constants.SENSORS_VALUES.get("light")) {
                    int low = Integer.parseInt(val.split(",")[0]);
                    int high = Integer.parseInt(val.split(",")[1]);
                    float current = Float.parseFloat(input.get(0));
                    if (low <= current && current < high) {
                        value = String.valueOf(low) + "," + String.valueOf(high);
                        break;
                    }
                }
                tree = tree.children.get(value);
            } else if (tree.attr.equals("noise")) {
                String value = "";
                for (String val : Constants.SENSORS_VALUES.get("noise")) {
                    int low = Integer.parseInt(val.split(",")[0]);
                    int high = Integer.parseInt(val.split(",")[1]);
                    float current = Float.parseFloat(input.get(2));
                    if (low <= current && current < high) {
                        value = String.valueOf(low) + "," + String.valueOf(high);
                        break;
                    }
                }
                tree = tree.children.get(value);
            } else if (tree.attr.equals("device_tmp")) {
                String value = "";
                for (String val : Constants.SENSORS_VALUES.get("device_tmp")) {
                    int low = Integer.parseInt(val.split(",")[0]);
                    int high = Integer.parseInt(val.split(",")[1]);
                    float current = Float.parseFloat(input.get(5));
                    if (low <= current && current < high) {
                        value = String.valueOf(low) + "," + String.valueOf(high);
                        break;
                    }
                }
                tree = tree.children.get(value);
            }
        }
        if (tree.attr.equals("Yes"))
            return true;
        return false;
    }

    public static String getClasses(HashMap<String, DecisionTree> trees, ArrayList<String> input) {
        String res = "";

        if (input.get(0).equals("None"))
            return "None";

        for (String s : trees.keySet()) {
            Log.i("Tree", s);
            if (isClass(trees.get(s), input))
                res += s + ", ";
        }
        return res.substring(0, res.length() - 2);
    }

}
