package test.blocktest;

import joy.aksd.coreThread.BroadcastBlock;
import joy.aksd.coreThread.CreatBlock;
import joy.aksd.coreThread.WriteBlock;
import joy.aksd.coreThread.powModule;
import joy.aksd.data.Block;
import joy.aksd.data.Record;

import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import static joy.aksd.data.dataInfo.unPackageRecord;
import static joy.aksd.data.dataInfo.blocks;
import static joy.aksd.data.dataInfo.PackageRecord;
import static test.blocktest.BlockTest.memcmp;

public class CoreTest implements Runnable{
	
	@Override
    public void run() {
        System.out.println("start joy.aksd.coreThread.coreProcess");
        int i=1;
        int j=0;
        WriteIntoSql wis=new WriteIntoSql();
        while (true) {
        	FirstBlock fblock=new FirstBlock();
        	
        	try {
				fblock.start();
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	Crecord creatrecord=new Crecord();
        	
        	try{
        		creatrecord.start();
        		creatrecord.start();
        		creatrecord.start();
        		
        	}
        	catch(InterruptedException e){
        		e.printStackTrace();
        	}
        	
            //创建区块
            CBlock creatBlock = new CBlock();
            Block block ;
            try {
                block = creatBlock.start();
            } catch (NoSuchAlgorithmException e) {
                System.out.println("创建区块失败");
                continue;
            }
            //pow找hash值完成区块的最后nonce值部分并加入链中
            Powmodule pow = new Powmodule();
            try {
            	
                pow.start(block);
            } catch (NoSuchAlgorithmException e) {
                System.out.println("pow模块失败");
                continue;
            } catch (InterruptedException e) {
                System.out.println("pow 中断结束");
                break;
            }
           // if(i%10==0){
            //	for(int m=0;m<i;m++)
            	//{
       /*    if(!blocks.isEmpty())
            		wis.saveBlock(blocks.get(i));
            if(PackageRecord.size()!=0)
            {
            		wis.saveRecord(PackageRecord.get(j));
            		wis.saveRecord(PackageRecord.get(j+1));
            		wis.saveRecord(PackageRecord.get(j+2));
            }
            	//}
            	//j+=10;
            	
            //}
            //广播区块
            
           i++;
           j+=3;*/
           WriteBlock writeBlock = new WriteBlock(block);
           try {
               writeBlock.start();
               unPackageRecord.clear();
           } catch (FileNotFoundException e) {
               System.out.println("写入失败");
               continue;
           }
           j++;
           if(j==6)
           {
        	   System.out.println("break");
        	   break;
           }
        }
       
        	
        System.out.println("joy.aksd.coreThread.coreProcess finished");
    }

}
