package com.mobius.software.android.iotbroker.main.net;

import java.io.StringReader;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

public class TLSHelper
{
    public static KeyStore getKeyStore(String ksContent,String ksPassword) throws Exception
    {
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 0);

        PEMParser  reader = new PEMParser (new StringReader(ksContent));
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);
        JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();

        KeyPair kp=null;
        List<X509Certificate> certificates = new ArrayList<>();

        Object o;
        while ((o = reader.readObject()) != null)
        {
            if(o instanceof PEMEncryptedKeyPair)
            {
                // Encrypted key - we will use provided password
                PEMEncryptedKeyPair ckp = (PEMEncryptedKeyPair) o;
                PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder().build(ksPassword.toCharArray());
                kp = converter.getKeyPair(ckp.decryptKeyPair(decProv));
            }
            else if(o instanceof PEMKeyPair)
                kp=converter.getKeyPair((PEMKeyPair)o);
            else if(o instanceof X509CertificateHolder)
                certificates.add(certConverter.getCertificate((X509CertificateHolder)o));
        }

        reader.close();
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(null, null);

        X509Certificate[] chain=new X509Certificate[certificates.size()];
        int index=0;
        for(X509Certificate curr:certificates)
        {
            ks.setCertificateEntry(curr.getSubjectX500Principal().getName(), curr);
            chain[index++]=curr;
        }

        if(kp!=null)
            ks.setKeyEntry("main", kp.getPrivate(), ksPassword.toCharArray(), chain);

        return ks;
    }
}
