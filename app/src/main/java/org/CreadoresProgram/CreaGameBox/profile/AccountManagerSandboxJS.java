package org.CreadoresProgram.CreaGameBox.profile;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.webkit.JavascriptInterface;
import org.json.JSONArray;
import java.util.ArrayList;
public class AccountManagerSandboxJS {
    private Context context;
    private AccountManager accountManager;
    private String appUUID;
    private ArrayList<Account> onlineAccounts;
    public AccountManagerSandboxJS(Context context, String appUUID, ArrayList<Account> onlineAccounts) {
        this.context = context;
        this.accountManager = AccountManager.get(context);
        this.appUUID = appUUID;
        this.onlineAccounts = onlineAccounts;
    }
    @JavascriptInterface
    public String getAccounts() {
        JSONArray jsonArray = new JSONArray();
        for (Account account : onlineAccounts) {
            jsonArray.put(account.name);
        }
        return jsonArray.toString();
    }
    @JavascriptInterface
    public void setUserData(String name, String key, String value) {
        Account profile = getProfileOnlineByName(name);
        if (profile == null) {
            return;
        }
        String keysandbox = appUUID + "_" + key;
        accountManager.setUserData(profile, keysandbox, value);
    }
    @JavascriptInterface
    public String getUserData(String name, String key) {
        Account profile = getProfileOnlineByName(name);
        if (profile == null) {
            return "";
        }
        String keysandbox = appUUID + "_" + key;
        String data = accountManager.getUserData(profile, keysandbox);
        return data != null ? data : "";
    }
    @JavascriptInterface
    public String getUserControllerId(String name){
        Account profile = getProfileOnlineByName(name);
        if(profile == null){
            return "";
        }
        String data = accountManager.getUserData(profile, "controllerId");
    }
    private Account getProfileOnlineByName(String name){
        for(Account profile : this.onlineAccounts){
            if(profile.name.equals(name)){
                return profile;
            }
        }
        return null;
    }
}
