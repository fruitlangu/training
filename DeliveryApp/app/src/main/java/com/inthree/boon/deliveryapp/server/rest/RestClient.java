/*
 * @category Learning Space
 * @copyright Copyright (C) 2017 Contus. All rights reserved.
 * @license http://www.apache.org/licenses/LICENSE-2.0
 */

package com.inthree.boon.deliveryapp.server.rest;



import com.inthree.boon.deliveryapp.server.LearningSpaceServices;
import com.inthree.boon.deliveryapp.server.ServerUrls;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * This is the Rest client class used to access the api request and response.
 *
 * @author Contus Team <developers@contus.in>
 * @version 1.0
 */
public class RestClient {

    /**
     * This interface is used to access the url methods.
     */
    private LearningSpaceServices learningSpaceServices;

    /**
     * This method is used to set the rest adapter for retrofit with the time out session and
     * headers for the api call.
     */
    public RestClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.addInterceptor(interceptor)
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .addNetworkInterceptor(new SessionRequestInterceptor())
                .build();

        /**
         * Adding Base url to the retrofit.
         */
        Retrofit restAdapter = getRetrofit(client);

        learningSpaceServices = restAdapter.create(LearningSpaceServices.class);
    }

    /**
     * Method used to get the Retrofit http Client
     *
     * @param httpClient OkHttpClient.Builder
     * @return Retrofit httpClient
     */
    public static Retrofit getRetrofit(OkHttpClient.Builder httpClient) {
        return new Retrofit.Builder()
                .baseUrl(ServerUrls.BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * This is the Learning Space service interface used to get the service callback methods
     *
     * @return LearningSpaceServices
     */
    public LearningSpaceServices getLearningSpaceService() {
        return learningSpaceServices;
    }

    public static Retrofit getRetroService() {
        return new Retrofit.Builder()
                .baseUrl(ServerUrls.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}