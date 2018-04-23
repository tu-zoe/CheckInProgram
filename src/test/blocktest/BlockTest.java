package test.blocktest;

import org.junit.Test;
import org.junit.internal.ArrayComparisonFailure;

import joy.aksd.data.Block;
import joy.aksd.data.Record;
import joy.aksd.tools.DatabaseHelper;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import sun.security.ec.ECPrivateKeyImpl;
import sun.security.ec.ECPublicKeyImpl;

import static test.blocktest.Powmodule.init;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static joy.aksd.tools.toInt.byteToInt;
import static joy.aksd.tools.toString.byteToString;
import static joy.aksd.tools.toString.tostring;
import static joy.aksd.data.dataInfo.blocks;
import static joy.aksd.data.dataInfo.indexBlock;
import static joy.aksd.data.dataInfo.location;
import static joy.aksd.tools.toByte.intToByte;
import static joy.aksd.tools.readAndPrintData.printRecord;

public class BlockTest {
	@Test
	public void test() throws InterruptedException
	{
		
		CoreTest ctest=new CoreTest();
		
		ctest.run();
	}
	@Test
	public void sqltest()
	{
		String sql="truncate table record_copy";
		DatabaseHelper.execute(sql);
	}
	@Test
	public void qtest()
	{
		Qurry qurry=new Qurry();
		ArrayList<Record> record=new ArrayList<Record>();
		record=qurry.QurryRecordByLockScript(5);
		for(int i=0;i<5;i++)
		{
			byte[] l=new byte[2];
			byte[] t=record.get(i).getBytesData();
			System.arraycopy(t, 2, l, 0, 2);
			byte[] tem=new byte[byteToInt(t)];
			System.arraycopy(t, 2, tem, 0, t.length-2);
			Record r=new Record(tem);
		    printRecord(r);
		}
		
	}
	@Test
	public void recovertest(){//检查sql中数据正确性，不正确则从block中恢复
		Qurry qurry=new Qurry();
		ReadBlock read=new ReadBlock();
		WriteIntoSql writesql=new WriteIntoSql();
		LinkedList<Block> block=new LinkedList<Block>();
		LinkedList<Record> ar=new LinkedList<Record>();
		block=qurry.findallBlock();
		ar=qurry.findallRecord();
		try {
			boolean b=qurry.checkblock(block, ar);
			if(!b)
			{
				LinkedList<Block> blocklist=new LinkedList<Block>();
				LinkedList<Record> recordlist=new LinkedList<Record>();
				read.readblock(blocklist, recordlist);
				writesql.recoverBlock(blocklist, recordlist);
			}
			else
				System.out.println(b);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void readtest()
	{
		//LinkedList<Integer> linkedList = new LinkedList<>();
		//for(int i=0;i<7;i++)
			//linkedList.add(i);
		ReadBlock rb=new ReadBlock();
		LinkedList<Block> block=new LinkedList<Block>();
		LinkedList<Record> ar=new LinkedList<Record>();
		try {
			rb.readblock(block, ar);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i=0;i<block.size();i++)
		{
			System.out.println(byteToInt(block.get(i).getNonce()));
			//System.out.println(linkedList.get(i));
		}
		for(int i=0;i<ar.size();i++)
		{
			System.out.println(byteToInt(ar.get(i).getOrderStamp()));
		}

	}
	@Test
	public void btest()
	{
		Qurry q=new Qurry();
		LinkedList<Block> lb=new LinkedList<Block>();
		lb=q.findallBlock();
		Block block=lb.getFirst();
		byte[] b=new byte[2];
		System.arraycopy(block.getBlockDatas(), 0, b, 0, 2);
		System.out.println(byteToInt(b));
		System.out.println(byteToString(block.getLastHash()));
	}
	@Test
	public void mactest() throws InterruptedException, NoSuchAlgorithmException{
		Crecord crecord=new Crecord();
		ECPublicKeyImpl publicKey = null;
        ECPrivateKeyImpl privateKey = null;
        try {
            KeyPair keyPair = crecord.getKeyPair();
            publicKey = (ECPublicKeyImpl) keyPair.getPublic();
            privateKey = (ECPrivateKeyImpl) keyPair.getPrivate();
        } catch (Exception e) {
            System.out.println("读取秘钥对失败");
            return;
        }
		Record record=new Record();
		
		record=crecord.registRecord(publicKey, privateKey);
		byte[] mac=crecord.getMacAddress();
		System.out.println(mac);
		System.out.println(byteToString(mac));
		System.out.println(memcmp(record.getMac(),mac,6));
	}
	
	@Test
	public void testfind() throws Exception
	{
		Qurry qurry=new Qurry();
		LinkedList<Block> blocklist=new LinkedList<Block>();
		LinkedList<Record> ar=new LinkedList<Record>();
		boolean b=false;
		blocklist=qurry.findallBlock();
		ar=qurry.findallRecord();
		b=qurry.checkblock(blocklist,ar);
		System.out.println(b);
		/*
		Block block=blocklist.get(0);
		Block b1=blocklist.get(1);MessageDigest sha=MessageDigest.getInstance("SHA-256");
        byte[] lashHash=block.getLastHash();
        byte[] merkle=block.getMerkle();
        byte[] time=block.getTime();
        byte difficulty=block.getDifficulty();
        byte[]nonce=block.getNonce();
        byte[] tem=new byte[lashHash.length+merkle.length+time.length+1+nonce.length];
        System.arraycopy(lashHash,0,tem,0,lashHash.length);
        System.arraycopy(merkle,0,tem,lashHash.length,merkle.length);
        System.arraycopy(time,0,tem,lashHash.length+merkle.length,time.length);
        tem[lashHash.length+merkle.length+time.length]=difficulty;
        System.arraycopy(nonce,0,tem,lashHash.length+merkle.length+time.length+1,nonce.length);
        sha.update(tem);
        byte []result=sha.digest(tem);
        System.out.println(Arrays.equals(result, b1.getLastHash()));*/
	}
	@Test
	public void dequetest(){
		Qurry qurry=new Qurry();
		LinkedList<Record> ar=new LinkedList<Record>();
		ar=qurry.findallRecord();
		ArrayDeque<byte []> adr=new ArrayDeque<byte []>();
		adr=qurry.findBlockdata(2, ar);
		for(byte[] b:adr)
		{
	       	 System.out.println(byteToString(b));
			}
	}
	@Test
	public void codetest(){
		byte ab=120;
		Crecord crecord=new Crecord();
		 byte[] macAddress = crecord.getMacAddress();
		 for(byte b:macAddress)
		 {
			 System.out.println(b);
		 }
		 System.out.println(byteToInt(macAddress));
		 Base64.Decoder decoder=Base64.getDecoder();
		 Base64.Encoder encoder=Base64.getEncoder();
		 String str=encoder.encodeToString(macAddress);
		 System.out.println(str);
		 byte[] macb=decoder.decode(str);
		 for(byte b:macb)
		 {
			 System.out.println(b);
		 }
		 String sql="insert into test(test) values('"+str+"')";
		 DatabaseHelper.execute(sql);
		 System.out.println(byteToString(macb));
		 System.out.println(tostring(macb));
		 System.out.println(Integer.toHexString(ab&0xff));
	}
	@Test
	public void ctest(){
		Crecord crecord=new Crecord();
		byte[] macAddress = crecord.getMacAddress();
		String sql="select * from test";
		ResultSet rs=DatabaseHelper.query(sql);
		String str=new String();
		int time=0;
		try {
			while(rs.next())
			str= rs.getString(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Base64.Decoder decoder=Base64.getDecoder();
		byte[] mac=str.getBytes();//decoder.decode(str);
		for(byte b:mac)
		System.out.println(b);
		for(byte b:macAddress)
			System.out.println(b);
		System.out.println(Arrays.equals(mac,macAddress));
	}
	
	@Test
	public void qurrytest() throws UnsupportedEncodingException
	{
		 
		Crecord crecord=new Crecord();
		Base64.Decoder decoder=Base64.getDecoder();
		Base64.Encoder encoder=Base64.getEncoder();
		ECPublicKeyImpl publicKey = null;
        ECPrivateKeyImpl privateKey = null;
        try {
            KeyPair keyPair = crecord.getKeyPair();
            publicKey = (ECPublicKeyImpl) keyPair.getPublic();
            privateKey = (ECPrivateKeyImpl) keyPair.getPrivate();
        } catch (Exception e) {
            System.out.println("读取秘钥对失败");
            return;
        }
	
		 byte[] macAddress = crecord.getMacAddress();
	        if (macAddress == null) {
	            System.out.println("mac 获取错误");
	            macAddress=new byte[6];
	        }
	        byte[] lockscript=new byte[32];
	        try {
				lockscript=crecord.getLockScript(publicKey);
			} catch (NoSuchAlgorithmException e1) {
				
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	       
	        String macb=byteToString(macAddress);
	        System.out.println(macb);
	        String sql="select * from record where mac='"+encoder.encodeToString(macAddress)+"'";
	        ResultSet rs = DatabaseHelper.query(sql);
	        ArrayList<Record> ar=new ArrayList<Record>();
	        
	        try
	        {
	        	while(rs.next())
	        	{
	        		
	        		int orderstamp=rs.getInt("orderStamp");
	        		int time=rs.getInt("time");
	        		String mac=rs.getString("mac");
	        		//String lock=rs.getString("lockScript");
	        		String unlock=rs.getString("unLockScript");
	        		int num=rs.getInt("blocknum");
	        		
	        		Record record=new Record();
	        		System.out.println(Arrays.equals(macAddress, decoder.decode(mac)));
	        		
	        		record.setMac(decoder.decode(mac));
	        		record.setOrderStamp(intToByte(orderstamp));
	        		record.setTime(intToByte(time));
	        		record.setLockScript(lockscript);
	        		record.setUnLockScript(decoder.decode(unlock));
	        		record.setBlocknum(num);
	        		ar.add(record);
	        	}
	        }catch (SQLException e) {
				e.printStackTrace();
			}
	        
	        
	}
	
	@Test
	public void recordtest() throws InterruptedException{
		Crecord crecord=new Crecord();
		crecord.start();
	}
	@Test
	public void difficulttest()
	{
		byte difficulty=(byte)0xff;
		int num=difficulty&0xff;
        int tem1=num/8;
        int tem2=num%8;
        byte[] target;
        if (tem2==0){
            target=new byte[tem1];
            init(target);
        }
        else {
            target=new byte[tem1+1];
            init(target);
            switch (tem2){
                case 1:
                    target[tem1]=0x7f;
                    break;
                case 2:
                    target[tem1]=0x3f;
                    break;
                case 3:
                    target[tem1]=0x1f;
                    break;
                case 4:
                    target[tem1]=0x0f;
                    break;
                case 5:
                    target[tem1]=0x07;
                    break;
                case 6:
                    target[tem1]=0x03;
                    break;
                case 7:
                    target[tem1]=0x01;
                    break;
            }
        }
        System.out.println(byteToInt(target));
	}
	public static boolean memcmp(byte[] data1, byte[] data2, int len) {
		  if (data1 == null && data2 == null) {
		   return true;
		  }
		  if (data1 == null || data2 == null) {
		   return false;
		  }
		  if (data1 == data2) {
		   return true;
		  }

		  boolean bEquals = true;
		  int i;
		  for (i = 0; i < data1.length && i < data2.length && i < len; i++) {
		   if (data1[i] != data2[i]) {
		    bEquals = false;
		    break;
		   }
		  
		  }
		  return bEquals;
	}
}
