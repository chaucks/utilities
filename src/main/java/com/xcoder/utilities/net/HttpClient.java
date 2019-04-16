package com.xcoder.utilities.net;

import com.xcoder.utilities.common.MixedUtensil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * Java net http client wrapper
 *
 * @author Chucl Lee
 */

public class HttpClient {

    private static final Log LOGGER = LogFactory.getLog(HttpClient.class);

    /**
     * Https client
     */
    private static final AbstractHttpClient HTTPS_CLIENT = new AbstractHttpClient("") {

        private static final String CHARSET_NAME = UTF_8_CHAR_SET;

        /**
         * 初始化Ssl socket factory
         *
         * @param conn conn
         * @throws NoSuchProviderException NoSuchProviderException
         * @throws NoSuchAlgorithmException NoSuchAlgorithmException
         * @throws KeyManagementException KeyManagementException
         */
        @Override
        public void initHttpURLConnection(HttpURLConnection conn) throws NoSuchProviderException, NoSuchAlgorithmException, KeyManagementException {
            super.initHttpURLConnection(conn);
            SSLSocketFactory sslSocketFactory = this.getSSLSocketFactory();
            if (null != sslSocketFactory) {
                ((HttpsURLConnection) conn).setSSLSocketFactory(sslSocketFactory);
            }
        }

        @Override
        public SSLSocketFactory getSSLSocketFactory() throws KeyManagementException, NoSuchProviderException, NoSuchAlgorithmException {
            return getSunJSSESSLSocketFactory();
        }

        @Override
        public String getRequestContent(Object... objects) {
            return MixedUtensil.appendString(objects);
        }

        @Override
        public String getCharsetOut() {
            return CHARSET_NAME;
        }

        @Override
        public String getCharsetIn() {
            return CHARSET_NAME;
        }

        @Override
        public int getCapacity() {
            return 1024;
        }
    };

    /**
     * Http client
     */
    private static final AbstractHttpClient HTTP_CLIENT = new AbstractHttpClient("") {

        private static final String CHARSET_NAME = UTF_8_CHAR_SET;

        @Override
        public SSLSocketFactory getSSLSocketFactory() {
            return null;
        }

        @Override
        public String getRequestContent(Object... objects) {
            return MixedUtensil.appendString(objects);
        }

        @Override
        public String getCharsetOut() {
            return CHARSET_NAME;
        }

        @Override
        public String getCharsetIn() {
            return CHARSET_NAME;
        }

        @Override
        public int getCapacity() {
            return 1024;
        }
    };


}
