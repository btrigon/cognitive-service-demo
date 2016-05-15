package com.example.facetest.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Benjamin on 15/05/16.
 */
public class CategoryItem implements Parcelable {

    public CategoryItem() {
    }

    public CategoryItem(int categoryId, String categoryName, int resourceId) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.resourceId = resourceId;
    }

    private int categoryId;
    private String categoryName;
    private int resourceId;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    protected CategoryItem(Parcel in) {
        categoryId = in.readInt();
        categoryName = in.readString();
        resourceId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(categoryId);
        dest.writeString(categoryName);
        dest.writeInt(resourceId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CategoryItem> CREATOR = new Parcelable.Creator<CategoryItem>() {
        @Override
        public CategoryItem createFromParcel(Parcel in) {
            return new CategoryItem(in);
        }

        @Override
        public CategoryItem[] newArray(int size) {
            return new CategoryItem[size];
        }
    };
}
