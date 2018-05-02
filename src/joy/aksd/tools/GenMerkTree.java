package joy.aksd.tools;

import static joy.aksd.data.dataInfo.identifedRecord;
import static joy.aksd.data.dataInfo.merkleTreeLimitation;
import static joy.aksd.data.dataInfo.unPackageRecord;
import static joy.aksd.tools.toInt.byteToInt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import joy.aksd.data.Block;
import joy.aksd.data.Record;


//在创块时生成Merkle树，把树根节点的值返回给block，整个树存储在data中
//在区块校验中，可以不用生成Merkle树，只对Merkle树进行查询

public class GenMerkTree {
	//ArrayList保存默克尔树的所有节点
	static List<byte[]> MerkleTree = Collections.synchronizedList(new ArrayList());
	
	//记录树高
	static int MerHigh = 1;
	
	public static byte[] GenMerkleTree(Block block) {
		 //读取纪录
        byte blockData[]=block.getData();
        int x=0; 
		//先从块中读取所有记录存在result中
		 ArrayDeque<byte []> result=new ArrayDeque<>();
		 for (int i=0;i<byteToInt(block.getRecordCount());i++){
             byte[] tem=new byte[2];
             System.arraycopy(blockData,x,tem,0,2);
             x+=2;//记录的前两个字节也是记录记录长度的
             tem=new byte[byteToInt(tem)];
             System.arraycopy(blockData,x,tem,0,tem.length);
             x+=tem.length;
             result.add(tem);
          }
		 
		 byte[] MerRootNode = calMerTree(result);
		 saveMer(block);
		 return MerRootNode;
		 

	}
	
	//生成MerTree，并保存在block的data中。
	public static byte[] calMerTree(ArrayDeque<byte []> result) {
	    MessageDigest digest= null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        for (int i=0;i<result.size();i++){
        	byte tem[]=result.removeFirst();
        	tem=digest.digest(tem);
        	//result中此时存放的是叶子结点的hash值
        	result.addLast(tem);
        	//保存叶子节点的hash值
        	MerkleTree.add(tem);
        }
        //循环使用叶子结点构建其父母节点
        ArrayDeque<byte []> temResult=new ArrayDeque<>();
        if(result.size()!=1){
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
        			//保存倒数第二个节点的hash值，然后这一层节点可以作为叶子结点然后接着循环。
        			MerkleTree.add(tem);
        			//temResult中存的是倒数第二层节点的，也就是叶子节点的父节点
        		}
        	}      
        }
        else {
        	//Mer树高度加一
        	MerHigh++;
        	if(temResult.size() > 1)
        		calMerTree(temResult);
        }

        //返回树根节点的值
        return MerkleTree.get(MerkleTree.size()-1);
	}
	
	//把Merkle整个树放到blockData中
	public static void saveMer(Block block){
    
		//Merkle树所需空间：节点数乘以32
		int MerSpace = MerkleTree.size() *  32;
		//填充block 的data数据
		byte BlockData[]=new byte[MerSpace];
		for(byte[] tem : MerkleTree) {
			System.arraycopy(tem, 0, BlockData, 0, tem.length);
			System.out.println(Arrays.toString(tem));
		}
		//block的setData会在现有的数据后接着往后写吗？
		block.setData(BlockData);
	}
}

//应该有一个计算区块各部分长度的函数

