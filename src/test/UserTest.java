package test;

import org.junit.Test;

import joy.aksd.ECC.ECC;
import joy.aksd.data.Block;
import joy.aksd.data.Record;
import joy.aksd.listenAndVerifyThread.*;
import joy.aksd.listenAndVerifyThread.verifyThread;
import joy.aksd.tools.DatabaseHelper;
import sun.security.ec.ECPrivateKeyImpl;
import sun.security.ec.ECPublicKeyImpl;

import static joy.aksd.data.dataInfo.*;
import static joy.aksd.data.protocolInfo.QUERYSTAMPANDTIME;
import static joy.aksd.data.protocolInfo.RECIVERECORD;
import static joy.aksd.tools.toByte.hexStringToByteArray;
import static joy.aksd.tools.toByte.intToByte;
import static joy.aksd.tools.toInt.byteToInt;
import static joy.aksd.tools.toString.byteToString;
import static joy.aksd.data.dataInfo.getTime;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import javax.crypto.KeyGenerator;


public class UserTest {
	@Test 
	public void jdbctest()
	{
		if(DatabaseHelper.JDBCexit())
			System.out.println(1);
		else
			System.out.println(2);
	}
	@Test
	public void sockettest() throws IOException
	{
		int port=49999;
		ServerSocket server=new ServerSocket(port);
		Socket socket = server.accept();
		InputStream inputStream = socket.getInputStream();
	    byte[] bytes = new byte[1024];
	    int len;
	    StringBuilder sb = new StringBuilder();
	    //只有当客户端关闭它的输出流的时候，服务端才能取得结尾的-1
	    while ((len = inputStream.read(bytes)) != -1) {
	      // 注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
	      sb.append(new String(bytes, 0, len, "UTF-8"));
	    }
	    System.out.println("get message from client: " + sb);

	    OutputStream outputStream = socket.getOutputStream();
	    outputStream.write("Hello Client,I get the message.".getBytes("UTF-8"));

	    inputStream.close();
	    outputStream.close();
	    socket.close();
	    server.close();
	}
	@Test
	public void listenertest()
	{
		 new Listener().run();
		 
	}
	
	@Test
	public void ctest() throws Exception{
		KeyPairGenerator kpg;
        kpg=KeyPairGenerator.getInstance("EC","SunEC");
        ECGenParameterSpec ecsp;
        ecsp=new ECGenParameterSpec(ECNAME);
        kpg.initialize(ecsp);

        
        KeyPair keyPair=kpg.generateKeyPair();
        ECPrivateKeyImpl priKey= (ECPrivateKeyImpl) keyPair.getPrivate();
        ECPublicKeyImpl pubKey= (ECPublicKeyImpl) keyPair.getPublic();

        byte[] x = hexStringToByteArray(String.format("%040x", pubKey.getW().getAffineX()));
        byte[] y = hexStringToByteArray(String.format("%040x", pubKey.getW().getAffineY()));
        byte[] result = new byte[x.length + y.length];
        System.arraycopy(x, 0, result, 0, x.length);
        System.arraycopy(y, 0, result, x.length, y.length);
        System.out.println(MessageDigest.getInstance("SHA-256").digest(result));
		String sql="insert into test(num) values('"+y+"')";
		DatabaseHelper.execute(sql);
		
	}
	@Test
	public void SHA256test() throws NoSuchAlgorithmException{
		MessageDigest sa=MessageDigest.getInstance("SHA-256");
		String h="EnjoyTheDeath";
		byte[] s=h.getBytes();
		byte[] hash;
		sa.update(s);
		hash=SHA256x.digest(s);
			System.out.println(byteToString(hash));
	
		
	}
	@Test
	public void changetest() throws UnsupportedEncodingException{
		int time=getTime();
		byte[] t=intToByte(time);
		//String mac="78acc097ca28";
		//byte[] b=mac.getBytes();
		//byte[] b=""
		//String str=byteToString(b);//new String(b);
		//System.out.println(str);
		System.out.println(time);
		System.out.println(byteToInt(t));
	}
	
/*	@Test
	public Boolean checkdataTest() throws Exception{//检查区块链是否被改动过
		LinkedList<Block> block=blocks;
		int recordcount=0;
		Block last =block.getLast();
		recordcount+=byteToInt(last.getRecordCount());
		ArrayList<Integer> ai=new ArrayList();
		block.removeLast();
		Block flast=block.getLast();
		byte[] fldata=flast.getData();
		if(last.getMerkle()!=generateMerkle(recordcount))
			return false;
		if(SHA256x.digest(fldata)==last.getLastHash())
		{
			block.addLast(flast);
			return checkdataTest();
		}
		else
		{
			byte[] tem=flast.getBlockNumber();
			ai.add(byteToInt(tem));
			block.addLast(flast);
		   	return false;
		}
	}
	 private byte[] generateMerkle(int recordcount) {
	        ArrayDeque<byte []> result=new ArrayDeque<>();
	        int bytes=0;
	        //取出纪录
	        synchronized (unPackageRecord) {
	            
	            for(int i=0;i<recordcount;i++){
	                Record record=unPackageRecord.get(unPackageRecord.size()-i);
	                byte []tem=record.getBytesData();
	                bytes+=tem.length;
	                result.add(tem);
	            }
	        }
	        //开始计算merkle tree root
	        MessageDigest digest= null;
	        try {
	            digest = MessageDigest.getInstance("SHA-256");
	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        }
	        for (int i=0;i<result.size();i++){
	            byte tem[]=result.removeFirst();
	            tem=digest.digest(tem);
	            result.addLast(tem);
	        }
	        while (result.size()!=1){
	            ArrayDeque<byte []> temResult=new ArrayDeque<>();
	            while (!result.isEmpty()){
	                byte []left=result.removeFirst();
	                byte []right=null;
	                try {
	                    right = result.removeFirst();
	                }catch (NoSuchElementException e){}
	                if (right==null){
	                    temResult.addLast(digest.digest(left));
	                }
	                else {
	                    byte []tem=new byte[left.length+right.length];
	                    System.arraycopy(left,0,tem,0,left.length);
	                    System.arraycopy(right,0,tem,left.length,right.length);
	                    temResult.addLast(digest.digest(tem));
	                }
	            }
	            result=temResult;
	        }
	        return result.getFirst();
	    }*/
	@Test
	public void c(){
		byte[] bytes =new byte[2];
	}

}
