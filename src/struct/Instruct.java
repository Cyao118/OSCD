package struct;

public class Instruct {
    //每条指令8字节
    public int InNum;//指令号
    public int InBlock; //指令所在盘块编号
    public int InCylinder; //指令所在磁道编号
    public int InTime;//指令执行时间
    public int InState;//指令类别
    public Instruct(){
        InState=0;
        InTime=0;
        InBlock=0;
        InCylinder=0;
        InNum=0;
    }
    public void setInCylinder(int inCylinder) { InCylinder = inCylinder; }
    public void setInBlock(int inBlock) {
        InBlock = inBlock;
    }
    public void setInState(int inState) {
        InState = inState;
    }
    public void setInNum(int inNum) {
        InNum = inNum;
    }
    public void setInTime(int inTime) {
        InTime = inTime;
    }
}
