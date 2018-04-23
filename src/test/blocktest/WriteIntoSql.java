package test.blocktest;


import java.util.Base64;
import java.util.LinkedList;

import joy.aksd.tools.DatabaseHelper;
import joy.aksd.data.Block;
import joy.aksd.data.Record;

import static joy.aksd.tools.toInt.byteToInt;

public class WriteIntoSql {
	
	public void saveBlock(Block block){
		Base64.Encoder encoder=Base64.getEncoder();//换了表测试
		String sql="insert into block_copy(lastHash,Merkle,time,difficulty,nonce,cumulativeDifficulty,blockNumber,recordCount,data)values('"
				+encoder.encodeToString(block.getLastHash())
				+"','"
				+encoder.encodeToString(block.getMerkle())
				+"',"
				+byteToInt(block.getTime())
				+","
				+(block.getDifficulty()&0xff)
				+","
				+byteToInt(block.getNonce())
				+","
				+byteToInt(block.getCumulativeDifficulty())
				+","
				+byteToInt(block.getBlockNumber())
				+","
				+byteToInt(block.getRecordCount())
				+",'"
				+encoder.encodeToString(block.getData())
				+"')";
		DatabaseHelper.execute(sql);
		
	}
	public void saveRecord(Record record){
		Base64.Encoder encoder=Base64.getEncoder();//换了表测试
		String sql="insert into record_copy(mac,orderStamp,time,lockScript,unLockScript,blocknum)values('"
				+encoder.encodeToString(record.getMac())
				+"',"
				+byteToInt(record.getOrderStamp())
				+","
				+byteToInt(record.getTime())
				+",'"
				+encoder.encodeToString(record.getLockScript())
				+"','"
				+encoder.encodeToString(record.getUnLockScript())
				+"',"
				+byteToInt(record.getBlocknum())
				+")";
		DatabaseHelper.execute(sql);
	}
	
	public boolean recoverBlock(LinkedList<Block> blocklist,LinkedList<Record> recordlist)
	{
		String sql="truncate table block_copy";
		DatabaseHelper.execute(sql);
		sql="truncate table record_copy";
		DatabaseHelper.execute(sql);
        int i=0;
		for(int j=0;j<blocklist.size();j++)
		{	
			saveBlock(blocklist.get(j));
			i++;
		}
		System.out.println(i);
		i=0;
		for(int j=0;j<recordlist.size();j++)
		{
			saveRecord(recordlist.get(j));
			i++;
		}
		System.out.println(recordlist.size());
		return true;
	}

}
