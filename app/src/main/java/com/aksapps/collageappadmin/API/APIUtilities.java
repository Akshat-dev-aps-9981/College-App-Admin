package com.aksapps.collageappadmin.API;

import static com.aksapps.collageappadmin.Models.Constants.BASE_URL;
import static com.aksapps.collageappadmin.Models.Constants.BASE_URL;

import androidx.annotation.Keep;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class APIUtilities {
    private static Retrofit retrofit = null;

    public static APIInterface getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(APIInterface.class);
    }
}
