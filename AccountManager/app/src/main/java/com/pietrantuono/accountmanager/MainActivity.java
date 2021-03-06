package com.pietrantuono.accountmanager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.pietrantuono.accountmanager.api.Api;
import com.pietrantuono.accountmanager.authenticator.AuthenticatorActivity;
import com.pietrantuono.accountmanager.pojos.TokenResponse;
import com.pietrantuono.accountmanager.pojos.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.pietrantuono.accountmanager.authenticator.Authenticator.REFRESH_TOKEN_TYPE;
import static com.pietrantuono.accountmanager.authenticator.Authenticator.ACCOUNT_TYPE;

public class MainActivity extends AppCompatActivity {
    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountManager = AccountManager.get(MainActivity.this);
        setContentView(R.layout.activity_main);
        if (accountExists()) {
            getTokenAndLogin();
        }
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAccountAndLogin();
            }
        });
    }

    private void addAccountAndLogin() {
        accountManager.addAccount(ACCOUNT_TYPE, REFRESH_TOKEN_TYPE, null, null, MainActivity.this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                try {
                    Bundle bundle = accountManagerFuture.getResult();
                    String refreshToken = bundle.getString(AuthenticatorActivity.REFRESH_TOKEN);
                    getAuthToken(refreshToken);
                } catch (Exception e) { e.printStackTrace();}
            }


        }, new Handler());
    }

    private void getAuthToken(String refreshToken) {
        if (refreshToken == null) {
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api service = retrofit.create(Api.class);
        Call<TokenResponse> call = service.getNewAuthToken(refreshToken, getString(R.string.client_id), "refresh_token");
        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response != null && response.body() != null && response.body().getAccessToken() != null) {
                    makeApiCall(response.body().getAccessToken());
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {

            }
        });
    }

    private void getTokenAndLogin() {
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        Account curr = accounts[0];
        accountManager.getAuthToken(curr, REFRESH_TOKEN_TYPE, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                try {
                    Bundle bundle = accountManagerFuture.getResult();
                    String authtoken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                    getAuthToken(authtoken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Handler());

    }

    private void makeApiCall(String authtoken) {
        if (authtoken == null) {
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api service = retrofit.create(Api.class);
        Call<UserResponse> call = service.getUserInfo(String.format("Bearer %s", authtoken));
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response != null && response.body() != null) {
                    populateUi(response.body());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {   }
        });
    }

    private void populateUi(UserResponse response) {
        findViewById(R.id.not_logged).setVisibility(View.INVISIBLE);
        findViewById(R.id.logged).setVisibility(View.VISIBLE);
        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText(response.toString());
        ImageView imageView = (ImageView) findViewById(R.id.image);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(MainActivity.this).build();
        ImageLoader.getInstance().init(config);
        ImageLoader.getInstance().displayImage(response.getPicture(), imageView);
    }

    private boolean accountExists() {
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        return accounts.length > 0;
    }
}
