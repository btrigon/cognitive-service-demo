package com.example.facetest.ui.groupphoto;

import com.example.facetest.network.apimodels.FaceEmotion;

import java.util.List;

/**
 * Created by Benjamin on 15/05/16.
 */
public interface  GroupPhotoView {


    void showLoading();

    void showError();

    void updateUiOnSending();

    void updateUiOnSentSuccess();

    void updateUiOnSentFailed(String errorMessage);

    void setResultsText(String resultsText);

    void animateResults(List<FaceEmotion> faceList, int categoryId);

    void setTopTenTitle(int count, String categoryName);

    void setSendButtonText(String text);

    void setTopTenIcon(int resourceId);



}
