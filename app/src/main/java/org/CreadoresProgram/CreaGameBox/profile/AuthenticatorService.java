package org.CreadoresProgram.CreaGameBox.profile;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticatorService extends Service {
    
    private CGBAuthenticator authenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        authenticator = new CGBAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}