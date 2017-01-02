package com.sansec.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Map.Entry;

import sun.misc.BASE64Decoder;

import com.sansec.jce.provider.SwxaProvider;

public class KeyTool {

    public static void main(String[] args) {
        SwxaProvider swpr  = new SwxaProvider("swsds.ini");
        
//        for(Entry entry : swpr.entrySet()){
//            println(entry.getKey() + ", " +entry.getValue());
//        }
//        
//        Enumeration<Object> en = swpr.elements();
//        for(;en.hasMoreElements();){
//            println(en.nextElement());
//        }
//        
//        for(String propName : swpr.stringPropertyNames()){
//            println(propName);
//        }
    }

    /**
     * 获取密钥
     * 
     * @param index
     * @return
     * @throws Exception
     */
    public static KeyPair GetRSAKey(int index) throws Exception {
        //		Security.addProvider( new SwxaProvider());

        KeyPair kp = null;
        if (index != 0) {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", "SwxaJCE");
            int keyIndex = index << 16;
            kpg.initialize(keyIndex);
            kp.getPublic();
            kp = kpg.genKeyPair();
            if (kp == null) {
                System.out.println("获取RSA密钥对失败！");
            }
        } else {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            kp = kpg.genKeyPair();
        }
        return kp;
    }

    /**
     * 解析证书
     * 
     * @param certFilePath
     * @return
     */
    public static X509Certificate d2i_X509Cerificate(String certFilePath) {
        if (certFilePath == null || certFilePath.length() <= 0) {
            System.out.println("请选择证书");
            return null;
        }
        File file = new File(certFilePath);
        if (file.exists() == false) {
            System.out.println("证书" + certFilePath + "不存在");
            return null;
        }
        ByteArrayOutputStream out = null;
        try {
            FileInputStream in = new FileInputStream(certFilePath);
            int len = 0;
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            in.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] cert = out.toByteArray();
        if (cert == null || cert.length <= 1) {
            System.out.println("读证书" + certFilePath + "失败");
            return null;
        }
        byte[] derCert = null;
        //判断是否是BASE64编码
        if (cert[0] != 0x30) {
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                String certstr = new String(cert);
                certstr = certstr.replace("-----BEGIN CERTIFICATE-----", "");
                certstr = certstr.replace("-----END CERTIFICATE-----", "");
                derCert = decoder.decodeBuffer(certstr);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        } else {
            derCert = cert;
        }
        X509Certificate x509cert = null;
        if (derCert != null) {
            InputStream ins = new ByteArrayInputStream(derCert);
            CertificateFactory cf;
            try {
                //cf = CertificateFactory.getInstance("X.509","SwxaJCE");
                cf = CertificateFactory.getInstance("X.509");
                x509cert = (X509Certificate) cf.generateCertificate(ins);
            } catch (CertificateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }

            if (x509cert != null)
                return x509cert;
            else
                return null;
        } else {
            return null;
        }
    }
}
