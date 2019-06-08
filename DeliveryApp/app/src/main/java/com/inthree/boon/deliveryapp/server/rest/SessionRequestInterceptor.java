/*
 * @category Learning Space
 * @copyright Copyright (C) 2017 Contus. All rights reserved.
 * @license http://www.apache.org/licenses/LICENSE-2.0
 */

package com.inthree.boon.deliveryapp.server.rest;


import com.inthree.boon.deliveryapp.app.AppController;
import com.inthree.boon.deliveryapp.app.Constants;

import java.io.IOException;

import okhttp3.Request;

/**
 * This is the SessionRequestInterceptor class used to access the api Header.
 *
 * @author Contus Team <developers@contus.in>
 * @version 1.0
 */
public class SessionRequestInterceptor implements okhttp3.Interceptor {

    @Override
    public okhttp3.Response intercept(Chain chain) throws IOException {
        String accessToken = AppController.getStringPreference(Constants.USER_LOGIN_ACCESS_TOKEN,
                "");
        String userId = String.valueOf(AppController.getIntegerPreferences(Constants.USER_ID, 0));
        Request.Builder builder = chain.request().newBuilder();
        builder.addHeader(Constants.ApiHeaders.LEARNINGSPACE_MOBILE_APP, Constants.MOBILE_HEADER);
        builder.addHeader(Constants.ApiHeaders.LEARNING_SPACE_USER_ID, userId);
        builder.addHeader(Constants.ApiHeaders.LEARNING_SPACE_ACCESS_TOKEN, accessToken);
        return chain.proceed(builder.build());
    }
}