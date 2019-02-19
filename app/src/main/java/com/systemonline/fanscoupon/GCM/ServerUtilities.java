package com.systemonline.fanscoupon.GCM;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.systemonline.fanscoupon.MainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import static com.systemonline.fanscoupon.GCM.CommonUtilities.SERVER_URL;


public final class ServerUtilities {
    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();

    /**
     * Register this account/device pair within the server.
     */
    public static void register(final Context context, final String regId, final int fansCoupon_id, String IMEI) {
        Log.e("0Fragment - SerUtil", "0fn - register");
        Log.e("gcm", ">>>>>>>>>>>>.    " + fansCoupon_id);
        //Log.e(TAG, "registering device (regId = " + regId + ")");
        String serverUrl = SERVER_URL;
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        params.put("IMEI", IMEI);
//        params.put("email", email);
        params.put("fansCoupon_id", fansCoupon_id + "");

        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.e("Server Utilities", "Attempt #" + i + " to register");
            try {
                post(serverUrl, params);
                GCMRegistrar.setRegisteredOnServer(context, true);
//                String message = context.getString(R.string.server_registered);
                return;
            } catch (IOException e) {
                Log.e("server utilities", "Failed to register on attempt " + i + ":" + e);
//                e.printStackTrace();
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    //Log.e(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    //Log.e(TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                    return;
                }
                backoff *= 2;
            }
        }
    }

    /**
     * Unregister this account/device pair within the server.
     */
    public static void unregister(final Context context) {
        Log.e("0Fragment - SerUtilit", "0fn - unregister");

        //Log.e(TAG, "unregistering device (regId = " + regId + ")");
//        String serverUrl = SERVER_URL + "/unregister";
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("regId", regId);
//        try {
//            post(serverUrl, params);
        GCMRegistrar.setRegisteredOnServer(context, false);

////            String message = context.getString(R.string.server_unregistered);
//        } catch (IOException e) {
//        }
    }

    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params   request parameters.
     * @throws IOException propagated from POST.
     */
    private static void post(String endpoint, Map<String, String> params)
            throws IOException {
//        Log.e("0Fragment - ServerUtil", "0fn - post");

        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        //Log.e(TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            //Log.e("URL", "> " + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            conn.setRequestProperty("Authorization", "Bearer " + MainActivity.WS_ACCESSTOKEN);
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            } else {
                Log.e("server utilities", "response code " + status);
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
                Log.e("server utilities", "response " + sb.toString());
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}