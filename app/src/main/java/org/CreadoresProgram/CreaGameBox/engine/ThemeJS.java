package org.CreadoresProgram.CreaGameBox.engine;

import android.webkit.JavascriptInterface;

public class ThemeJS {
    private ChromeClient chclient;
    public ThemeJS(ChromeClient chclient){
        this.chclient = chclient;
    }
    public void String setTheme(String theme){
        switch(theme.toLowerCase()){
            case "base":
                this.chclient.setTheme(android.R.style.Theme_Dialog);
                break;
            case "holo":
                this.chclient.setTheme(android.R.style.Theme_Holo_Dialog);
                break;
            case "holo_light":
                this.chclient.setTheme(android.R.style.Theme_Holo_Light_Dialog);
                break;
            case "holo_light_dark":
                this.chclient.setTheme(android.R.style.Theme_Holo_Light_DarkActionBar_Dialog);
        }
    }
}