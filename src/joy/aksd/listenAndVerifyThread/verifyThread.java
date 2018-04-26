package joy.aksd.listenAndVerifyThread;

import joy.aksd.data.Record;

import static joy.aksd.data.dataInfo.*;
import static joy.aksd.tools.toInt.byteToInt;
import static joy.aksd.tools.toString.byteToString;

/** 验证线程
 * Created by EnjoyD on 2017/5/4.
 */
public class verifyThread extends Thread{
    @Override
    public void run() {
        while (true) {
            Record record;
            try {
                record = effectiveRecord.take();
            } catch (InterruptedException e) {
                return;
            }
            //时间和顺序双重把控，确保即使发生网络延迟，记录的顺序依旧正确
            if (verifyStamp(record) && verifyTime(record)) {
                String key = byteToString(record.getLockScript());
                freshRecord.put(key, record);
                identifedRecord.add(record);
            }
        }
    }

    private boolean verifyTime(Record record) {//此处验证需要扩展，比如需要时间上后面的先于前面的到达，进行重新入队处理，目前仅做简单处理
        String key=byteToString(record.getLockScript());
        boolean resflag = false;
        //比较该用户当前记录与上一记录时间，当前时间应小于上一记录时间
        if (byteToInt(freshRecord.get(key).getTime())<byteToInt(record.getTime())) {
        	//判断当前记录时间是否在允许误差范围内
        	if( byteToInt(record.getTime())>(errorTime -NTPTime) && byteToInt(record.getTime()) <= (errorTime + NTPTime)) { 
        		resflag = true;
        		System.out.println("该记录时间验证通过");
        	}
        	else
        	{
        		System.out.println("该记录时间超时，已丢弃");
        	}
        }
        else {
        	effectiveRecord.add(record);
            System.out.println("已将该记录重新入队");
        }
		return resflag;
    }
    private boolean verifyStamp(Record record) {//此处验证需要扩展，比如顺序上后面的先于前面的到达，需要进行重新入队处理
        String key=byteToString(record.getLockScript());
        boolean resflag = false;
        if (byteToInt(freshRecord.get(key).getOrderStamp())+1==byteToInt(record.getOrderStamp())) {
        	resflag = true;
        	System.out.println("该记录顺序验证通过");
        }           
        else {
        	effectiveRecord.add(record);
        	System.out.println("已将该记录重新入队");
        }
		return resflag;	
    }
}
