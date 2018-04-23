package test.blocktest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.junit.Test;

import joy.aksd.data.Record;
import joy.aksd.tools.DatabaseHelper;
import joy.aksd.tools.readAndPrintData;
import sun.security.ec.ECPrivateKeyImpl;
import sun.security.ec.ECPublicKeyImpl;

import static joy.aksd.data.dataInfo.*;
import static joy.aksd.tools.readAndPrintData.printRecord;
import static joy.aksd.tools.toByte.hexStringToByteArray;
import static joy.aksd.tools.toInt.byteToInt;

public class Ctest {
	private static String ip;
    private static int port=49998;
    static {
        Scanner sc= null;
        try {
            sc = new Scanner(new File("./ip.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("error in set ip");
        }
        String tem=sc.nextLine().trim();
        sc.close();
        ip=tem;
        System.out.println(ip);
    }
	@Test
	public void qurrytest() throws IOException//预期效果先在本机搜索，没有结果数目不够再跳转
	{
		int count=10;
        byte[] lockScript = new byte[0];
        try {
            lockScript = getLockScript();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SearchpcRecord(lockScript, count);
        if(count!=0)
        {
			Socket socket=new Socket(ip,port);
			InputStream in=socket.getInputStream();
	        OutputStream out=socket.getOutputStream();
	        out.write(0x10);
	        out.write(lockScript);
	        ArrayList<Record> ar=new ArrayList<Record>();
	        try {
	            while (true) {
	                byte[] tem = new byte[2];
	                in.read(tem);
	                tem = new byte[byteToInt(tem)];
	                in.read(tem);
	                Record record=new Record(tem);
	                ar.add(record);
	                
	            }
	        }catch (Exception e){
	        	e.printStackTrace();
	        }
	       for(int i=0;i<ar.size();i++)
	    	   printRecord(ar.get(i));
	       socket.close();
        }
	}
	@Test
	public void sockettest() throws IOException
	{
		String host = "10.170.39.225";
	    int port = 49999;
	    // 与服务端建立连接
	    Socket socket = new Socket(host, port);
	    // 建立连接后获得输出流
	    OutputStream outputStream = socket.getOutputStream();
	    String message = "你好  yiwangzhibujian";
	    socket.getOutputStream().write(message.getBytes("UTF-8"));
	    //通过shutdownOutput高速服务器已经发送完数据，后续只能接受数据
	    socket.shutdownOutput();
	    
	    InputStream inputStream = socket.getInputStream();
	    byte[] bytes = new byte[1024];
	    int len;
	    StringBuilder sb = new StringBuilder();
	    while ((len = inputStream.read(bytes)) != -1) {
	      //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
	      sb.append(new String(bytes, 0, len,"UTF-8"));
	    }
	    System.out.println("get message from server: " + sb);
	    
	    inputStream.close();
	    outputStream.close();
	    socket.close();
	}
	public ArrayList<Record> qurryRinlist(ArrayList<Record> ar,int count,byte[] lockScrpit)
	{
		ArrayList<Record> rl=new ArrayList<Record>();
		int size=ar.size();
    	for(int i=0;i<size;i++){
    		if(count==0)
    			break;
    		if (Arrays.equals(ar.get(size-1-i).getLockScript(),lockScrpit)){
                rl.add(ar.get(size-1-i));
                count--;
            }
    	  }
	    
		return rl;
	}
	 public void SearchpcRecord(byte lockScrpit[],int count) throws IOException//新加的，通过sql查询块内数据
	    {
	    	Qurry qurry=new Qurry();
	    	ArrayList<Record> copyl=new ArrayList<Record>();//复制全局List的数据
	    	ArrayList<Record> rtbs=new ArrayList<Record>();//要发送的数据
	    	synchronized (identifedRecord){
	    		copyl.addAll(identifedRecord);
	    	}
	    	int size=copyl.size();
	    	System.out.println(1);
	    	 if(size!=0)
	    	 {
	    	rtbs=qurryRinlist(copyl, count, lockScrpit);
	    	for(Record record:rtbs){
	    		readAndPrintData.printRecord(record);
	    	}
	    	copyl.clear();
	    	rtbs.clear();
	    	 }
	    	 else
	    		 System.out.println("ir has no element");
	    	synchronized (unPackageRecord){
	    		copyl.addAll(unPackageRecord);
	    	}
	    	 size=copyl.size();
	    	 if(size!=0)
	    	 {
	    		 rtbs=qurryRinlist(copyl, count, lockScrpit);
	    	
	    	for(Record record:rtbs){
	    		readAndPrintData.printRecord(record);
	    	}
	    	
	    	 }
	    	 else
	    		 System.out.println("upr has no element");
	    	if(count==0)
	    		return;
	    	if(DatabaseHelper.JDBCexit())
	    	{
	    	ArrayList<Record> recordToBeSent=new ArrayList<>();
	    	recordToBeSent=qurry.QurryRecordByLockScript(count);//lockScrpit,
	    	for (Record record:recordToBeSent){
	    		readAndPrintData.printRecord(record);
	        }
	    	}
	    	else
	    	{
	    		System.out.println("no sql");
	    	}
	    }
	 private byte[] getLockScript() throws IOException, NoSuchAlgorithmException {
	        DataInputStream in = new DataInputStream(new FileInputStream("./key"));
	        in.readUTF();
	        String second = in.readUTF();
	        String third = in.readUTF();
	        in.close();
	        byte []x=hexStringToByteArray(second);
	        byte []y=hexStringToByteArray(third);
	        byte[] result = new byte[x.length + y.length];
	        System.arraycopy(x, 0, result, 0, x.length);
	        System.arraycopy(y, 0, result, x.length, y.length);
	        return MessageDigest.getInstance("SHA-256").digest(result);
	    }
}
