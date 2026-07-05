package org.CreadoresProgram.CreaGameBox.apps;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.CreadoresProgram.CreaGameBox.R;

public class PackageInstaller extends Activity {

    private ProgressBar progressBar;
    private TextView txtProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_installer);
        
        progressBar = findViewById(R.id.progress_bar);
        txtProgress = findViewById(R.id.txt_progress);

        Intent intent = getIntent();
        final Uri fileUri = intent.getData();

        if (fileUri != null) {
            txtProgress.setText("Analizando paquete/Analyzing package...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    readAndPrompt(fileUri);
                }
            }).start();
        } else {
            Log.e("CGB_INSTALLER", "No se recibieron datos del paquete./No data was received from the package.");
            finish();
        }
    }

    private void processAndInstall(Uri uri, String uuid) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ZipInputStream zipStream = new ZipInputStream(new BufferedInputStream(inputStream));
            ZipEntry entry;

            File appsDir = new File(getFilesDir(), "apps/"+uuid);
            if (!appsDir.exists()) appsDir.mkdirs();

            byte[] buffer = new byte[8192];
            
            updateInterface("Instalando componentes/Installing components...", 0);

            while ((entry = zipStream.getNextEntry()) != null) {
                String entryName = entry.getName();

                if (entryName.contains("../")) continue;

                File fileTarget = new File(appsDir, entryName);

                if (entry.isDirectory()) {
                    fileTarget.mkdirs();
                } else {
                    File parent = fileTarget.getParentFile();
                    if (parent != null && !parent.exists()) {
                        parent.mkdirs();
                    }

                    try (FileOutputStream fos = new FileOutputStream(fileTarget)) {
                        int readBytes;
                        while ((readBytes = zipStream.read(buffer)) != -1) {
                            fos.write(buffer, 0, readBytes);
                        }
                        fos.flush();
                    }
                }
                zipStream.closeEntry();
            }
            zipStream.close();

            Log.d("CGB_INSTALLER", "¡Instalación nativa completada con éxito!");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtProgress.setText("¡Instalación Completada/Completed Installation!");
                    finish();
                }
            });

        } catch (final Exception e) {
            Log.e("CGB_INSTALLER", "Error crítico instalando CGBA: " + e.getMessage());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    txtProgress.setText("Error al instalar la aplicación/Error installing the application.");
                }
            });
        }
    }

    private void updateInterface(final String message, final int progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtProgress.setText(message);
                if (progress > 0) {
                    progressBar.setProgress(progress);
                }
            }
        });
    }
    private void readAndPrompt(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ZipInputStream zipStream = new ZipInputStream(new BufferedInputStream(inputStream));
            ZipEntry entry;
            String jsonStr = null;

            while ((entry = zipStream.getNextEntry()) != null) {
                if (entry.getName().equals("manifest.json")) {
                    ByteArrayOutputStream result = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = zipStream.read(buffer)) != -1) {
                        result.write(buffer, 0, length);
                    }
                    jsonStr = result.toString("UTF-8");
                    zipStream.closeEntry();
                    break;
                }
                zipStream.closeEntry();
            }
            zipStream.close();

            if (jsonStr != null) {
                JSONObject manifest = new JSONObject(jsonStr);
                final String nameApp = manifest.optString("name", "Unknown Game");
                final String versionApp = manifest.optString("version", "1.0.0");
                StringBuilder permsFormat = new StringBuilder();
                JSONArray permissionsArray = manifest.optJSONArray("permissions");
                if (permissionsArray != null && permissionsArray.length() > 0) {
                    for (int i = 0; i < permissionsArray.length(); i++) {
                        permsFormat.append("• ").append(permissionsArray.getString(i)).append("\n");
                    }
                } else {
                    permsFormat.append("• Ninguno / None\n");
                }
                runOnUiThread(() -> showDialogConfirm(nameApp, versionApp, permsFormat.toString(), uri, manifest.optString("uuid", UUID.randomUUID().toString())));

            } else {
                Log.e("CGB_INSTALLER", "No se encontró manifest.json dentro del paquete.");
                runOnUiThread(() -> txtProgress.setText("Error!"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> txtProgress.setText("Error!"));
        }
    }
    private void showDialogConfirm(String name, String version, String perms, final Uri fileUri, final String uuid) {
        String menssage = "Version: " + version + "\n\n" +
                         "Permisos requeridos/Permits required:\n" + perms + "\n" +
                         "¿Deseas instalar esta aplicación/You want to install this application?";

        new AlertDialog.Builder(this)
                .setTitle("Instalar/Install " + name)
                .setMessage(menssage)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(() -> processAndInstall(fileUri, uuid)).start();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }
}
