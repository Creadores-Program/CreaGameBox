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
            case "base_notitle":
                this.chclient.setTheme(android.R.style.Theme_Dialog_NoTitleBar);
                break;
            case "base_alert":
                this.chclient.setTheme(android.R.style.Theme_Dialog_Alert);
                break;
            case "holo":
                this.chclient.setTheme(android.R.style.Theme_Holo_Dialog);
                break;
            case "holo_notitle":
                this.chclient.setTheme(android.R.style.Theme_Holo_Dialog_NoTitleBar);
                break;
            case "holo_minwidth":
                this.chclient.setTheme(android.R.style.Theme_Holo_Dialog_MinWidth);
                break;
            case "holo_light":
                this.chclient.setTheme(android.R.style.Theme_Holo_Light_Dialog);
                break;
            case "holo_light_notitle":
                this.chclient.setTheme(android.R.style.Theme_Holo_Light_Dialog_NoTitleBar);
                break;
            case "holo_light_minwidth":
                this.chclient.setTheme(android.R.style.Theme_Holo_Light_Dialog_MinWidth);
                break;
            case "material":
                this.chclient.setTheme(android.R.style.Theme_Material_Dialog);
                break;
            case "material_notitle":
                this.chclient.setTheme(android.R.style.Theme_Material_Dialog_NoTitleBar);
                break;
            case "material_alert":
                this.chclient.setTheme(android.R.style.Theme_Material_Dialog_Alert);
                break;
            case "material_light":
                this.chclient.setTheme(android.R.style.Theme_Material_Light_Dialog);
                break;
            case "material_light_notitle":
                this.chclient.setTheme(android.R.style.Theme_Material_Light_Dialog_NoTitleBar);
                break;
            case "material_light_alert":
                this.chclient.setTheme(android.R.style.Theme_Material_Light_Dialog_Alert);
                break;
            case "devicedef":
                this.chclient.setTheme(android.R.style.Theme_DeviceDefault_Dialog);
                break;
            case "devicedef_alert":
                //code
            case "devicedef_light":
            case "devicedef_light_alert":
            default:
                this.chclient.setTheme(android.R.style.Theme_Holo_Light_Dialog);
                break;
        }
    }
}