package struct;

import hardware.SystemClock;
import os.HighLevelScheduling;

public class PCB {
    //每个进程都有页表，页表起始地址和页表长度的信息在进程不被CPU执行的时候，存放在其PCB内

    public int ProId;//进程号
    public int Priority;//进程的优先级
    public int ProState;//进程状态，0表示运行，1表示活动就绪，2表示静止就绪，3表示活动阻塞，4表示静止阻塞, 5表示缺页阻塞，6完成
    public int InstructCount;//作业运行所需指令数目
    public int BlockNum;//所包含盘块数目
    public int BlockList[];//所包含盘块序列
    public int CylinderNum;//所在磁道号
    public int InstructNum;//第一条指令指令编号
    public int NowBlockNum;//当前执行指令执行到了第几个盘块
    public int NowInstruct;//当前执行到了第几条指令
    public int StarTime;//进程进入内存时间
    public int RunTime;//进程占用CPU运行时间
    public int EndTime; //作业/进程结束时间
    public int timeslice; //时间片长度
    public int PCBArea;//该PCB所在内存物理块号
    public int PageArea;//该进程页表所在内存物理块号
    public int PageLength;//页表长度
    public int Blockreason = 0;//阻塞原因，0缺页，1缺资源
    public int Blocktime = 0;
    public int Statictime = 0;//挂起时间
    public int AllocateA = 0,AllocateB = 0,AllocateC = 0;
    public int NeedA = 0,NeedB = 0,NeedC = 0;
    public Page page;
    public void CreatePCB(JCB j){
        setProId(HighLevelScheduling.countPro);
        HighLevelScheduling.countPro++;
        j.setProId(ProId);
        setPriority(j.Priority);
      //  setProState(1);
        setInstructCount(j.InstructCount);
        setBlockNum(j.BlockNum);
        setBlockList(j.BlockList);
        setCylinderNum(j.CylinderNum);
        setInstructNum(j.InstructNum);
        setNowBlockNum(0);
        setNowInstruct(0);
        setStarTime(SystemClock.getTime());
        setRunTime(0);
        setTimeslice(0);
        setPageLength(BlockNum);
        page=new Page(this);
    }

    public void setPriority(int priority) {
        Priority = priority;
    }

    public void setCylinderNum(int cylinderNum) {
        CylinderNum = cylinderNum;
    }

    public void setInstructCount(int instructCount) {
        InstructCount = instructCount;
    }

    public void setInstructNum(int instructNum) {
        InstructNum = instructNum;
    }

    public void setBlockNum(int blockNum) {
        BlockNum = blockNum;
    }

    public void setProId(int proId) {
        ProId = proId;
    }

    public void setStarTime(int starTime) {
        StarTime = starTime;
    }

    public int getRunTime() { return RunTime; }

    public void setRunTime(int runTime) { RunTime = runTime; }

    public void setBlockList(int[] blockList) {
        BlockList = blockList;
    }

    public void setEndTime(int endTime) {
        EndTime = endTime;
    }

    public void setNowInstruct(int nowInstruct) {
        NowInstruct = nowInstruct;
    }

    public void setNowBlockNum(int nowBlockNum) {
        NowBlockNum = nowBlockNum;
    }

    public void setPageLength(int pageLength) {
        PageLength = pageLength;
    }

    public int getPCBArea() {
        return PCBArea;
    }

    public void setPCBArea(int PCBArea) {
        this.PCBArea = PCBArea;
    }

    public int getPageArea() {
        return PageArea;
    }

    public void setPageArea(int pageArea) {
        PageArea = pageArea;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public void setProState(int proState) {
        ProState = proState;
    }

    public void setTimeslice(int timeslice) {
        this.timeslice = timeslice;
    }

    public  String PrintState()
    {
        switch (ProState)
        {
            case 0:return  "正在运行\t";
            case 1:return  "活动就绪\t";
            case 2:return  "静止就绪\t";
            case 3:return  "活动阻塞\t";
            case 4:return  "静止阻塞\t";
            case 5:return  "缺页阻塞\t";
            case 6:return  "完成结束\t";
            default:return  "";
        }
    }
}
