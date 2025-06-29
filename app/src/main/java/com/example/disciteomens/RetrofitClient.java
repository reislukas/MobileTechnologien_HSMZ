package com.example.disciteomens;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//mockapi+retrofit
public class RetrofitClient {
    private static final String BASE_URL = "https://685fe62bc55df675589f926e.mockapi.io/";
    private static Retrofit retrofit = null;

    public static DisciteOmnesApi getApi() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(DisciteOmnesApi.class);
    }
}
