package com.example.facetest.ui.categoryselect;

import com.example.facetest.models.CategoryItem;

import java.util.List;

/**
 * Created by Benjamin on 15/05/16.
 */
public interface CategorySelectView {

    void showLoading();
    void showCategories(List<CategoryItem> categoryItemList);

}
