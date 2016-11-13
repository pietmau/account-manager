package com.pietrantuono.accountmanager;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com
 */
public interface Api {
    @GET("oauth2/v3/userinfo")
    Call<Object> getUserInfo(@Header("Authorization") String token);
}
