package org.CreadoresProgram.CreaGameBox.profile;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.webkit.JavascriptInterface;
import android.os.Build;

import org.json.JSONArray;

import java.util.ArrayList;

public class AccountManagerJS{
    private Context context;
    private AccountManager accountManager;
    private Account[] currentAccounts;
    private ArrayList<Account> onlineAccounts;
    private boolean alrRemTemp = false;
    public AccountManagerJS(Context context, ArrayList<Account> onlineAccounts) {
        this.context = context;
        this.accountManager = AccountManager.get(context);
        this.onlineAccounts = onlineAccounts;
    }
    private void removeTempAccounts(){
        if(alrRemTemp){
            return;
        }
        this.alrRemTemp = true;
        for(Account profile : this.currentAccounts){
            if(accountManager.getUserData(profile, "isTemporary") != null && accountManager.getUserData(profile, "isTemporary").equals("true")){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    accountManager.removeAccount(profile, null, null, null);
                }else{
                    accountManager.removeAccount(profile, null, null);
                }
            }
        }
    }
    @JavascriptInterface
    public String getAccounts() {
        currentAccounts = accountManager.getAccountsByType("com.creagamebox.account");
        if(!alrRemTemp){
            removeTempAccounts();
        }
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
    public void addOnlineAccount(int index, int controllerId){
        this.onlineAccounts.add(currentAccounts[index]);
        //call update in apps open or home
    }
    @JavascriptInterface
    public void createAccount(String name, int controllerId){
        //delete account temp by controllerId
        Account profile = new Account(name, "com.creagamebox.account");
        accountManager.addAccountExplicitly(profile, null, null);
        accountManager.setUserData(profile, "controllerId", String.valueOf(controllerId));
    }
    @JavascriptInterface
    public void deleteAccount(int index){
        Account profile = currentAccounts[index];
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            accountManager.removeAccount(profile, null, null, null);
        }else{
            accountManager.removeAccount(profile, null, null);
        }
    }
}
