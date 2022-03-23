package struct;
import os.InSpooling;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class Job {
    //作业由三部分构成：程序，数据和作业说明书，它是用户在完成一项任务过程中要求计算机系统所做工作的集合。
    //程序
    public int InstructCount;//作业运行所需指令数目
    public int BlockNum;//所包含盘块数目
    public int BlockList[];//所包含盘块序列
    public int InstructNum;//第一条指令指令编号
    public int CylinderNum;//同一作业的所有指令应在同一个磁道上

    //数据

    //作业说明书
    public int JobId;//作业ID即作业标识
    public boolean flag;//表示作业信息是否进入系统（即作业是否提交完毕）
    public int JobState;//作业状态，0表示提交，1表示收容（后备），2表示执行，3表示完成
    public int JobPriority;//作业/进程的优先级
    public JCB JobJCB;//每一个Job都有对应的JCB
    public int CreateTime;//作业生成时间
    public int Inspoolingnum;//输入井位置

    public Job()
    {
        InstructCount = 0;//
        CylinderNum = 0;
        JobId = 0;
        flag = false;
        JobState = 0;
        JobPriority = 0;
        BlockNum=0;
        BlockList=new int[BlockNum];
    }
    public void CreateJcb(){
        JobJCB=new JCB(this);
        JobJCB.setJobId(JobId);
        JobJCB.setPriority(JobPriority);
        JobJCB.setState(1);
        JobJCB.setInstructCount(InstructCount);
        JobJCB.setBlockList(BlockList);
        JobJCB.setBlockNum(BlockNum);
        JobJCB.setCylinderNum(CylinderNum);
        JobJCB.setInstructNum(InstructNum);
        JobJCB.setCreateTime(CreateTime);
        this.setJobState(1);
        this.flag=true;
    }
    public void WriteFile(int l1){

        String path="D:/OS_ZXQ/Disk_0/Cylinder_0/Track_"+ l1 +".txt";
        File f=new File(path);
        f.delete();
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileWriter fw=null;
        try {
            String s2="";
            for(int i=0;i<BlockNum;i++)
            {
                s2+="\r\n"+BlockList[i];
            }
            fw=new FileWriter(path,true);
            fw.write("程序：\r\n");
            String s="作业运行所需指令数目:"+InstructCount+"\r\n"
                    +"所包含盘块数目:"+BlockNum+"\r\n"
                    +"所包含盘块序列:"+s2+"\r\n"
                    +"磁道号:"+CylinderNum+"\r\n"
                    +"第一条指令指令编号:"+InstructNum+"\r\n"
                    +"\r\n";
            fw.write(s);
            fw.write("数据：\r\n");
            fw.write("\r\n");
            fw.write("作业说明书：\r\n");

            String  s1="作业ID:"+JobId+"\r\n"
                    +"作业状态:"+JobState+"\r\n"
                    +"作业优先级:"+JobPriority+"\r\n"
                    +"作业生成时间:"+CreateTime+"\r\n"
                    +"\r\n";
            fw.write(s1);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void WriteFinish(int l1,PCB p){

        String path="D:/OS_ZXQ/Disk_0/Cylinder_0/Track_"+ l1 +".txt";
        File f=new File(path);
        f.delete();
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileWriter fw=null;
        try {
            String s2="";
            for(int i=0;i<BlockNum;i++)
            {
                s2+="\r\n"+BlockList[i];
            }
            fw=new FileWriter(path,true);
            fw.write("程序：\r\n");
            String s="作业运行所需指令数目:"+InstructCount+"\r\n"
                    +"所包含盘块数目:"+BlockNum+"\r\n"
                    +"所包含盘块序列:"+s2+"\r\n"
                    +"磁道号:"+CylinderNum+"\r\n"
                    +"第一条指令指令编号:"+InstructNum+"\r\n"
                    +"\r\n";
            fw.write(s);
            fw.write("数据：\r\n");
            fw.write("\r\n");
            fw.write("作业说明书：\r\n");

            String  s1="作业ID:"+JobId+"\r\n"
                    +"作业状态:"+JobState+"\r\n"
                    +"作业优先级:"+JobPriority+"\r\n"
                    +"作业生成时间:"+CreateTime+"\r\n"
                    +"作业进入系统时间:"+JobJCB.JobInTime+"\r\n"
                    +"作业开始运行时间:"+JobJCB.StarTime+"\r\n"
                    +"作业结束时间:"+JobJCB.EndTime+"\r\n";
            fw.write(s1);

            fw.write("进程说明书：\r\n");
            DecimalFormat df =new DecimalFormat("#.00");//响应比保留两位小数
            s1="进程ID:"+JobJCB.ProId+"\r\n"
                +"进程执行到第几条指令:"+p.NowInstruct+"\r\n"
                +"进程开始时间:"+p.StarTime+"\r\n"
                +"进程结束时间:"+p.EndTime+"\r\n"
                +"进程占用CPU时间:" + p.RunTime+"\r\n"
                +"进程响应比:" +df.format((double)(p.EndTime-p.StarTime)/(double)p.RunTime);
            fw.write(s1);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void setCylinderNum(int cylinderNum) {
        CylinderNum = cylinderNum;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void setInstructCount(int instructCount) {
        InstructCount = instructCount;
    }
    public void setBlockList(int a[]){
        for(int i=0;i<BlockNum;i++)
        {
            BlockList[i]=a[i];
        }
    }

    public void setCreateTime(int createTime) {
        CreateTime = createTime;
    }

    public void setBlockNum(int blockNum) {
        BlockNum = blockNum;
    }

    public void setInstructNum(int instructNum) {
        InstructNum = instructNum;
    }

    public void setJobId(int jobId) {
        JobId = jobId;
    }

    public void setJobJCB(JCB jobJCB) {
        JobJCB = jobJCB;
    }

    public void setJobPriority(int jobPriority) {
        JobPriority = jobPriority;
    }

    public void setJobState(int jobState) {
        JobState = jobState;
    }


}
