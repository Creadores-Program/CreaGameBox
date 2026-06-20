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
    public void setUserData(int index, String key, String value) {
        if (index >= 0 && index < onlineAccounts.size()) {
            Account accountTarget = onlineAccounts.get(index);
            String keysandbox = appUUID + "_" + key;
            accountManager.setUserData(accountTarget, keysandbox, value);
        }
    }
    @JavascriptInterface
    public String getUserData(int index, String key) {
        if (index >= 0 && index < onlineAccounts.size()) {
            Account accountTarget = onlineAccounts.get(index);
            String keysandbox = appUUID + "_" + key;
            String data = accountManager.getUserData(accountTarget, keysandbox);
            return data != null ? data : "";
        }
        return "";
    }
}
