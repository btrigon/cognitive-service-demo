package com.example.facetest.network.apimodels;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Benjamin on 15/05/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Scores {

    public Scores() {
    }

    @JsonIgnore
    private int categoryIndex;
    @JsonProperty
    private double anger;
    @JsonProperty
    private double contempt;
    @JsonProperty
    private double disgust;
    @JsonProperty
    private double fear;
    @JsonProperty
    private double happiness;
    @JsonProperty
    private double neutral;
    @JsonProperty
    private double sadness;
    @JsonProperty
    private double surprise;

    public int getCategoryIndex() {
        return categoryIndex;
    }

    public void setCategoryIndex(int categoryIndex) {
        this.categoryIndex = categoryIndex;
    }

    public double getAnger() {
        return anger;
    }

    public void setAnger(double anger) {
        this.anger = anger;
    }

    public double getContempt() {
        return contempt;
    }

    public void setContempt(double contempt) {
        this.contempt = contempt;
    }

    public double getDisgust() {
        return disgust;
    }

    public void setDisgust(double disgust) {
        this.disgust = disgust;
    }

    public double getFear() {
        return fear;
    }

    public void setFear(double fear) {
        this.fear = fear;
    }

    public double getHappiness() {
        return happiness;
    }

    public void setHappiness(double happiness) {
        this.happiness = happiness;
    }

    public double getNeutral() {
        return neutral;
    }

    public void setNeutral(double neutral) {
        this.neutral = neutral;
    }

    public double getSadness() {
        return sadness;
    }

    public void setSadness(double sadness) {
        this.sadness = sadness;
    }

    public double getSurprise() {
        return surprise;
    }

    public void setSurprise(double surprise) {
        this.surprise = surprise;
    }

    public double getScoreForCategoryId(int id){

        switch(id){
            case 0 : return getAnger();
            case 1 : return getContempt();
            case 2 : return getDisgust();
            case 3 : return getFear();
            case 4 : return getHappiness();
            case 5 : return getNeutral();
            case 6 : return getSadness();
            case 7 : return getSurprise();
            default: return 0d;
        }

    }

    public double getCurrentCategoryScore(){
        return getScoreForCategoryId(getCategoryIndex());
    }
}
