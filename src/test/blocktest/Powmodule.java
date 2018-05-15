package test.blocktest;

import static joy.aksd.data.dataInfo.PackageRecord;
import static joy.aksd.data.dataInfo.SHA256x;
import static joy.aksd.data.dataInfo.blocks;
import static joy.aksd.data.dataInfo.cacheBlockCount;
import static joy.aksd.data.dataInfo.getTime;
import static joy.aksd.data.dataInfo.interupt;
import static joy.aksd.data.dataInfo.num;
import static joy.aksd.data.dataInfo.timeRecord;
import static joy.aksd.data.dataInfo.unPackageRecord;
import static joy.aksd.tools.toByte.intToByte;
import static joy.aksd.tools.toInt.byteToInt;
import static joy.aksd.tools.toString.byteToString;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import joy.aksd.data.Block;
import joy.aksd.data.Record;

public class Powmodule {
	public void start(Block block) throws NoSuchAlgorithmException, InterruptedException {
        findNonceAndTime(block);
    }

    public static void findNonceAndTime(Block block) throws NoSuchAlgorithmException, InterruptedException {
    	MessageDigest sha=MessageDigest.getInstance("SHA-256");
        byte target[]=getTarget(block.getDifficulty());
        byte[] lashHash=block.getLastHash();
        byte[] merkle=block.getMerkle();
        byte[] cumulativeDiff=block.getCumulativeDifficulty();
        byte difficulty=block.getDifficulty();
        byte[] tem=new byte[block.getBlockByteNum()];
        System.arraycopy(lashHash,0,tem,0,lashHash.length);
        System.arraycopy(merkle,0,tem,lashHash.length,merkle.length);
        System.arraycopy(cumulativeDiff,0,tem,lashHash.length+merkle.length,cumulativeDiff.length);
        tem[lashHash.length+merkle.length+cumulativeDiff.length+4]=difficulty;//+4 为后面的4字节time字段
//        int unixTimeStart= getSystemTime();
//        int NTPTimeStart=getNTPtime();
        for (int i=0;true;i++){
            if (interupt){
                Thread.currentThread().interrupt();
                if (Thread.currentThread().isInterrupted()){
                    TimeUnit.SECONDS.sleep(1);
                }
            }
            if (i==Integer.MAX_VALUE)
                i=0;
            byte[]time=intToByte(getTime());
            byte[]nonce=intToByte(i);
            System.arraycopy(time,0,tem,lashHash.length+merkle.length+cumulativeDiff.length,time.length);
            System.arraycopy(nonce,0,tem,lashHash.length+merkle.length+cumulativeDiff.length+time.length+1,nonce.length);//+1 为上面diffculty的一字节字段
            sha.update(tem);
            
            if (isRight(sha.digest(tem),target)){
                block.setNonce(nonce);
                block.setTime(time);
                while (blocks.size()>cacheBlockCount)//缓存最近cacheBlockCount个区块
                { 
                	Iterator<Record> ir=PackageRecord.iterator();
                	while(ir.hasNext())
                	{
                		Record r=ir.next();
                		if(r.getBlocknum().equals(blocks.getFirst().getBlockNumber()))
                			ir.remove();
                	}
                	blocks.remove(0);
                	
                }
                if(blocks.size()!=0)
                num=byteToInt(blocks.getLast().getBlockNumber())+1;
                else
                	num=1;
                blocks.addLast(block);
                timeRecord.add(byteToInt(time));
                block.setBlockNumber(num);
                ArrayDeque<Record> result=new ArrayDeque<>();
                int count=byteToInt(block.getRecordCount());
                
                synchronized (unPackageRecord) {
                	 if(unPackageRecord.size()!=0)
                    for(int j=0;j<count;j++){
                    	Record record=unPackageRecord.get(unPackageRecord.size()-j-1);
    	                result.add(record);
                    }
                    	
                }
                while(result.size()!=0)
                {
                	Record record=result.removeFirst();
                	record.setBlocknum(num);
                	PackageRecord.add(record);
                }
                return;
            }
        }

    }
  

	/**
     * 判断是否满足目标
     *
     * @param hash 要判断的值
     * @param target 目标值
     * @return 正确与否
     */
    private static boolean isRight(byte[] hash, byte[] target) {
        for (int i = 0; i < target.length - 1; i++) {
            if (hash[i] != target[i])
                return false;
        }
        int temHash=hash[target.length-1]&0xff;
        int temTarget=target[target.length-1]&0xff;
        return temHash <= temTarget;

//        if (target[target.length - 1] == -1) {
//            if (hash[target.length - 1] < 0 || hash[target.length - 1] > 16)//0x0f
//                return false;
//        } else {
//            if (hash[target.length - 1] != target[target.length - 1])//0x00
//                return false;
//        }
//        return true;

    }

    /**
     * 根据难度求目标
     * 难度值表示二进制中前多少位是0
     * @param difficulty 难度值
     * @return 难度前缀
     */
    private static byte[] getTarget(byte difficulty) {
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
        return target;


    }

    public static void init(byte[] target) {
        for(int i=0;i<target.length;i++){
            target[i]=0x00;
        }
    }

}
