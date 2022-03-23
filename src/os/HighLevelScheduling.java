package os;

import hardware.InternalMemory;
import hardware.SystemClock;
import struct.JCB;
import struct.Job;
import struct.PCB;

import java.util.Random;

public class HighLevelScheduling extends Thread {
    public static int countPro=0;
    //高级调度，称作业调度或长程调度(Long-term Scheduling)。
    //在批处理操作系统中，作业首先进入系统在辅存上的后备作业队列等候调度，因此，作业调度是必须的。
    //它将按照系统预定的调度策略， 决定把后备队列作业中的哪些作业调入主存，
    //为它们创建进程、分配资源，并将它们排在进程就绪队列外，使得这些作业的进程获得竞争处理机的权利，准备执行。
    //采用先来先服务策略
    public HighLevelScheduling(){
        start();
    }
    public static volatile boolean stopHS= false;
    public static void StopMe() {
        stopHS=true;
    }
    public static void ReStartMe() {
        stopHS=false;
    }
    public void run(){
        try{
            while(true){
                if(!stopHS) {

                    while (!Scheduling.JobReserve.isEmpty()) {
                        if (InternalMemory.PDIM() == true)//先判断pcb池和页表区有无位置
                        {
                            Job test = Scheduling.JobReserve.get(0);
                            test.JobJCB = new JCB(test);
                            if (PDBM(test.JobJCB) == true)//判断虚存有无位置
                            {
                                //创建PCB
                               // HighLevelScheduling.countPro++;
                               PCB p=Primitive.CreatePCB(test);

                            }
                        }
                    }
                }
            }
        }
        finally {
        }
    }
    private boolean PDBM(JCB test){//判断虚存是否有位置
        int x=0;
        for(int i=0;i<31;i++)//前两块调入内存
        {
           if(BitMap.Bitmap[i][0]==0&&x<2)
           {
               x++;
           }
           if(x==2) break;
        }
        if(test.BlockNum==2&&x==2)
        {
            return true;
        }
        else if(test.BlockNum>2&&x==2)
        {
            int y=0;
            int z=test.BlockNum-2;
            for(int i=32;i<255;i++)
            {
                if(BitMap.Bitmap[i][0]==0&&y<z)
                {
                    y++;
                }
                if(y==z) break;
            }
            if(y==z)
                return true;
        }
        return false;
    }
    private static int Random(Integer min,Integer max){
        Random random = new  Random();
        int r= random.nextInt(max) % (max-min+1) + min;
        return r;
    }
}
