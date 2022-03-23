package os;

import hardware.SystemClock;
import os.BitMap;
import os.Scheduling;

public class PageMissing extends Thread{//缺页调度处理
    public static volatile boolean stop = false;
    public PageMissing()
    {
        start();
    }
    public static void StopMe()
    {
        stop=true;
    }
    public static void ReStartMe()
    {
        stop=false;
    }
    public void run()
    {
        while(true){
            if(!stop){
                while(Scheduling.PCBMissBlock.size()!=0)
                {
                    if(LowScheduling.LossPage == 0&&LowScheduling.Locked == 0) {
                        //当系统处于内核态执行将运行进程调入阻塞队列时，由于低级调度进程会修改阻塞队列，
                        // 此时为了实现互斥，不允许唤醒程序同时修改阻塞队列
                        BitMap.LRU(Scheduling.PCBMissBlock.get(0));
                        Scheduling.PCBMissBlock.get(0).ProState = 1;
                        try {
                            sleep(10 * SystemClock.cir);//仿真页面调度所需时间，需要10个时钟周期
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Scheduling.PCBReady.add(Scheduling.PCBMissBlock.get(0));
                        Scheduling.PCBMissBlock.remove(0);
                    }
                }
            }
        }
    }

}
