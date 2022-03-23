package hardware;

import struct.Instruct;
import struct.PCB;
import struct.Page;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class InternalMemory {
    public static String Addressstr = "D:/OS_ZXQ/Inmem/";
    public int ImemTrack[];//0-7块系统区，8-19页表，20-31PCB池，32-63用户区

    public static ArrayList<PCB> PCBMenoryList = new ArrayList<PCB>();//内存PCB池和页表区
    public static Instruct[][] InstructList = new Instruct[32][32];//内存用户区

    public static int YorN[]=new int[32];
    public InternalMemory(){
        ImemTrack=new int[64];
            for(int i =0 ;i<32;i++)
            {
                for(int j=0;j<32;j++)
                {
                    InstructList[i][j]=new Instruct();
                }
            }
        YorN[0] = 1;
        YorN[1] = 1;
    }
    //public
    public void FirstLoading(){
        String path=Addressstr ;
        File FilePath=new File(path);
        FilePath.mkdirs();
        for(int i=0;i<64;i++)
        {
            path=Addressstr ;
            path+="Track_"+i+".txt";
            File f=new File(path);
            f.delete();
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for(int i=0;i<32;i++)
            YorN[i]=0;//表示空
    }
    public static void ReadSystemBlock()
    {

    }
    public static void WriteSystemBlock(){

    }

    public static void ReadPagelistBlock(){

    }

    public static void WritePagelistBlock(PCB p){
        String path=Addressstr ;
        path+="Track_"+p.getPageArea()+".txt";
        File f=new File(path);
        f.delete();//清除原有数据
        try {
            f.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(f));
            out.write("PCBArea:\r\n" + p.PCBArea);
            out.write("\r\nPageArea:\r\n" + p.PageArea);
            out.write("\r\nPageLength:\r\n" + p.PageLength);
            out.write("\r\nPage:\r\nProid\tPGNum\tMBNum\tYorN\tLastTime" );
            for(int i=0;i<p.PageLength;i++){
                out.write("\r\n"+p.page.page[i][0]+"\t"
                        +p.page.page[i][1]+"\t"
                        +p.page.page[i][2]+"\t"
                        +p.page.page[i][3]+"\t"
                        +p.page.page[i][4]);
            }
            out.flush(); // 把缓存区内容压入文件
            out.close(); // 最后记得关闭文件

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static String ItoS(int num,int length,int radix)//将int转为定长指定进制的字符串，num数值，长度，radix进制
    {
        String temp = Integer.toString(num,radix);//转化为指定进制
        int tr=Integer.valueOf(temp);
        String Length="%"+Integer.toString(length)+"d";//指定长度
        String str = String.format(Length, tr).replace(" ", "0");
        return str;
    }


    public static PCB ReadPCBlistBlock(int Blocknum){//读PCB
        PCB p=new PCB();
        String path=Addressstr ;
        path+="Track_"+Blocknum+".txt";
        File f=new File(path);
        InputStreamReader reader = null; // 建立一个输入流对象reader
        try {
            reader = new InputStreamReader(
                    new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
        try {
            br.readLine();
            p.ProId = Integer.valueOf(br.readLine());
            br.readLine();
            p.Priority = Integer.valueOf(br.readLine());
            br.readLine();
            p.ProState = Integer.valueOf(br.readLine());
            br.readLine();
            p.InstructCount = Integer.valueOf(br.readLine());
            br.readLine();
            p.BlockNum = Integer.valueOf(br.readLine());
            br.readLine();
            p.BlockList = new int[Blocknum];
            for(int k=0;k<p.BlockNum;k++){
                p.BlockList[k]=Integer.valueOf(br.readLine());
            }
            br.readLine();
            p.CylinderNum = Integer.valueOf(br.readLine());
            br.readLine();
            p.InstructNum = Integer.valueOf(br.readLine());
            br.readLine();
            p.NowInstruct = Integer.valueOf(br.readLine());
            br.readLine();
            p.NowBlockNum = Integer.valueOf(br.readLine());
            br.readLine();
            p.StarTime = Integer.valueOf(br.readLine());
            br.readLine();
            p.EndTime = Integer.valueOf(br.readLine());
            br.readLine();
            p.timeslice = Integer.valueOf(br.readLine());
            br.readLine();
            p.PCBArea = Integer.valueOf(br.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p;
    }

    public static void WritePCBlistBlock(PCB p){//写PCB
        String path=Addressstr ;
        path+="Track_"+p.getPCBArea()+".txt";
        File f=new File(path);
        f.delete();//清除原有数据
        try {
            f.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(f));
            out.write("ProId:\r\n"+p.ProId);
            out.write("\r\nPriority:\r\n"+p.Priority);
            out.write("\r\nProState:\r\n"+p.ProState);
            out.write("\r\nInstructCount:\r\n"+p.InstructCount);
            out.write("\r\nBlockNum:\r\n"+p.BlockNum);
            out.write("\r\nBlockList:\r\n");
            for(int k=0;k<p.BlockNum;k++){
                out.write(p.BlockList[k]+"\r\n");
            }
            out.write("CylinderNum:\r\n"+p.CylinderNum);
            out.write("\r\nInstructNum:\r\n"+p.InstructNum);
            out.write("\r\nNowInstruct:\r\n"+p.NowInstruct);
            out.write("\r\nNowBlockNum:\r\n"+p.NowBlockNum);
            out.write("\r\nStarTime:\r\n"+p.StarTime);
            out.write("\r\nRunTime:\r\n"+p.RunTime);
            out.write("\r\nEndTime:\r\n"+p.EndTime);
            out.write("\r\ntimeslice:\r\n"+p.timeslice);
            out.write("\r\nPCBArea:\r\n"+p.PCBArea);
            out.flush(); // 把缓存区内容压入文件
            out.close(); // 最后记得关闭文件
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ReadInstructBlock(){
    }

    public static void WriteInstructBlock(){
    }
    public static boolean PDIM(){//判断内存PCB池和页表区是否有位置
        boolean jg=false;
        int k=0;
        for(int i=8;i<20;i++)
        {
            if(YorN[i]==0)
            {
                k++;
                break;
            }
        }
        for(int i=20;i<32;i++)
        {
            if(YorN[i]==0)
            {
                k++;
                break;
            }
        }
        if(k==2)
            jg=true;
        return jg;
    }
    public static int AllocatePCB()
    {
        int i = Random(20,31);
        while(YorN[i]!=0){
            i = Random(20,31);
        }
        YorN[i]=1;
        return i;
    }
    public static int AllocatePage()
    {
        int i = Random(8,20);
        while(YorN[i]!=0){
            i = Random(8,20);
        }
        YorN[i]=1;
        return i;
    }
    private static int Random(Integer min,Integer max){
        Random random = new  Random();
        int r= random.nextInt(max) % (max-min+1) + min;
        return r;
    }
}
