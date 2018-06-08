package com.mobius.software.android.iotbroker.main.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;

public class FileManager {

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        fin.close();
        return ret;
    }

    public static KeyStore loadKeyStore(String path, String password) {

        try {
            FileInputStream is = new FileInputStream(path);
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            keystore.load(is, password.toCharArray());
            return keystore;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
