package os;

import hardware.CPU;
import hardware.HardDisk;
import hardware.InternalMemory;
import struct.Instruct;
import struct.PCB;

import java.io.*;
import java.util.Random;

public class BitMap {
    //位示图，对虚存进行管理   当其值为“0”时，表示对应的盘块空闲；为“1”时，表示已经分配
    public static int Bitmap[][]=new int[256][3];//0-31内存 32-255交换区，共256(块）x512B（每块大小）=128KB（虚地址空间大小）= 2^16(=64K)(地址线16位）x2B（字长一个字2字节）
    public static String Mempath = "D:/OS_ZXQ/Inmem/";
    public static String Diskpath="D:/OS_ZXQ/Disk_0/Cylinder_";
    public void FirstSet(){//初始化
        for(int i=0;i<256;i++) {
            Bitmap[i][0] = 0;
            Bitmap[i][1] = -1;//对应磁盘磁道号
            Bitmap[i][2] = -1;//盘块号
        }

    }
    public static boolean Fullcheck()//判断内存是否满
    {
        boolean full = true;
        int t = 0;
        for(int i=0;i<32;i++)
        {
            if(Bitmap[i][0]==0)  t++;
            if(t==1) full = false;
        }
        return full;
    }

    public static int GetCylinderNum(int num)//虚存实际位置
    {
        if(num>=32)
        return (num - 32)/32+1;
        else
            return -1;
    }
    public static int GetBlockNum(int num)
    {
        if(num>=32)
        return (num - 32)%32;
        else
            return -1;
    }
    public static void Allocate(PCB p)
    {
        int i,j,k,occupy=0;
        for(i=0;i<p.BlockNum;i++)
        {
                if (occupy < 2)//至少两块装入内存
                {
                    j = Random(0, 31);
                    while (Bitmap[j][0] == 1) {
                        j = Random(0, 31);
                    }
                    Bitmap[j][0] = 1;
                    Bitmap[j][1] = p.CylinderNum;
                    Bitmap[j][2] = p.BlockList[i];
                    LoadinMemory(j);
                    InternalMemory.InstructList[j] = HardDisk.ReadOneTrackInstruct(p.CylinderNum,p.BlockList[i]);//读入用户区
                    p.page.page[i][2] = j+32;//写入页表
                    p.page.page[i][3] = 1;//在内存中
                    occupy++;

                }
                else//后面装入交换区
                {
                    j = Random(32, 255);
                    while (Bitmap[j][0] == 1) {
                        j = Random(32, 255);
                    }
                    Bitmap[j][0] = 1;
                    Bitmap[j][1] = p.CylinderNum;
                    Bitmap[j][2] = p.BlockList[i];
                    LoadinMemory(j);
                    p.page.page[i][2] = j+32;//写入页表
                    p.page.page[i][3] = 0;//在交换区中
                }
        }
        p.page.Occupy = 2;
    }
    private static int Random(Integer min,Integer max){
        Random random = new  Random();
        int r= random.nextInt(max) % (max-min+1) + min;
        return r;
    }

