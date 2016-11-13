package com.pietrantuono.accountmanager.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserResponse {

    @SerializedName("sub")
    @Expose
    private String sub;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("given_name")
    @Expose
    private String given_name;

    @SerializedName("family_name")
    @Expose
    private String family_name;

    @SerializedName("profile")
    @Expose
    private String profile;

    @SerializedName("picture")
    @Expose
    private String picture;

    @SerializedName("gender")
    @Expose
    private String gender;

    @SerializedName("locale")
    @Expose
    private String locale;

    public String getFamily_name() {
        return family_name;
    }

    public String getGender() {
        return gender;
    }

    public String getGiven_name() {
        return given_name;
    }

    public String getLocale() {
        return locale;
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }

    public String getProfile() {
        return profile;
    }

    public String getSub() {
        return sub;
    }

    @Override
    public String toString() {
        return
                "name='" + name + '\'' +
                 ", given_name='" + given_name + '\'' +
                 "family_name='" + family_name + '\'' +
                 ", sub='" + sub + '\'' +
                 ", profile='" + profile + '\'' +
                 ", picture='" + picture + '\'' +
                 ", gender='" + gender + '\'' +
                 ", locale='" + locale + ' ';
    }
}
