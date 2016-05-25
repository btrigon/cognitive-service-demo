package com.example.facetest.di2;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.facetest.network.retrofit.EmotionApiInterface;
import com.example.facetest.rx.RxBus;


import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Benjamin on 15/05/16.
 */
@Module
public class NetModule {
    private static final int API_PROD = 0;
    private static final String[] API_LIST = {"https://api.projectoxford.ai/emotion/v1.0/" };

    private static final String API_BASE = API_LIST[API_PROD];

    private static final int CONNECT_TIMEOUT_SECONDS = 120;
    private static final int READ_TIMEOUT_SECONDS = 120;

    @Provides
    @Named("uncachedOkHttpClient")
    @Singleton
    OkHttpClient provideUncachedOkHttpClient(Application app) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        ArrayList<Protocol> protocols = new ArrayList<>();
        protocols.add(Protocol.HTTP_1_1);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .protocols(protocols)
                .followRedirects(true)
                .followSslRedirects(true)
                .addInterceptor(logging)
                .build();

        return client;
    }

    @Provides
    @Named("uncachedRetrofit")
    @Singleton
    Retrofit provideUncachedRetrofit(@Named("uncachedOkHttpClient") OkHttpClient okHttpClient){
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(API_BASE)
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    @Provides
    @Named("uncachedEmotionApi")
    @Singleton
    EmotionApiInterface providesUncachedApiEndpointInterface(@Named("uncachedRetrofit") Retrofit retrofit){
        EmotionApiInterface apiService;
        apiService = retrofit.create(EmotionApiInterface.class);
        return apiService;
    }

    @Provides
    @Singleton
    RxBus providesRxBus(){
        return new RxBus();
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }


}
