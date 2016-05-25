package com.example.facetest.ui.groupphoto;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.facetest.FaceTestApplication;
import com.example.facetest.di2.DaggerNetInjectorComponent;
import com.example.facetest.models.CategoryItem;
import com.example.facetest.network.apimodels.FaceEmotion;
import com.example.facetest.network.retrofit.EmotionApiInterface;
import com.example.facetest.presenter.BasePresenter;
import com.example.facetest.utils.EmotionComporators;
import com.example.facetest.utils.FileUtils;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Benjamin on 15/05/16.
 */
public class GroupPhotoPresenter extends BasePresenter<List<FaceEmotion>, GroupPhotoView> {
    private static final String TAG = "GroupPhotoPresenter";
    private boolean isLoadingData = false;
    private Observable<List<FaceEmotion>> mSendPhotoCall;
    private CategoryItem mCategoryItem;

    @Inject
    @Named("uncachedEmotionApi")
    EmotionApiInterface mEmotionApi;

    public GroupPhotoPresenter() {
        DaggerNetInjectorComponent.builder()
                .netComponent(FaceTestApplication.getNetComponent())
                .build()
                .inject(this);
    }

    @Override
    protected void updateView() {
        // data and view loaded, setup display

    }

    @Override
    public void bindView(@NonNull GroupPhotoView view) {
        super.bindView(view);
        if(model==null && isLoadingData){
            view.showLoading();
        }
        if(mCategoryItem!=null ){
            view.setSendButtonText(mCategoryItem.getCategoryName());
            view.setTopTenIcon(mCategoryItem.getResourceId());
            view.setTopTenTitle(10, mCategoryItem.getCategoryName());
        }
    }

    //TODO: not in Presentation layer, move to Domain
    private void submitPhoto(String API_KEY, RequestBody requestBody){
        mSendPhotoCall = mEmotionApi.sendPhoto(API_KEY, requestBody);
        isLoadingData = true;
        Subscription subscription = mSendPhotoCall
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<FaceEmotion>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoadingData = false;
                        view().updateUiOnSentFailed("submission failed: " + e.getMessage());
                    }

                    @Override
                    public void onNext(List<FaceEmotion> faceEmotions) {
                        isLoadingData = false;
                        view().updateUiOnSentSuccess();
                        setModel(faceEmotions);
                        if (model == null || model.size() == 0) {
                            view().updateUiOnSentFailed("no faces found, try again");
                        } else {
                            for (FaceEmotion faceEmotion : model) {
                                faceEmotion.getScores().setCategoryIndex(mCategoryItem.getCategoryId()); // set each score category
                            }
                            Collections.sort(model, new EmotionComporators.CurrentCategoryComparator());
                            Collections.reverse(model);
                            view().animateResults(model, mCategoryItem.getCategoryId());
                        }
                    }
                });
    }

    public void setCategory(CategoryItem categoryItem){
        mCategoryItem = categoryItem;

        if(view()==null)return;

        view().setSendButtonText(categoryItem.getCategoryName());
        view().setTopTenIcon(categoryItem.getResourceId());
    }


    // It's OK for this class not to be static and to keep a reference to the Presenter, as this
    // is retained during orientation changes and is lightweight (has no activity/view reference)
    private class BinaryConverterTask extends AsyncTask<Integer, Void, byte[]> {
        Bitmap bitmap;
        String apikey;
        int categoryId;

        public BinaryConverterTask(Bitmap bitmap, String apikey, int categoryId) {
            this.bitmap = bitmap;
            this.apikey = apikey;
            this.categoryId = categoryId;
        }

        // Decode image in background.
        @Override
        protected byte[] doInBackground(Integer... params) {
            return FileUtils.toBinary(bitmap);
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            createPhotoCall(bytes, apikey);
        }
    }

    private void createPhotoCall(byte[] data, String API_KEY ){
        RequestBody requestBody = RequestBody
                .create(MediaType.parse("application/octet-stream"), data);
        if(mEmotionApi!=null) {
            submitPhoto(API_KEY, requestBody);
        }else{
            Log.e(TAG, "mEmotionApi was null");
        }

    }

    public void sendBitmap(Bitmap bitmap, String apikey){
        if (bitmap != null) {
            BinaryConverterTask task = new BinaryConverterTask(bitmap, apikey, mCategoryItem.getCategoryId());
            task.execute();
        }
    }


}
