package com.pietrantuono.accountmanager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.pietrantuono.accountmanager.Authenticator.AUTH_TYPE;
import static com.pietrantuono.accountmanager.Authenticator.ACCOUNT_TYPE;

public class MainActivity extends AppCompatActivity {
    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountManager = AccountManager.get(MainActivity.this);
        setContentView(R.layout.activity_main);
        if (accountExists()) {
            getTokenAndLogin();
        } else {
            addAccountAndLogin();
        }
    }

    private void addAccountAndLogin() {
        accountManager.addAccount(ACCOUNT_TYPE, AUTH_TYPE, null, null, MainActivity.this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                try {
                    Bundle bar = accountManagerFuture.getResult();
                    String authtoken = bar.getString("Foo");
                    getAuthToken(authtoken);
                } catch (Exception ignored) {
                }
            }


        }, new Handler());
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
        Call call = service.getToken(refreshToken, "292906832055-ha4vjpa3oprvspeafobrjii7t26g2oe1.apps.googleusercontent.com","refresh_token");
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                foo();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                foo();
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
        Call call = service.getUserInfo(String.format("Bearer %s", authtoken));
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                populateUi(response);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
            }
        });
    }

    private void populateUi(Response response) {
        if(response == null || response.body() == null){return;}
        Object body = response.body();
        foo();
    }

    private void foo() {

    }

    private boolean accountExists() {
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        return accounts.length > 0;
    }


}
