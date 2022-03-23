import hardware.*;
import os.*;
import struct.*;
import ui.FirstFrame;
import ui.TaskManager;

public class main {
    public static void main(String[] args) {
        //测试代码提交
        HardDisk Disk_0=new HardDisk();
        InternalMemory Im=new InternalMemory();
        //Disk_0.FirstLoading();
        //Im.FirstLoading();
        BitMap bitMap=new BitMap();
        bitMap.FirstSet();
        FirstFrame ff = new FirstFrame(Disk_0,Im);


    }
}
