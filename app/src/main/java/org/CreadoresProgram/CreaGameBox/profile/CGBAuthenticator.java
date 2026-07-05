package org.CreadoresProgram.CreaGameBox.profile;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.Bundle;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.CreadoresProgram.CreaGameBox.MainActivity;

public class CGBAuthenticator extends AbstractAccountAuthenticator {

    private final Context context;

    public CGBAuthenticator(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) {
        final Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, android.accounts.Account account, Bundle options) { return null; }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) { return null; }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, android.accounts.Account account, String authTokenType, Bundle options) { return null; }

    @Override
    public String getAuthTokenLabel(String authTokenType) { return null; }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, android.accounts.Account account, String[] features) { return null; }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, android.accounts.Account account, String authTokenType, Bundle options) { return null; }
}