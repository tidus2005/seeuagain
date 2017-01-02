package sw.jce.func.test;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

import com.sansec.util.KeyTool;
import com.sansec.util.RSASignUtil;
public class RSASignAndVerifyTest {
	public static void functionTest(int flagindex) {
		// 获取RSA密钥对
		String signature;
		byte[] dataInput = "北京三未信安".getBytes();
		List<String> alg = new ArrayList<String>();
		alg.add("SHA1WithRSA"); // SHA1
		alg.add("SHA256WithRSA"); // SHA256
		alg.add("SHA384WithRSA"); // SHA384
		alg.add("SHA512WithRSA"); // SHA512
		System.out.println("Source Data : " + new String(dataInput));
		for (int i = 0; i < alg.size(); i++) {
			System.out.println("----------------------------------------");
			System.out.println("签名算法:[ " + alg.get(i)+ " ]");
			try{
				//获取内部密钥对
				KeyPair kp = KeyTool.GetRSAKey(flagindex);
				
				// 签名
				signature = RSASignUtil.rsaSignData(dataInput, alg.get(i), kp);
				System.out.println("签名值 : "+ signature);
				
				// 证书验签
				boolean flag = RSASignUtil.rsaVerifySignWithCert(dataInput, alg.get(i), signature, ConfigUtil.rsacert);
				if (flag == true) {
					System.out.println("RSA用证书验证成功");
				} else {
					System.out.println("RSA用证书验证失败，请确定ConfigPath路径下证书与内部密钥是否匹配");
				}
				
				// 公钥验签
				flag = RSASignUtil.resVerifySignWithPublicKey(dataInput, alg.get(i), signature, kp);
				if (flag == true) {
					System.out.println("RSA用公钥验证成功");
				} else {
					System.out.println("RSA用公钥验证失败");
				}
				
			}catch(Exception e){
				e.printStackTrace();
				System.err.println("Exception="+e.getMessage());
			}
		}
	}
}