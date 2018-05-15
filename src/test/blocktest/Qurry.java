package test.blocktest;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Base64;

import joy.aksd.data.Block;
import joy.aksd.data.Record;
import joy.aksd.tools.DatabaseHelper;

import static joy.aksd.tools.toByte.intToByte;
import static joy.aksd.tools.toByte.intToOneByte;
import static joy.aksd.tools.toInt.byteToInt;
import static joy.aksd.tools.toString.byteToString;

public class Qurry {
	
	public LinkedList<Block> findallBlock(){//获取mysql所有块数据
		Base64.Decoder decoder=Base64.getDecoder();
		String sql="select * from block_copy";
		ResultSet rs=DatabaseHelper.query(sql);
		LinkedList<Block> ab=new LinkedList<Block>();
		try {
			while(rs.next()){
				String lastHash=rs.getString("lastHash");
				String merkle=rs.getString("Merkle");
				int time=rs.getInt("time");
				int difficulty=rs.getInt("difficulty");
				int nonce=rs.getInt("nonce");
				int cumul=rs.getInt("cumulativeDifficulty");
				int blockNumber=rs.getInt("blockNumber");
				int recordCount=rs.getInt("recordCount");
				String data=rs.getString("data");
				Block block=new Block();
				block.setLastHash(decoder.decode(lastHash));
				block.setMerkle(decoder.decode(merkle));
				block.setTime(intToByte(time));
				block.setDifficulty(intToOneByte(difficulty));
				block.setNonce(intToByte(nonce));
				block.setCumulativeDifficulty(intToByte(cumul));
				block.setBlockNumber(blockNumber);
				block.setRecordCount(recordCount);
				block.setData(decoder.decode(data));
				ab.add(block);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ab;
	}
	
	public LinkedList<Record> findallRecord(){//获取mysql中的所有记录
		Base64.Decoder decoder=Base64.getDecoder();
		String sql="select * from record_copy";
		ResultSet rs=DatabaseHelper.query(sql);
		LinkedList<Record> ar=new LinkedList<Record>();
		try {
			while(rs.next()){
				String mac=rs.getString("mac");
				int orderstamp=rs.getInt("orderStamp");
				int time=rs.getInt("time");
				String lockscript=rs.getString("lockScript");
				String unlock=rs.getString("unlockScript");
				//int blocknum=rs.getInt("blocknum");
				Record record=new Record();
				record.setMac(decoder.decode(mac));
				record.setOrderStamp(intToByte(orderstamp));
				record.setTime(intToByte(time));
				record.setLockScript(decoder.decode(lockscript));
				record.setUnLockScript(decoder.decode(unlock));
				//record.setBlocknum(blocknum);
				ar.add(record);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ar;
	}
	
	public static ArrayDeque<byte[]> QurryRecordByBlocknum(int blocknum){//通过块号查询记录
		Base64.Decoder decoder=Base64.getDecoder();
		String sql="select * from record_copy where blocknum= "+blocknum+" order by orderStamp DESC";
		ResultSet rs=DatabaseHelper.query(sql);
		ArrayDeque<byte[]> ar=new ArrayDeque<byte[]>();
		try {
			while(rs.next()){
				String mac=rs.getString("mac");
				int orderstamp=rs.getInt("orderStamp");
				int time=rs.getInt("time");
				String lockscript=rs.getString("lockScript");
				String unlock=rs.getString("unlockScript");
				Record record=new Record();
				record.setMac(decoder.decode(mac));
				record.setOrderStamp(intToByte(orderstamp));
				record.setTime(intToByte(time));
				record.setLockScript(decoder.decode(lockscript));
				record.setUnLockScript(decoder.decode(unlock));
				record.setBlocknum(blocknum);
				ar.add(record.getBytesData());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ar;
	}
	
	public Block QurryBlockByRecord(Record record) {//通过记录查询其所在的块并把块返回。
		Base64.Decoder decoder=Base64.getDecoder();
		String sql = "select * from block where blockNumber = "+ record.getBlocknum();
		ResultSet rs=DatabaseHelper.query(sql);
		Block block=new Block();
		try {
			while(rs.next()) {
					String lastHash=rs.getString("lastHash");
					String merkle=rs.getString("Merkle");
					int time=rs.getInt("time");
					int difficulty=rs.getInt("difficulty");
					int nonce=rs.getInt("nonce");
					int cumul=rs.getInt("cumulativeDifficulty");
					int blockNumber=rs.getInt("blockNumber");
					int recordCount=rs.getInt("recordCount");
					String data=rs.getString("data");
					block.setLastHash(decoder.decode(lastHash));
					block.setMerkle(decoder.decode(merkle));
					block.setTime(intToByte(time));
					block.setDifficulty(intToOneByte(difficulty));
					block.setNonce(intToByte(nonce));
					block.setCumulativeDifficulty(intToByte(cumul));
					block.setBlockNumber(blockNumber);
					block.setRecordCount(recordCount);
					block.setData(decoder.decode(data));
			}	
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return block;
	}
	
	
	
	public ArrayList<Record> QurryRecordByLockScript(int count)//通过锁查询记录byte[] lockscript,
	{
		Base64.Encoder encoder=Base64.getEncoder();
		Base64.Decoder decoder=Base64.getDecoder();
		ArrayList<Record> ar=new ArrayList<Record>();
		String sql="select * from record_copy limit "+count;//where lockScript='"+encoder.encodeToString(lockscript)+"'";
		ResultSet rs=DatabaseHelper.query(sql);
		try {
			while(rs.next())
			{
				String mac=rs.getString("mac");
				int orderstamp=rs.getInt("orderStamp");
				int time=rs.getInt("time");
				int blocknum=rs.getInt("blocknum");
				String loc=rs.getString("lockScript");
				String unlock=rs.getString("unlockScript");
				Record record=new Record();
				record.setMac(decoder.decode(mac));
				record.setOrderStamp(intToByte(orderstamp));
				record.setTime(intToByte(time));
				record.setLockScript(decoder.decode(loc));
				record.setUnLockScript(decoder.decode(unlock));
				record.setBlocknum(blocknum);
				ar.add(record);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ar;
	}
	//校验块的哈希值
	public boolean checkblock(LinkedList<Block> block,LinkedList<Record> ar) throws Exception{
		if(block.size()==0)
			return true;
		MessageDigest sha=MessageDigest.getInstance("SHA-256");
		sha.update("EnjoyTheDeath".getBytes(StandardCharsets.UTF_8));
        byte[]lastHash=sha.digest();
		ar=findallRecord();
		Block last =block.removeLast();
		if(Arrays.equals(lastHash, last.getLastHash()))
		{
			return true;
		}
		
		if(byteToInt(last.getRecordCount())!=0)
		{
		int blocknum=byteToInt(last.getBlockNumber());
		ArrayDeque<byte []> adr=new ArrayDeque<byte []>();
		adr=QurryRecordByBlocknum(blocknum);
		if(!Arrays.equals(last.getMerkle(), generateMerkle(adr)))
		{
			return false;
		}
		}
		
		Block flast=block.getLast();
		if(Arrays.equals(gethash(flast),last.getLastHash()))
		{
			return checkblock(block,ar);
		}
		else
		{
			return false;
		}
	}
	private byte[] gethash(Block block) throws NoSuchAlgorithmException
	{
		MessageDigest sha=MessageDigest.getInstance("SHA-256");
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
        return result;
	}
	public ArrayDeque<byte []> findBlockdata(int blocknum,LinkedList<Record> ar){
		//System.out.println(blocknum);
		ArrayDeque<byte []> result=new ArrayDeque<byte []>();
		if(byteToInt(ar.getLast().getBlocknum())<blocknum)
			return null;
		while(!ar.isEmpty()){
			if(byteToInt(ar.getLast().getBlocknum())==blocknum)
			{	Record record=new Record();
				
				record=ar.removeLast();
				result.add(record.getBytesData());
			}
			else if(byteToInt(ar.getLast().getBlocknum())>blocknum)
				ar.removeLast();
			else
				return result;
		}
		return result;
	}
	
	 private byte[] generateMerkle(ArrayDeque<byte []> adr) {
	        //开始计算merkle tree root
		    ArrayDeque<byte[]> result=new ArrayDeque<byte[]>();
	        MessageDigest digest= null;
	        try {
	            digest = MessageDigest.getInstance("SHA-256");
	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        }
	        while(!adr.isEmpty()){
	        	byte[] tem=adr.removeLast();
	        	tem=digest.digest(tem);
	            result.add(tem);
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
	    }
	
}
