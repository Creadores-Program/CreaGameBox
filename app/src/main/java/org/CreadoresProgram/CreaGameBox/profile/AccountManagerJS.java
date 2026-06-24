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
        if(!alrRemTemp){
            removeTempAccounts();
        }
        JSONArray jsonArray = new JSONArray();
        for (Account account : accountManager.getAccountsByType("com.creagamebox.account")) {
            jsonArray.put(account.name);
        }
        return jsonArray.toString();
    }
    @JavascriptInterface
    public void setUserData(String name, String key, String value) {
        if (currentAccounts != null && index >= 0 && index < currentAccounts.length) {
            accountManager.setUserData(currentAccounts[index], key, value);
        }
    }
    @JavascriptInterface
    public String getUserData(String name, String key) {
        if (currentAccounts != null && index >= 0 && index < currentAccounts.length) {
            String data = accountManager.getUserData(currentAccounts[index], key);
            return data != null ? data : "";
        }
        return "";
    }
    @JavascriptInterface
    public void addOnlineAccount(String name, int controllerId){
        this.onlineAccounts.add(currentAccounts[index]);
    }
    @JavascriptInterface
    public void removeOnlineAccount(String name){
        this.onlineAccounts.remove();
    }
    @JavascriptInterface
    public void createAccount(String name, int controllerId){
        //delete account temp by controllerId
        Account profile = new Account(name, "com.creagamebox.account");
        accountManager.addAccountExplicitly(profile, null, null);
        accountManager.setUserData(profile, "controllerId", String.valueOf(controllerId));
    }
    @JavascriptInterface
    public void deleteAccount(String name){
        Account profile = getProfileByName(name);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            accountManager.removeAccount(profile, null, null, null);
        }else{
            accountManager.removeAccount(profile, null, null);
        }
    }
    private Account getProfileOnlineByName(String name){
        for(Account profile : this.onlineAccounts){
            if(profile.name.equals(name)){
                return profile;
            }
        }
        return null;
    }
    private Account getProfileByName(String name){
        for(Account profile : accountManager.getAccountsByType("com.creagamebox.account")){
            if(profile.name.equals(name)){
                return profile;
            }
        }
        return null;
    }
}
