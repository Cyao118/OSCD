package ui;

import hardware.*;
import os.*;
import struct.Instruct;
import struct.InstructManual;
import struct.PCB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;

public class SetProcess extends Thread{//初始化进程界面
    private JPanel Prolist;
    private JScrollPane ProlistScor;
    private JScrollPane ProCenterScor;
    private JPanel ProcessNorth = new JPanel();
    private JPanel ProcessCenter= new JPanel();
    private TaskManager ts;
    public static int IdMode = 0;
    public static int StateMode = 1;
    public static int MemoryMode = 2;
    public static int DiskMode = 3;
    public static int CPUMode = 4;

    public static int ShowMode = IdMode;

    public static volatile boolean stop = false;
    public SetProcess(TaskManager ts)
    {
        this.ts=ts;
        Prolist = new JPanel();
        ProcessNorth = new JPanel();
        ProcessNorthset();
        ProcessCenter= new JPanel();
        ProcessCenter.setLayout(new BorderLayout());
        ProcessCenterset();
        Prolist.setLayout(new BorderLayout());
        Prolist.add(ProcessCenter,BorderLayout.CENTER);
        Prolist.add(ProcessNorth,BorderLayout.NORTH);
        Prolist.setVisible(true);
        ProlistScor =new JScrollPane(Prolist);
        ts.Process.add(ProlistScor,BorderLayout.CENTER);
    }
    public void run()
    {
        while(true) {

            try {
                if(!stop)
                {
                    this.SetOnce();
                    sleep(SystemClock.cir);
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public  void StopMe()
    {
        stop = true;
    }

    public  void ReStartMe()
    {
       stop = false;
    }
    public void SetOnce(){
        ProcessCenter.removeAll();
        ProcessCenterset();

    }

    public  void ProcessNorthset()//上方标题栏
    {
        ProcessNorth=new JPanel();
        ProcessNorth.setLayout(new GridLayout(1,2,0,0));

        JPanel ProcessNorth1=new JPanel();
        ProcessNorth1.setLayout(new BorderLayout());

        JButton PStop=new JButton("暂停");
        PStop.setBackground(Color.WHITE);
        ProcessNorth1.add(PStop,BorderLayout.WEST);
        PStop.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
             if (!stop) {
                 StopMe();
                 SystemClock.StopMe();
                 HighLevelScheduling.StopMe();
                 MiddleScheduling.StopMe();
                 LowScheduling.StopMe();
                 PageMissing.StopMe();
                 BlockWaking.StopMe();
                 SetCPUPerfor.StopMe();
                 PStop.setText("恢复");
             }
             else
             {
                 ReStartMe();
                 SystemClock.ReStartMe();
                 HighLevelScheduling.ReStartMe();
                 MiddleScheduling.ReStartMe();
                 LowScheduling.ReStartMe();
                 PageMissing.ReStartMe();
                 BlockWaking.ReStartMe();
                 SetCPUPerfor.ReStartMe();
                 PStop.setText("暂停");
             }


            }
        });
        JButton PName=new JButton("名称");
        PName.setBackground(Color.WHITE);
        ProcessNorth1.add(PName,BorderLayout.CENTER);
        PName.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                ShowMode = IdMode;
            }
        });

        JButton PState=new JButton("进程状态");
        PState.setBackground(Color.WHITE);
        ProcessNorth1.add(PState,BorderLayout.EAST);
        PState.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                ShowMode = StateMode;
            }
        });

        ProcessNorth.add(ProcessNorth1);

        JPanel ProcessNorth2=new JPanel();
        ProcessNorth2.setLayout(new GridLayout(1,3,0,0));

        JButton PCpu=new JButton("CPU");
        PCpu.setBackground(Color.WHITE);
        ProcessNorth2.add(PCpu);
        PCpu.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                ShowMode =CPUMode;
            }
        });

        JButton PMenory=new JButton("内存");
        PMenory.setBackground(Color.WHITE);
        ProcessNorth2.add(PMenory);
        PMenory.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                ShowMode =MemoryMode;
            }
        });

        JButton PDisk=new JButton("磁盘");
        PDisk.setBackground(Color.WHITE);
        ProcessNorth2.add(PDisk);
        PDisk.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                ShowMode =DiskMode;
            }
        });

        ProcessNorth.add(ProcessNorth2);
        


    }
    public  void ProcessCenterset()//中间信息栏
    {
        JPanel ProCenter=new JPanel();
        ProCenter.setLayout(new GridLayout(InternalMemory.PCBMenoryList.size(),1,0,0));
        if(ShowMode == IdMode) {
            Collections.sort(InternalMemory.PCBMenoryList, new Comparator<PCB>() {
                @Override
                public int compare(PCB o1, PCB o2) {
                    if(o1.ProId >= o2.ProId) {
                        return 1;
                    }
                    else {
                        return -1;
                    }
                }
            });
            for (int i = 0; i < InternalMemory.PCBMenoryList.size(); i++) {
                OneProcess o = new OneProcess(InternalMemory.PCBMenoryList.get(i), ts);
                ProCenter.add(o.OnePanel);
                // System.out.println(i);
            }
        }
        else if(ShowMode == StateMode)
        {
            Collections.sort(InternalMemory.PCBMenoryList, new Comparator<PCB>() {
                @Override
                public int compare(PCB o1, PCB o2) {
                    if(o1.ProState >= o2.ProState) {
                        return 1;
                    }
                    else {
                        return -1;
                    }
                }
            });
            for (int i = 0; i < InternalMemory.PCBMenoryList.size(); i++) {
                OneProcess o = new OneProcess(InternalMemory.PCBMenoryList.get(i), ts);
                ProCenter.add(o.OnePanel);
                // System.out.println(i);
            }

        }
        else if(ShowMode == MemoryMode)
        {
            Collections.sort(InternalMemory.PCBMenoryList, new Comparator<PCB>() {
                @Override
                public int compare(PCB o1, PCB o2) {
                    if(o1.page.Occupy >= o2.page.Occupy) {
                        return 1;
                    }
                    else {
                        return -1;
                    }
                }
            });
            for (int i = 0; i < InternalMemory.PCBMenoryList.size(); i++) {
                OneProcess o = new OneProcess(InternalMemory.PCBMenoryList.get(i), ts);
                ProCenter.add(o.OnePanel);
                // System.out.println(i);
            }
        }
        else if(ShowMode == DiskMode)
        {
            Collections.sort(InternalMemory.PCBMenoryList, new Comparator<PCB>() {
                @Override
                public int compare(PCB o1, PCB o2) {
                    if(o1.BlockNum >= o2.BlockNum) {
                        return 1;
                    }
                    else {
                        return -1;
                    }
                }
            });
            for (int i = 0; i < InternalMemory.PCBMenoryList.size(); i++) {
                OneProcess o = new OneProcess(InternalMemory.PCBMenoryList.get(i), ts);
                ProCenter.add(o.OnePanel);
                // System.out.println(i);
            }
        }
        else if(ShowMode == CPUMode)
        {
            Collections.sort(InternalMemory.PCBMenoryList, new Comparator<PCB>() {
                @Override
                public int compare(PCB o1, PCB o2) {
                    if(o1.RunTime >= o2.RunTime) {
                        return 1;
                    }
                    else {
                        return -1;
                    }
                }
            });
            for (int i = 0; i < InternalMemory.PCBMenoryList.size(); i++) {
                OneProcess o = new OneProcess(InternalMemory.PCBMenoryList.get(i), ts);
                ProCenter.add(o.OnePanel);
                // System.out.println(i);
            }
        }
        ProCenterScor = new JScrollPane(ProCenter);
        ProCenterScor.setVisible(true);
        ProcessCenter.add(ProCenterScor,BorderLayout.CENTER);
        ProcessCenter.setVisible(true);
        ProcessCenter.updateUI();

    }
    static class OneProcess//每个进程显示和操作的行
    {
        private PCB Onepro;//后期将PCB池里的每个PCB传进来每个都生成自己的按钮
        public JPanel OnePanel = new JPanel();
        private JButton ProName;
        private JButton ProState;
        private JButton ProCpu;
        private JButton ProMenory;
        private JButton ProDisk;
        private JPopupMenu ProPopu;
        private JMenuItem ProShowAll;//显示全部信息
        private JMenuItem InstructAll;//显示指令序列
        private JMenuItem PageAll;//显示页表
        private JMenuItem ProShutdown;//关闭进程
        private TaskManager ts;
        public OneProcess(PCB p,TaskManager ts)
        {
            this.ts = ts;
            Onepro = p;
            ProName = new JButton(Integer.toString(p.ProId));
            ProState = new JButton(p.PrintState());
            ProCpu = new JButton(Integer.toString(p.RunTime));
            int k=0;
            for(int i=0;i<p.PageLength;i++)
            {
                if(p.page.page[i][3] == 1) k++;
            }
            ProMenory = new JButton(Integer.toString(k));
            ProDisk = new JButton(Integer.toString(p.BlockNum));
            OnePanel.setLayout(new GridLayout(1,2,0,0));
            JPanel OnePanel1=new JPanel();
            OnePanel1.setLayout(new BorderLayout());
            ProName.setBackground(Color.WHITE);
            OnePanel1.add(ProName,BorderLayout.CENTER);
            ProState.setBackground(Color.WHITE);
            OnePanel1.add(ProState,BorderLayout.EAST);
            OnePanel.add(OnePanel1);
            JPanel OnePanel2=new JPanel();
            OnePanel2.setLayout(new GridLayout(1,3,0,0));
            ProCpu.setBackground(Color.WHITE);
            OnePanel2.add(ProCpu);
            ProMenory.setBackground(Color.WHITE);
            OnePanel2.add(ProMenory);
            ProDisk.setBackground(Color.WHITE);
            OnePanel2.add(ProDisk);
            OnePanel.add(OnePanel2);

            ProPopu = new JPopupMenu();
            ProShowAll=new JMenuItem("详细信息");
            InstructAll=new JMenuItem("指令序列");
            PageAll=new JMenuItem("页表");
            ProShutdown=new JMenuItem("结束任务");
            ProPopu.add(ProShowAll);
            ProPopu.add(InstructAll);
            ProPopu.add(PageAll);
            ProPopu.add(ProShutdown);

            ProName.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    ProPopu.show(ProName,ProName.getX()+ProName.getWidth()-50,ProName.getY());
                }
            });

            ProShowAll.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    PCB p=Onepro;
                    JPanel jp = new JPanel();
                    JTextArea tx = new JTextArea();
                    tx.setEditable(false);
                    Font x = new Font("黑体",0,15);
                    tx.setFont(x);
                    jp.setLayout(new BorderLayout());
                    jp.add(tx,BorderLayout.CENTER);
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
                    tx.append("\r\n进程占用CPU时间:\r\n"+p.RunTime);
                    tx.append("\r\nPCB所在内存块号:\r\n"+p.PCBArea);
                    tx.append("\r\n页表所在内存块号:\r\n"+p.PageArea);
                    tx.append("\r\n进程占用资源A:\r\n"+p.AllocateA);
                    tx.append("\r\n进程占用资源B:\r\n"+p.AllocateB);
                    tx.append("\r\n进程占用资源C:\r\n"+p.AllocateC);
                    tx.append("\r\n进程还需资源A:\r\n"+p.NeedA);
                    tx.append("\r\n进程还需资源B:\r\n"+p.NeedB);
                    tx.append("\r\n进程还需资源C:\r\n"+p.NeedC);
                    JFrame frame = new JFrame(Onepro.ProId+"进程");
                    frame.setSize(250, 400);
                    frame.setLocation(900,200);
                    frame.setVisible(true);
                    JScrollPane jsp = new JScrollPane(jp);
                    frame.add(jsp);
                }
            });

            InstructAll.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    PCB p=Onepro;
                    JPanel jp = new JPanel();
                    JTextArea tx = new JTextArea();
                    tx.setEditable(false);
                    Font x = new Font("黑体",0,15);
                    tx.setFont(x);
                    jp.setLayout(new BorderLayout());
                    jp.add(tx,BorderLayout.CENTER);
                    tx.append("共"+p.InstructCount+"条指令\r\n");
                    int k=1;
                    for(int i=0;i<p.BlockNum;i++){
                        Instruct[] is = HardDisk.ReadOneTrackInstruct(p.CylinderNum,p.BlockList[i]);
                        int j;
                        if(i==0) j=p.InstructNum;
                        else j=0;
                        for(;j<is.length;j++)
                        {
                            tx.append("("+k+")："+Integer.toHexString((k-1)*8)+"H  \t"+ InstructManual.GetStructname(is[j].InState)+"\t执行时间："+InstructManual.GetStructtime(is[j].InState)+"\r\n");
                            k++;
                        }
                    }
                    JFrame frame = new JFrame(Onepro.ProId+"进程");
                    frame.setSize(350, 400);
                    frame.setLocation(900,200);
                    frame.setVisible(true);
                    JScrollPane jsp = new JScrollPane(jp);
                    frame.add(jsp);
                }
            });
            PageAll.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    PCB p=Onepro;
                    JPanel jp = new JPanel();
                    JTextArea tx = new JTextArea();
                    tx.setEditable(false);
                    Font x = new Font("黑体",0,15);
                    tx.setFont(x);
                    jp.setLayout(new BorderLayout());
                    jp.add(tx,BorderLayout.CENTER);
                    for(int i=0;i<p.PageLength;i++){
                        tx.append(i+"\t");
                        if(p.page.page[i][3]==1)
                            tx.append(p.page.page[i][2]+"");
                        tx.append("\r\n");
                    }
                    JFrame frame = new JFrame(Onepro.ProId+"进程");
                    frame.setSize(350, 400);
                    frame.setLocation(900,200);
                    frame.setVisible(true);
                    JScrollPane jsp = new JScrollPane(jp);
                    frame.add(jsp);
                }
            });
            ProShutdown.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    if(Onepro.ProState == 0)//撤销的是一个运行中的进程
                    {
                        CPU.PSW = 1;//传递中断信号
                        RunStruct.Runable = 0;
                        LowScheduling.Processshutdown = 1;
                    }
                    else Primitive.RemovePCB(Onepro);//撤销进程
                    System.out.println("进程"+Onepro.ProId+"被撤销");
                    System.out.println( Scheduling.PCBReady.size());
                }
            });
        }
    }
}
