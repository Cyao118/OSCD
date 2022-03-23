package os;

import hardware.EquipmentIn;
import hardware.SystemClock;
import struct.JCB;
import struct.Job;
import ui.SetPerformance;

import java.io.IOException;
import java.util.ArrayList;

public class InSpooling extends Thread{
    //判断输入井是否满，没满且作业创建时间小于等于当前时间就从作业请求队列调入作业进入输入井，并创建JCB
    public static volatile boolean stopIn= false;
    public static int length=0;
    public static int[] Allocate = new int[28];
    public InSpooling(){
        for(int i=0;i<28;i++)
        {
            Allocate[i]=0;
        }
        start();
    }
    public void StopMe() {
        stopIn=true;
    }
    public void ReStartMe() {
        stopIn=false;
    }
    public void run(){

            while(true){
                if(!stopIn){
                    length = Scheduling.JcbTable.size();
                    for (int i = 0; i < Scheduling.JobRequest.size(); i++) {
                        if (SystemClock.getTime() >= Scheduling.JobRequest.get(i).CreateTime&&length < 28) {//磁盘输入井为28
                            System.out.println(Scheduling.JobRequest.get(i).CreateTime + "时" + Scheduling.JobRequest.get(i).JobId + "任务到达");
                            SetPerformance.htxt.append(Scheduling.JobRequest.get(i).CreateTime + "时" + Scheduling.JobRequest.get(i).JobId + "任务到达\r\n");
                            try {
                                SetPerformance.fw.write(Scheduling.JobRequest.get(i).CreateTime + "时" + Scheduling.JobRequest.get(i).JobId + "任务到达\r\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Job j = Scheduling.JobRequest.get(i);
                            j.CreateJcb();
                            for(int k =0;k<28;k++){
                                if(Allocate[k] == 0) {
                                    Allocate[k] = 1;
                                    j.Inspoolingnum = k;
                                    j.WriteFile(k+8);
                                    break;
                                }
                            }
                            Scheduling.JcbTable.add(j.JobJCB);
                            length = Scheduling.JcbTable.size();
                            Scheduling.JobReserve.add(j);
                            Scheduling.JobRequest.remove(i);
                            i--;
                        }
                    }
                }
            }

    }
}
