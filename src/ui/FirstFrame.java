package ui;

import hardware.*;
import os.*;
import struct.InstructManual;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FirstFrame {
    public JFrame frame;
    public HardDisk Disk_0;
    public InternalMemory Im;
    public EquipmentIn ep;
    public SystemClock sc;
    public HighLevelScheduling hl;
    public MiddleScheduling ms;
    public LowScheduling ls ;
    public PageMissing pm;
    public FirstFrame ff = this;
    public TaskManager ts;
    public InstructManual im;
    public RunStruct rs;
    public InSpooling is;
    public BlockWaking bw;

    public FirstFrame(HardDisk d,InternalMemory i)
    {
        Disk_0 = d;
        Im = i;
        frame = new JFrame("ZXQ OS");
        frame.setSize(300, 200);
        frame.setLocation(600, 200);
        JPanel First = new JPanel();
        frame.add(First);
        SetFirst(First);
        frame.setVisible(true);
    }
    public void SetFirst(JPanel j) {
        j.setLayout(new BorderLayout());
        JPanel North = new JPanel();
        j.add(North, BorderLayout.NORTH);
        JLabel blank = new JLabel("\t");
        North.add(blank);
        JPanel South = new JPanel();
        j.add(South, BorderLayout.SOUTH);
        JPanel Center = new JPanel();
        j.add(Center, BorderLayout.CENTER);
        JPanel East = new JPanel();
        j.add(East, BorderLayout.EAST);
        JPanel West = new JPanel();
        j.add(West, BorderLayout.WEST);
        int lines = 3;//初始生成作业数，时钟周期时间（ms)，预设盘块数
        Center.setLayout(new GridLayout(lines, 1, 0, 0));
        East.setLayout(new GridLayout(lines, 1, 0, 0));
        West.setLayout(new GridLayout(lines, 1, 0, 0));

        JLabel jjobnum1 = new JLabel(" 初始生成作业数");
        JLabel jcirnum1 = new JLabel(" 时钟周期时间（ms)");
        JLabel jblnum1 = new JLabel(" 作业盘块数");
        West.add(jjobnum1);
        West.add(jcirnum1);
        West.add(jblnum1);


        JTextField tjobnum = new JTextField("10");
        JTextField tcirnum = new JTextField("1000");
        JTextField tblnum1 = new JTextField("3");
        JTextField tblnum2 = new JTextField("8");
        JLabel jl = new JLabel("   ——   ");
        JPanel pblnum = new JPanel();
        pblnum.setLayout(new GridLayout(1,3,0,0));
        pblnum.add(tblnum1);
        pblnum.add(jl);
        pblnum.add(tblnum2);
        Center.add(tjobnum);
        Center.add(tcirnum);
        Center.add(pblnum);

        JLabel jjobnum2 = new JLabel("√    ");
        JLabel jcirnum2 = new JLabel("√    ");
        JLabel jblnum2 = new JLabel("√    ");
        East.add(jjobnum2);
        East.add(jcirnum2);
        East.add(jblnum2);

        South.setLayout(new FlowLayout(FlowLayout.LEADING,30,10));
        JButton firstloading = new JButton("初始化磁盘");
        South.add(firstloading);
        JButton running = new JButton("开始运行");
        South.add(running);

        firstloading.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Disk_0.FirstLoading();
                Im.FirstLoading();
            }
        });
        running.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ep=new EquipmentIn();
                sc = new SystemClock();
                SystemClock.cir = Integer.valueOf(tcirnum.getText());
                EquipmentIn.minb = Integer.valueOf(tblnum1.getText());
                EquipmentIn.maxb = Integer.valueOf(tblnum2.getText());
                is = new InSpooling();
                hl=new HighLevelScheduling();
                ms = new MiddleScheduling();
                ls = new LowScheduling();
                pm = new PageMissing();
                bw = new BlockWaking();

                ts=new TaskManager(ff);
                im = new InstructManual();
                rs = new RunStruct();
                ep.CreateFutureJob(Integer.valueOf(tjobnum.getText()));
                frame.setVisible(false);
            }
        });
    }
}


