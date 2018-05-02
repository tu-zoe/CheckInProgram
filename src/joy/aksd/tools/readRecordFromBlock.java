package joy.aksd.tools;

import static joy.aksd.tools.toInt.byteToInt;

import java.util.ArrayDeque;

import joy.aksd.data.Block;

//从block中读出record并存储在一个ArrayDeque中
public class readRecordFromBlock {
	
	public static ArrayDeque<byte[]> readReFromBlock(Block block) {
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
			return result;
	}
	
	

}
