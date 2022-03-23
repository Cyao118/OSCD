package hardware;
import struct.InstructManual;
import struct.Instruct;

import java.io.*;
import java.util.Random;

public class HardDisk {
    public static String rootpath="D:/OS_ZXQ/Disk_0";
    public int[][] Track;//Track[CylinderNum][BlockNum]表示一个扇区
    //CylinderNum表示磁道号，BlockNum表示扇区编号
    //Track[0][0-7]表示系统区，开机需载入内存的系统核心文件 4kb
    //Track[0][8-63]表示缓冲区28kb  Track[0][8-35]表示Spooling技术中的输入井  Track[0][36-63]表示Spooling技术中的输出井//当作业数量超过28时默认向后打印
    //Track[1-3][i]+Track[4][0-31]表示交换区 112KB
    // Track[5-31][0-63]/Track[4][32-63]表示用户区880KB
    public HardDisk(){
        Track=new int[32][64];
    }
    public void CreateMkdirs(){
        String path="D:/OS_ZXQ/Disk_0/Cylinder_";
        for(int i=0;i<32;i++) {
            String path1=path+i;
            File FilePath1=new File(path1);
            FilePath1.mkdirs();
            for(int j=0;j<64;j++){
                String  path2=path+i+"/Track_"+j+".txt";
                File FilePath2=new File(path2);
                FilePath2.delete();
                try {
                    FilePath2.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private int LoadRandom(Integer min,Integer max){
        Random random = new  Random();
        int r= random.nextInt(max) % (max-min+1) + min;
        return r;
    }
    public void WriteOneTrackInstruct(Instruct[] InstructList,int C,int B){
        String path="D:/OS_ZXQ/Disk_0"+"/"+"Cylinder_"+C+"/"+"Track_"+B+".txt";
        File f=new File(path);
        f.delete();
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileWriter fw=null;
        try {
            fw=new FileWriter(path,true);
            fw.write("\r\n");
            String s="InNum\tInCyl\tInBlock\tInTime\tInState\t";
            fw.write(s);
            for(int i=0;i<InstructList.length;i++){
                fw.write("\r\n");
                Instruct test=InstructList[i];
                String d=test.InNum+"\t"+test.InCylinder+"\t"+test.InBlock+"\t"+test.InTime+"\t"+test.InState;
                fw.write(d);
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Instruct[]  ReadOneTrackInstruct(int C,int B){
        String path="D:/OS_ZXQ/Disk_0"+"/"+"Cylinder_"+C+"/"+"Track_"+B+".txt";
        File f=new File(path);
        Instruct[] Instructlist=new Instruct[32];
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
            br.readLine();
            for (int i = 0; i < 32; i++) {
                Instructlist[i] = StoInstruct(br.readLine());
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Instructlist;
    }

    public static Instruct StoInstruct(String s)
    {
        Instruct i=new Instruct();
        String[] arrs = new String[5];
        arrs = s.split("\t");
        i.InNum = Integer.valueOf(arrs[0]);
        i.InCylinder= Integer.valueOf(arrs[1]);
        i.InBlock= Integer.valueOf(arrs[2]);
        i.InTime= Integer.valueOf(arrs[3]);
        i.InState= Integer.valueOf(arrs[4]);
        return i;
    }

    public void CreateOneTrackInstruct(int C,int B){
        int num=32;//盘块有多少指令
        Instructnums[C][B]=num;
        InstructCreating test=new InstructCreating(num);
        test.CreateID();
        Instruct[] InstructList=new Instruct[num];
        int[] t=test.getInstructList();
        for(int i=0;i<num;i++)
        {
            InstructList[i]=new Instruct();
            InstructList[i].setInNum(i+1);
            InstructList[i].setInCylinder(C);
            InstructList[i].setInBlock(B);
            InstructList[i].setInState(t[i]);
            InstructManual ic=new InstructManual();
            int tc=ic.GetStructtime(t[i]);
            InstructList[i].setInTime(tc);
        }
        WriteOneTrackInstruct(InstructList,C,B);
    }
    public void WriteInstructFile(){
        String path="D:/OS_ZXQ/Disk_0/Cylinder_0/Track_5.txt";
       File f=new File(path);
        f.delete();
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileWriter fw=null;
        try {
            fw=new FileWriter(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i=0;i<32;i++){
            for(int j=0;j<64;j++)
            {
                try {
                    String s1=String.valueOf(Instructnums[i][j])+"\r\n";
                    fw.write(s1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static int Instructnums[][]=new int[32][64];
    public void CreateInstructs(){
        for(int j=32;j<64;j++) {
            CreateOneTrackInstruct(4,j);
        }
        for(int i=5;i<32;i++) {
            for (int j = 0; j < 64; j++) {
                CreateOneTrackInstruct(i, j);
            }
        }
        WriteInstructFile();
    }
    public void FirstLoading(){
        CreateMkdirs();
        CreateInstructs();
    }
    //内部类，用于FirstLoading生成指令
    class InstructCreating {
        private int Instcuctnum;
        private int[] InstructList;
        private int A = 0;//进程在执行某条指令的时刻占用A类资源数
        private int B = 0;
        private int C = 0;
        private  int MaxA = PublicSource.MaxA;//A类资源最大数
        private  int MaxB = PublicSource.MaxB;
        private  int MaxC = PublicSource.MaxC;
        public InstructCreating (int num){
            Instcuctnum = num;
            InstructList = new int[num];
        }
        public int[] getInstructList() {
            return InstructList;
        }
        public void CreateID() {
            for(int i=0;i<Instcuctnum;i++) {
                InstructList[i] = Once(Instcuctnum - i);
            }
        }
        private int Once(int last){//last表示剩下多少未生成的指令
            int ID = Random(0,3);
            if(last == 1) ID=Random(2,3);
            if((A+B+C)== last) {
                int temp = Random(1, 3);
                if(temp == 1) {
                    if(A>0) {
                        ID = 5;
                        A--;
                    }
                    else {
                        ID = Once(last);
                    }
                }
                else if(temp == 2) {
                    if(B>0) {
                        ID = 7;
                        B--;
                    }
                    else {
                        ID = Once(last);
                    }
                }
                else {
                    if(C>0) {
                        ID = 9;
                        C--;
                    }
                    else {
                        ID = Once(last);
                    }
                }
            }
            else if((A+B+C)<last -1) {
                ID = Random(0, 10);
                if (ID == 4) {//PA
                    if (A < MaxA) {//A类资源可分
                        A++;
                    }
                    else {
                        ID = Once(last);
                    }
                }
                else if(ID == 6){
                    if (B < MaxB) {//B类资源可分
                        B++;
                    }
                    else {
                        ID = Once(last);
                    }
                }
                else if(ID==8){
                    if (C < MaxC) {//C类资源可分
                        C++;
                    }
                    else {
                        ID = Once(last);
                    }
                }
                else if(ID == 5) {
                    if(A>0){
                        A--;
                    }
                    else {
                        ID = Once(last);
                    }
                }
                else if(ID == 7) {
                    if(B>0){
                        B--;
                    }
                    else {
                        ID = Once(last);
                    }
                }
                else if(ID == 9) {
                    if(C>0){
                        C--;
                    }
                    else {
                        ID = Once(last);
                    }
                }
            }
            return ID;
        }

        private int Random(Integer min,Integer max){
            Random random = new  Random();
            int r= random.nextInt(max) % (max-min+1) + min;
            return r;
        }
    }

}
