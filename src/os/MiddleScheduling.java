package os;

import hardware.CPU;
import hardware.InternalMemory;
import hardware.SystemClock;
import struct.PCB;
import struct.Page;
import ui.SetPerformance;

import java.io.IOException;

public class MiddleScheduling extends Thread{
    //中级调度
    //被调到外存等待的进程处于挂起态。该进程的数据段和代码段会被调回外存
    // 但PCB依旧会留在内存中并不会被调回外存，因为操作系统只有通过该进程的PCB，才能对其进行管理。
    // 被挂起进程的PCB会被操作系统放到挂起队列中。
    public static volatile boolean stop = false;
    public MiddleScheduling()
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
    public void run() {
        while (true) {
            if (!stop) {
                if (SystemClock.getTime() % 5 == 4) {
                    if (BitMap.Fullcheck())//内存数据区满了需要发生挂起用于释放长期静止进程的内存给其他进程使用
                    {
                        if ((InternalMemory.PDIM() && !Scheduling.JobReserve.isEmpty()) || Scheduling.PCBStaticReady.size() != 0 || Scheduling.PCBStaticBlock.size() != 0) {

                            int Static = 0;//每次只需要挂起一个即可
                            for (int i = 0; i < Scheduling.PCBReady.size(); i++) {
                                if (Static == 0) {
                                    PCB p = Scheduling.PCBReady.get(i);
                                    if (p.page.Occupy > 3)//占用过多内存
                                    {
                                        Scheduling.PCBReady.remove(i);
                                        p.ProState = 2;

                                        //挂起
                                        for (int j = 0; j < p.PageLength; j++) {
                                            int OutNum = p.page.page[j][2] - 32;
                                            if (OutNum >= 0) {
                                                BitMap.Bitmap[OutNum][0] = 0;
                                                BitMap.Bitmap[OutNum][1] = -1;//对应磁盘磁道号
                                                BitMap.Bitmap[OutNum][2] = -1;//盘块号
                                            }
                                        }
                                        p.page = new Page(p);
                                        p.Statictime = SystemClock.getTime();
                                        Scheduling.PCBStaticReady.add(p);
                                        System.out.println("进程"+p.ProId+"挂起");
                                        SetPerformance.htxt.append("进程"+p.ProId+"挂起\r\n");
                                        try {
                                            SetPerformance.fw.write("进程"+p.ProId+"挂起\r\n");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        Static = 1;
                                    }
                                }
                            }
                            for (int i = 0; i < Scheduling.PCBBlock.size(); i++) {
                                if (Static == 0) {
                                    PCB p = Scheduling.PCBBlock.get(i);
                                    if (p.page.Occupy > 3)//占用过多内存
                                    {
                                        Scheduling.PCBBlock.remove(i);
                                        p.ProState = 4;
                                        //挂起

                                        for (int j = 0; j < p.PageLength; j++) {
                                            int OutNum = p.page.page[j][2] - 32;
                                            if (OutNum >= 0) {
                                                BitMap.Bitmap[OutNum][0] = 0;
                                                BitMap.Bitmap[OutNum][1] = -1;//对应磁盘磁道号
                                                BitMap.Bitmap[OutNum][2] = -1;//盘块号
                                            }
                                        }
                                        p.page = new Page(p);
                                        p.Statictime = SystemClock.getTime();
                                        Scheduling.PCBStaticBlock.add(p);
                                        System.out.println("进程"+p.ProId+"挂起");
                                        SetPerformance.htxt.append("进程"+p.ProId+"挂起\r\n");
                                        try {
                                            SetPerformance.fw.write("进程"+p.ProId+"挂起\r\n");
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        Static = 1;
                                    }
                                }
                            }
                        }
                    }
                    if (Scheduling.PCBStaticReady.size() != 0) {
                        for (int i = 0; i < Scheduling.PCBStaticReady.size(); i++) {
                            if (!BitMap.Fullcheck()) {
                                PCB p = Scheduling.PCBStaticReady.get(i);
                                if (SystemClock.getTime() - p.Statictime > 40) {
                                    Scheduling.PCBStaticReady.remove(i);
                                    BitMap.Allocate(p);
                                    p.ProState = 1;
                                    p.Statictime = 0;
                                    Scheduling.PCBReady.add(p);

                                    System.out.println("进程"+p.ProId+"挂起结束");
                                    SetPerformance.htxt.append("进程"+p.ProId+"挂起结束\r\n");
                                    try {
                                        SetPerformance.fw.write("进程"+p.ProId+"挂起结束\r\n");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    i--;
                                }
                            }
                        }
                    }
                    if (Scheduling.PCBStaticBlock.size() != 0) {
                        for (int i = 0; i < Scheduling.PCBStaticBlock.size(); i++) {
                            if (!BitMap.Fullcheck()) {
                                PCB p = Scheduling.PCBStaticBlock.get(i);
                                if (SystemClock.getTime() - p.Statictime > 40) {
                                    Scheduling.PCBStaticBlock.remove(i);
                                    BitMap.Allocate(p);
                                    p.ProState = 3;
                                    p.Statictime = 0;
                                    Scheduling.PCBBlock.add(p);

                                    System.out.println("进程"+p.ProId+"挂起结束");
                                    SetPerformance.htxt.append("进程"+p.ProId+"挂起结束\r\n");
                                    try {
                                        SetPerformance.fw.write("进程"+p.ProId+"挂起结束\r\n");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    i--;
                                }
                            }

                        }
                    }
                }
            }

        }
    }


}
