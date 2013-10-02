package net.cloudslides.app.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import android.util.Log;
/**
 * Hmac-Sha1 hash
 * @author Felix
 *
 */
public class HmacSha1Signature {
	
	  	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	 
		public static String encryptHMAC_SHA(String data, String key){
			String result = null;
			try {
				SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
				Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
				mac.init(signingKey);

				byte[] digest = mac.doFinal(data.getBytes());

		        StringBuilder sb = new StringBuilder(digest.length*2);
		        String s;
		        for (byte b : digest){
		        	s = Integer.toHexString(0xFF & b);
		        	if(s.length() == 1) {
		        		sb.append('0');
		        	}
		        	sb.append(s);
		        }

		        result = sb.toString();

			} catch (Exception e) 
			{
				
			}

			Log.i("签名结果:",result);
			Log.i("Seed",key);
			return result;
	}
}

