package ui;

import hardware.CPU;
import hardware.HardDisk;
import hardware.InternalMemory;
import hardware.SystemClock;
import struct.InstructManual;
import struct.PCB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SetPerformance {
    public static String CPUstr1="进程：";
    public static String CPUstr2="指令：";
    public static String CPUstr3="CPU已运行时间：";
    public static JLabel CPUs1,CPUs2,CPUs3;
    public static JButton[] mem=new JButton[64];
    public static JTextArea htxt;
    public static FileWriter fw=null;

    public static void CpuPerforset(JPanel CpuPerfor,JPanel MenoryPerfor,JPanel DiskPerfor,JPanel History){

        CpuPerfor.setLayout(new BorderLayout());
        CPUPic.Show(CpuPerfor);
        JPanel cpusouth=new JPanel();
        CpuPerfor.add(cpusouth,BorderLayout.SOUTH);
        cpusouth.setLayout(new GridLayout(3,2,0,0));
        JLabel jl = new JLabel(CPUstr1);
        cpusouth.add(jl);
        CPUs1 = new JLabel();
        cpusouth.add(CPUs1);
        JLabel j2 = new JLabel(CPUstr2);
        cpusouth.add(j2);
        CPUs2= new JLabel();
        cpusouth.add(CPUs2);
        JLabel j3 = new JLabel(CPUstr3);
        cpusouth.add(j3);
        CPUs3 = new JLabel();
        cpusouth.add(CPUs3);

        History.setLayout(new BorderLayout());
        htxt = new JTextArea();
        htxt.setEditable(false);
        JScrollPane jhsp = new JScrollPane(htxt);
        History.add(jhsp,BorderLayout.CENTER);


        String path="D:/OS_ZXQ/Process.txt";
        File f=new File(path);
        f.delete();
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fw=new FileWriter(path,true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MenoryPerfor.setLayout(new GridLayout(8,8,0,0));
        for(int i=0;i<64;i++)
        {
            mem[i]=new JButton("0");
            mem[i].setBackground(Color.WHITE);
            final int j = i;
            mem[i].addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                 {
                     JPanel jp = new JPanel();
                     JTextArea tx = new JTextArea();
                     tx.setEditable(false);
                     Font x = new Font("黑体",0,15);
                     tx.setFont(x);
                     jp.setLayout(new BorderLayout());
                     jp.add(tx,BorderLayout.CENTER);
                     if(j<8) tx.append("系统区");
                     else if(j<20) tx.append("页表区");
                     else if(j<32) tx.append("PCB区");
                     else tx.append("用户区");
                     tx.append("\r\n");
                     if(Integer.valueOf(mem[j].getText())==0)
                     {
                         tx.append("未占用");
                     }
                     else
                     {
                         if(j<8){
                            if(j==0)
                            {
                                tx.append("PC:\t"+CPU.PC+"\r\n");
                                tx.append("PSW:\t"+CPU.PSW+"\r\n");
                                tx.append("IR:\t"+CPU.IR+"\r\n");
                                tx.append("AR:\t"+CPU.AR+"\r\n");
                                tx.append("CR3:\t"+CPU.CR3+"\r\n");
                            }
                            else if(j==1)
                            {
                                double m=0;
                                for(int n=0;n<64;n++)
                                {
                                    if(Integer.valueOf(mem[n].getText())==1) m++;
                                }
                                tx.append("内存大小：\t32.0KB\r\n");
                                tx.append("内存已使用：\t"+m/2.0+"KB\r\n");
                                tx.append("磁盘大小：\t1024.0KB\r\n");
                                tx.append("磁盘位置：\t"+ HardDisk.rootpath+"\r\n");
                                tx.append("时钟周期：\t"+ SystemClock.cir + "ms\r\n");

                            }
                         }
                         else if(j<20)
                         {
                             for(int t=0;t<InternalMemory.PCBMenoryList.size();t++) {
                                 if (InternalMemory.PCBMenoryList.get(t).PageArea == j) {
                                     PCB p=InternalMemory.PCBMenoryList.get(t);
                                     for(int l=0;l<p.PageLength;l++){
                                         tx.append(l+"\t");
                                         if(p.page.page[l][3]==1)
                                             tx.append(p.page.page[l][2]+"");
                                         tx.append("\r\n");
                                     }
                                 }
                             }
                         }
                         else if(j<32)
                         {
                             for(int t=0;t<InternalMemory.PCBMenoryList.size();t++)
                             {
                                 if(InternalMemory.PCBMenoryList.get(t).PCBArea == j)
                                 {
                                     PCB p=InternalMemory.PCBMenoryList.get(t);
                                     tx.append("进程ID:\r\n"+p.ProId);
                                     tx.append("\r\n优先级:\r\n"+p.Priority);
                                     tx.append("\r\n进程状态:\r\n"+p.ProState);
                                     tx.append("\r\n指令数:\r\n"+p.InstructCount);
                                     tx.append("\r\n指令文件所在磁道号:\r\n"+p.CylinderNum);
                                     tx.append("\r\n指令文件占用盘块数量:\r\n"+p.BlockNum);
                                     tx.append("\r\n盘块号序列:\r\n");
                                     for(int k=0;k<p.BlockNum;k++){
                                         tx.append(p.BlockList[k]+"  ");
                                     }
                                     tx.append("\r\n第一条指令位置:\r\n"+p.InstructNum);
                                     if(p.ProState == 0)
                                         tx.append("\r\n当前执行到第几条指令:\r\n"+ CPU.PC/8);
                                     else
                                         tx.append("\r\n当前执行到第几条指令:\r\n"+p.NowInstruct);

                                     tx.append("\r\n进程创建时间:\r\n"+p.StarTime);
                                     tx.append("\r\n进程占用CPU时间:\r\n"+p.EndTime);
                                     tx.append("\r\nPCB所在内存块号:\r\n"+p.PCBArea);
                                     tx.append("\r\n页表所在内存块号:\r\n"+p.PageArea);
                                     tx.append("\r\n进程占用资源A:\r\n"+p.AllocateA);
                                     tx.append("\r\n进程占用资源B:\r\n"+p.AllocateB);
                                     tx.append("\r\n进程占用资源C:\r\n"+p.AllocateC);
                                     tx.append("\r\n进程还需资源A:\r\n"+p.NeedA);
                                     tx.append("\r\n进程还需资源B:\r\n"+p.NeedB);
                                     tx.append("\r\n进程还需资源C:\r\n"+p.NeedC);
                                 }
                             }
                         }
                         else
                         {
                             for(int t=0;t<32;t++){
                                 tx.append("("+t+")：\t"+ InstructManual.GetStructname(InternalMemory.InstructList[j-32][t].InState)+"\r\n");
                             }
                         }
                     }
                     JFrame frame = new JFrame("内存第"+j+"块");
                     frame.setSize(280, 400);
                     frame.setLocation(900,200);
                     frame.setVisible(true);
                     JScrollPane jsp = new JScrollPane(jp);
                     frame.add(jsp);
                 }
             });
            MenoryPerfor.add(mem[i]);
        }

        SetCPUPerfor scpf= new SetCPUPerfor();
    }
}
