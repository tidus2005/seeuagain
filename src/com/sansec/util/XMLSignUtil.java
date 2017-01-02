package com.sansec.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyName;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jcp.xml.dsig.internal.dom.DOMX509Data;
import org.jcp.xml.dsig.internal.dom.XMLDSigRI;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.sansec.jce.provider.SwxaProvider;

import sun.security.x509.X509CertImpl;
/**
 * XML签名工具类
 * @author lvxingsheng
 *
 */
public class XMLSignUtil {
	    /**
	     * Xml签名<SHA1摘要算法>
	     * @param orignalData 原始业务报文数据XML格式
	     * @param keyInfoType 签名结果中KeyInfo的类型，支持两种，X509Data和RSAKeyValue
	     * @return W3C标准的Xml签名
	     * @throws Exception
	     */
	    public static byte[] signXml(byte[] orignalData,String keyInfoType,X509Certificate x509cert,int flag) throws Exception{
	    	//check key type
	    	if(keyInfoType==null||keyInfoType.length()<=0)
	    		throw new Exception("keyInfoType Error!Type should be X509Data or PublicKey");
	    	int type = 0;
	    	if(keyInfoType.equalsIgnoreCase("X509Data")){
	    		type = 1;
	    	}else if(keyInfoType.equalsIgnoreCase("RSAKeyValue")){
	    		type = 2;
	    	}else{
	    		throw new Exception("keyInfoType Error!Type should be X509Data or PublicKey");
	    	}
	    	if(orignalData==null||orignalData.length<=0){
	    		throw new Exception("OrignalData null error");
	    	}
	    	 // Create a DOM XMLSignatureFactory that will be used to generate the
	        // enveloped signature
	        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
	        // Create a Reference to the enveloped document (in this case we are
	        // signing the whole document, so a URI of "" signifies that) and
	        // also specify the SHA1 digest algorithm and the ENVELOPED Transform.
	        Reference ref = fac.newReference
	            ("", fac.newDigestMethod(DigestMethod.SHA1, null),
	             Collections.singletonList
	              (fac.newTransform
	                (Transform.ENVELOPED, (TransformParameterSpec) null)),
	             null, null);

	        // Create the SignedInfo
	        SignedInfo si = fac.newSignedInfo
	            (fac.newCanonicalizationMethod
	             (CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
	              (C14NMethodParameterSpec) null),
	             fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
	             Collections.singletonList(ref));
	       //get key from VSM
	        KeyPair kp = KeyTool.GetRSAKey(flag);
	        if(kp==null){
	        	throw new Exception("Get RSA Key of Index 1 Error");
	        }
	        // Create a KeyValue containing the RSA PublicKey/Certificate that was generated
	        KeyInfo ki = null;
	        switch(type){
	        case 1:
	        	KeyInfoFactory kif = fac.getKeyInfoFactory();
	            //X509Certificate x509cert = d2i_X509Cerificate(cert);
	            if(x509cert==null){
	            	throw new Exception("证书解析失败");
	            }
	            X509Data kd = kif.newX509Data(Collections.singletonList(x509cert));
	            KeyName keyName = kif.newKeyName(x509cert.getSerialNumber().toString());
				List<Object> contet = new ArrayList<Object>();
				contet.add(keyName);
	            // Create a KeyInfo and add the KeyValue to it
				contet.add(kd);
	            ki = kif.newKeyInfo(contet);
//	            ki = kif.newKeyInfo(Collections.singletonList(kd));
	        	break;
	        case 2:
	        	kif = fac.getKeyInfoFactory();
	            KeyValue kv = kif.newKeyValue(kp.getPublic());
	            // Create a KeyInfo and add the KeyValue to it
	            ki = kif.newKeyInfo(Collections.singletonList(kv));
	        	break;
	        default:
	        	;
	        }
	        // Instantiate the document to be signed
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        dbf.setNamespaceAware(true);
	        //Document doc = dbf.newDocumentBuilder().parse(new FileInputStream(dataFile));
	        Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(orignalData));
	        // Create a DOMSignContext and specify the DSA PrivateKey and
	        // location of the resulting XMLSignature's parent element
	        DOMSignContext dsc = new DOMSignContext(kp.getPrivate(), doc.getDocumentElement());
	        dsc.putNamespacePrefix("http://www.w3.org/2000/09/xmldsig#", "ds");
	        // Create the XMLSignature (but don't sign it yet)
	        XMLSignature signature = fac.newXMLSignature(si, ki);
	        // Marshal, generate (and sign) the enveloped signature
	        signature.sign(dsc);
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        TransformerFactory tf = TransformerFactory.newInstance();
	        Transformer trans = tf.newTransformer();
	        trans.transform(new DOMSource(doc), new StreamResult(out));
	    	return out.toByteArray();
	    }
	    public static boolean verifyXml(byte[] xmlData,byte[] publicKey) throws Exception{
//	    	Security.addProvider( new XMLDSigRI());
	    	// Instantiate the document to be validated
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        dbf.setNamespaceAware(true);
	        Document doc = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xmlData));
	        // Find Signature element
	        NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
	        if (nl.getLength() == 0) {
	            throw new Exception("Cannot find Signature element");
	        }
	        // Create a DOM XMLSignatureFactory that will be used to unmarshal the
	        // document containing the XMLSignature
	        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");//,"XMLDSig"
	        // Create a DOMValidateContext and specify a KeyValue KeySelector
	        // and document context
	        PublicKey pubKey=null;
	        DOMValidateContext valContext = null;
	        if(publicKey!=null&&publicKey.length>0){
	        	KeySpec  keySpec = new X509EncodedKeySpec(publicKey);  
	            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	            pubKey = keyFactory.generatePublic(keySpec);  
	            valContext = new DOMValidateContext(pubKey, nl.item(0));
	        }else{
	        	  valContext = new DOMValidateContext(new KeyValueKeySelector(), nl.item(0));
	        }
	        // unmarshal the XMLSignature
	        XMLSignature signature = fac.unmarshalXMLSignature(valContext);
	        // Validate the XMLSignature (generated above)
	        boolean coreValidity = signature.validate(valContext);
	        // Check core validation status
	        if (coreValidity == false) {
	            System.err.println("Signature failed core validation");
	            boolean sv = signature.getSignatureValue().validate(valContext);
	            System.out.println("signature validation status: " + sv);
	            // check the validation status of each Reference
	            Iterator i = signature.getSignedInfo().getReferences().iterator();
	            String errmsg = "";
	            for (int j=0; i.hasNext(); j++) {
	                boolean refValid = ((Reference) i.next()).validate(valContext);
	                errmsg +="ref["+j+"] validity status: " + refValid;
	            }
	            throw new Exception("签名验证失败:"+errmsg);
	        }
	    	return true;
	    }
	    /**
	     * KeySelector which retrieves the public key out of the
	     * KeyValue element and returns it.
	     * NOTE: If the key algorithm doesn't match signature algorithm,
	     * then the public key will be ignored.
	     */
	    private static class KeyValueKeySelector extends KeySelector {
	        public KeySelectorResult select(KeyInfo keyInfo,KeySelector.Purpose purpose,AlgorithmMethod method,XMLCryptoContext context)
	            throws KeySelectorException {
	            if (keyInfo == null) {
	                throw new KeySelectorException("Null KeyInfo object!");
	            }
	            SignatureMethod sm = (SignatureMethod) method;
	            List list = keyInfo.getContent();
	            for (int i = 0; i < list.size(); i++) {
	                XMLStructure xmlStructure = (XMLStructure) list.get(i);
	                if (xmlStructure instanceof KeyValue) {
	                    PublicKey pk = null;
	                    try {
	                        pk = ((KeyValue)xmlStructure).getPublicKey();
	                    } catch (KeyException ke) {
	                        throw new KeySelectorException(ke);
	                    }
	                    // make sure algorithm is compatible with method
	                    if (algEquals(sm.getAlgorithm(), pk.getAlgorithm())) {
	                        return new SimpleKeySelectorResult(pk);
	                    }
	                }
	                if(xmlStructure instanceof X509Data){
	                	PublicKey pk = null;
	                    List contents = ((X509Data)xmlStructure).getContent();
	                    int size = contents.size();
	                    for(int j=0;j<size;j++){
	                    	Object obj = list.get(j);
	                    	if(obj instanceof DOMX509Data){
	                    		DOMX509Data domx509 = (DOMX509Data)obj;
	                    		if(domx509.getContent().size()<1){
	                    			throw new KeySelectorException("No Certificate element found!");
	                    		}
	                    		Object contentObj = domx509.getContent().get(0);
	                    		if(contentObj instanceof X509CertImpl)
	                    		{
	                    			X509CertImpl certimpl = (X509CertImpl)contentObj;
	                    			pk = certimpl.getPublicKey();
	                    		}
	                    		break;
	                    	}else if(obj instanceof  String){
	                    		
	                    	}else if(obj instanceof byte[] ){
	                    		
	                    	}else if(obj instanceof  X509CRL){
	                    		
	                    	}
	                    }
	                    if(pk==null){
	                    	throw new KeySelectorException("Get public key from sign Document!");
	                    }
	                    // make sure algorithm is compatible with method
	                    if (algEquals(sm.getAlgorithm(), pk.getAlgorithm())) {
	                        return new SimpleKeySelectorResult(pk);
	                    }
	                }
	            }
	            throw new KeySelectorException("Get public key from sign Document!");
	        }

	        //this should also work for key types other than DSA/RSA
	        static boolean algEquals(String algURI, String algName) {
	            if (algName.equalsIgnoreCase("DSA") &&
	                algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1)) {
	                return true;
	            } else if (algName.equalsIgnoreCase("RSA") &&
	                       algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1)) {
	                return true;
	            } else {
	                return false;
	            }
	        }
	    }

	    private static class SimpleKeySelectorResult implements KeySelectorResult {
	        private PublicKey pk;
	        SimpleKeySelectorResult(PublicKey pk) {
	            this.pk = pk;
	        }
	        public Key getKey() { return pk; }
	    }
}
