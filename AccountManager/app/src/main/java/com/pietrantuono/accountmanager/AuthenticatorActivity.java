package com.pietrantuono.accountmanager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.TokenResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com
 */
public class AuthenticatorActivity extends AppCompatActivity {
    private static final String USED_INTENT = "USED_INTENT";
    public static final String SHARED_PREFERENCES_NAME = "prefs";
    public static final String AUTH_STATE = "auth";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(AuthenticatorActivity.this);
        textView.setText("FFFFFFF");
        setContentView(textView);
        if (!intentContanisAuth(getIntent())) {
            authenticate();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        foo();
    }

    private void foo() {

    }

    private void authenticate() {
        AuthorizationServiceConfiguration serviceConfiguration = new AuthorizationServiceConfiguration(
                Uri.parse("https://accounts.google.com/o/oauth2/v2/auth") /* auth endpoint */,
                Uri.parse("https://www.googleapis.com/oauth2/v4/token") /* token endpoint */
        );
        AuthorizationService authorizationService = new AuthorizationService(AuthenticatorActivity.this);
        String clientId = "292906832055-ha4vjpa3oprvspeafobrjii7t26g2oe1.apps.googleusercontent.com";
        Uri redirectUri = Uri.parse("com.pietrantuono.accountmanager:/oauth2callback");
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
                serviceConfiguration,
                clientId,
                AuthorizationRequest.RESPONSE_TYPE_CODE,
                redirectUri
        );

        builder.setScopes("profile");
        Map<String, String> params = new HashMap<>();
        //params.put("approval_prompt","auto");
        //params.put("access_type","online");
        //builder.setAdditionalParameters(params);
//        if(mMainActivity.getLoginHint() != null){
//            Map loginHintMap = new HashMap<String, String>();
//            loginHintMap.put(LOGIN_HINT,mMainActivity.getLoginHint());
//            builder.setAdditionalParameters(loginHintMap);
//
//            Log.i(LOG_TAG, String.format("login_hint: %s", mMainActivity.getLoginHint()));
//        }

        AuthorizationRequest request = builder.build();
        String action = "com.pietrantuono.accountmanager.HANDLE_AUTHORIZATION_RESPONSE";
        Intent postAuthorizationIntent = new Intent(action);
        PendingIntent pendingIntent = PendingIntent.getActivity(AuthenticatorActivity.this, request.hashCode(), postAuthorizationIntent, 0);
        authorizationService.performAuthorizationRequest(request, pendingIntent);
    }

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

    private void handleAuthorizationResponse(@NonNull Intent intent) {
        AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
        AuthorizationException error = AuthorizationException.fromIntent(intent);
        final AuthState authState = new AuthState(response, error);
        if (response != null) {
            AuthorizationService service = new AuthorizationService(this);
            service.performTokenRequest(response.createTokenExchangeRequest(), new AuthorizationService.TokenResponseCallback() {
                @Override
                public void onTokenRequestCompleted(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException exception) {
                    if (exception != null) {
                    } else {
                        if (tokenResponse != null) {
                            authState.update(tokenResponse, exception);
                            persistAuthState(authState);
                        }
                    }
                }
            });
        }
    }
    private void persistAuthState(@NonNull AuthState authState) {
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
                .putString(AUTH_STATE, authState.toJsonString())
                .commit();
    }


}
