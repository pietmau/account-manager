package com.pietrantuono.accountmanager.authenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;


/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com
 */

public class Authenticator extends AbstractAccountAuthenticator {
    public static final String ACCOUNT_TYPE = "pirean";
    public static final String REFRESH_TOKEN_TYPE = "refresh_token_type";
    public static final String ACCOUNT_NAME = "user";
    public static final String PW = "password";
    public static final String SHARED_PREFERENCES_NAME = "account_prefs";
    public static final String AUTH_STATE = "auth_state";
    private final Context context;

    public Authenticator(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Intent intent = new Intent(context, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        AccountManager accountManager = AccountManager.get(context);
        String refreshToken = accountManager.peekAuthToken(account, authTokenType);
        if (!TextUtils.isEmpty(refreshToken)) {
            Bundle result = new Bundle();
            result.putString(AccountManager.KEY_AUTHTOKEN, refreshToken);
            result.putString(AccountManager.KEY_ACCOUNT_NAME, ACCOUNT_NAME);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
            return result;
        } else {
            refreshToken = getRefreshToken();
            if (refreshToken != null) {
                accountManager.setAuthToken(account, Authenticator.REFRESH_TOKEN_TYPE, refreshToken);
            }
            Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, ACCOUNT_NAME);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
            result.putString(AccountManager.KEY_AUTHTOKEN, refreshToken);
            return result;
        }
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
        return null;
    }

    @Nullable
    private String getRefreshToken() {
        String token = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .getString(AUTH_STATE, null);
        return token;
    }

}
