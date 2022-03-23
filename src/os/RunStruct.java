package os;

import hardware.*;
import struct.Instruct;
import struct.InstructManual;
import struct.PCB;
import ui.SetPerformance;

import java.io.IOException;
import java.sql.Time;
import java.util.Random;

public class RunStruct extends Thread {
    public static int getSystemtime() {
        return systemtime;
    }

    public static void setSystemtime(int systemtime) {
        RunStruct.systemtime = systemtime;
    }

    private static int systemtime = 0;
    private static int Timeslice = 10;//时间片
    private static int nowslice = 0;

    public static int Runable = 0;//判断CPU内有无进程运行

    public static volatile boolean stop = false;
    public RunStruct()
    {
        start();
    }
    public void run(){
        try{
            int nowslice = 0;
            //System.out.println("Server");
            systemtime = SystemClock.getTime();
            while(true){
                if(!stop) {
                    try {
                        if (SystemClock.getTime() >= systemtime) {
                            RunOneSlice();
                            //执行
                            }
                        } catch (Exception e) {
                    }
                }
            }
        }
        finally{}
    }
    public static void StopMe()
    {
        stop=true;
    }

    public static void ReStartMe()
    {
        stop=false;
    }

    private int Random(Integer min,Integer max){
        Random random = new  Random();
        int r= random.nextInt(max) % (max-min+1) + min;
        return r;
    }

