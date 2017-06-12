package com.max.app.contextlearning.algorithms;

import android.util.Log;

import com.max.app.contextlearning.utilities.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
                ArrayList<HashMap<String, String>> newData = getAttrValData(data, bestAttr, value);

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
    private float gain(ArrayList<HashMap<String, String>> data, String attr) {
        float e = entropy(data);
        float ev = 0;
        float x = data.size();

        for (String val : Constants.SENSORS_VALUES.get(attr)) {
            ArrayList<HashMap<String, String>> newData = getAttrValData(data, attr, val);
            float newEntropy = entropy(newData);
            float xv = newData.size();
            ev += xv / x * newEntropy;
        }

        return e - ev;
    }

    /* Get examples with attributes based on value */
    private ArrayList<HashMap<String, String>> getAttrValData(
            ArrayList<HashMap<String, String>> data, String attr, String val) {
        ArrayList<HashMap<String, String>> newData = new ArrayList<>();

        for (HashMap<String, String> s : data)
            if (s.get(attr).equals(val))
                newData.add(s);

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

    public static ArrayList<HashMap<String, String>> parseData(ArrayList<String> data) {
        ArrayList<HashMap<String, String>> newData = new ArrayList<>();

        System.out.println(data);
        for (String s : data) {
            HashMap<String, String> newEntry = new HashMap<>();
            String [] entries = s.split("=")[0].split(" ");
            newEntry.put("label", s.split("=")[1]);
            int i = 0;
            for (String entry : Constants.SENSORS_VALUES.keySet()) {
                newEntry.put(entry, entries[i]);
                i++;
            }
            newData.add(newEntry);
        }
        return newData;
    }

    public static HashSet<String> getClasses(ArrayList<HashMap<String, String>> data) {
        HashSet<String> classes = new HashSet<>();

        for (HashMap<String, String> s : data)
            classes.add(s.get("label"));

        return classes;
    }

    private static boolean isLabel(HashMap<String, String> input, DecisionTree tree) {
        while (tree != null) {
            String attr = tree.attr;
            String value = input.get(attr);
            if (tree.children.get(value).attr.equals("Yes"))
                return true;
            else if (tree.children.get(value).attr.equals("No"))
                return false;
            else
                tree = tree.children.get(value);
        }
        return false;
    }

    public static ArrayList<String> getLabels(HashMap<String, String> input, HashMap<String, DecisionTree> trees) {
        ArrayList<String> labels = new ArrayList<>();
        for (String s : trees.keySet()) {
            boolean ok = isLabel(input, trees.get(s));
            if (ok)
                labels.add(s);
        }
        return labels;
    }
}
