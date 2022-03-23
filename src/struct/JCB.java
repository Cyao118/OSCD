package struct;
import hardware.*;

public class JCB {
    //为了调度和管理作业，在批处理系统中，为每一个作业配置了一个JCB
    //JCB是作业存在的标志
    //包括作业情况、作业状态、资源需求、资源使用情况等
    //作业情况
    private String username;//用户名 默认为ZXQ
    public int JobId;//作业ID即作业标识
    public int ProId;//作业对应进程
    public int Priority;//作业/进程的优先级
    //作业状态
    public int State;//作业状态，0表示提交，1表示收容（后备），2表示执行，3表示完成
    // 资源需求
    public int InstructCount;//作业运行所需指令数目
    public int BlockNum;//所包含盘块数目
    public int BlockList[];//所包含盘块序列
    public int CylinderNum;//所在磁道号
    public int InstructNum;//第一条指令指令编号
    // 资源使用情况
    public int CreateTime;
    public int JobInTime;//作业进入系统时间(进入输入井时间)
    public int StarTime;//开始运行时间
    public int EndTime;//作业完成时间

    public void setCreateTime(int createTime) {
        CreateTime = createTime;
    }

    public void setJobId(int jobId) {
        JobId = jobId;
    }

    public void setInstructNum(int instructNum) {
        InstructNum = instructNum;
    }

    public void setInstructCount(int instructCount) {
        InstructCount = instructCount;
    }

    public void setCylinderNum(int cylinderNum) {
        CylinderNum = cylinderNum;
    }

    public void setJobInTime(int jobInTime) {
        JobInTime = jobInTime;
    }

    public void setPriority(int priority) {
        Priority = priority;
    }

    public void setBlockList(int BlockList[] ){
        for(int i=0;i<BlockNum;i++)
         {
            this.BlockList[i]=BlockList[i];
         }
    }

    public void setBlockNum(int blockNum) {
        BlockNum = blockNum;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setProId(int proId) {
        ProId = proId;
    }

    public void setStarTime(int starTime) {
        StarTime = starTime;
    }

    public String getUsername() {
        return username;
    }

    public void setState(int state) {
        State = state;
    }

    public JCB(Job j)
    {
        username="ZXQ";
        JobId = j.JobId;
        Priority = j.JobPriority;
        State = j.JobState;
        InstructCount = j.InstructCount;
        CylinderNum = j.CylinderNum;
        InstructNum = j.InstructNum;
        BlockNum = j.BlockNum;
        BlockList=new int[BlockNum];
        for(int i=0;i<BlockNum;i++)
        {
            this.BlockList[i]=j.BlockList[i];
        }
        JobInTime = SystemClock.getTime();
    }
}
