package com.pietrantuono.appb;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.pietrantuono.appb.api.Api;
import com.pietrantuono.appb.pojos.TokenResponse;
import com.pietrantuono.appb.pojos.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    private AccountManager accountManager;
    public static final String ACCOUNT_TYPE = "pirean";
    public static final String AUTH_TYPE = "auth_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountManager = AccountManager.get(MainActivity.this);
        setContentView(R.layout.activity_main);
        if (accountExists()) {
            getTokenAndLogin();
        }
    }

    private void getAuthToken(String refreshToken) {
        if (refreshToken == null) {
            return;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api service = retrofit.create(Api.class);
        Call<TokenResponse> call = service.getNewAuthToken(refreshToken, "292906832055-ha4vjpa3oprvspeafobrjii7t26g2oe1.apps.googleusercontent.com", "refresh_token");
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
        accountManager.getAuthToken(curr, AUTH_TYPE, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                try {
                    Bundle bnd = accountManagerFuture.getResult();
                    String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
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
                .baseUrl("https://www.googleapis.com/")
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

    private void foo() {

    }

    private boolean accountExists() {
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        return accounts.length > 0;
    }
}
