package com.example.facetest.network.apimodels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Benjamin on 15/05/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FaceEmotion {

    public FaceEmotion() {
    }

    @JsonProperty
    private FaceRectangle faceRectangle;
    @JsonProperty
    private Scores scores;

    public FaceRectangle getFaceRectangle() {
        return faceRectangle;
    }

    public void setFaceRectangle(FaceRectangle faceRectangle) {
        this.faceRectangle = faceRectangle;
    }

    public Scores getScores() {
        return scores;
    }

    public void setScores(Scores scores) {
        this.scores = scores;
    }
}
