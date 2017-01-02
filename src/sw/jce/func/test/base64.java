package sw.jce.func.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import sun.misc.BASE64Decoder;

public class base64 {

	public base64() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String privatekey = "09ctFgLuxfAnsZywoZXpEQoGF0M42jNhtdpGMZyAktoHd3g/JSa6IKbTvhlmiM8q";
		String publickey="A0IABFmYlnduVN2d8H0gZ/EUBKWRrC220bMdFBl8SCDJTTRBl8JpyvvEg9Ny7cUbZBo+Ld/iFw2y0O8X7KxLT0bB3Ao=";
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] rest = decoder.decodeBuffer(privatekey);
			FileOutputStream file = new FileOutputStream("F:\\privatekey.der");
			file.write(rest);
			file.close();
			file = new FileOutputStream("F:\\publickey.der");
			rest = decoder.decodeBuffer(publickey);
			file.write(rest);
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
