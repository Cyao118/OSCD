package ui;

import hardware.CPU;
import hardware.InternalMemory;
import hardware.SystemClock;
import os.BitMap;
import os.RunStruct;
import struct.InstructManual;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SetCPUPerfor extends Thread{

    public static volatile boolean stop = false;
    public static void StopMe()
    {
        stop = true;
    }
    public static void ReStartMe()
    {
        stop = false;
    }
    public SetCPUPerfor()
    {
        start();
    }
    public void run()
    {
        while(true){
                int i = 0;
                while (true) {
                    if(CPU.PSW == 0&&!stop) {
                        SetPerformance.CPUs1.setText(Integer.toString(CPU.PCBrun.ProId));
                        SetPerformance.CPUs2.setText(Integer.toString(CPU.IR));
                        SetPerformance.CPUs3.setText(Integer.toString(SystemClock.getTime()));
                        double factor = (double)( CPU.IR+1);
                        CPUPic.hundroud = CPU.IR;
                        CPUPic.jfreechart.setTitle("CPU正在执行" + InstructManual.GetStructname(CPUPic.hundroud));
                        CPUPic.jfreechart.getTitle().setFont(new Font("微软雅黑", 0, 16));//设置标题字体

                        CPUPic.xyCPUseries.add(i, factor);

                        try {
                            Thread.currentThread();
                            Thread.sleep(SystemClock.cir);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        i++;
                        if (i == 60) {
                            i = 0;
                            CPUPic.xyCPUseries.delete(0, 59);
                            continue;
                        }
                    }
                    else if(CPU.PSW == 1&&!stop)
                    {
                        SetPerformance.CPUs1.setText("");
                        SetPerformance.CPUs2.setText("");
                        SetPerformance.CPUs3.setText(Integer.toString(SystemClock.getTime()));
                        double factor = 0;
                        CPUPic.hundroud = 0;
                        CPUPic.jfreechart.setTitle("CPU当前无进程执行");
                        CPUPic.jfreechart.getTitle().setFont(new Font("微软雅黑", 0, 16));//设置标题字体

                        CPUPic.xyCPUseries.add(i, factor);

                        try {
                            Thread.currentThread();
                            Thread.sleep(SystemClock.cir);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        i++;
                        if (i == 60) {
                            i = 0;
                            CPUPic.xyCPUseries.delete(0, 59);
                            continue;
                        }
                    }
                    if(!stop)
                    {
                        for(int k=0;k<64;k++){
                            if(k>=32){
                                SetPerformance.mem[k].setText(String.valueOf(BitMap.Bitmap[k-32][0]));
                            }
                            else {
                                SetPerformance.mem[k].setText(String.valueOf(InternalMemory.YorN[k]));
                            }
                        }

                    }
            }
        }
    }
}
