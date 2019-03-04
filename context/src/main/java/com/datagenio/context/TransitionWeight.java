package com.datagenio.context;

import com.google.gson.annotations.SerializedName;

public class TransitionWeight {

    @SerializedName("id")
    private String transitionId;

    @SerializedName("weight")
    private int weight;

    public String getTransitionId() {
        return transitionId;
    }

    public void setTransitionId(String transitionId) {
        this.transitionId = transitionId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
