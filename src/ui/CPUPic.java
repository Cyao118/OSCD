package ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;
import struct.Instruct;
import struct.InstructManual;

import javax.swing.*;
import java.awt.*;

public class CPUPic {

    public static XYSeries xyCPUseries = new XYSeries("CPU");

    public static int hundroud = 0;
    public static JFreeChart jfreechart = null;

    public JPanel getCPUJFreeChart(){

        jfreechart = ChartFactory.createXYLineChart(
                null, null, null, createDataset1(),
                PlotOrientation.VERTICAL, false, true, false);

        StandardChartTheme mChartTheme = new StandardChartTheme("CN");
        mChartTheme.setLargeFont(new Font("黑体", Font.BOLD, 20));
        mChartTheme.setExtraLargeFont(new Font("宋体", Font.PLAIN, 15));
        mChartTheme.setRegularFont(new Font("宋体", Font.PLAIN, 15));
        ChartFactory.setChartTheme(mChartTheme);

        jfreechart.setBorderPaint(new Color(0,204,205));
        jfreechart.setBorderVisible(true);

        XYPlot xyplot = (XYPlot) jfreechart.getPlot();

        // Y轴
        NumberAxis numberaxis = (NumberAxis) xyplot.getRangeAxis();
        numberaxis.setLowerBound(0);
        numberaxis.setUpperBound(InstructManual.TypeNum);
        numberaxis.setTickUnit(new NumberTickUnit(InstructManual.TypeNum));
        // 只显示整数值
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // numberaxis.setAutoRangeIncludesZero(true);
        numberaxis.setLowerMargin(0); // 数据轴下（左）边距 ­
        numberaxis.setMinorTickMarksVisible(true);// 标记线是否显示
        numberaxis.setTickMarkInsideLength(0);// 外刻度线向内长度
        numberaxis.setTickMarkOutsideLength(0);

        // X轴的设计
        NumberAxis x = (NumberAxis) xyplot.getDomainAxis();
        x.setAutoRange(true);// 自动设置数据轴数据范围
        // 自己设置横坐标的值
        x.setAutoTickUnitSelection(false);
        x.setTickUnit(new NumberTickUnit(60d));
        // 设置最大的显示值和最小的显示值
        x.setLowerBound(0);
        x.setUpperBound(60);
        // 数据轴的数据标签：只显示整数标签
        x.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        x.setAxisLineVisible(true);// X轴竖线是否显示
        x.setTickMarksVisible(true);// 标记线是否显示

        RectangleInsets offset = new RectangleInsets(0, 0, 0, 0);
        xyplot.setAxisOffset(offset);// 坐标轴到数据区的间距
        xyplot.setBackgroundAlpha(0.0f);// 去掉柱状图的背景色
        xyplot.setOutlinePaint(null);// 去掉边框

        // ChartPanel chartPanel = new ChartPanel(jfreechart);
        // chartPanel.restoreAutoDomainBounds();//重置X轴

        ChartPanel chartPanel = new ChartPanel(jfreechart, true);

        return chartPanel;
    }

    /**
     * 该方法是数据的设计
     *
     * @return
     */
    public static XYDataset createDataset1() {
        XYSeriesCollection xyseriescollection = new XYSeriesCollection();
        xyseriescollection.addSeries(xyCPUseries);
        return xyseriescollection;
    }

    /**
     * 随机生成的数据
     */


    public static void Show(JPanel p) {
        CPUPic jz = new CPUPic();
        p.add(jz.getCPUJFreeChart(), BorderLayout.CENTER);
        p.setVisible(true);

      //  dynamicRun();
    }
}