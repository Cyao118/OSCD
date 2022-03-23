package ui;

import hardware.EquipmentIn;
import hardware.InternalMemory;
import struct.PCB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TaskManager {//任务管理器主界面

    private JFrame frame;
    private JPanel All;
    private JTabbedPane Center;
    public JPanel Process;
    private JPanel Perforence;
    private JPanel History;

    public static JTextArea htxt;

    private JTabbedPane Perforencelist;
    private JPanel CPUperfor;
    private JPanel MemoryPerfor;
    private JPanel DiskPerfor;
    private Menu m;

    public SetProcess Stpro;


    public TaskManager(FirstFrame ff) {
        m=new Menu(ff);
        frame = new JFrame("任务管理器");
        frame.setJMenuBar(m);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(this.All);
        frame.pack();
        frame.setSize(500, 350);
        frame.setLocation(400,200);
        frame.setVisible(true);


        Stpro =new SetProcess(this);
        Stpro.start();
        SetPerformance.CpuPerforset(CPUperfor,MemoryPerfor,DiskPerfor,History);


    }
    public static void main(String[] args)
    {

    }
}
