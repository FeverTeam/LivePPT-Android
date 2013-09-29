package net.cloudslides.app.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import android.util.Log;
/**
 * AES加密
 * @author Felix
 */
public class AESEnc {
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();	
	
	 public static String bytesToHex(byte[] bytes) {
	        char[] hexChars = new char[bytes.length * 2];
	        int v;
	        for ( int j = 0; j < bytes.length; j++ ) {
	            v = bytes[j] & 0xFF;
	            hexChars[j * 2] = hexArray[v >>> 4];
	            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	        }
	        return new String(hexChars);
	    } 
	 
	public static String encryptAES(String value,String privateKey) {
		    byte[] raw=null;
		    byte[] result=null;
			try 
			{
				raw = privateKey.getBytes("utf-8");
				
				SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
				
				Cipher cipher = Cipher.getInstance("AES");
				
				cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
				
				result=cipher.doFinal(value.getBytes("utf-8"));
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				e.printStackTrace();
			}
			Log.i("加密结果:",bytesToHex(result));
            Log.i("seed",privateKey);            
			return bytesToHex(result);		   
		  }	
}
