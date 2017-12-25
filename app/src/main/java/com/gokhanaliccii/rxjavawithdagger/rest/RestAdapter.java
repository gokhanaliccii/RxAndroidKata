package com.gokhanaliccii.rxjavawithdagger.rest;

import com.gokhanaliccii.rxjavawithdagger.di.RestClient;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gokhan on 12/11/17.
 */

public class RestAdapter {

    public static String BASE_URL = "http://gokhan.com";

    private static RestAdapter sRestAdapter;

    private Retrofit retrofit;
    private RestClient restClient;

    private RestAdapter(String baseUrl) {
        init(baseUrl);
    }

    public static RestAdapter getInstance() {
        return getInstance(BASE_URL);
    }

    public static RestAdapter getInstance(String baseUrl) {
        if (sRestAdapter == null)
            sRestAdapter = new RestAdapter(baseUrl);

        return sRestAdapter;
    }


    void init(String baseUrl) {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        restClient = retrofit.create(RestClient.class);
    }

    public RestClient restClient() {
        return restClient;
    }
}
