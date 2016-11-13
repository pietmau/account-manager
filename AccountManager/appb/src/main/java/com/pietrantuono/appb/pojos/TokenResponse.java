package com.pietrantuono.appb.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenResponse {

    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("token_type")
    @Expose
    private String tokenType;
    @SerializedName("expires_in")
    @Expose
    private Integer expiresIn;
    @SerializedName("id_token")
    @Expose
    private String idToken;

    /**
     * @return The accessToken
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @param accessToken The access_token
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * @return The tokenType
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     * @param tokenType The token_type
     */
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    /**
     * @return The expiresIn
     */
    public Integer getExpiresIn() {
        return expiresIn;
    }

    /**
     * @param expiresIn The expires_in
     */
    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    /**
     * @return The idToken
     */
    public String getIdToken() {
        return idToken;
    }

    /**
     * @param idToken The id_token
     */
    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

}
