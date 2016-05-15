package com.example.facetest.network.apimodels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Benjamin on 15/05/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FaceRectangle {

    public FaceRectangle() {
    }

    @JsonProperty
    private int height;
    @JsonProperty
    private int left;
    @JsonProperty
    private int top;
    @JsonProperty
    private int width;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
