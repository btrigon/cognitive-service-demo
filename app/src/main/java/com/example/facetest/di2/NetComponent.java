package com.example.facetest.di2;

import android.content.SharedPreferences;

import com.example.facetest.network.retrofit.EmotionApiInterface;
import com.example.facetest.rx.RxBus;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Benjamin on 15/05/16.
 */
@Singleton
@Component(modules = {NetModule.class}, dependencies = ApplicationComponent.class)
public interface NetComponent {

    @Named("uncachedEmotionApi")
    EmotionApiInterface getUncachedApiEndpointInterface();

    RxBus getRxBus();

    SharedPreferences getSharedPreferences();

}
