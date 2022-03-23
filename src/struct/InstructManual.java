package struct;

public class InstructManual {
    //指令说明书

    public static int TypeNum = 10;
    private static int[] time;//不同指令类型的时钟周期
    //资源值包括三类：A、B、C
    public InstructManual()
    {
        time=new int[TypeNum];
        time[0] = 1;//nanosleep 使进程睡眠指定的时间;系统调用指令，CPU切换为内核态，进程睡眠，时钟周期为1
        time[1] = 2;//vfork 创建一个子进程，与父进程内容相同，系统调用指令，CPU切换为内核态，时钟周期为2
        time[2] = 1;//单周期运算类指令，时钟周期为1
        time[3] = 2;//双周期运算类指令，时钟周期为2
        time[4] = 1;//申请使用资源A，时钟周期为1
        time[5] = 1;//释放资源A，时钟周期为1
        time[6] = 2;//申请使用资源B，时钟周期为2
        time[7] = 1;//释放资源B，时钟周期为1
        time[8] = 1;//申请使用资源C，时钟周期为1
        time[9] = 2;//释放资源C，时钟周期为2
    }
    public static int GetStructtime(int InState)
    {
        int t = time[InState];
        return t;
    }
    public static String GetStructname(int Instate)
    {
        switch (Instate)
        {
            case 0:return "系统调用指令1";
            case 1:return "系统调用指令2";
            case 2:return "单周期运算类指令";
            case 3:return "双周期运算类指令";
            case 4:return "申请使用资源A指令";
            case 5:return "释放资源A指令";
            case 6:return "申请使用资源B指令";
            case 7:return "释放资源B指令";
            case 8:return "申请使用资源C指令";
            case 9:return "释放资源C指令";
            default:return "未知指令";
        }
    }

}
