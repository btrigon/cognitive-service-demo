package com.example.facetest.di2;

import android.app.Application;

import dagger.Component;

/**
 * Created by Benjamin on 15/05/16.
 */
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    //Exposes Application to any component which depends on this
    Application getApplication();
}
