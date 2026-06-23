package org.CreadoresProgram.CreaGameBox.profile;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.webkit.JavascriptInterface;

import org.json.JSONArray;

import java.util.ArrayList;

public class AccountManagerJS{
    private Context context;
    private AccountManager accountManager;
    private Account[] currentAccounts;
    private ArrayList<Account> onlineAccounts;
    public AccountManagerJS(Context context, ArrayList<Account> onlineAccounts) {
        this.context = context;
        this.accountManager = AccountManager.get(context);
        this.onlineAccounts = onlineAccounts;
    }
    @JavascriptInterface
    public String getAccounts() {
        currentAccounts = accountManager.getAccountsByType("com.creagamebox.account");
        JSONArray jsonArray = new JSONArray();
        for (Account account : currentAccounts) {
            jsonArray.put(account.name);
        }
        return jsonArray.toString();
    }
    @JavascriptInterface
    public void setUserData(int index, String key, String value) {
        if (currentAccounts != null && index >= 0 && index < currentAccounts.length) {
            accountManager.setUserData(currentAccounts[index], key, value);
        }
    }
    @JavascriptInterface
    public String getUserData(int index, String key) {
        if (currentAccounts != null && index >= 0 && index < currentAccounts.length) {
            String data = accountManager.getUserData(currentAccounts[index], key);
            return data != null ? data : "";
        }
        return "";
    }
    @JavascriptInterface
    public void addOnlineAccount(int index){
        this.onlineAccounts.add(currentAccounts[index]);
        //call update in apps open or home
    }
}
