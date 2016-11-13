package com.pietrantuono.accountmanager;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com
 */
public interface Api {
    @GET("oauth2/v3/userinfo")
    Call<Object> getUserInfo(@Header("Authorization") String token);

    @FormUrlEncoded
    @POST("oauth2/v3/token")
    Call<Object> getToken(@Field("refresh_token") String refresh_token, @Field("client_id") String client_id,@Field("grant_type") String grant_type );


}
