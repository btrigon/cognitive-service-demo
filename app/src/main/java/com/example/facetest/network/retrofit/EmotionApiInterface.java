package com.example.facetest.network.retrofit;

import com.example.facetest.network.apimodels.FaceEmotion;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Benjamin on 15/05/16.
 */
public interface EmotionApiInterface {

    // Send photo
    @Headers({
            "Content-Type: application/octet-stream",
            "Content-Type: charset=utf-8"
    })
    @POST("recognize")
    Call<List<FaceEmotion>> sendPhoto(
            @Header("Ocp-Apim-Subscription-Key") String apiKey,
            @Body RequestBody image
    );


}
