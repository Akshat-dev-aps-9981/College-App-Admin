package com.aksapps.collageappadmin.API;

import static com.aksapps.collageappadmin.Models.Constants.CONTENT_TYPE;
import static com.aksapps.collageappadmin.Models.Constants.SERVER_KEY;

import androidx.annotation.Keep;

import com.aksapps.collageappadmin.Models.PushNotification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface APIInterface {
    @Headers({"Authorization: key="+ SERVER_KEY, "Contet-Type:" + CONTENT_TYPE})
    @POST("fcm/send")
    Call<PushNotification> sendNotification(@Body PushNotification notification);

}
