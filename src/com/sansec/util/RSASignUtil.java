package com.sansec.util;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.X509Certificate;

import com.sansec.jce.provider.SwxaProvider;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.security.provider.Sun;

public class RSASignUtil {
	/**
	 * @param data 数据
	 * @param alg 摘要算法
	    SHA1WithRSA,SHA1/RSA,SHA224WithRSA,SHA256WithRSA
		SHA384WithRSA,SHA512WithRSA,MD2WithRSA,MD4WithRSA,MD5WithRSA
	 * @return base64编码签名
	 * @throws Exception 
	 */
	public static String rsaSignData(byte[]data,String alg, KeyPair kp) throws Exception{
		PrivateKey privateKey = kp.getPrivate();
		Signature signatue = null;
		byte[] out;
		if(alg==null||alg.length()<=0){
			System.err.println("alg error");
			return null;
		}
		if(!alg.equals("SHA1WithRSA")&&!alg.equals("SHA1/RSA")&&!alg.equals("SHA224WithRSA")&&
		!alg.equals("SHA256WithRSA")&&!alg.equals("SHA384WithRSA")&&!alg.equals("SHA512WithRSA")&&
		!alg.equals("MD2WithRSA")&&!alg.equals("MD4WithRSA")&&!alg.equals("MD5WithRSA")){
			System.err.println("alg error:"+alg);
			return null;
		}
		signatue = Signature.getInstance(alg, "SwxaJCE");
		// 签名
		signatue.initSign(privateKey);
		signatue.update(data);
		out = signatue.sign();
		return new BASE64Encoder().encode(out);
	}
	/**
	 * 
	 * @param data
	 * @param alg 接要算法
	 * @param sign base64编码签名
	 * @param cert 证书
	 * @return 验证结果
	 * @throws Exception
	 */
	public static boolean rsaVerifySignWithCert(byte[]data,String alg,String sign,X509Certificate cert) throws Exception{
		if(data==null||data.length<=0){
			System.err.println("data error");
			return false;
		}
		if(sign==null||sign.length()<=0){
			System.err.println("sign error");
			return false;
		}
		if(cert==null){
			System.err.println("cert error");
			return false;
		}
		if(alg==null||alg.length()<=0){
			System.err.println("alg error");
			return false;
		}
		if(!alg.equals("SHA1WithRSA")&&!alg.equals("SHA1/RSA")&&!alg.equals("SHA224WithRSA")&&
		!alg.equals("SHA256WithRSA")&&!alg.equals("SHA384WithRSA")&&!alg.equals("SHA512WithRSA")&&
		!alg.equals("MD2WithRSA")&&!alg.equals("MD4WithRSA")&&!alg.equals("MD5WithRSA")){
			System.err.println("alg error:"+alg);
			return false;
		}
		PublicKey publickey = cert.getPublicKey();
		if (publickey == null) {
			System.out.println("公钥解析失败");
			return false;
		}
		BASE64Decoder decoder = new BASE64Decoder();
		byte[]derSign = decoder.decodeBuffer(sign);
		Signature signatue = Signature.getInstance(alg,"SunRsaSign");
		signatue.initVerify(publickey);
		signatue.update(data);
		boolean verify = signatue.verify(derSign);
		return verify;
	}
	/**
	 * @param data
	 * @param alg
	 * @param sign
	 * @param kp
	 * @return
	 */
	public static boolean resVerifySignWithPublicKey(byte[]data,String alg,String sign,KeyPair kp)throws Exception{
		if(!alg.equals("SHA1WithRSA")&&!alg.equals("SHA1/RSA")&&!alg.equals("SHA224WithRSA")&&
		!alg.equals("SHA256WithRSA")&&!alg.equals("SHA384WithRSA")&&!alg.equals("SHA512WithRSA")&&
		!alg.equals("MD2WithRSA")&&!alg.equals("MD4WithRSA")&&!alg.equals("MD5WithRSA")){
			System.err.println("alg error:"+alg);
			return false;
		}
		PublicKey publickey = kp.getPublic();
		if (publickey == null) {
			System.out.println("公钥获取失败");
			return false;
		}
		BASE64Decoder decoder = new BASE64Decoder();
		Signature signatue = Signature.getInstance(alg,"SunRsaSign");
		byte[]derSign = decoder.decodeBuffer(sign);
		signatue.initVerify(publickey);
		signatue.update(data);
		boolean verify = signatue.verify(derSign);
		return verify;
	}
}
