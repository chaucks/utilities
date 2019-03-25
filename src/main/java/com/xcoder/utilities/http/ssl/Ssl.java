package com.xcoder.utilities.http.ssl;

import org.apache.http.ssl.TrustStrategy;

import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Ssl配置&Api类
 *
 * @author Chuck Lee
 */
public class Ssl extends AbstractSsl {

    /**
     * Constructor
     *
     * @param keyStoreFilePath keyStoreFilePath
     * @param keyStorePassword keyStorePassword
     */
    public Ssl(final String keyStoreFilePath, final String keyStorePassword) {
        super(keyStoreFilePath, keyStorePassword);
    }

    @Override
    public String getKeyStoreType() {
        return DEFAULT_KEY_STORE_TYPE;
    }

    @Override
    public TrustStrategy getTrustStrategy() {
        return DEFAULT_TRUST_STRATEGY;
    }

    @Override
    public KeyManager[] getKeyManagers() {
        return null;
    }

    @Override
    public TrustManager[] getTrustManagers() {
        return new TrustManager[]{
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
    }

    @Override
    public SecureRandom getSecureRandom() {
        try {
            return getDefaultSecureRandom();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
