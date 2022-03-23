package struct;

import hardware.SystemClock;

public class Page {
    public int page[][];//[x][0]进程号[1]逻辑页号
    // [2]内存物理块号（32-63表示在内存中）/[3]中断位，1在内存，0不在内存/[4]上次被访问的时间,初始都是0到时候方便LRU算法算
    public int Area;//该页表所在内存物理块号
    public int Occupy;
    public Page(PCB p){
        Occupy = 0;
        Area = p.PageArea;
        page=new int[p.PageLength][5];
        for(int i=0;i<p.PageLength;i++)
        {
            page[i][0]=p.ProId;
            page[i][1]=i;
           // page[i][2]=-1;
            page[i][2]=0;
            page[i][3]=0;
            page[i][4]=0;
        }
    }
    public void setArea(int area) {
        Area = area;
    }
    public void setPage(int i,int area){
        page[i][3]=1;
        page[i][2]=area;
    }
    public void setTime(int i){
        page[i][4]= SystemClock.getTime();
    }
}
