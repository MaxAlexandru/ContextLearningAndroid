/*******************************************************************************
 * Copyright (c) 2017 Maximilian Alexandru.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
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
