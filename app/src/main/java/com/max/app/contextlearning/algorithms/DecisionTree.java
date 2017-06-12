package com.max.app.contextlearning.algorithms;

import java.util.HashMap;

public class DecisionTree {

    public String attr;
    public HashMap<String, DecisionTree> children = new HashMap<>();

    public DecisionTree(String attr) {
        this.attr = attr;
    }

    public void addNode(String value, DecisionTree child) {
        children.put(value, child);
    }

}
