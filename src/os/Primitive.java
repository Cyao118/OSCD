package os;

import hardware.CPU;
import hardware.InternalMemory;
import hardware.PublicSource;
import hardware.SystemClock;
import struct.Job;
import struct.PCB;
import ui.SetPerformance;

import java.io.IOException;

public class Primitive {//进程原语
    public static PCB CreatePCB(Job test)//进程创建
    {
        PCB p = new PCB();
        p.CreatePCB(test.JobJCB);
        p.setPCBArea(InternalMemory.AllocatePCB());
        p.setPageArea(InternalMemory.AllocatePage());
        InternalMemory.WritePCBlistBlock(p);//写入PCB池
        BitMap.Allocate(p);//分配虚存,修改位示图,创建页表,将作业所需盘块内容存到虚存对应的内存位置
        InternalMemory.WritePagelistBlock(p);//保存页表到页表池
        InternalMemory.PCBMenoryList.add(p);
        p.ProState =1;//活动就绪；
        Scheduling.PCBReady.add(p);//加入进程就绪队列
        System.out.println("作业"+test.JobId+"进程"+p.ProId+"就绪");
        SetPerformance.htxt.append("作业"+test.JobId+"进程"+p.ProId+"就绪\r\n");
        try {
            SetPerformance.fw.write("作业"+test.JobId+"进程"+p.ProId+"就绪\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        test.JobState = 2;//置为运行态
        test.JobJCB.State = 2;
        test.JobJCB.StarTime = SystemClock.getTime();//得到开始时间
        Scheduling.JobWorking.add(test);
        Scheduling.JobReserve.remove(0);
        return p;
    }
    public static void RemovePCB(PCB p)//进程撤销
    {
        Job j;
        for(int i=0;i<Scheduling.JobWorking.size();i++)
        {
            if(Scheduling.JobWorking.get(i).JobJCB.ProId == p.ProId)//找到进程对应的作业
            {
                j = Scheduling.JobWorking.get(i);
                j.JobState = 3;//置为完成态
                j.JobJCB.State = 3;
                p.EndTime= SystemClock.getTime();//得到结束时间
                j.JobJCB.EndTime= p.EndTime ;
                p.ProState = 6;//进程完成，撤销进程

                Scheduling.JobWorking.remove(i);//移出运行作业
                Scheduling.JobAchieve.add(j);//作业完成
                for (int m=0;m<Scheduling.JcbTable.size();m++)
                {
                    if(Scheduling.JcbTable.get(m).JobId == j.JobId){
                        Scheduling.JcbTable.remove(m);
                        break;//释放作业输入区资源
                    }
                }
                InSpooling.Allocate[j.Inspoolingnum] = 0;//释放作业输入区文件
                int write = OutSpooling.FinishJobnum + 32;
                OutSpooling.FinishJobnum++;
                j.WriteFinish(write,p);
                break;
            }
        }

        //释放内存空间：1用户区 2页表区 3PCB
        //释放用户区
        for(int i=0;i<p.PageLength;i++){
            int OutNum = p.page.page[i][2]-32;
            if(OutNum >= 0) {
                BitMap.Bitmap[OutNum][0] = 0;
                BitMap.Bitmap[OutNum][1] = -1;//对应磁盘磁道号
                BitMap.Bitmap[OutNum][2] = -1;//盘块号
            }
        }
        InternalMemory.YorN[p.PageArea] = 0;//释放页表区
        //释放PCB区
        int k;
        for(k=0;k<InternalMemory.PCBMenoryList.size();k++){
            if(InternalMemory.PCBMenoryList.get(k).ProId == p.ProId)
                break;
        }
        InternalMemory.YorN[p.PCBArea] = 0;//释放PCB

        //检查所有队列，确保进程不在任何队列中
        for(int i=0;i<Scheduling.PCBReady.size();i++) {
            if(Scheduling.PCBReady.get(i).ProId == p.ProId)Scheduling.PCBReady.remove(i);
         }
        for(int i=0;i<Scheduling.PCBBlock.size();i++) {
            if(Scheduling.PCBBlock.get(i).ProId == p.ProId)Scheduling.PCBBlock.remove(i);
        }
        for(int i=0;i<Scheduling.PCBMissBlock.size();i++) {
            if(Scheduling.PCBMissBlock.get(i).ProId == p.ProId)Scheduling.PCBMissBlock.remove(i);
        }
        for(int i=0;i<Scheduling.PCBStaticReady.size();i++) {
            if(Scheduling.PCBStaticReady.get(i).ProId == p.ProId)Scheduling.PCBStaticReady.remove(i);
        }
        for(int i=0;i<Scheduling.PCBStaticBlock.size();i++) {
            if(Scheduling.PCBStaticBlock.get(i).ProId == p.ProId)Scheduling.PCBStaticBlock.remove(i);
        }

        PublicSource.SignalA+=p.AllocateA;
        PublicSource.SignalB+=p.AllocateB;
        PublicSource.SignalC+=p.AllocateC;

        InternalMemory.PCBMenoryList.remove(k);

    }
    public static void BlockPCB(PCB p,int state)
    {
        p.NowInstruct=CPU.PC/8;
        p.NowBlockNum=(CPU.CR3+CPU.PC)/256;
        //保存进程信息
        p.ProState = state;
        p.Blocktime = SystemClock.getTime();
        if(state==3)
        Scheduling.PCBBlock.add(p);//调入活动阻塞队列
        else if(state==5)
        Scheduling.PCBMissBlock.add(p);
    }
    public static int WakePCB(PCB p)//唤醒原语
    {
        int Release = 0;
        int time = SystemClock.getTime();
        if((time - p.Blocktime) >= 2)//达到阻塞唤醒需要的时间
        {
            if(p.Blockreason == 2) {
                if (PublicSource.SignalA >= p.NeedA
                        && PublicSource.SignalB >= p.NeedB
                        && PublicSource.SignalC >= p.NeedC)//满足分配条件
                {
                    PublicSource.SignalA -= p.NeedA;//分配公共资源
                    PublicSource.SignalB -= p.NeedB;
                    PublicSource.SignalC -= p.NeedC;
                    p.AllocateA += p.NeedA;//占用公共资源
                    p.AllocateB += p.NeedB;
                    p.AllocateC += p.NeedC;
                    p.NeedA = 0;//需求清空
                    p.NeedB = 0;
                    p.NeedC = 0;
                    Release = 1;//唤醒
                }
            }
            else if (p.Blockreason == 1) {//系统调用阻塞
                Release = 1;//唤醒
            }
        }
        return Release;
    }
    public static int  DeadlockFinding()//死锁检测（环路检测）
    {
        int isDeadlock = 0;
        int NowA=0,NowB=0,NowC=0;
        for (int i = 0; i < Scheduling.PCBBlock.size(); i++)
        {
            if(Scheduling.PCBBlock.get(i).Blockreason == 2) {
                NowA += Scheduling.PCBBlock.get(i).AllocateA;
                NowB += Scheduling.PCBBlock.get(i).AllocateB;
                NowC += Scheduling.PCBBlock.get(i).AllocateC;
            }
        }//计算当前阻塞队列中进程占用的全部资源

        for (int i = 0; i < Scheduling.PCBBlock.size(); i++)
        {
            if(Scheduling.PCBBlock.get(i).Blockreason == 2) {
                if((PublicSource.SignalA+NowA)>(Scheduling.PCBBlock.get(i).NeedA+Scheduling.PCBBlock.get(i).AllocateA)
                   &&(PublicSource.SignalB+NowB)>(Scheduling.PCBBlock.get(i).NeedB+Scheduling.PCBBlock.get(i).AllocateB)
                   &&(PublicSource.SignalC+NowC)>(Scheduling.PCBBlock.get(i).NeedC+Scheduling.PCBBlock.get(i).AllocateC))//当前资源可供某一进程解除阻塞，说明存在占有等待现象
                {
                    isDeadlock = 1;
                    break;
                }
            }
        }
        return isDeadlock;
    }
    public static void Deadlockkilling()//死锁消除，采用剥夺阻塞队列全部进程所占资源方式
    {
        for (int i = 0; i < Scheduling.PCBBlock.size(); i++)
        {
            if(Scheduling.PCBBlock.get(i).Blockreason == 2) {
                PublicSource.SignalA += Scheduling.PCBBlock.get(i).AllocateA;
                PublicSource.SignalB += Scheduling.PCBBlock.get(i).AllocateB;
                PublicSource.SignalC += Scheduling.PCBBlock.get(i).AllocateC;
                Scheduling.PCBBlock.get(i).NeedA += Scheduling.PCBBlock.get(i).AllocateA;
                Scheduling.PCBBlock.get(i).NeedB += Scheduling.PCBBlock.get(i).AllocateB;
                Scheduling.PCBBlock.get(i).NeedC += Scheduling.PCBBlock.get(i).AllocateC;
                Scheduling.PCBBlock.get(i).AllocateA = 0;
                Scheduling.PCBBlock.get(i).AllocateB = 0;
                Scheduling.PCBBlock.get(i).AllocateC = 0;
            }
        }
    }
    public static void PA(PCB p)//P原语
    {
        PublicSource.SignalA--;
        p.AllocateA++;
    }
    public static void PB(PCB p)//P原语
    {
        PublicSource.SignalB--;
        p.AllocateB++;
    }
    public static void PC(PCB p)//P原语
    {
        PublicSource.SignalC--;
        p.AllocateC++;
    }
    public static void VA(PCB p)//V原语
    {
        PublicSource.SignalA++;
        p.AllocateA--;
    }
    public static void VB(PCB p)//V原语
    {
        PublicSource.SignalB++;
        p.AllocateB--;
    }
    public static void VC(PCB p)//V原语
    {
        PublicSource.SignalC++;
        p.AllocateC--;
    }

}
