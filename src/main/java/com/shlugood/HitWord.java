package com.shlugood;

public class HitWord {
    private final int endIndex;
    private double frequency;
    public HitWord(int endIndex, double frequency){
        this.endIndex = endIndex;
        this.frequency = frequency;
    }
    public int getEndIndex() {
        return endIndex;
    }

    public double getFrequency() {
        return frequency;
    }


    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }
}
