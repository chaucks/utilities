package com.xcoder.utilities.net;

import com.xcoder.utilities.IUniversal;
import com.xcoder.utilities.common.MixedUtensil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Abstract http client
 *
 * @author Chuck Lee
 */
public abstract class AbstractHttpClient implements IUniversal {

    private static final Log LOGGER = LogFactory.getLog(AbstractHttpClient.class);

    /**
     * Http server address not empty
     */
    private final String serverAddress;

    /**
     * Default simple trust manager array
     */
    private static final TrustManager[] DEFAULT_TMS = new TrustManager[]{
            new X509TrustManager() {

                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }
    };

    public AbstractHttpClient(String serverAddress) {
        MixedUtensil.objectNullPointerException(serverAddress, "Http server address can not be null...Please check!");
        this.serverAddress = serverAddress;
    }

    /**
     * Get SSLSocketFactory
     *
     * @return SSLSocketFactory
     * @throws KeyManagementException   KeyManagementException
     * @throws NoSuchProviderException  NoSuchProviderException
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     */
    public abstract SSLSocketFactory getSSLSocketFactory() throws KeyManagementException, NoSuchProviderException, NoSuchAlgorithmException;

    /**
     * Get http request content
     *
     * @param objects objects
     * @return String content
     * @throws NullPointerException NullPointerException
     */
    public abstract String getRequestContent(Object... objects) throws NullPointerException;

    /**
     * Request charset
     *
     * @return charset
     */
    public abstract String getCharsetOut();

    /**
     * Response charset
     *
     * @return charset
     */
    public abstract String getCharsetIn();

    /**
     * Response buffer(StringBuilder) capacity
     *
     * @return int
     */
    public abstract int getCapacity();

    /**
     * Do http request
     *
     * @param router        router
     * @param requestMethod requestMethod
     * @param objects       objects
     * @return String
     * @throws IOException              IOException
     * @throws KeyManagementException   KeyManagementException
     * @throws NoSuchProviderException  NoSuchProviderException
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     */
    public final String request(String router, String requestMethod, Object... objects) throws IOException
            , KeyManagementException, NoSuchProviderException, NoSuchAlgorithmException {
        LOGGER.debug(MixedUtensil.appendString("requestMethod:", requestMethod));
        String requestUrl = this.getRequestUrl(router);
        LOGGER.debug(MixedUtensil.appendString("requestUrl:", requestUrl));
        HttpURLConnection conn = (HttpURLConnection) new URL(requestUrl).openConnection();
        try {
            conn.setRequestMethod(requestMethod);
            this.initHttpURLConnection(conn);
            conn.connect();

            if (MixedUtensil.arrayNotEmpty(objects)) {
                String content = this.getRequestContent(objects);
                LOGGER.debug("request content:" + content);
                String charsetOut = this.getCharsetOut();
                LOGGER.debug("request charset:" + charsetOut);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(content.getBytes(charsetOut));
                }
            }

            String charsetIn = this.getCharsetIn();
            LOGGER.debug("response charset:" + charsetIn);
            try (InputStreamReader isr = new InputStreamReader(conn.getInputStream(), charsetIn);
                 BufferedReader br = new BufferedReader(isr)) {
                int capacity = this.getCapacity();
                StringBuilder builder = new StringBuilder(capacity);
                for (String line = br.readLine(); null != line; line = br.readLine()) {
                    builder.append(line);
                }
                String resRst = builder.toString();
                LOGGER.debug("response content:" + resRst);
                return resRst;
            }
        } finally {
            if (null != conn) {
                conn.disconnect();
            }
        }
    }

    /**
     * Init HttpURLConnection
     *
     * @param conn conn
     * @throws NoSuchProviderException  NoSuchProviderException
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws KeyManagementException   KeyManagementException
     */
    public void initHttpURLConnection(HttpURLConnection conn) throws NoSuchProviderException, NoSuchAlgorithmException, KeyManagementException {
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Charset", this.getCharsetOut());
//        conn.setRequestProperty("Connection", "Keep-Alive");

        conn.setUseCaches(false);
        conn.setDoOutput(true);
        conn.setDoInput(true);
    }

    /**
     * Get http request url
     *
     * @param router router
     * @return url
     */
    private String getRequestUrl(String router) {
        return MixedUtensil.appendString(this.serverAddress, router);
    }

    /**
     * Get SunJSSE SSLSocketFactory
     *
     * @return SSLSocketFactory
     * @throws KeyManagementException   KeyManagementException
     * @throws NoSuchProviderException  NoSuchProviderException
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     */
    public static SSLSocketFactory getSunJSSESSLSocketFactory() throws KeyManagementException, NoSuchProviderException, NoSuchAlgorithmException {
        SSLContext sslCtx = SSLContext.getInstance("SSL", "SunJSSE");
        SecureRandom sr = new SecureRandom();
        sslCtx.init(null, DEFAULT_TMS, sr);

        SSLSocketFactory rst = sslCtx.getSocketFactory();
        return rst;
    }
}
