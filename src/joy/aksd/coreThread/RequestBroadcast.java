package joy.aksd.coreThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static joy.aksd.data.dataInfo.*;
import static joy.aksd.data.protocolInfo.DOWNLOADBLOCK;


public class RequestBroadcast{
	/* 从其他节点下载区块后恢复，下载区块数量为cacheBlockCount*/
	private static ExecutorService RequestBroadCast = Executors.newFixedThreadPool(10);
	private String ip;
	private int ttl = 6;
	
	public RequestBroadcast() {
		// TODO Auto-generated constructor stub
	}
	public void start(){
		System.out.println("start boratcast request……");
		System.out.println("list size is"+IPList.size());
		System.out.println("ttl is" + ttl);
		ArrayList<String> iplist=new ArrayList<>();
		synchronized(IPList){
			iplist.addAll(IPList);
		}
		for (String ip:iplist){			
			if(ip.equals(ip)){
				RequestBroadCast.execute(new RequestBroadcastThread(ip,this.ttl));
			}
		}
	}
}
class RequestBroadcastThread implements Runnable{
	private int RequestBlocknum;
	private String ip;
	private int ttl;
	public RequestBroadcastThread(String ip,int ttl){
		this.ip=ip;
		this.ttl=ttl;
	}
	@Override
	public void run(){
		System.out.println("boradcast request to" + ip);
		Socket socket = null;
		OutputStream out = null;
		InputStream in = null;
		try{
			socket=new Socket(this.ip,PORT);
            out = socket.getOutputStream();	
            out.write(DOWNLOADBLOCK);
            System.out.println("the request is boradcasting……");
		}catch(IOException e){
			System.out.println("request boradcast to " + this.ip + " is error");
		}
	}
}





	
	
