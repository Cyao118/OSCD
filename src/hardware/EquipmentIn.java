package hardware;

import os.Scheduling;
import struct.Instruct;
import struct.Job;
import ui.SetPerformance;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;

public class EquipmentIn {
    public static int AllJobNum=0;//现有作业数，用于生成作业id
    public static int minb = 3;
    public static int maxb = 8;
    public EquipmentIn(){
        ReadFile();
    }
    public void CreateFutureJob(int NUM){//建立未来作业请求
        int time=SystemClock.getTime();
        for(int i=0;i<NUM;i++)
        {
            Job test = new Job();
            //程序段
            test.setBlockNum(CreateRandom(minb, maxb));
            test.BlockList = new int[test.BlockNum];
            test.setCylinderNum(CreateRandom(4, 31));
            if (test.CylinderNum == 4){
                for(int j=0;j<test.BlockNum;j++)
                {
                    test.BlockList[j]=CreateRandom(32,63);
                    while(HardDisk.Instructnums[test.CylinderNum][test.BlockList[j]]==0)
                    {
                        test.BlockList[j]=CreateRandom(32,63);
                    }

                }
            }
            else {
                for(int j=0;j<test.BlockNum;j++)
                {
                    test.BlockList[j]=CreateRandom(0,63);
                }
            }
            for(int j=0;j<test.BlockNum;j++)
            {
                test.InstructCount+=HardDisk.Instructnums[test.CylinderNum][test.BlockList[j]];
            }

            int t2=HardDisk.Instructnums[test.CylinderNum][test.BlockList[0]];
            Instruct[] is =HardDisk.ReadOneTrackInstruct(test.CylinderNum,test.BlockList[0]);
            for(int ll=0;ll<t2;ll++)
            {
                if(is[ll].InState==4||is[ll].InState==6||is[ll].InState==8)
                t2=ll;
            }//确保PV不发生混乱
            if(t2>0)
            test.setInstructNum(CreateRandom(0,t2));
            else test.setInstructNum(0);

            test.InstructCount-=test.InstructNum;
            //说明书
            test.setJobId(AllJobNum+1);
            AllJobNum++;
            test.setFlag(false);
            test.setJobState(0);
            test.setJobPriority(CreateRandom(1,3));
            test.setCreateTime(time);
            time+=CreateRandom(1,5);
            System.out.println("新任务"+test.JobId+"在"+test.CreateTime+"到达");
            SetPerformance.htxt.append("新任务"+test.JobId+"在"+test.CreateTime+"到达\r\n");
            try {
                SetPerformance.fw.write("新任务"+test.JobId+"在"+test.CreateTime+"到达\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            AddJobRequest(test);
        }
    }
    public void CreateOneNewJob() {//即时建立一个新作业需求
        Job test = new Job();
        //程序段
        test.setBlockNum(CreateRandom(minb, maxb));
        test.BlockList = new int[test.BlockNum];
        test.setCylinderNum(CreateRandom(4, 31));
        if (test.CylinderNum == 4){
            for(int i=0;i<test.BlockNum;i++)
            {
                test.BlockList[i]=CreateRandom(32,63);
                while(HardDisk.Instructnums[test.CylinderNum][test.BlockList[i]]==0)
                {
                    test.BlockList[i]=CreateRandom(32,63);
                }
            }
        }
        else {
            for(int i=0;i<test.BlockNum;i++)
            {
                test.BlockList[i]=CreateRandom(0,63);
            }
        }
        for(int i=0;i<test.BlockNum;i++)
        {
            test.InstructCount+=HardDisk.Instructnums[test.CylinderNum][test.BlockList[i]];
        }
        int t2=HardDisk.Instructnums[test.CylinderNum][test.BlockList[0]];
        Instruct[] is =HardDisk.ReadOneTrackInstruct(test.CylinderNum,test.BlockList[0]);
        for(int ll=0;ll<t2;ll++)
        {
            if(is[ll].InState==4||is[ll].InState==6||is[ll].InState==8)
                t2=ll;
        }
        if(t2>0)
        test.setInstructNum(CreateRandom(0,t2));
        else test.setInstructNum(0);
        //说明书
        test.setJobId(AllJobNum+1);
        AllJobNum++;
        test.setFlag(false);
        test.setJobState(0);
        test.setJobPriority(CreateRandom(1,3));
        test.setCreateTime(SystemClock.getTime());
        System.out.println("新任务"+test.JobId+"在"+test.CreateTime+"到达");
        SetPerformance.htxt.append("新任务"+test.JobId+"在"+test.CreateTime+"到达\r\n");
        try {
            SetPerformance.fw.write("新任务"+test.JobId+"在"+test.CreateTime+"到达\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        AddJobRequest(test);
    }

    public void CreateJobFromFile(String filename) {//建立一个从文件读入的作业请求
        Job test = new Job();
        //程序段
        File f=new File(filename);
        InputStreamReader reader = null; // 建立一个输入流对象reader
        try {
            reader = new InputStreamReader(
                    new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(reader);
        try {
            br.readLine();
            String s[];
            s = br.readLine().split(":");
            test.InstructCount = Integer.valueOf(s[1]);
            s = br.readLine().split(":");
            test.BlockNum = Integer.valueOf(s[1]);
            br.readLine();
            test.BlockList = new int [test.BlockNum];
            for(int i=0;i<test.BlockNum;i++)
            {
                test.BlockList[i] = Integer.valueOf(br.readLine());
            }
            s = br.readLine().split(":");
            test.CylinderNum = Integer.valueOf(s[1]);
            s = br.readLine().split(":");
            test.InstructNum = Integer.valueOf(s[1]);
            br.readLine();
            br.readLine();
            br.readLine();
            br.readLine();
            br.readLine();
            test.setFlag(false);
            test.setJobId(AllJobNum+1);
            AllJobNum++;
            s = br.readLine().split(":");
            test.JobState = Integer.valueOf(s[1]);
            s = br.readLine().split(":");
            test.JobPriority = Integer.valueOf(s[1]);
            s = br.readLine().split(":");
            test.CreateTime = Integer.valueOf(s[1]);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //说明书
        System.out.println("新任务"+test.JobId+"在"+test.CreateTime+"到达");
        SetPerformance.htxt.append("新任务"+test.JobId+"在"+test.CreateTime+"到达\r\n");
        try {
            SetPerformance.fw.write("新任务"+test.JobId+"在"+test.CreateTime+"到达\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        AddJobRequest(test);
    }
    private int CreateRandom(Integer min,Integer max){
        Random random = new  Random();
        int r= random.nextInt(max) % (max-min+1) + min;
        return r;
    }
    public void AddJobRequest(Job j)//向作业需求队列中加入新作业
    {
        Scheduling.JobRequest.add(j);
    }
    public void ReadFile(){
        String path="D:/OS_ZXQ/Disk_0/Cylinder_0/Track_5.txt";
        File file = new File(path);
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String strLine = null;
        try {
            while(null != (strLine = bufferedReader.readLine())){
               for(int i=0;i<32;i++)
               {
                   for(int j=0;j<64;j++)
                   {
                       HardDisk.Instructnums[i][j]=Integer.valueOf(strLine);
                   }
               }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
