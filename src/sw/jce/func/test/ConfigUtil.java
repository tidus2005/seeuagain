package sw.jce.func.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import com.sansec.util.KeyTool;
/**
 * 读取配置
 * @author lvxingsheng
 *
 */
public class ConfigUtil {
	public static String curentPath;
	static{
		File directory	= new File(".");
		try {
			curentPath = directory.getCanonicalPath()+File.separator+"ConfigPath"+File.separator;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static final String conffigFile=curentPath+"testconfig.ini";
	public static final String XMLDATAFILE=curentPath+"data.xml";
	public static final String XMLSIGNRESULTFILE=curentPath+"signResult";
	private static Properties property;
	private static String  rsacertsuffix;
	public static X509Certificate rsacert=null;
	public static boolean loadConfig(){
		try {
			File configPath = new File(conffigFile);
			if(!configPath.exists()){
				System.out.println("配置文件"+configPath+"不存在");
				return false;
			}
			FileInputStream filein = new FileInputStream(configPath);
			property = new Properties();
			property.load(filein);
			String value = property.getProperty("rsacertsuffix");
			if(value==null||value==""){
				System.err.println("RSA证书后缀配置项不存在");
				return false;
			}
			rsacertsuffix = value;
			value = property.getProperty("rsakeyindex");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("读取配置文件异常:"+e.getMessage());
			return false;
		}
		
		return true;
	}
	public static boolean loadCert(int type){
		if(type==1){
			String rsacertfile=curentPath+"rsacert."+rsacertsuffix;
			File file = new File(rsacertfile);
			if(!file.exists()){
				System.out.println("证书"+rsacertfile+"不存在");
				return false;
			}
			rsacert = KeyTool.d2i_X509Cerificate(rsacertfile);
			if(rsacert==null){
				System.out.println("证书解析错误");
				return false;
			}
			System.out.println("加载证书"+rsacertfile+"成功");
		}else if(type==2){
			//sm2证书
		}else{
			System.out.println("类型错误");
			return false;
		}
		return true;
	}
}
