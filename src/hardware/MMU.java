package hardware;

import os.BitMap;
import os.Scheduling;
import struct.Instruct;
import struct.PCB;
import struct.Page;

import java.util.ArrayList;

public class MMU {//查页表，如果不在内存就进行缺页中断，将对应物理块从虚存调入内存
    private static int NowBlockNum;
    private static int NowInstructNum;
    private static ArrayList<Page> TLB = new ArrayList<Page>();//快表
    public static Instruct GetInstruct()//取指令
    {
        NowBlockNum = (CPU.CR3+CPU.PC)/256;//地址变换
        NowInstructNum =((CPU.CR3+CPU.PC)%256)/8;
        CPU.AR = Exchange();
        Instruct i = InternalMemory.InstructList[CPU.AR][NowInstructNum];//取指令
        return i;
    }
    public static int Exchange()//先查快表后查页表进行地址变换
    {
        for(int i=0;i<TLB.size();i++){
            if(TLB.get(i).page[0][0] == CPU.PCBrun.ProId){
                TLB.get(i).page[NowBlockNum][4] = 0;
                return TLB.get(i).page[NowBlockNum][2] - 32;}//从快表中得到内存物理地址
        }
        if(TLB.size()<4){
            TLB.add(CPU.PCBrun.page);
        }
        else
        {
            TLB.add(CPU.PCBrun.page);
            TLB.remove(0);//替换进快表
        }
        CPU.PCBrun.page.page[NowBlockNum][4] = 0;
        return CPU.PCBrun.page.page[NowBlockNum][2] - 32;//得到内存物理地址
    }


}
