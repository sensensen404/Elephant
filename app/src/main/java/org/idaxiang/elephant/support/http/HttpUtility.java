package org.idaxiang.elephant.support.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.idaxiang.elephant.utils.AppLogger;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLHandshakeException;

import static org.idaxiang.elephant.BuildConfig.DEBUG;


public class HttpUtility
{
	private static final String TAG = HttpUtility.class.getSimpleName();
	
	public static final String POST = "POST";
	public static final String GET = "GET";
	
	public static String doRequest(String url, BaseParameters params, String method) throws Exception {
		boolean isGet = false;
		if (method.equals(GET)) {
			isGet = true;
		}
		
		String myUrl = url;
		
		String send = params.toString();
		if (isGet) {
			myUrl += "?" + send;
		}

		if (DEBUG) {
			Log.d(TAG, "send = " + send);
			Log.d(TAG, "myUrl = " + myUrl);
		}

		URL u = new URL(myUrl);
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		
		conn.setRequestMethod(method);
		conn.setDoOutput(!isGet);
		
		if (!isGet) {
			conn.setDoInput(true);
		}
		
		conn.setUseCaches(false);
		conn.setConnectTimeout(10000);
		conn.setReadTimeout(10000);
		
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty("Charset", "UTF-8");
		
		
		if (send != null) {
			conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
			
			conn.connect();
			
			if (!isGet) {
				DataOutputStream o = new DataOutputStream(conn.getOutputStream());
				
				o.write(send.getBytes());
				o.flush();
				o.close();
			}
			
		} else {
			Object[] r = params.toBoundaryMsg();
			String b = (String) r[0];
			Bitmap bmp = (Bitmap) r[1];
			String s = (String) r[2];
			byte[] bs = ("--" + b + "--\r\n").getBytes("UTF-8");
			
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
			byte[] img = bytes.toByteArray();
			
			int l =  s.getBytes("UTF-8").length + img.length + 2 * bs.length;
			
			conn.setRequestProperty("Content-type", "multipart/form-data;boundary=" + b);
			conn.setRequestProperty("Content-Length", String.valueOf(l));
			conn.setFixedLengthStreamingMode(l);
			
			conn.connect();
			
			DataOutputStream o = new DataOutputStream(conn.getOutputStream());
			o.write(s.getBytes("UTF-8"));
			
			o.write(img);
			o.write(bs);
			o.write(bs);
			o.flush();
			o.close();
			
			if (DEBUG) {
				Log.d(TAG, b);
				Log.d(TAG, s);
			}
		}
		
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			return null;
		} else {
			InputStream in = conn.getInputStream();
			
			String en = conn.getContentEncoding();
			
			if (en != null && en.equals("gzip")) {
				in = new GZIPInputStream(in);
			}
			
			BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
			
			String s;
			StringBuilder str = new StringBuilder();
			
			while ((s = buffer.readLine()) != null) {
				str.append(s);
			}
			
			return str.toString();
		}
		
	}

    private static final String CHARSET = HTTP.UTF_8;

    private static final int RETRIED_TIME = 3;

    private static final int TIMEOUT = 6 * 1000;

    /**
     * get a stream from web
     *
     * @param context
     * @param url
     * @param params
     */
    public static InputStream getStream(Context context, String url, Bundle params)
            throws IOException, Exception {

        if (params != null) {
            if (url.contains("?")) {
                url = url + "&" + UrlUtils.encodeUrl(params);
            } else {
                url = url + "?" + UrlUtils.encodeUrl(params);
            }
        }

        AppLogger.d("GET:" + url);

        HttpGet request = new HttpGet(url);
        HttpClient httpClient = getInstance(context);

        // 解决：HttpClient WARNING: Cookie rejected: Illegal domain attribute
        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                CookiePolicy.BROWSER_COMPATIBILITY);

        HttpResponse response = httpClient.execute(request);
        return response.getEntity().getContent();
    }

    private static volatile DefaultHttpClient customerHttpClient = null;

    public static HttpClient getInstance(Context context) {
        if (customerHttpClient == null) {
            synchronized (HttpUtility.class) {
                if (customerHttpClient == null) {
                    HttpParams params = new BasicHttpParams();

                    // 设置一些基本参数
                    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                    HttpProtocolParams.setContentCharset(params, CHARSET);
                    HttpProtocolParams.setUseExpectContinue(params, true);
                    HttpProtocolParams.setUserAgent(params, System
                            .getProperties().getProperty("http.agent")
                            + " Mozilla/5.0 Firefox/26.0");

					/* 连接超时 */
                    HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);

					/* 请求超时 */
                    HttpConnectionParams.setSoTimeout(params, TIMEOUT);

                    // 支持http和https两种模式
                    SchemeRegistry schReg = new SchemeRegistry();
                    schReg.register(new Scheme("http", PlainSocketFactory
                            .getSocketFactory(), 80));
                    schReg.register(new Scheme("https", getSSLSocketFactory(),
                            443));

                    // 使用线程安全的连接管理来创建HttpClient
                    ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
                            params, schReg);

                    customerHttpClient = new DefaultHttpClient(conMgr, params);
                    customerHttpClient
                            .setHttpRequestRetryHandler(requestRetryHandler);

                    ConnectivityManager manager = (ConnectivityManager) context
                            .getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo networkinfo = manager.getActiveNetworkInfo();
                    String net = networkinfo != null ? networkinfo
                            .getExtraInfo() : null;

                    // wifi的值为空
                    if (!TextUtils.isEmpty(net)) {
                        String proxyHost = getDefaultHost();

                        if (!TextUtils.isEmpty(proxyHost)) {
                            HttpHost proxy = new HttpHost(proxyHost, getDefaultPort(), "http");

                            customerHttpClient.getParams().setParameter(
                                    ConnRoutePNames.DEFAULT_PROXY, proxy);
                        }
                    }

                }
            }
        }
        return customerHttpClient;
    }

    private static SSLSocketFactory getSSLSocketFactory() {

        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore
                    .getDefaultType());

            trustStore.load(null, null);

            SSLSocketFactory sslSocketFactory = new TrustAllSSLSocketFactory(
                    trustStore);
            sslSocketFactory
                    .setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            return sslSocketFactory;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 异常自动恢复处理, 使用HttpRequestRetryHandler接口实现请求的异常恢复
     *
     */
    private static HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler() {

        @Override
        public boolean retryRequest(IOException exception, int executionCount,
                                    HttpContext context) {
            // 设置恢复策略，在发生异常时候将自动重试N次
            if (executionCount >= RETRIED_TIME) {
                // Do not retry if over max retry count
               AppLogger.e(
                       "Do not retry if over max retry count:"
                               + executionCount);
                return false;
            }

            if (exception instanceof NoHttpResponseException) {
                // Retry if the server dropped connection on us
                AppLogger
                        .i("Retry if the server dropped connection on us:exception instanceof NoHttpResponseException");
                return true;
            }

            if (exception instanceof SSLHandshakeException) {
                // Do not retry on SSL handshake exception
                AppLogger.e(
                        "Do not retry on SSL handshake SSLHandshakeException ");
                return false;
            }

            HttpRequest request = (HttpRequest) context
                    .getAttribute(ExecutionContext.HTTP_REQUEST);

            boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
            if (!idempotent) {
                // Retry if the request is considered idempotent
                AppLogger.i(
                        "Retry if the request is considered idempotent");
                return true;
            }

            return false;
        }
    };

    @SuppressWarnings("deprecation")
    private static String getDefaultHost() {
        return android.net.Proxy.getDefaultHost();
    }

    @SuppressWarnings("deprecation")
    private static int getDefaultPort() {
        return android.net.Proxy.getDefaultPort();
    }
}
