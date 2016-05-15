package com.example.facetest;

import android.app.Application;

import com.example.facetest.di2.ApplicationComponent;
import com.example.facetest.di2.ApplicationModule;
import com.example.facetest.di2.DaggerApplicationComponent;
import com.example.facetest.di2.DaggerNetComponent;
import com.example.facetest.di2.NetComponent;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Benjamin on 15/05/16.
 */
public class FaceTestApplication extends Application {

    private static NetComponent sNetComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/thaisansneue-regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        ApplicationComponent applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        sNetComponent = DaggerNetComponent.builder()
                .applicationComponent(applicationComponent)
                .build();
    }

    public static NetComponent getNetComponent(){
        return sNetComponent;
    }
}
