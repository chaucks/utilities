package com.xcoder.utilities.http.ssl;

import com.xcoder.utilities.common.MixedUtensil;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * 抽象Ssl配置&Api类
 *
 * @author Chuckm Lee
 */
public abstract class AbstractSsl {

    /**
     * Keystore file path empty message
     */
    public static final String KEY_STORE_FILE_PATH_EMPTY_ERROR_MESSAGE = "Ssl key store file path can not be null...Please check!";

    /**
     * Keystore password path empty message
     */
    public static final String KEY_STORE_PASSWORD_EMPTY_ERROR_MESSAGE = "Ssl key store password can not be null...Please check!";

    /**
     * Default keystore type
     */
    public static final String DEFAULT_KEY_STORE_TYPE = KeyStore.getDefaultType();

    /**
     * Default trust strategy
     * 默认true 不验证服务端证书
     */
    public static final TrustStrategy DEFAULT_TRUST_STRATEGY = (x509Certificates, s) -> true;

    /**
     * Keystore file path
     */
    private String keyStoreFilePath;

    /**
     * Keystore password
     */
    private String keyStorePassword;

    /**
     * Keystore password char array
     */
    private char[] ksPwdChars;

    /**
     * Constructor
     *
     * @param keyStoreFilePath keyStoreFilePath
     * @param keyStorePassword keyStorePassword
     */
    public AbstractSsl(final String keyStoreFilePath, final String keyStorePassword) {
        MixedUtensil.stringEmptyRuntimeException(keyStoreFilePath, KEY_STORE_FILE_PATH_EMPTY_ERROR_MESSAGE);
        MixedUtensil.stringEmptyRuntimeException(keyStorePassword, KEY_STORE_PASSWORD_EMPTY_ERROR_MESSAGE);
        this.keyStoreFilePath = keyStoreFilePath;
        this.keyStorePassword = keyStorePassword;
        this.ksPwdChars = keyStorePassword.toCharArray();
    }

    /**
     * KeyStore type
     *
     * @return KeyStore type
     */
    public abstract String getKeyStoreType();

    /**
     * TrustStrategy
     *
     * @return TrustStrategy
     */
    public abstract TrustStrategy getTrustStrategy();

    /**
     * KeyManager array
     *
     * @return KeyManager array
     */
    public abstract KeyManager[] getKeyManagers();

    /**
     * TrustManager array
     *
     * @return TrustManager array
     */
    public abstract TrustManager[] getTrustManagers();

    /**
     * SecureRandom
     *
     * @return SecureRandom
     */
    public abstract SecureRandom getSecureRandom();

    /**
     * Get CloseableHttpClient
     *
     * @return CloseableHttpClient
     * @throws IOException               IOException
     * @throws CertificateException      CertificateException
     * @throws NoSuchAlgorithmException  NoSuchAlgorithmException
     * @throws KeyStoreException         KeyStoreException
     * @throws KeyManagementException    KeyManagementException
     * @throws UnrecoverableKeyException UnrecoverableKeyException
     */
    public final CloseableHttpClient getCloseableHttpClient() throws IOException, CertificateException
            , NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnrecoverableKeyException {
        final SSLConnectionSocketFactory sslCsf = this.getSSLConnectionSocketFactory();
        final CloseableHttpClient chc = HttpClients.custom().setSSLSocketFactory(sslCsf).build();
        return chc;
    }

    /**
     * Get SSLConnectionSocketFactory
     *
     * @return SSLConnectionSocketFactory
     * @throws KeyManagementException    KeyManagementException
     * @throws NoSuchAlgorithmException  NoSuchAlgorithmException
     * @throws KeyStoreException         KeyStoreException
     * @throws IOException               IOException
     * @throws CertificateException      CertificateException
     * @throws UnrecoverableKeyException UnrecoverableKeyException
     */
    private SSLConnectionSocketFactory getSSLConnectionSocketFactory() throws KeyManagementException, NoSuchAlgorithmException
            , KeyStoreException, IOException, CertificateException, UnrecoverableKeyException {
        final SSLConnectionSocketFactory sslCsf = new SSLConnectionSocketFactory(this.getSSLContext());
        return sslCsf;
    }

    /**
     * Get SSLContext
     * <p>
     * SSLContext debug 获取
     * SSLContext sslCtx = SSLContext.getInstance("SSL", "SunJSSE");
     *
     * @return SSLContext
     * @throws IOException               IOException
     * @throws KeyStoreException         KeyStoreException
     * @throws NoSuchAlgorithmException  NoSuchAlgorithmException
     * @throws KeyManagementException    KeyManagementException
     * @throws CertificateException      CertificateException
     * @throws UnrecoverableKeyException UnrecoverableKeyException
     */
    private SSLContext getSSLContext() throws IOException, KeyStoreException, NoSuchAlgorithmException
            , KeyManagementException, CertificateException, UnrecoverableKeyException {
        final String keyStoreFilePath = this.getKeyStoreFilePath();
        final String keyStoreType = this.getKeyStoreType();
        final char[] ksPwdChars = this.getKsPwdChars();

        final KeyStore ks = getKeyStore(keyStoreFilePath, keyStoreType, ksPwdChars);
        final TrustStrategy ts = this.getTrustStrategy();

        // 若不需要对服务器端证书验证，则不需要发送客户端证书信息，只需要loadTrustMaterial()，不需要loadKeyMaterial()
        final SSLContext sslCtx = SSLContexts.custom()
                // 加载客户端证书
                .loadKeyMaterial(ks, ksPwdChars)
                // 加载服务端证书
                .loadTrustMaterial(ks, ts)
                .build();

        final KeyManager[] kms = this.getKeyManagers();
        final TrustManager[] tms = this.getTrustManagers();
        final SecureRandom sr = this.getSecureRandom();
        sslCtx.init(kms, tms, sr);

        return sslCtx;
    }

    /**
     * Get keystore
     *
     * @param keyStoreFilePath keyStoreFilePath
     * @param keyStoreType     keyStoreType
     * @param ksPwdChars       ksPwdChars
     * @return KeyStore
     * @throws CertificateException     CertificateException
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws IOException              IOException
     * @throws KeyStoreException        KeyStoreException
     */
    private static KeyStore getKeyStore(final String keyStoreFilePath, final String keyStoreType, final char[] ksPwdChars)
            throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException {
        final File ksf = new File(keyStoreFilePath);
        try (FileInputStream fis = new FileInputStream(ksf)) {
            final KeyStore ks = KeyStore.getInstance(keyStoreType);
            ks.load(fis, ksPwdChars);
            return ks;
        }
    }

    /**
     * Get default SecureRandom
     *
     * @return SecureRandom
     */
    public static SecureRandom getDefaultSecureRandom() throws NoSuchProviderException, NoSuchAlgorithmException {
        final SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
        return sr;
    }

    /**
     * Get keystore file path
     *
     * @return keystore file path
     */
    public String getKeyStoreFilePath() {
        return keyStoreFilePath;
    }

    /**
     * Get keystore password
     *
     * @return keystore password
     */
    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    /**
     * Get keystore password char array
     *
     * @return keystore password char array
     */
    public char[] getKsPwdChars() {
        return ksPwdChars;
    }
}
