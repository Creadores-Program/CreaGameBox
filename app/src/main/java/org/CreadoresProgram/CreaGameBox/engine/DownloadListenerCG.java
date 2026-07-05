package org.CreadoresProgram.CreaGameBox.engine;

import android.util.Base64;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.content.Context;
import android.content.Intent;
import android.content.DialogInterface;
import android.app.AlertDialog;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.TlsVersion;
import okhttp3.ConnectionSpec;

public class DownloadListenerCG implements DownloadListener{
    private Context context;
    private boolean isMarket;
    private ChromeClient chclient;
    private OkHttpClient clientHt = new OkHttpClient.Builder()
        .connectionSpecs(Arrays.asList(
            new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_3, TlsVersion.TLS_1_2)
                .build(),
            ConnectionSpec.COMPATIBLE_TLS
        ))
        .build();
    public DownloadListenerCG(Context context, ChromeClient chclient, boolean isMarket){
        this.context = context;
        this.chclient = chclient;
        this.isMarket = isMarket;
    }
    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
        if(url.startsWith("http://") || !(url.startsWith("https://") || url.startsWith("data:"))){
            return;
        }
        new Thread(()-> downloadQuery(url, mimeType, contentDisposition)).start();
    }
    private void downloadQuery(String url, String mimeType, String contentDisposition){
        try{
            String nameFile = URLUtil.guessFileName(url, contentDisposition, mimeType);
            final CountDownLatch latch = new CountDownLatch(1);
            final boolean[] result = new boolean[1];
            new AlertDialog.Builder(context, chclient.getTheme())
                .setTitle("Download File")
                .setMessage("Download "+ nameFile + "?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(){
                        result[0] = true;
                        latch.countDown();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(){
                        result[0] = false;
                        latch.countDown();
                    }
                })
                .setCancelable(false)
                .create().show();
            latch.await();
            if(!result[0]){
                return;
            }
            byte[] decodedBytes = new byte[0];
            if(url.startsWith("https://")){
                Request request = new Request.Builder()
                    .url(url)
                    .build();
                Response response = null;
                try{
                    response = clientHt.newCall(request).execute();
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                    decodedBytes = response.body().bytes();
                }finally{
                    if(response != null) response.close();
                }
            }else{
                String parts = url.split(",");
                if(parts.length < 2){
                    return;
                }
                String head = parts[0];
                String baseData = parts[1];
                decodedBytes = Base64.decode(baseData, Base64.DEFAULT);
                /*String extension = "bin";
                if(mimeType != null && !mimeType.isEmpty()){
                    extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
                }else{
                    try {
                        String mimeExtract = head.substring(head.indexOf(":") + 1, head.indexOf(";"));
                        extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeExtract);
                    } catch (Exception e) {
                        extension = "bin";
                        e.printStackTrace();
                    }
                }
                nameFile = "File_" + System.currentTimeMillis() + "." + extension;*/
            }
            File fileTarget = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), nameFile);
            java.io.FileOutputStream fos = new java.io.FileOutputStream(fileTarget);
            fos.write(decodedBytes);
            fos.flush();
            fos.close();
            if(isMarket){
                if(nameFile.endsWith(".apk")){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(fileTarget), "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else if(nameFile.endsWith(".cgba")){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(fileTarget), "application/x-creagamebox-app");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}