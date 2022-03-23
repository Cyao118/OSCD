package ui;

import hardware.CPU;
import hardware.EquipmentIn;
import hardware.InternalMemory;
import struct.PCB;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class Menu extends JMenuBar {//任务管理器上方菜单
    EquipmentIn ep;
    public Menu(FirstFrame ff)     {
        ep = ff.ep;
        add(FileMenu());    //添加“文件”菜单
        add(OptionMenu());
        //add(ShowMenu());
        setVisible(true);
    }

    private JMenu FileMenu()
    {
        JMenu menu=new JMenu("作业(J)");
        menu.setMnemonic(KeyEvent.VK_J);    //设置快速访问符
        JMenuItem Createitem=new JMenuItem("新建实时作业(N)",KeyEvent.VK_N);
        Createitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,ActionEvent.CTRL_MASK));
        Createitem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                ep.CreateOneNewJob();
            }
        });
        menu.add(Createitem);
        JMenuItem CreateFutureitem=new JMenuItem("新建未来作业(F)",KeyEvent.VK_F);
        CreateFutureitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,ActionEvent.CTRL_MASK));
        CreateFutureitem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                int i=0 ;
                JOptionPane j = new JOptionPane();
                String s =JOptionPane.showInputDialog(j,"请输入数量","新建未来作业数量",1);
                if(s!=null)
                i= Integer.valueOf(s);
                if(i>0) {
                    ep.CreateFutureJob(i);
                }
            }
        });
        menu.add(CreateFutureitem);
        JMenuItem Openitem=new JMenuItem("打开作业文件(O)",KeyEvent.VK_O);
        Openitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.CTRL_MASK));
        Openitem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String s ="未选择文件";
                JFileChooser fc=new JFileChooser("D:\\OS_ZXQ\\");
                int val=fc.showOpenDialog(null);    //文件打开对话框
                if(val==fc.APPROVE_OPTION)
                {
                    //正常选择文件
                    s = fc.getSelectedFile().toString();
                }
                else
                {
                    //未正常选择文件，如选择取消按钮
                    s ="未选择文件";
                }
                if(s!="未选择文件")
                {
                    ep.CreateJobFromFile(s);
                }

            }
        });
        menu.add(Openitem);

        /*JMenuItem Saveitem=new JMenuItem("保存(S)",KeyEvent.VK_S);
        Saveitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK));
        menu.add(Saveitem);*/

        menu.addSeparator();

        JMenuItem Exititem=new JMenuItem("退出任务管理器(E)",KeyEvent.VK_E);
        Exititem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,ActionEvent.CTRL_MASK));
        Exititem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menu.add(Exititem);
        return menu;
    }
    private JMenu OptionMenu()
    {
        JMenu menu=new JMenu("选项(O)");
        menu.setMnemonic(KeyEvent.VK_O);    //设置快速访问符
        JMenuItem Showitem=new JMenuItem("显示进程信息(P)",KeyEvent.VK_P);
        Showitem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,ActionEvent.CTRL_MASK));
        Showitem.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                int i=0 ;
                JOptionPane j = new JOptionPane();
                String s =JOptionPane.showInputDialog(j,"请输入进程号","查看进程",1);
                if(s!=null)
                    i= Integer.valueOf(s);
                int k = -1;
                for(int l=0;l<InternalMemory.PCBMenoryList.size();l++)
                {
                    if(InternalMemory.PCBMenoryList.get(l).ProId == i) k=l;
                }
                if(k!=-1) {
                    PCB p = InternalMemory.PCBMenoryList.get(k);
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
                    for(int r=0;r<p.BlockNum;r++){
                        tx.append(p.BlockList[r]+"  ");
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
                    JFrame frame = new JFrame(p.ProId+"进程");
                    frame.setSize(250, 400);
                    frame.setLocation(900,200);
                    frame.setVisible(true);
                    JScrollPane jsp = new JScrollPane(jp);
                    frame.add(jsp);

                }
                else
                {
                    JFrame o=new JFrame("错误");
                    o.setSize(250, 100);
                    o.setLocation(900,200);
                    JLabel jl =new JLabel("该进程不存在！");
                    o.add(jl);
                    o.setVisible(true);
                }
            }
        });
        menu.add(Showitem);
        return menu;
    }
    /*private JMenu ShowMenu()
    {
        JMenu menu=new JMenu("查看(V)");
        menu.setMnemonic(KeyEvent.VK_V);    //设置快速访问符
        JMenuItem item=new JMenuItem("按状态分组(G)",KeyEvent.VK_G);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,ActionEvent.CTRL_MASK));
        menu.add(item);
        item.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if(SetProcess.ShowMode==SetProcess.IdMode){
                    item.setText("按进程号分组(G)");
                    SetProcess.ShowMode=SetProcess.StateMode;
                }
                else if(SetProcess.ShowMode==SetProcess.StateMode){
                    item.setText("按状态分组(G)");
                    SetProcess.ShowMode=SetProcess.IdMode;
                }

            }
        });
        return menu;
    }*/
}