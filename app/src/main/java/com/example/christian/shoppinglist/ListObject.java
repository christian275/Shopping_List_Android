package com.example.christian.shoppinglist;

import java.io.Serializable;

/**
 * Created by Christian on 8/21/2017.
 */

public class ListObject implements Serializable {

    private double weight;
    private String weightType;
    int quantity;
    String sizeType;
    String completeWeight;
    String description;

    public ListObject(int quantity, double weight, String weightType, String description){
        this.quantity = quantity;
        this.weight = weight;
        this.weightType = weightType;
        this.description = description;

        if (weight != 0)
            completeWeight = weight + " " + weightType;
        else
            completeWeight = weight + " N/A";

    }
}
