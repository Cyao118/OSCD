package hardware;

import os.LowScheduling;
import os.RunStruct;
import ui.SetPerformance;

import java.io.IOException;

public class SystemClock extends Thread {
    public static int getTime() {
        return time;
    }
    public static void setTime(int time) {
        SystemClock.time = time;
    }
    private static int time = 0;
    public static int cir=1000;//每个时钟周期一秒
    public static volatile boolean stop = false;
    public SystemClock()
    {
        start();
    }
    public void run(){
        try{
            //System.out.println("Server");
            int threadtime= RunStruct.getSystemtime();
            while(true){
                if(!stop) {
                    try {
                        if (RunStruct.getSystemtime() >= time) {
                            System.out.println("当前时间 "+time);
                            SetPerformance.htxt.append("当前时间 "+time+"\r\n");
                            try {
                                SetPerformance.fw.write("当前时间 "+time+"\r\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            sleep(cir);//time每秒加1
                            time++;
                         }
                    }  catch(Exception e){ }
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
    //时钟类，线程实现，效果：后台线程运行，每秒time值+1；
}
