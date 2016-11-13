package com.pietrantuono.accountmanager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
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
                    foo();
                } catch (OperationCanceledException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (AuthenticatorException e) {
                    e.printStackTrace();
                }
            }


        }, new Handler());
    }

    private void foo() {

    }

    private void getTokenAndLogin() {
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        Account curr = accounts[0];
        accountManager.getAuthToken(curr, AUTH_TYPE, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                try {
                    Bundle bnd = accountManagerFuture.getResult();

                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    foo();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Handler());


    }

    private boolean accountExists() {
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        return accounts.length > 0;
    }


}
