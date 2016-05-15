package com.example.facetest.di2;

import android.app.Application;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Benjamin on 15/05/16.
 */
@Module
public class ApplicationModule {

    private static Application sApplication;

    public ApplicationModule(Application application) {
        sApplication = application;
    }

    @Provides
    Application providesApplication(){
        return sApplication;
    }

}
