package sw.jce.func.test;
import java.security.cert.X509Certificate;

import com.sansec.util.FileUtil;
import com.sansec.util.XMLSignUtil;
/**
 * XML签名测试
 * @author lvxingsheng
 *
 */
public class XMLSignTest {
	public static void functionTest(int flag) {
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
		for(int type=1;type<=2;type++){
			if(type==1)
	    		System.out.println("签名结果带证书XML签名测试");
	    	else
	    		System.out.println("签名结果带公钥XML签名测试");
			byte[]signResult = SignXmlTest(type,xmlData,cert,flag);
			//把签名结果写到文件
	        if(signResult==null){
	        	System.out.println("签名失败");
				return;
	        }else{
	        	System.out.println("签名文档"+ConfigUtil.XMLDATAFILE+"成功");
	        	System.out.println(new String(signResult));
	        	//写文件
	        	String fileName = ConfigUtil.XMLSIGNRESULTFILE+"_type"+type+".xml";
	        	FileUtil.write(fileName, signResult);
	        	System.out.println("签名结果写入文件 "+fileName+" OK");
	        }
		}
    }
    public static byte[] SignXmlTest(int type,byte[]xmldata,X509Certificate x509cert,int flag){
    	byte[]signResult = null;
		try {
			
			//3 签名,支持两种方式X509Data和RSAKeyValue，如是X509Data，cert不能为空，签名结果<keyInfo></keyInfo>内容为证书，
			//如果是RSAKeyValue，签名结果<keyInfo></keyInfo>内容为RSA公钥模数和指数
			if(type==1){
				signResult = XMLSignUtil.signXml(xmldata,"X509Data",x509cert,flag);
			}else{
				//第二种方式
				signResult = XMLSignUtil.signXml(xmldata,"RSAKeyValue",null,flag);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("签名失败:"+e.getMessage());
			e.printStackTrace();
			return null;
		}
		return signResult;
    }
   
}
