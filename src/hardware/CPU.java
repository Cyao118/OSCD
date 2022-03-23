package hardware;

import struct.Instruct;
import struct.PCB;

public class CPU {//用寄存器保存运行态的进程的相关信息
    public static int PC=0;//下一条指令地址
    public static int PSW=1;//当前运算的状态，分为内核态和用户态/*//0为用户态，1为内核态
    public static int IR=0;//指令寄存器,指令类型InState
    public static int AR=0;//保存CPU当前所访问的主存单元的地址
    public static int CR3 = 0;//段基地址寄存器
    public static PCB PCBrun = new PCB();
    //PC=p.NowBlockNum*256+p.NowInstruct*4;
}