    public void RunOneSlice(){


        if(nowslice == Timeslice){//一个时间片用完
            nowslice = 0;
            LowScheduling.Timefinish = 1;
            System.out.println("时间片用完，执行调度");
            SetPerformance.htxt.append("时间片用完，执行调度\r\n");
            try {
                SetPerformance.fw.write("时间片用完，执行调度\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            CPU.PSW =1;//传递时间片完信号进入调度
            //等待调度结束
        }
        if(CPU.PSW == 0)//系统处于用户态可以执行指令
        {
            if (CPU.PC == CPU.PCBrun.InstructCount  * 8) {//进程全部指令执行完毕
                LowScheduling.Processfinish = 1;
                Runable = 0;
                System.out.println("进程" + CPU.PCBrun.ProId + "完成");
                SetPerformance.htxt.append("进程" + CPU.PCBrun.ProId + "完成\r\n");
                try {
                    SetPerformance.fw.write("进程" + CPU.PCBrun.ProId + "完成\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                CPU.PSW = 1;//传递完成信号
            }
            else {
                PCB p = CPU.PCBrun;
                if (BitMap.Isexist() == 0) {
                    LowScheduling.LossPage = 1;
                    Runable = 0;
                    System.out.println("进程" + CPU.PCBrun.ProId + "缺页中断");
                    SetPerformance.htxt.append("进程" + CPU.PCBrun.ProId + "缺页中断\r\n");
                    try {
                        SetPerformance.fw.write("进程" + CPU.PCBrun.ProId + "缺页中断\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    CPU.PSW = 1;//传递缺页中断信号
                } else {

                    Runable = 1;
                  //  System.out.println("取指");
                    Instruct i = MMU.GetInstruct();//取指
                    int Address = (CPU.AR+32)*256+((CPU.CR3+CPU.PC)%256);


                    CPU.IR = i.InState;//将指令取出
                    System.out.println("当前进程" + CPU.PCBrun.ProId + " PC " + CPU.PC + " 页号 " + (CPU.CR3 + CPU.PC) / 256+" 内存物理地址 "+Address + " 运行指令" + CPU.IR);
                    SetPerformance.htxt.append("当前进程" + CPU.PCBrun.ProId + " PC " + CPU.PC + " 页号 " + (CPU.CR3 + CPU.PC) / 256+" 内存物理地址 "+Address + " 运行指令" + CPU.IR+"\r\n");
                    try {
                        SetPerformance.fw.write("当前进程" + CPU.PCBrun.ProId + " PC " + CPU.PC + " 页号 " + (CPU.CR3 + CPU.PC) / 256+" 内存物理地址 "+Address + " 运行指令" + CPU.IR+"\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    int Structtime = InstructManual.GetStructtime(CPU.IR);
                    if (Structtime <= (Timeslice - nowslice)) {
                        RunInstruct();
                      //  System.out.println("指令运行完毕");
                    } else//时间片不够执行完该指令，空转
                    {
                        System.out.println("当前剩余时间片不足，空转");
                        SetPerformance.htxt.append("当前剩余时间片不足，空转\r\n");
                        try {
                            SetPerformance.fw.write("当前剩余时间片不足，空转\r\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        systemtime += Timeslice - nowslice;
                        try {
                            sleep(SystemClock.cir * (Timeslice - nowslice));

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        nowslice = Timeslice;
                    }


                }
            }
        }
        else
        {
            nowslice++;
            systemtime++;
            try {
                sleep( SystemClock.cir );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void RunInstruct()
    {
        switch (CPU.IR) {//执行指令
            case 0:
                Instruct0();
                break;
            case 1:
                Instruct1();
                break;
            case 2:
                Instruct2();
                break;
            case 3:
                Instruct3();
                break;
            case 4:
                Instruct4();
                break;
            case 5:
                Instruct5();
                break;
            case 6:
                Instruct6();
                break;
            case 7:
                Instruct7();
                break;
            case 8:
                Instruct8();
                break;
            case 9:
                Instruct9();
                break;
            default:
                break;
        }

    }
    public void RunInstruct2()
    {
        int Structtime = InstructManual.GetStructtime(CPU.IR);
        try {//仿真指令执行用时
            nowslice += Structtime;
            systemtime += Structtime;
            CPU.PCBrun.RunTime += Structtime;
            sleep(SystemClock.cir * Structtime);
            //System.out.println(systemtime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CPU.PC = CPU.PC + 8;//pc=pc+8
    }
    public void Instruct0(){
        System.out.println("进程"+CPU.PCBrun.ProId+"执行系统调用指令1");
        SetPerformance.htxt.append("进程"+CPU.PCBrun.ProId+"执行系统调用指令1\r\n");
        try {
            SetPerformance.fw.write("进程"+CPU.PCBrun.ProId+"执行系统调用指令1\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        int Structtime = InstructManual.GetStructtime(CPU.IR);
        try {//仿真指令执行用时
            nowslice += Structtime;
            systemtime += Structtime;
            CPU.PCBrun.RunTime += Structtime;
            sleep(SystemClock.cir * Structtime);
            //System.out.println(systemtime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CPU.PC = CPU.PC + 8;//pc=pc+8
        CPU.PCBrun.Blockreason = 1;
        LowScheduling.IsBlocked = 1;//系统调用指令
        Runable = 0;
        CPU.PSW = 1;


    }
    public void Instruct1(){
        System.out.println("进程"+CPU.PCBrun.ProId+"执行系统调用指令2");
        SetPerformance.htxt.append("进程"+CPU.PCBrun.ProId+"执行系统调用指令2\r\n");
        try {
            SetPerformance.fw.write("进程"+CPU.PCBrun.ProId+"执行系统调用指令2\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        int Structtime = InstructManual.GetStructtime(CPU.IR);
        try {//仿真指令执行用时
            nowslice += Structtime;
            systemtime += Structtime;
            CPU.PCBrun.RunTime += Structtime;
            sleep(SystemClock.cir * Structtime);
            //System.out.println(systemtime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CPU.PC = CPU.PC + 8;//pc=pc+8
        CPU.PCBrun.Blockreason = 1;
        LowScheduling.IsBlocked = 1;//系统调用指令
        Runable = 0;
        CPU.PSW = 1;


    }
    public void Instruct2(){
        System.out.println("进程"+CPU.PCBrun.ProId+"执行单周期运算指令");
        SetPerformance.htxt.append("进程"+CPU.PCBrun.ProId+"执行单周期运算指令\r\n");
        try {
            SetPerformance.fw.write("进程"+CPU.PCBrun.ProId+"执行单周期运算指令\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        int Structtime = InstructManual.GetStructtime(CPU.IR);
        try {//仿真指令执行用时
            nowslice += Structtime;
            systemtime += Structtime;
            CPU.PCBrun.RunTime += Structtime;
            sleep(SystemClock.cir * Structtime);
            //System.out.println(systemtime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CPU.PC = CPU.PC + 8;//pc=pc+8

    }
    public void Instruct3(){
        System.out.println("进程"+CPU.PCBrun.ProId+"执行双周期运算指令");
        SetPerformance.htxt.append("进程"+CPU.PCBrun.ProId+"执行双周期运算指令\r\n");
        try {
            SetPerformance.fw.write("进程"+CPU.PCBrun.ProId+"执行双周期运算指令\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        int Structtime = InstructManual.GetStructtime(CPU.IR);
        try {//仿真指令执行用时
            nowslice += Structtime;
            systemtime += Structtime;
            CPU.PCBrun.RunTime += Structtime;
            sleep(SystemClock.cir * Structtime);
            //System.out.println(systemtime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CPU.PC = CPU.PC + 8;//pc=pc+8
    }
    public void Instruct4(){
        if(PublicSource.SignalA<=0)
        {
            System.out.println("进程"+CPU.PCBrun.ProId+"申请资源A失败，进入阻塞队列");
            SetPerformance.htxt.append("进程"+CPU.PCBrun.ProId+"申请资源A失败，进入阻塞队列\r\n");
            try {
                SetPerformance.fw.write("进程"+CPU.PCBrun.ProId+"申请资源A失败，进入阻塞队列\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            int Structtime = InstructManual.GetStructtime(CPU.IR);
            try {//仿真指令执行用时
                nowslice += Structtime;
                systemtime += Structtime;
                CPU.PCBrun.RunTime += Structtime;
                sleep(SystemClock.cir * Structtime);
                //System.out.println(systemtime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            CPU.PC = CPU.PC + 8;//pc=pc+8
            CPU.PCBrun.NeedA++;
            LowScheduling.IsBlocked = 1;//阻塞
            CPU.PCBrun.Blockreason = 2;
            Runable = 0;
            CPU.PSW = 1;
        }
        else
        {
            System.out.println("进程"+CPU.PCBrun.ProId+"申请资源A成功");
            SetPerformance.htxt.append("进程"+CPU.PCBrun.ProId+"申请资源A成功\r\n");
            try {
                SetPerformance.fw.write("进程"+CPU.PCBrun.ProId+"申请资源A成功\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Primitive.PA(CPU.PCBrun);
            int Structtime = InstructManual.GetStructtime(CPU.IR);
            try {//仿真指令执行用时
                nowslice += Structtime;
                systemtime += Structtime;
                CPU.PCBrun.RunTime += Structtime;
                sleep(SystemClock.cir * Structtime);
                //System.out.println(systemtime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            CPU.PC = CPU.PC + 8;//pc=pc+8
        }
    }
    public void Instruct5(){
        System.out.println("进程"+CPU.PCBrun.ProId+"释放资源A");
        SetPerformance.htxt.append("进程"+CPU.PCBrun.ProId+"释放资源A\r\n");
        try {
            SetPerformance.fw.write("进程"+CPU.PCBrun.ProId+"释放资源A\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Primitive.VA(CPU.PCBrun);
        int Structtime = InstructManual.GetStructtime(CPU.IR);
        try {//仿真指令执行用时
            nowslice += Structtime;
            systemtime += Structtime;
            CPU.PCBrun.RunTime += Structtime;
            sleep(SystemClock.cir * Structtime);
            //System.out.println(systemtime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CPU.PC = CPU.PC + 8;//pc=pc+8
    }
    public void Instruct6(){
        if(PublicSource.SignalB <=0)
        {
            System.out.println("进程"+CPU.PCBrun.ProId+"申请资源B失败，进入阻塞队列");
            SetPerformance.htxt.append("进程"+CPU.PCBrun.ProId+"申请资源B失败，进入阻塞队列\r\n");
            try {
                SetPerformance.fw.write("进程"+CPU.PCBrun.ProId+"申请资源B失败，进入阻塞队列\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            int Structtime = InstructManual.GetStructtime(CPU.IR);
            try {//仿真指令执行用时
                nowslice += Structtime;
                systemtime += Structtime;
                CPU.PCBrun.RunTime += Structtime;
                sleep(SystemClock.cir * Structtime);
                //System.out.println(systemtime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            CPU.PC = CPU.PC + 8;//pc=pc+8
            CPU.PCBrun.NeedB++;
            LowScheduling.IsBlocked = 1;//阻塞
            Runable = 0;
            CPU.PCBrun.Blockreason = 2;
            CPU.PSW = 1;
        }
        else
        {
            System.out.println("进程"+CPU.PCBrun.ProId+"申请资源B成功");
            SetPerformance.htxt.append("进程"+CPU.PCBrun.ProId+"申请资源B成功\r\n");
            try {
                SetPerformance.fw.write("进程"+CPU.PCBrun.ProId+"申请资源B成功\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Primitive.PB(CPU.PCBrun);
            int Structtime = InstructManual.GetStructtime(CPU.IR);
            try {//仿真指令执行用时
                nowslice += Structtime;
                systemtime += Structtime;
                CPU.PCBrun.RunTime += Structtime;
                sleep(SystemClock.cir * Structtime);
                //System.out.println(systemtime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            CPU.PC = CPU.PC + 8;//pc=pc+8
        }

    }
    public void Instruct7(){
        System.out.println("进程"+CPU.PCBrun.ProId+"释放资源B");
        SetPerformance.htxt.append("进程"+CPU.PCBrun.ProId+"释放资源B\r\n");
        try {
            SetPerformance.fw.write("进程"+CPU.PCBrun.ProId+"释放资源B\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Primitive.VB(CPU.PCBrun);
        int Structtime = InstructManual.GetStructtime(CPU.IR);
        try {//仿真指令执行用时
            nowslice += Structtime;
            systemtime += Structtime;
            CPU.PCBrun.RunTime += Structtime;
            sleep(SystemClock.cir * Structtime);
            //System.out.println(systemtime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CPU.PC = CPU.PC + 8;//pc=pc+8
    }
    public void Instruct8(){
        if(PublicSource.SignalC<=0)
        {
            System.out.println("进程"+CPU.PCBrun.ProId+"申请资源C失败，进入阻塞队列");
            SetPerformance.htxt.append("进程"+CPU.PCBrun.ProId+"申请资源C失败，进入阻塞队列\r\n");
            try {
                SetPerformance.fw.write("进程"+CPU.PCBrun.ProId+"申请资源C失败，进入阻塞队列\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            int Structtime = InstructManual.GetStructtime(CPU.IR);
            try {//仿真指令执行用时
                nowslice += Structtime;
                systemtime += Structtime;
                CPU.PCBrun.RunTime += Structtime;
                sleep(SystemClock.cir * Structtime);
                //System.out.println(systemtime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            CPU.PC = CPU.PC + 8;//pc=pc+8
            CPU.PCBrun.NeedC++;
            LowScheduling.IsBlocked = 1;//阻塞
            Runable = 0;
            CPU.PCBrun.Blockreason = 2;
            CPU.PSW = 1;
        }
        else
        {
            System.out.println("进程"+CPU.PCBrun.ProId+"申请资源C成功");
            SetPerformance.htxt.append("进程"+CPU.PCBrun.ProId+"申请资源C成功\r\n");
            try {
                SetPerformance.fw.write("进程"+CPU.PCBrun.ProId+"申请资源C成功\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Primitive.PC(CPU.PCBrun);
            int Structtime = InstructManual.GetStructtime(CPU.IR);
            try {//仿真指令执行用时
                nowslice += Structtime;
                systemtime += Structtime;
                CPU.PCBrun.RunTime += Structtime;
                sleep(SystemClock.cir * Structtime);
                //System.out.println(systemtime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            CPU.PC = CPU.PC + 8;//pc=pc+8
        }

    }
    public void Instruct9(){
        System.out.println("进程"+CPU.PCBrun.ProId+"释放资源C");
        SetPerformance.htxt.append("进程"+CPU.PCBrun.ProId+"释放资源C\r\n");
        try {
            SetPerformance.fw.write("进程"+CPU.PCBrun.ProId+"释放资源C\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Primitive.VC(CPU.PCBrun);
        int Structtime = InstructManual.GetStructtime(CPU.IR);
        try {//仿真指令执行用时
            nowslice += Structtime;
            systemtime += Structtime;
            CPU.PCBrun.RunTime += Structtime;
            sleep(SystemClock.cir * Structtime);
            //System.out.println(systemtime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CPU.PC = CPU.PC + 8;//pc=pc+8
    }



}
