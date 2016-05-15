package com.example.facetest.di2;

import com.example.facetest.ui.categoryselect.CategorySelectPresenter;
import com.example.facetest.ui.groupphoto.GroupPhotoFragment;
import com.example.facetest.ui.groupphoto.GroupPhotoPresenter;

import dagger.Component;

/**
 * Created by Benjamin on 15/05/16.
 */
@PerApp
@Component(dependencies = {NetComponent.class})
public interface NetInjectorComponent {

    void inject (GroupPhotoFragment groupPhotoFragment);
    void inject (GroupPhotoPresenter groupPhotoPresenter);
    void inject (CategorySelectPresenter categorySelectPresenter);

}
