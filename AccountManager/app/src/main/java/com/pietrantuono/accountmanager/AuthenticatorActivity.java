package com.pietrantuono.accountmanager;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com
 */
public class AuthenticatorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(AuthenticatorActivity.this);
        textView.setText("FFFFFFF");
        setContentView(textView);
        authenticate();
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
        String action = "com.google.codelabs.appauth.HANDLE_AUTHORIZATION_RESPONSE";
        Intent postAuthorizationIntent = new Intent(action);
        PendingIntent pendingIntent = PendingIntent.getActivity(AuthenticatorActivity.this, request.hashCode(), postAuthorizationIntent, 0);
        authorizationService.performAuthorizationRequest(request, pendingIntent);
    }
}
