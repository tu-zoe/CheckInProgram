package joy.aksd.coreThread;

import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.*;

import joy.aksd.data.Block;
import joy.aksd.data.Record;
import joy.aksd.tools.readRecordFromBlock;
import test.blocktest.Qurry.*;

import static joy.aksd.tools.readRecordFromBlock.readReFromBlock;
import static joy.aksd.tools.toInt.byteToInt;
import static joy.aksd.tools.toByte.intToByte;
/*
 * 1.在数据库中查询记录所属区块
 * 2.取出Merkle树生成验证路径
 * 3.验证路径验证
 * 
 * 未完：改成固定数组
 */

public class CheckRecordExist {
	Record record = new Record();
	Block block=new Block();
	int recordCount = byteToInt(block.getRecordCount());
	ArrayList<byte[]> MerkleTree = getMerkle(block);
	int pos=0;
	
	//block = QurryBlockByRecord(record);
	//ArrayList<byte[]> MerklePath = getProof(block);
	
	public CheckRecordExist(Record record) {
		this.record = record;
	}
	
	//merkle路径 
	public ArrayList<byte[]> getMerkle(Block block){
		//1.取出Merkle树
		byte blockData[]=block.getData();
		int RecordLength = readRecordFromBlock.RecordLength;
		ArrayList<byte[]> Merkle = null;
		
		MessageDigest digest= null;
		try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
		byte[]  recordByte = digest.digest(record.getBytesData());
		
		//2.哈希当前记录，将哈希值与树的叶子节点一一对比，找到在树中的位置
		//3.树还原
		for(int i=0;i<recordCount;i++) {
			int cumulLen =0 ; 
			byte[] MerkleNode = new byte[32];
			System.arraycopy(blockData,RecordLength+cumulLen,MerkleNode,0,32);
			Merkle.add(MerkleNode);
			if(Arrays.equals(MerkleNode,recordByte)) pos=i;
			cumulLen+=32;		
		}
	
		return Merkle;	
	}
	
	
	//4.计算并保存Merkle树路径
	ArrayList<byte[]> MerklePath = calPath(MerkleTree,pos);
	public ArrayList<byte[]> calPath(ArrayList<byte[]> merkleTree, int i){
		int tem = recordCount;
		if(tem%2!=0) tem=tem+1;
		
		for(int j=0;i<MerkleTree.size();j++) {
			if(j%2==0) {
				//循环添加路径上的点
				MerklePath.add(MerkleTree.get(j-1));
				for(int k=0;k<=tem;k++) {
					MerkleTree.remove(k);
				}
				calPath(MerkleTree, j/2);
			}
			else {
				MerklePath.add(MerkleTree.get(j+1));
				for(int k=0;k<=tem;k++) {
					MerkleTree.remove(k);
				}
				calPath(MerkleTree, (j-1)/2);
			}
		}	
		//calPath(MerkleNode,i);
		return null;
	}
}
