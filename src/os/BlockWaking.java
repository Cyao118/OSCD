package os;

import hardware.CPU;
import hardware.PublicSource;
import hardware.SystemClock;
import ui.SetPerformance;

import java.io.IOException;

public class BlockWaking extends Thread{//用于阻塞队列唤醒
    public static volatile boolean stop = false;

    public BlockWaking()
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
        while(true)
        {

            if(!stop)
            {
                //死锁检测与撤销
                if(LowScheduling.IsBlocked == 0) {
                    //当系统处于内核态执行将运行进程调入阻塞队列时，由于低级调度进程会修改阻塞队列，
                    // 此时为了实现互斥，不允许唤醒程序同时修改阻塞队列
                    if (SystemClock.getTime() % 5 == 1) {//每5秒检测一次死锁
                        if (Primitive.DeadlockFinding() == 1) {
                            System.out.println("系统死锁！");
                            SetPerformance.htxt.append("系统死锁！\r\n");
                            try {
                                SetPerformance.fw.write("系统死锁！\r\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Primitive.Deadlockkilling();
                            try {
                                sleep(SystemClock.cir);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.out.println("死锁解除！");
                            SetPerformance.htxt.append("死锁解除！\r\n");
                            try {
                                SetPerformance.fw.write("死锁解除！\r\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else if(SystemClock.getTime() %5 ==2 ||SystemClock.getTime() %5 ==3)
                    {
                        for (int i = 0; i < Scheduling.PCBBlock.size(); i++) {//阻塞唤醒
                            int Release = Primitive.WakePCB(Scheduling.PCBBlock.get(i));
                            if (Release == 1&&LowScheduling.Locked == 0) {
                                System.out.println("进程" + Scheduling.PCBBlock.get(i).ProId + "唤醒");
                                SetPerformance.htxt.append("进程" + Scheduling.PCBBlock.get(i).ProId + "唤醒\r\n");
                                try {
                                    SetPerformance.fw.write("进程" + Scheduling.PCBBlock.get(i).ProId + "唤醒\r\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Scheduling.PCBBlock.get(i).setProState(1);
                                Scheduling.PCBBlock.get(i).Blockreason = 0;
                                Scheduling.PCBReady.add(Scheduling.PCBBlock.get(i));
                                Scheduling.PCBBlock.remove(i);
                                i--;
                            }
                        }
                    }
                }
            }
        }
    }
}
