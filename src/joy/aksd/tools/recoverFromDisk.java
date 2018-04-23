package joy.aksd.tools;

import static joy.aksd.data.dataInfo.adjustCount;
import static joy.aksd.data.dataInfo.blocks;
import static joy.aksd.data.dataInfo.cacheBlockCount;
import static joy.aksd.data.dataInfo.freshRecord;
import static joy.aksd.data.dataInfo.indexBlock;
import static joy.aksd.data.dataInfo.location;
import static joy.aksd.data.dataInfo.num;
import static joy.aksd.data.dataInfo.timeRecord;
import static joy.aksd.tools.toInt.byteToInt;
import static joy.aksd.tools.toString.byteToString;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import joy.aksd.data.Block;
import joy.aksd.data.Record;

public class recoverFromDisk {

public static void recoverFDisk() throws IOException {
    DataInputStream in=new DataInputStream(new FileInputStream(location));
    long index=0;
    byte tem[];
    while (true){
            //读取区块
            tem=new byte[2];
            in.read(tem);//读取区块长度
            int byteCount=byteToInt(tem);
            if (byteCount==0)
                break;
            //建立索引
            indexBlock.add(index);
            tem=new byte[byteCount];
            in.read(tem);
            index+=(2+tem.length);
            //复原区块
            Block block=new Block(tem);
            //添加区块缓存
            if (blocks.size()>cacheBlockCount)//缓存最近cacheBlockCount个区块
                blocks.remove(0);//移除缓存区最老的区块
            blocks.addLast(block);
            //添加time
            if (timeRecord.size()==adjustCount)
                timeRecord.clear();
            timeRecord.add(byteToInt(block.getTime()));
            //读取纪录
            byte blockData[]=block.getData();
            int x=0;
            //一条一条记录读
            for (int i=0;i<byteToInt(block.getRecordCount());i++){
                tem=new byte[2];
                System.arraycopy(blockData,x,tem,0,2);
                x+=2;//记录的前两个字节也是记录记录长度的
                tem=new byte[byteToInt(tem)];
                System.arraycopy(blockData,x,tem,0,tem.length);
                x+=tem.length;
                Record record=new Record(tem);
                //添加未使用纪录
                freshRecord.put(byteToString(record.getLockScript()),record);
                System.out.println(record+" "+byteToString(record.getLockScript()));
            }
        }

    in.close();
    num=byteToInt(blocks.getLast().getBlockNumber());
    System.out.println("end"+num);
}
}
