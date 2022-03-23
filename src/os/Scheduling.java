package os;

import struct.JCB;
import struct.Job;
import struct.PCB;

import java.util.ArrayList;

public class Scheduling {
    public static ArrayList<Job> JobRequest=new ArrayList<Job>();//作业需求队列
    public static ArrayList<Job> JobReserve=new ArrayList<Job>();//作业后备队列
    public static ArrayList<JCB> JcbTable=new ArrayList<JCB>();//JCB表，系统通过 JCB 感知作业的存在
    public static ArrayList<Job> JobWorking=new ArrayList<Job>();//运行状态的作业
    public static ArrayList<Job> JobAchieve=new ArrayList<Job>();//已完成的作业，实际上不存在，只是方便输出显示
    public static ArrayList<PCB> PCBReady=new ArrayList<PCB>();//活动就绪队列
    public static ArrayList<PCB> PCBStaticReady=new ArrayList<PCB>();//静止就绪队列
    public static ArrayList<PCB> PCBBlock=new ArrayList<PCB>();//活动阻塞队列
    public static ArrayList<PCB> PCBStaticBlock=new ArrayList<PCB>();//静止阻塞队列
    public static ArrayList<PCB> PCBMissBlock=new ArrayList<PCB>();//缺页阻塞队列

}
