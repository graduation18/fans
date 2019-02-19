package com.systemonline.fanscoupon.WebServices;

import android.os.AsyncTask;
import android.util.Log;

import com.systemonline.fanscoupon.Interfaces.IServiceCallBack;
import com.systemonline.fanscoupon.MainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class JSONAsync extends AsyncTask<String, String, Void> {

    private String METHOD_NAME = "", requestType, url, accessToken;
    private List<NameValuePair> ServicePram = null;
    private IServiceCallBack serviceInterfaceObj;
    private JSONTokener tokener;

    public JSONAsync(IServiceCallBack serviceInterfaceObj, String MethodName,
                     List<NameValuePair> servicePram, String url, String requestType) {
        this.METHOD_NAME = MethodName;
        this.url = url;
        this.serviceInterfaceObj = serviceInterfaceObj;
        this.ServicePram = servicePram;
        this.requestType = requestType;
        this.accessToken = null;
    }

    public JSONAsync(IServiceCallBack serviceInterfaceObj, String accessToken, String MethodName,
                     List<NameValuePair> servicePram, String url, String requestType) {
        this.METHOD_NAME = MethodName;
        this.accessToken = accessToken;
        this.url = url;
        this.serviceInterfaceObj = serviceInterfaceObj;
        this.ServicePram = servicePram;
        this.requestType = requestType;
    }

    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(String... params) {
        switch (requestType) {
            case "GET":
//                tokener = sendGetRequest();
                tokener = sendGetRequest_ssl();
                break;
            case "POST":
                tokener = sendPostRequest_ssl();
                break;
            case "PUT":
                tokener = sendPutRequest_ssl();
                break;
            case "DELETE":
                tokener = sendDeleteRequest_ssl();
                break;
            case "R_GET":
                tokener = sendGetRequestReceiver_ssl();
                break;
        }
        return null;
    }

    protected void onPostExecute(Void v) {

        this.serviceInterfaceObj.PostBackExecutionJSON(tokener);

    }

    private JSONTokener sendPostRequest() {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url + METHOD_NAME);
        Log.e("post url", url + METHOD_NAME);
        try {
            if (MainActivity.currentFan != null) {
                httppost.addHeader("Authorization", "Bearer " + MainActivity.WS_ACCESSTOKEN);
                httppost.addHeader("X-Requested-With", "XMLHttpRequest");
                Log.e("fanscoupon access token", "Bearer " + MainActivity.WS_ACCESSTOKEN);
            }
            if (ServicePram != null) {
//                httppost.setEntity(new UrlEncodedFormEntity(ServicePram));
                httppost.setEntity(new UrlEncodedFormEntity(ServicePram, "UTF-8"));
            }
            HttpResponse response = httpclient.execute(httppost);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line).append("\n");
            }
            JSONTokener tokener = new JSONTokener(builder.toString());
            return tokener;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    private JSONTokener sendPostRequest_ssl() {
        try {
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpPut httpPut = new HttpPut(url + METHOD_NAME);
            Log.e("POST req. url", url + METHOD_NAME);


//            HttpResponse response = httpclient.execute(httpPut);


            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{new CustomX509TrustManager()},
                    new SecureRandom());

            HttpClient client = new DefaultHttpClient();

            SSLSocketFactory ssf = new CustomSSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = client.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", ssf, 443));
            DefaultHttpClient sslClient = new DefaultHttpClient(ccm,
                    client.getParams());

            HttpPost httpPost = new HttpPost(new URI(url + METHOD_NAME));

            httpPost.addHeader("X-Requested-With", "XMLHttpRequest");

            if (MainActivity.currentFan != null) {
                httpPost.addHeader("Authorization", "Bearer " + MainActivity.WS_ACCESSTOKEN);
                Log.e("fanscoupon access token", "Bearer " + MainActivity.WS_ACCESSTOKEN);
            }
            if (ServicePram != null) {
//                httpPut.setEntity(new UrlEncodedFormEntity(ServicePram));
                httpPost.setEntity(new UrlEncodedFormEntity(ServicePram, "UTF-8"));

            }

            HttpResponse response = sslClient.execute(httpPost);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line).append("\n");
            }
            JSONTokener tokener = new JSONTokener(builder.toString());
            return tokener;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONTokener sendPutRequest() {

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPut httpPut = new HttpPut(url + METHOD_NAME);
            Log.e("PUT req. url", url + METHOD_NAME);
            if (MainActivity.currentFan != null) {
                httpPut.addHeader("Authorization", "Bearer " + MainActivity.WS_ACCESSTOKEN);
                httpPut.addHeader("X-Requested-With", "XMLHttpRequest");
                Log.e("fanscoupon access token", "Bearer " + MainActivity.WS_ACCESSTOKEN);
            }
            if (ServicePram != null) {
//                httpPut.setEntity(new UrlEncodedFormEntity(ServicePram));
                httpPut.setEntity(new UrlEncodedFormEntity(ServicePram, "UTF-8"));

            }

            HttpResponse response = httpclient.execute(httpPut);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line).append("\n");
            }
            JSONTokener tokener = new JSONTokener(builder.toString());
            return tokener;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONTokener sendPutRequest_ssl() {

        try {
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpPut httpPut = new HttpPut(url + METHOD_NAME);
            Log.e("PUT req. url", url + METHOD_NAME);


//            HttpResponse response = httpclient.execute(httpPut);


            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{new CustomX509TrustManager()},
                    new SecureRandom());

            HttpClient client = new DefaultHttpClient();

            SSLSocketFactory ssf = new CustomSSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = client.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", ssf, 443));
            DefaultHttpClient sslClient = new DefaultHttpClient(ccm,
                    client.getParams());

            HttpPut httpPut = new HttpPut(new URI(url + METHOD_NAME));

            httpPut.addHeader("X-Requested-With", "XMLHttpRequest");

            if (MainActivity.currentFan != null) {
                httpPut.addHeader("Authorization", "Bearer " + MainActivity.WS_ACCESSTOKEN);
                Log.e("fanscoupon access token", "Bearer " + MainActivity.WS_ACCESSTOKEN);
            }
            if (ServicePram != null) {
//                httpPut.setEntity(new UrlEncodedFormEntity(ServicePram));
                httpPut.setEntity(new UrlEncodedFormEntity(ServicePram, "UTF-8"));

            }

            HttpResponse response = sslClient.execute(httpPut);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line).append("\n");
            }
            JSONTokener tokener = new JSONTokener(builder.toString());
            return tokener;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONTokener sendDeleteRequest() {
        HttpClient httpclient = new DefaultHttpClient();
        HttpDelete httpDelete = new HttpDelete(url + METHOD_NAME);
        Log.e("Delete req. url", url + METHOD_NAME);
        try {
            if (MainActivity.currentFan != null) {
                httpDelete.addHeader("Authorization", "Bearer " + MainActivity.WS_ACCESSTOKEN);
                httpDelete.addHeader("X-Requested-With", "XMLHttpRequest");
                Log.e("fanscoupon access token", "Bearer " + MainActivity.WS_ACCESSTOKEN);
            }
//            if (ServicePram != null) {
//                httpDelete.setEntity(new UrlEncodedFormEntity(ServicePram));
//                httpDelete.setEntity(new UrlEncodedFormEntity(ServicePram, "UTF-8"));
//
//            }

            HttpResponse response = httpclient.execute(httpDelete);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line).append("\n");
            }
            JSONTokener tokener = new JSONTokener(builder.toString());
            return tokener;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    private JSONTokener sendDeleteRequest_ssl() {
//        HttpClient httpclient = new DefaultHttpClient();
//        HttpDelete httpDelete = new HttpDelete(url + METHOD_NAME);
        Log.e("Delete req. url", url + METHOD_NAME);
        try {

//            if (ServicePram != null) {
//                httpDelete.setEntity(new UrlEncodedFormEntity(ServicePram));
//                httpDelete.setEntity(new UrlEncodedFormEntity(ServicePram, "UTF-8"));
//
//            }

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{new CustomX509TrustManager()},
                    new SecureRandom());

            HttpClient client = new DefaultHttpClient();

            SSLSocketFactory ssf = new CustomSSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = client.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", ssf, 443));
            DefaultHttpClient sslClient = new DefaultHttpClient(ccm,
                    client.getParams());

            HttpDelete httpDelete = new HttpDelete(new URI(url + METHOD_NAME));

            httpDelete.addHeader("X-Requested-With", "XMLHttpRequest");

            if (MainActivity.currentFan != null) {
                httpDelete.addHeader("Authorization", "Bearer " + MainActivity.WS_ACCESSTOKEN);
                Log.e("fanscoupon access token", "Bearer " + MainActivity.WS_ACCESSTOKEN);
            }
//            if (MainActivity.currentFan != null) {
//                get.addHeader("Authorization", "Bearer " + MainActivity.WS_ACCESSTOKEN);
//                Log.e("ACCESS TOKEN", "Bearer " + MainActivity.WS_ACCESSTOKEN);
//
//            } else if (accessToken != null) {
//                get.addHeader("Authorization", "Bearer " + accessToken);
//                Log.e("ACCESS TOKEN", "Bearer " + accessToken);
//            }

            HttpResponse response = sslClient.execute(httpDelete);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line).append("\n");
            }
            JSONTokener tokener = new JSONTokener(builder.toString());
            return tokener;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    private JSONTokener sendGetRequest() {
        try {

            METHOD_NAME += "?";
            String params = "";
            if (ServicePram != null)
                for (int i = 0; i < ServicePram.size(); i++) {
                    params += URLEncoder.encode(ServicePram.get(i).getName(), "UTF-8") + "=" +
                            URLEncoder.encode(ServicePram.get(i).getValue(), "UTF-8") + "&";
                }

            Log.e("get url", url + METHOD_NAME + params);
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url + METHOD_NAME + params);

            if (MainActivity.currentFan != null) {
                httpGet.addHeader("Authorization", "Bearer " + MainActivity.WS_ACCESSTOKEN);
                Log.e("ACCESS TOKEN", "Bearer " + MainActivity.WS_ACCESSTOKEN);

            } else if (accessToken != null) {
                httpGet.addHeader("Authorization", "Bearer " + accessToken);
                Log.e("ACCESS TOKEN", "Bearer " + accessToken);
            }

            HttpResponse response = httpclient.execute(httpGet);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line).append("\n");
            }
            JSONTokener tokener = new JSONTokener(builder.toString());
            return tokener;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    private JSONTokener sendGetRequest_ssl() {
        try {


            METHOD_NAME += "?";
            String params = "";
            if (ServicePram != null)
                for (int i = 0; i < ServicePram.size(); i++) {
                    params += URLEncoder.encode(ServicePram.get(i).getName(), "UTF-8") + "=" +
                            URLEncoder.encode(ServicePram.get(i).getValue(), "UTF-8") + "&";
                }

            Log.e("get url", url + METHOD_NAME + params);
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpGet httpGet = new HttpGet(url + METHOD_NAME + params);


            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{new CustomX509TrustManager()},
                    new SecureRandom());

            HttpClient client = new DefaultHttpClient();

            SSLSocketFactory ssf = new CustomSSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = client.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", ssf, 443));
            DefaultHttpClient sslClient = new DefaultHttpClient(ccm,
                    client.getParams());

            HttpGet get = new HttpGet(new URI(url + METHOD_NAME + params));
            get.addHeader("X-Requested-With", "XMLHttpRequest");

            if (MainActivity.currentFan != null) {
                get.addHeader("Authorization", "Bearer " + MainActivity.WS_ACCESSTOKEN);
                Log.e("ACCESS TOKEN", "Bearer " + MainActivity.WS_ACCESSTOKEN);

            } else if (accessToken != null) {
                get.addHeader("Authorization", "Bearer " + accessToken);
                Log.e("ACCESS TOKEN", "Bearer " + accessToken);
            }

            HttpResponse response = sslClient.execute(get);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line).append("\n");
            }
            JSONTokener tokener = new JSONTokener(builder.toString());
            return tokener;


