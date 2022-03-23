package os;

import hardware.CPU;
import hardware.InternalMemory;
import hardware.SystemClock;
import struct.PCB;
import ui.SetPerformance;

import java.io.IOException;

public class LowScheduling extends Thread{
    //低级调度主要任务是按照某种规则从就绪队列中选取一个进程，将CPU分配给它
    public static int LossPage = 0;//缺页中断标志位
    public static int IsBlocked = 0;//阻塞中断标志位
    public static int Timefinish = 0;//时间片用完标志位
    public static int Processfinish = 0;//进程完成标志位
    public static int Processshutdown = 0;//进程突然撤销标志位

    public static int Locked = 0;//互斥修改就绪队列

    public static volatile boolean stop = false;

    public LowScheduling()
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
                if(CPU.PSW == 1){
                    if(Processshutdown == 1){//撤销进程
                        CPU.PCBrun.NowInstruct=CPU.PC/8;
                        CPU.PCBrun.NowBlockNum=(CPU.CR3+CPU.PC)/256;
                        //保存进程信息
                        CPU.PCBrun.ProState = 6;
                        Primitive.RemovePCB(CPU.PCBrun);//进程完成，撤销进程
                        Processshutdown = 0;
                    }
                    else if(Timefinish == 1&&RunStruct.Runable==1)//时间片用完执行调度，将CPU内运行态进程调入活动就绪队列
                    {
                        CPU.PCBrun.NowInstruct=CPU.PC/8;
                        CPU.PCBrun.NowBlockNum=(CPU.CR3+CPU.PC)/256;
                        //保存进程信息
                        CPU.PCBrun.ProState=1;
                        CPU.PCBrun.Priority=1;//优先级置为最小值
                        for(int i=0;i<Scheduling.PCBReady.size();i++){
                            Scheduling.PCBReady.get(i).Priority++;//就绪队列内全部进程优先级自增
                        }
                        Scheduling.PCBReady.add(CPU.PCBrun);//调入活动就绪队列
                        Timefinish = 0;
                    }
                    else if(LossPage == 1)//缺页中断执行调度
                    {
                        /*CPU.PCBrun.NowInstruct=CPU.PC/8;
                        CPU.PCBrun.NowBlockNum=(CPU.CR3+CPU.PC)/256;
                        //保存进程信息
                        CPU.PCBrun.ProState = 5;
                        Scheduling.PCBMissBlock.add(CPU.PCBrun);//调入缺页阻塞队列*/
                        Primitive.BlockPCB(CPU.PCBrun,5);
                        LossPage = 0;
                    }
                    else if(IsBlocked == 1)//阻塞执行调度
                    {
                       /* CPU.PCBrun.NowInstruct=CPU.PC/8;
                        CPU.PCBrun.NowBlockNum=(CPU.CR3+CPU.PC)/256;
                        //保存进程信息
                        CPU.PCBrun.ProState = 3;
                        CPU.PCBrun.Blocktime = SystemClock.getTime();
                        Scheduling.PCBBlock.add(CPU.PCBrun);//调入活动阻塞队列*/
                        Primitive.BlockPCB(CPU.PCBrun,3);
                        IsBlocked = 0;
                    }
                    else if(Processfinish == 1)//进程完成执行调度
                    {
                        CPU.PCBrun.NowInstruct=CPU.PC/8;
                        CPU.PCBrun.NowBlockNum=(CPU.CR3+CPU.PC)/256;
                        //保存进程信息
                        CPU.PCBrun.ProState = 6;
                        Primitive.RemovePCB(CPU.PCBrun);//进程完成，撤销进程
                        Processfinish = 0;
                    }
                    if(Scheduling.PCBReady.size()!=0){
                        Locked = 1;
                        int out = 0;
                        for(int i=0;i<Scheduling.PCBReady.size();i++){
                            if(Scheduling.PCBReady.get(i).Priority>Scheduling.PCBReady.get(out).Priority) out = i;
                        }//找到优先级最大的
                        CPU.PCBrun = Scheduling.PCBReady.get(out);
                        CPU.PCBrun.ProState = 0;//设为运行
                        Scheduling.PCBReady.remove(out);
                        Locked = 0;
                        RunStruct.Runable = 1;
                        System.out.println("进程"+CPU.PCBrun.ProId+"开始运行"+",就绪队列剩余进程数"+Scheduling.PCBReady.size());
                        SetPerformance.htxt.append("进程"+CPU.PCBrun.ProId+"开始运行"+",就绪队列剩余进程数"+Scheduling.PCBReady.size()+"\r\n");
                        try {
                            SetPerformance.fw.write("进程"+CPU.PCBrun.ProId+"开始运行"+",就绪队列剩余进程数"+Scheduling.PCBReady.size()+"\r\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        CPU.PC=CPU.PCBrun.NowInstruct*8;//修改PC
                        CPU.CR3 =CPU.PCBrun.InstructNum*8;//修改段基地址
                        CPU.PSW = 0;//可以运行进程，返回用户态


                    }

                }
            }
        }
    }



}
