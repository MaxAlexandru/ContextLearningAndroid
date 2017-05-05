package com.example.max.contextlearning;

import java.util.ArrayList;

/**
 * Created by Max on 5/5/2017.
 */

public class Node {

    public String current;
    public ArrayList<Node> children;
    public ArrayList<String> values;

    public Node(String current) {
        this.current = current;
        this.children = new ArrayList<>();
        this.values = new ArrayList<>();
    }

    public void addChild(Node child, String attr) {
        children.add(child);
        values.add(attr);
    }
}