//            DataLoader dl = new DataLoader();
//            String url = "https://IpAddress";
//            HttpResponse response = dl.secureLoadData(url);

//            StringBuilder sb = new StringBuilder();
//            sb.append("HEADERS:\n\n");
//
//            Header[] headers = response.getAllHeaders();
//            for (int i = 0; i < headers.length; i++) {
//                Header h = headers[i];
//                sb.append(h.getName()).append(":\t").append(h.getValue()).append("\n");
//            }
//
//            InputStream is = response.getEntity().getContent();
//            StringBuilder out = new StringBuilder();
//            BufferedReader br = new BufferedReader(new InputStreamReader(is));
//            for (String line = br.readLine(); line != null; line = br.readLine())
//                out.append(line);
//            br.close();
//
//            sb.append("\n\nCONTENT:\n\n").append(out.toString());
//
//            Log.e("response", sb.toString());
//            text.setText(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONTokener sendGetRequestReceiver() {
        try {

            METHOD_NAME += "?";
            String params = "";
            if (ServicePram != null)
                for (int i = 0; i < ServicePram.size(); i++) {
                    params += URLEncoder.encode(ServicePram.get(i).getName(), "UTF-8") + "=" +
                            URLEncoder.encode(ServicePram.get(i).getValue(), "UTF-8") + "&";
                }

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url + METHOD_NAME + params);
            Log.e("url", url + METHOD_NAME + params);
            HttpResponse response = httpclient.execute(httpGet);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line).append("\n");
            }
            JSONTokener tokener = new JSONTokener(builder.toString());
            return tokener;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    private JSONTokener sendGetRequestReceiver_ssl() {
        try {


            METHOD_NAME += "?";
            String params = "";
            if (ServicePram != null)
                for (int i = 0; i < ServicePram.size(); i++) {
                    params += URLEncoder.encode(ServicePram.get(i).getName(), "UTF-8") + "=" +
                            URLEncoder.encode(ServicePram.get(i).getValue(), "UTF-8") + "&";
                }

            Log.e("get receiver url", url + METHOD_NAME + params);
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpGet httpGet = new HttpGet(url + METHOD_NAME + params);


            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{new CustomX509TrustManager()},
                    new SecureRandom());

            HttpClient client = new DefaultHttpClient();

            SSLSocketFactory ssf = new CustomSSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = client.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", ssf, 443));
            DefaultHttpClient sslClient = new DefaultHttpClient(ccm,
                    client.getParams());

            HttpGet get = new HttpGet(new URI(url + METHOD_NAME + params));
//            if (MainActivity.currentFan != null) {
//                get.addHeader("Authorization", "Bearer " + MainActivity.WS_ACCESSTOKEN);
//                Log.e("ACCESS TOKEN", "Bearer " + MainActivity.WS_ACCESSTOKEN);
//
//            } else if (accessToken != null) {
//                get.addHeader("Authorization", "Bearer " + accessToken);
//                Log.e("ACCESS TOKEN", "Bearer " + accessToken);
//            }

            HttpResponse response = sslClient.execute(get);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null; ) {
                builder.append(line).append("\n");
            }
            JSONTokener tokener = new JSONTokener(builder.toString());
            return tokener;


//
//            METHOD_NAME += "?";
//            String params = "";
//            if (ServicePram != null)
//                for (int i = 0; i < ServicePram.size(); i++) {
//                    params += URLEncoder.encode(ServicePram.get(i).getName(), "UTF-8") + "=" +
//                            URLEncoder.encode(ServicePram.get(i).getValue(), "UTF-8") + "&";
//                }
//
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpGet httpGet = new HttpGet(url + METHOD_NAME + params);
//            Log.e("url", url + METHOD_NAME + params);
//            HttpResponse response = httpclient.execute(httpGet);
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
//            StringBuilder builder = new StringBuilder();
//            for (String line = null; (line = reader.readLine()) != null; ) {
//                builder.append(line).append("\n");
//            }
//            JSONTokener tokener = new JSONTokener(builder.toString());
//            return tokener;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

}
