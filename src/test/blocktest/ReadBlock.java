package test.blocktest;

import static joy.aksd.data.dataInfo.location;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayDeque;
import java.util.Base64;
import java.util.LinkedList;


import joy.aksd.data.Block;
import joy.aksd.data.Record;

import static joy.aksd.tools.toByte.intToByte;
import static joy.aksd.tools.toInt.byteToInt;
import static joy.aksd.tools.toString.byteToString;

public class ReadBlock {

	public void readblock(LinkedList<Block> blocklist) throws FileNotFoundException{//,LinkedList<Record> recordlist
		//读取和建立索引可以分开，读取可以考虑是否需要记录的链表
		
    	
		RandomAccessFile file=new RandomAccessFile("blockTest","r");
        try {
        	while(file.getFilePointer()!=file.length())
        	{
        	byte[] head=new byte[2];
            file.read(head);
            byte[] tem=new byte[byteToInt(head)];
            file.read(tem);
            Block block=new Block(tem);
            blocklist.add(block);
           /* if(byteToInt(recordcount)!=0)
            {
            	ArrayDeque<byte []> result=new ArrayDeque<>();
     	        int bytes=0;
     	      
            	for(int i=0;i<byteToInt(recordcount);i++)
            	{
            		Record record=new Record();
            		byte[] rhead=new byte[2];
                	byte[] mac=new byte[6];
                	byte[] rtime=new byte[4];
                	byte []orderStamp=new byte[4];
                    byte []lockScript=new byte[32];//32
                    byte []unLockScript;//80-100
            		 byte[] tem;
            		file.read(rhead);
            		file.read(mac);
            		file.read(orderStamp);
            		file.read(rtime);
            		file.read(lockScript);
            	    int l=byteToInt(rhead)-6-4-4-32;
            		unLockScript=new byte[l];
            		file.read(unLockScript);
            		record.setMac(mac);
            		record.setOrderStamp(orderStamp);
            		record.setTime(rtime);
            		record.setLockScript(lockScript);
            		record.setUnLockScript(unLockScript);
            		tem=record.getBytesData();
            		bytes+=tem.length;
            		result.add(tem);
            		record.setBlocknum(byteToInt(block.getBlockNumber()));
            		recordlist.add(record);
            	}
            	 byte BlockData[]=new byte[bytes];
            	 bytes=0;
            	for(byte[] tem:result)
            	{
            		System.arraycopy(tem, 0, BlockData, bytes, tem.length);
    	            bytes += tem.length;
            	}
            	block.setData(BlockData);
            	 blocklist.add(block);
            }
            else
            {
            	block.setData(new byte[0]);
            	blocklist.add(block);
            }
           

        	}*/
        	}
            file.close();
        }catch (IOException e) {
            System.out.println("error in readBlock");
        }


	}
	public LinkedList<Record> getRecordinBlock(Block block)
	{
		LinkedList<Record> lr=new LinkedList<Record>();
		int locate=0;
		
		byte[] byterecord=block.getData();
		int count=byteToInt(block.getRecordCount());
		for(int i=0;i<count;i++)
		{
			byte[] head=new byte[2];
			System.arraycopy(byterecord, locate, head, 0, 2);
			int length=byteToInt(head);
			byte[] tem=new byte[length];
			locate=locate+2;
			System.arraycopy(byterecord, locate, tem, 0, length);
			Record record=new Record(tem);
			record.setBlocknum(byteToInt(block.getBlockNumber()));
			lr.add(record);
			locate+=length;
		}
		return lr;
		
	}
	


}
