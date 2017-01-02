package sw.jce.func.test;

import java.security.cert.X509Certificate;

import com.sansec.util.FileUtil;
import com.sansec.util.XMLSignUtil;
public class XMLVerifyTest {
	public static void functionTest() {
		byte[]xmlData = FileUtil.read(ConfigUtil.XMLDATAFILE);
		if(xmlData==null||xmlData.length<=0){
			System.out.println("读取XML签名数据文档失败");
			return;
		}
		boolean res = ConfigUtil.loadCert(1);
		if(res==false){
			System.out.println("加载证书失败");
			return;
		}
		X509Certificate cert = ConfigUtil.rsacert;
		for(int type=1;type<3;type++){
        	//读文件
        	String fileName = ConfigUtil.XMLSIGNRESULTFILE+"_type"+type+".xml";
        	byte []signResult = FileUtil.read(fileName);
        	if(signResult==null||signResult.length<=0){
        		System.out.println("读签名文档 "+fileName+"失败");
    			return;
        	}
        	System.out.println("读签名文档 "+fileName+"成功");
	        if(type==1){
	    		System.out.println("签名结果带证书XML验证测试");
	        }else{
	    		System.out.println("签名结果带公钥XML验证测试");
	        }
	        res = VerifyXmlTest(type, signResult, cert);
		}
    }
	 public static boolean VerifyXmlTest(int type,byte[]xmlsignData,X509Certificate x509cert){
			try {
				if(x509cert==null){
					System.out.println("证书为空");
					return false;
				}
				byte[]publickey = x509cert.getPublicKey().getEncoded();
				//如果参数publicKey不为空，使用publickey验证签名，否则使用签名文档中的证书或公钥验证
				boolean res = XMLSignUtil.verifyXml(xmlsignData,null);
				if(res==true){
					System.out.println("XML签名验证成功");
				}else{
					System.out.println("XML签名验证失败");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("XML签名验证失败"+e.getMessage());
				return false;
			}
			System.out.println();
			return true;
	    }
}
