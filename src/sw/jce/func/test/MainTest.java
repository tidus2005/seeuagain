package sw.jce.func.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.Provider;
import java.security.Security;
import java.util.Scanner;

public class MainTest extends Provider{

	protected MainTest(String name, double version, String info) {
        super(name, version, info);
    }

    public static void main(String[] args) {
	    
	    
	    Provider[] providers = Security.getProviders();
	    
	    for(Provider provider : providers){
	        System.out.println(provider.getName());
	    }
	    
	    System.out.println("======abc======");
	    
	    
	    Security.addProvider(new com.sun.crypto.provider.SunJCE());
	    Security.addProvider(new sun.security.smartcardio.SunPCSC());
	    Security.insertProviderAt(new com.sansec.jce.provider.SwxaProvider(), 2);
	    providers = Security.getProviders();
	    for(Provider provider : providers){
            System.out.println(provider.getName());
        }
	    
		// TODO Auto-generated method stub
		Scanner sc= new Scanner(System.in);
		System.out.println("加载配置文件:"+ConfigUtil.conffigFile);
		boolean res = ConfigUtil.loadConfig();
		if(res==true){
			System.out.println("加载配置文件成功");
		}else{
			System.out.println("加载配置文件失败");
			return;
		}
		boolean realcert = ConfigUtil.loadCert(1);
		if(realcert==true)
			System.out.println("加载证书成功");
		else
			System.out.println("加载证书失败");
		
		while ( true ) {
			int choice = -1;
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++");
			System.out.println("+++++++++++++++三未 云密码机功能测试+++++++++++++++++");
			System.out.println("                                                   ");
			System.out.println(" 1 RSA字符串签名验证测试                                                                          ");
			System.out.println(" 2 RSA签名XML文档                                                                                           ");
			System.out.println(" 3 RSA验证XML文档                                                                                           ");
			System.out.println("                                                   ");
			System.out.println(" 0 返回                                                                                                                       ");
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++");
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++");
			choice = sc.nextInt();
			if (choice == 0) {
				return;
			}
			if ((choice < 1) || (choice > 3)) {
				continue;
			}
			int flag = 10001;
			switch (choice) {
			case 1:
				flag = getFlag();
				RSASignAndVerifyTest.functionTest(flag);
				break;
			case 2:
				flag = getFlag();
				XMLSignTest.functionTest(flag);
				break;
			case 3:
				XMLVerifyTest.functionTest();
			}
		}
	}
	
	public static int getFlag(){
		int flag = 10001;
        while( true ) {
        	System.out.print("请输入5位标识串(默认10001): ");
        	try {
				String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
				int i = Integer.parseInt(str);
				if("".equals(str) || i<10001 || i>32699) {
					continue;
				} else {
					flag = i;
					break;
				}
			} catch (Exception e) {
				break;
			}
        }
        return flag;
    }

}
