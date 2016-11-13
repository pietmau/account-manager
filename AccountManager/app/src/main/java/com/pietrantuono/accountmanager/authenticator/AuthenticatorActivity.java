package com.pietrantuono.accountmanager.authenticator;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pietrantuono.accountmanager.R;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.TokenResponse;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity {
    private static final String USED_INTENT = "USED_INTENT";
    public static final String REFRESH_TOKEN = "refresh_token";
    private AccountManager accountManager;
    private Intent savedIntent;
    private AuthorizationService service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountManager = AccountManager.get(AuthenticatorActivity.this);
        if (!intentContanisAuth(getIntent())) {
            savedIntent = getIntent();
            login();
        }
    }

    /**
     * When we authenticate we receive the intent here
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        intentContanisAuth(intent);
    }

    private void login() {
        AuthorizationServiceConfiguration serviceConfiguration = new AuthorizationServiceConfiguration(
                Uri.parse(getString(R.string.auth_endpoint)) /* auth endpoint */,
                Uri.parse(getString(R.string.token_endpoint)) /* token endpoint */
        );
        AuthorizationService authorizationService = new AuthorizationService(AuthenticatorActivity.this);
        String clientId = getString(R.string.client_id);
        Uri redirectUri = Uri.parse(getString(R.string.redirect_uri));
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                serviceConfiguration,
                clientId,
                AuthorizationRequest.RESPONSE_TYPE_CODE,
                redirectUri
        );
        builder.setScopes(getString(R.string.PROFILE));
        AuthorizationRequest request = builder.build();
        String action = getString(R.string.ACTION_HANDLE_AUTHORIZATION_RESPONSE);
        Intent postAuthorizationIntent = new Intent(action);
        PendingIntent pendingIntent = PendingIntent.getActivity(AuthenticatorActivity.this, request.hashCode(), postAuthorizationIntent, 0);
        authorizationService.performAuthorizationRequest(request, pendingIntent);
    }

    /**
     * Check if it is an authentication intent, if it is we inform the Authenticator
     */
    private boolean intentContanisAuth(@Nullable Intent intent) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            switch (action) {
                case "com.pietrantuono.accountmanager.HANDLE_AUTHORIZATION_RESPONSE":
                    if (!intent.hasExtra(USED_INTENT)) {
                        handleAuthorizationResponse(intent);
                        intent.putExtra(USED_INTENT, true);
                    }
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    private void handleAuthorizationResponse(@NonNull final Intent intent) {
        AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
        AuthorizationException error = AuthorizationException.fromIntent(intent);
        final AuthState authState = new AuthState(response, error);
        if (response != null) {
            service = new AuthorizationService(this);
            service.performTokenRequest(response.createTokenExchangeRequest(), new AuthorizationService.TokenResponseCallback() {
                @Override
                public void onTokenRequestCompleted(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException exception) {
                    if (exception == null && tokenResponse != null) {
                        authState.update(tokenResponse, exception);
                        persistAuthState(authState);
                    }
                }
            });
        }
    }

    /** We persist the state and return response to the Authenticator */
    private void persistAuthState(@NonNull AuthState authState) {
        getSharedPreferences(Authenticator.SHARED_PREFERENCES_NAME, Context.MODE_MULTI_PROCESS).edit()
                .putString(Authenticator.AUTH_STATE, authState.getRefreshToken())
                .commit();
        Account account = new Account(Authenticator.ACCOUNT_NAME, Authenticator.ACCOUNT_TYPE);
        String refreshToken = authState.getRefreshToken();
        String tokenType = Authenticator.REFRESH_TOKEN_TYPE;
        accountManager.addAccountExplicitly(account, Authenticator.PW, null);
        accountManager.setAuthToken(account, tokenType, refreshToken);
        accountManager.setPassword(account, Authenticator.PW);
        savedIntent.putExtra(AccountManager.KEY_ACCOUNT_NAME, Authenticator.ACCOUNT_NAME);
        savedIntent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Authenticator.ACCOUNT_TYPE);
        savedIntent.putExtra(REFRESH_TOKEN, refreshToken);
        setAccountAuthenticatorResult(savedIntent.getExtras());
        setResult(RESULT_OK, savedIntent);
        service.dispose();
        finish();
    }
}
