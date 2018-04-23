package joy.aksd.tools;

import static joy.aksd.data.dataInfo.effectiveRecord;
import static joy.aksd.listenAndVerifyThread.Listener.*;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.ECPoint;
import java.util.Arrays;

import joy.aksd.ECC.ECC;
import joy.aksd.coreThread.BroadcastRecord;
import joy.aksd.data.Record;
import sun.security.ec.ECPublicKeyImpl;

public class checkRecord {
	
	public static  boolean verifyScriptRecord(Record record) {
		
	    MessageDigest digest= null;
	    try {
	        digest = MessageDigest.getInstance("SHA-256");
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    }
	    //获得解锁脚本
	    byte [] unLockScrpit=record.getUnLockScript();       
	    byte [] LockScript=record.getLockScript(); //获得锁定脚本
	    //验证锁定脚本
	    byte [] tem=new byte[40];
	    System.arraycopy(unLockScrpit,0,tem,0,40);
	    byte [] temHash=digest.digest(tem);//
	    if (!Arrays.equals(temHash,LockScript))
	        return false;
	    System.out.println("锁定脚本验证成功");
	    //验证签名
	    tem=new byte[14];
	    byte [] mac=record.getMac();
	    byte [] orderStamp=record.getOrderStamp();
	    byte [] time=record.getTime();
	    byte [] verifingSign=new byte[unLockScrpit.length-40];
	    byte [] x=new byte[20];
	    byte [] y=new byte[20];
	    System.arraycopy(unLockScrpit,0,x,0,20);
	    System.arraycopy(unLockScrpit,20,y,0,20);
	    System.arraycopy(unLockScrpit,40,verifingSign,0,verifingSign.length);
	    System.arraycopy(mac,0,tem,0,6);
	    System.arraycopy(orderStamp,0,tem,6,4);
	    System.arraycopy(time,0,tem,10,4);
	    temHash= digest.digest(tem);
	    boolean result=false;
	    try {
	        ECPublicKeyImpl publicKey = new ECPublicKeyImpl(new ECPoint(new BigInteger(1, x), new BigInteger(1, y)), ECC.spec);
	        Signature s = Signature.getInstance("SHA1withECDSA", "SunEC");
	        s.initVerify(publicKey);
	        s.update(temHash);
	        result= s.verify(verifingSign);
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } catch (SignatureException e) {
	        e.printStackTrace();
	    } catch (NoSuchProviderException e) {
	        e.printStackTrace();
	    } catch (InvalidKeyException e) {
	        e.printStackTrace();
	    }
	    System.out.println("签名验证"+ result);
	    return result;
	}

}

