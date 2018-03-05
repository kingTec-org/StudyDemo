package daily.cn.commonlib.http.util;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * author :zuoshengyong
 * e-mail: partric23@gmail.com
 * time: 2018/03/05
 */
public class RqbTrustManager {
    private static Context context;
    private static RqbTrustManager instance;

    public static void init(@NonNull Application application) {
        RqbTrustManager.context = application.getApplicationContext();
    }

    public synchronized static RqbTrustManager getInstance() throws NullPointerException {
        if (instance == null) {
            if (context == null) {
                throw new NullPointerException("RqbTrustManager has not been initialized, please call RqbTrustManager.init(context) method to initialize");
            }
            instance = new RqbTrustManager();
        }
        return instance;
    }

    private TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {

        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];

        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return originalTrustManager.getAcceptedIssuers();
                    }

                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        try {
                            originalTrustManager.checkClientTrusted(certs, authType);
                        } catch (CertificateException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        try {
                            originalTrustManager.checkServerTrusted(certs, authType);
                        } catch (CertificateException e) {
                            e.printStackTrace();
                        }
                    }
                }
        };
    }

    public SSLSocketFactory getSSLSocketFactory()
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException, NoSuchProviderException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
//        InputStream caInput = context.getResources().openRawResource(keystoreResId);
        InputStream caInput = context.getAssets().open(KEY_CERT);
        Certificate ca = cf.generateCertificate(caInput);
        caInput.close();
//        if (keyStoreType == null || keyStoreType.length() == 0) {
//            keyStoreType = KeyStore.getDefaultType();
//        }
//        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);
        TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf.getTrustManagers());
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, wrappedTrustManagers, null);
        return sslContext.getSocketFactory();
    }

    public final static String KEY_CERT = "-.office.cn.crt";//https证书文件路径

}