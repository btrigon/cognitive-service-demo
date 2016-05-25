package com.example.facetest.ui.categoryselect;

import android.support.annotation.NonNull;

import com.example.facetest.models.CategoryItem;
import com.example.facetest.network.retrofit.EmotionApiInterface;
import com.example.facetest.presenter.BasePresenter;
import com.example.facetest.ui.categoryselect.CategorySelectView;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by Benjamin on 15/05/16.
 */
public class CategorySelectPresenter extends BasePresenter<List<CategoryItem>, CategorySelectView> {
    private boolean isLoadingData = false;

    @Inject
    @Named("uncachedEmotionApi")
    EmotionApiInterface mEmotionApi;

    @Override
    protected void updateView() {
        // Business logic is in the presenter
//        if (model.size() == 0) {
//            view().showEmpty();
//        } else {
//            view().showCounters(model);
//        }
    }

    @Override
    public void bindView(@NonNull CategorySelectView view) {
        super.bindView(view);

        // Let's not reload data if it's already here
        if (model == null && !isLoadingData) {
            view().showLoading();
            loadData();
        }
    }

    private void loadData() {
        List<CategoryItem> categoryList;
        isLoadingData = true;

      //  setModel();
    }
}
