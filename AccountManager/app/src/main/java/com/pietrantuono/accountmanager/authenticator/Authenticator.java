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
    public static final String AUTH_TYPE = "auth_type";
    public static final String ACCOUNT_NAME = "user";
    public static final String PASSWORD = "password";
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
        final Intent intent = new Intent(context, AuthenticatorActivity.class);
//        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, accountType);
//        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
//        intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(context);

        String authToken = am.peekAuthToken(account, authTokenType);

        // Lets give another try to authenticate the user
//        if (TextUtils.isEmpty(authToken)) {
//            final String password = am.getPassword(account);
//            if (password != null) {
//                authToken = authenticatorService.userSignIn(account.name, password, authTokenType);
//            }
//        }

        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            //result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            //result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            result.putString(AccountManager.KEY_ACCOUNT_NAME, ACCOUNT_NAME);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
            return result;
        }
        else {
            authToken = restoreAuthState();
            if(authToken!=null){
                am.setAuthToken(account, Authenticator.AUTH_TYPE, authToken);
            }
            final Bundle result = new Bundle();
            //result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            //result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_ACCOUNT_NAME, ACCOUNT_NAME);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }
//
//        // If we get here, then we couldn't access the user's password - so we
//        // need to re-prompt them for their credentials. We do that by creating
//        // an intent to display our AuthenticatorActivity.
//        final Intent intent = new Intent(context, AuthenticatorActivity.class);
//        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
////        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, account.type);
////        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
//        final Bundle bundle = new Bundle();
//        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
//        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
//        if (AUTHTOKEN_TYPE_FULL_ACCESS.equals(authTokenType))
//            return AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
//        else if (AUTHTOKEN_TYPE_READ_ONLY.equals(authTokenType))
//            return AUTHTOKEN_TYPE_READ_ONLY_LABEL;
//        else
//            return authTokenType + " (Label)";
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
    private String restoreAuthState() {
        String jsonString = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .getString(AUTH_STATE, null);

        return jsonString;
    }

}