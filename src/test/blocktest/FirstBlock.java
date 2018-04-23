package test.blocktest;

import joy.aksd.coreThread.WriteBlock;
import joy.aksd.data.Block;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static test.blocktest.Powmodule.findNonceAndTime;
import static joy.aksd.data.dataInfo.*;
import static joy.aksd.tools.toString.byteToString;
import static joy.aksd.tools.toInt.byteToInt;

public class FirstBlock {
	public void start() throws NoSuchAlgorithmException, FileNotFoundException {
        if (blocks.size()==0){
        	MessageDigest sha=MessageDigest.getInstance("SHA-256");
            Block block=new Block();
            block.setMerkle(new byte[32]);
//            block.setTime(intToByte((int) (System.currentTimeMillis()/1000)));
            sha.update("EnjoyTheDeath".getBytes(StandardCharsets.UTF_8));
            byte[]lastHash=sha.digest();
            block.setLastHash(lastHash);
            block.setDifficulty((byte) 0x14);
            block.setBlockNumber(num);
            block.setCumulativeDifficulty(new byte[4]);
            //
            block.setRecordCount(0);
            block.setData(new byte[0]);
            getNonceAndTime(block);
            byte[] head=new byte[2];
            System.arraycopy(block.getBlockDatas(), 0, head, 0, 2);
            System.out.println(byteToInt(head));
            System.out.println(head[0]);
            System.out.println(head[1]);
            blocks.add(block);
          //  new WriteIntoSql().saveBlock(block);
             new WriteBlock(block).start();
            indexBlock.add((long) 0);
        }
    }

    private static void getNonceAndTime(Block block) throws NoSuchAlgorithmException {
        try {
            findNonceAndTime(block);
        } catch (Exception e) {
        	System.out.println(e);
            System.out.println("error in firstBlock");
        }
    }

}