    public static void FreeMemory(int i)//清空其中一块
    {
        int j=i+32;
        String Targetpath=Mempath+"Track_"+j+".txt";
        File f=new File(Targetpath);
        f.delete();
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void LoadinMemory(int i)//i的值为位示图前一位0-256，通过读位示图将指定位置由磁盘装入虚存
    {
        String Sourcepath=Diskpath+Bitmap[i][1]+"/"+"Track_"+Bitmap[i][2]+".txt";
        String Targetpath;
        if(i<32)//调入内存
        {
            int j = i+32;
            Targetpath=Mempath+"Track_"+j+".txt";
        }
        else//调入虚存
        {
            int Cylindernum=(i-32)/64+1;
            int Blocknum=(i-32)%64;//换算虚存位置
            Targetpath=Diskpath+Cylindernum+"/"+"Track_"+Blocknum+".txt";
        }

        //System.out.println(Targetpath);
        copyFile(Sourcepath,Targetpath);
    }
    public static void Exchange(int OutNum,int InNum)//页面替换时将内存和交换区互换
    {
        int Cylindernum=OutNum/64+1;
        int Blocknum=OutNum%64;//换算虚存位置
        String OutFile = Diskpath+Cylindernum+"/"+"Track_"+Blocknum+".txt";

        int j = InNum + 32;
        String Infile = Mempath+"Track_"+j+".txt";

        String Temp = Mempath+"temp.txt";
        copyFile(OutFile,Temp);
        copyFile(Infile,OutFile);
        copyFile(Temp,Infile);

        File f=new File(Temp);
        f.delete();

    }

    public static void copyFile(String sourceFile,String targetFile)
    {
        File f=new File(targetFile);
        f.delete();
//	创建对象
        FileReader fr=null;
        FileWriter fw=null;
        try {
            fr=new FileReader(sourceFile);
            fw=new FileWriter(targetFile);
//		循环读和循环写
            int len=0;
            while((len=fr.read())!=-1)
            {
                fw.write((char)len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally
        {
            if(fr!=null)
            {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fw!=null)
            {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static int LRU(PCB p)//使用LRU算法将当前需要装入的某块装入内存
    {
        for(int i=0;i<p.PageLength;i++){
            p.page.page[p.NowBlockNum][4]++;
        }
        int OutNum = p.page.page[p.NowBlockNum][2]-32;
        if(OutNum>32)//表示在交换区中
        {
            for(int i = 0; i < 32; i++)//内存位置未满。直接装入内存
            {
                if (Bitmap[i][0] == 0) {
                    Bitmap[i][0] = 1;
                    Bitmap[i][1] = Bitmap[OutNum][1];
                    Bitmap[i][2] = Bitmap[OutNum][2];
                    LoadinMemory(i);
                    InternalMemory.InstructList[i] = HardDisk.ReadOneTrackInstruct(p.CylinderNum,p.BlockList[p.NowBlockNum]);//读入用户区

                    Bitmap[OutNum][0] = 0;
                    Bitmap[OutNum][1] = -1;//对应磁盘磁道号
                    Bitmap[OutNum][2] = -1;//盘块号

                    p.page.page[p.NowBlockNum][2] = i+32;
                    p.page.page[p.NowBlockNum][3] = 1;
                    p.page.page[p.NowBlockNum][4] = 0;

                    p.page.Occupy++;
                    return i;
                }
            }
            //内存位置已满，页面替换。
            int ex = 0;
            for(int i=0;i<p.PageLength;i++){
                if(p.page.page[i][3]==1){
                    ex = i;
                    break;//ex的初始值
                }
            }
            for(int i=0;i<p.PageLength;i++){
                if(p.page.page[i][3]==1){
                    if(p.page.page[ex][4]<p.page.page[i][4])ex = i;//找到最远访问的那个
                }
            }

            int InNum = p.page.page[ex][2]-32;
            Exchange(OutNum,InNum);
            InternalMemory.InstructList[InNum] = HardDisk.ReadOneTrackInstruct(p.CylinderNum,p.BlockList[p.NowBlockNum]);//读入用户区

            p.page.page[p.NowBlockNum][2] = InNum+32;
            p.page.page[p.NowBlockNum][3] = 1;
            p.page.page[p.NowBlockNum][4] = 0;

            p.page.page[ex][2] = OutNum+32;
            p.page.page[ex][3] = 0;
            p.page.page[ex][4] = 0;

            return InNum;

        }

        return 0;
    }

    public static int Isexist()//查询进程当前页面是否在内存中
    {
        PCB p=CPU.PCBrun;
        p.NowBlockNum = (CPU.CR3+CPU.PC)/256;//地址变换
        int exist;
        if (p.page.page[p.NowBlockNum][3] == 1)//查询页表在内存中
        {
            exist = 1;//在内存中
        }
        else//缺页中断
        {
            exist = 0;
        }
        return exist;
    }
}



