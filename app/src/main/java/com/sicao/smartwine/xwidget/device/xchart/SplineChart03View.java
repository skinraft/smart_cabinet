package com.sicao.smartwine.xwidget.device.xchart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.xclcharts.chart.CustomLineData;
import org.xclcharts.chart.PointD;
import org.xclcharts.chart.SplineChart;
import org.xclcharts.chart.SplineData;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.event.click.PointPosition;
import org.xclcharts.renderer.XEnum;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.sicao.smartwine.SmartCabinetApplication;

public class SplineChart03View extends DemoView {
    private String TAG = "SplineChart03View";
    private SplineChart chart = new SplineChart();
    //分类轴标签集合
    private LinkedList<String> labels = new LinkedList<String>();
    private LinkedList<SplineData> chartData = new LinkedList<SplineData>();

    private Paint mPaintTooltips = new Paint(Paint.ANTI_ALIAS_FLAG);

    // splinechart支持横向和竖向定制线
    private List<CustomLineData> mXCustomLineDataset = new ArrayList<CustomLineData>();
    private List<CustomLineData> mYCustomLineDataset = new ArrayList<CustomLineData>();

    public SplineChart03View(Context context) {
        super(context);
        initView();
    }

    public SplineChart03View(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SplineChart03View(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        chartLabels();
        chartDataSet();
        chartRender();
        //綁定手势滑动事件
        this.bindTouch(this, chart);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //图所占范围大小
        chart.setChartRange(w, h);
    }
    private void chartRender() {
        try {
            //设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
            int[] ltrb = getBarLnDefaultSpadding();
            chart.setPadding(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);
            //数据源
            chart.setCategories(labels);
            chart.setDataSource(chartData);
            //坐标系
            //数据轴最大值
            chart.getDataAxis().setAxisMax(200);
            chart.getDataAxis().setAxisMin(0);
            //数据轴刻度间隔
            chart.getDataAxis().setAxisSteps(30);
            chart.getDataAxis().setAxisLineStyle(XEnum.AxisLineStyle.CAP);
            chart.setCustomLines(mYCustomLineDataset); //y轴
            //标签轴最大值
            chart.setCategoryAxisMax(31);
            //标签轴最小值
            chart.setCategoryAxisMin(0);
            //chart.setCustomLines(mXCustomLineDataset); //y轴
            chart.setCategoryAxisCustomLines(mXCustomLineDataset); //x轴
            //设置图的背景色
            chart.setApplyBackgroundColor(true);
            chart.setBackgroundColor(Color.parseColor("#FFFFFF"));
            //调轴线与网络线风格
            chart.getCategoryAxis().hideTickMarks();
            chart.getDataAxis().hideAxisLine();
            chart.getDataAxis().hideTickMarks();//刻度线
            chart.getPlotGrid().showHorizontalLines();
            //横线为虚线
            chart.getPlotGrid().setHorizontalLineStyle(XEnum.LineStyle.DASH);
            chart.getPlotGrid().getHorizontalLinePaint().setColor(Color.rgb(229, 229, 229));
            chart.getCategoryAxis().getAxisPaint().setColor(
                    chart.getPlotGrid().getHorizontalLinePaint().getColor());
            chart.getCategoryAxis().getAxisPaint().setStrokeWidth(0);
            //定义交叉点标签显示格式,特别备注,因曲线图的特殊性，所以返回格式为:  x值,y值
            //请自行分析定制
            chart.setDotLabelFormatter(new IFormatterTextCallBack() {
                @Override
                public String textFormatter(String value) {
                    String label = "[" + value + "]";
                    return (label);
                }
            });
            //激活点击监听
            chart.ActiveListenItemClick();
            //为了让触发更灵敏，可以扩大5px的点击监听范围
            chart.extPointClickRange(5);
            chart.showClikedFocus();
            //显示平滑曲线
            chart.setCrurveLineStyle(XEnum.CrurveLineStyle.BEELINE);
            chart.getCategoryAxis().getAxisPaint().setColor(Color.parseColor("#ad1c79"));
            //图例显示在正下方
            chart.getPlotLegend().setVerticalAlign(XEnum.VerticalAlign.BOTTOM);
            chart.getPlotLegend().setHorizontalAlign(XEnum.HorizontalAlign.CENTER);
            //仅能横向移动
            chart.setPlotPanMode(XEnum.PanMode.HORIZONTAL);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }

    private void chartDataSet() {
        //线1的数据集
        List<PointD> linePoint1 = new ArrayList<PointD>();
        linePoint1.add(new PointD(1d, 170d));
        linePoint1.add(new PointD(3d, 150d));
        linePoint1.add(new PointD(5d, 120d));
        linePoint1.add(new PointD(10d, 180d));
        linePoint1.add(new PointD(15d, 180d));
        linePoint1.add(new PointD(17d, 160d));
        linePoint1.add(new PointD(22d, 0d));
        linePoint1.add(new PointD(25d, 50d));
        linePoint1.add(new PointD(26d, 60d));
        linePoint1.add(new PointD(27d, 30d));
        linePoint1.add(new PointD(29d, 68d));
        SplineData dataSeries1 = new SplineData("", linePoint1,
                Color.parseColor("#ad1c79"));
        //把线弄细点
        dataSeries1.getLinePaint().setStrokeWidth(2);
        //设定数据源
        chartData.add(dataSeries1);
    }

    private void chartLabels() {
        labels.add("1");
        labels.add("2");
        labels.add("3");
        labels.add("4");
        labels.add("5");
        labels.add("6");
        labels.add("7");
        labels.add("8");
        labels.add("9");
        labels.add("10");
        labels.add("11");
        labels.add("12");
        labels.add("13");
        labels.add("14");
        labels.add("15");
        labels.add("16");
        labels.add("17");
        labels.add("18");
        labels.add("19");
        labels.add("20");
        labels.add("21");
        labels.add("22");
        labels.add("23");
        labels.add("24");
        labels.add("25");
        labels.add("26");
        labels.add("27");
        labels.add("28");
        labels.add("29");
        labels.add("30");
    }

    @Override
    public void render(Canvas canvas) {
        try {
            chart.render(canvas);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            triggerClick(event.getX(), event.getY());
        }
        return true;
    }

    //触发监听
    private void triggerClick(float x, float y) {
        if (!chart.getListenItemClickStatus()) return;
        PointPosition record = chart.getPositionRecord(x, y);
        if (null == record) return;
        if (record.getDataID() >= chartData.size()) return;
        SplineData lData = chartData.get(record.getDataID());
        List<PointD> linePoint = lData.getLineDataSet();
        int pos = record.getDataChildID();
        int i = 0;
        Iterator it = linePoint.iterator();
        while (it.hasNext()) {
            PointD entry = (PointD) it.next();
            if (pos == i) {
                Double xValue = entry.x;
                Double yValue = entry.y;
                float r = record.getRadius();
                chart.showFocusPointF(record.getPosition(), r + r * 0.8f);
                chart.getFocusPaint().setStyle(Style.FILL);
                chart.getFocusPaint().setStrokeWidth(3);
                if (record.getDataID() >= 2) {
                    chart.getFocusPaint().setColor(Color.BLUE);
                } else {
                    chart.getFocusPaint().setColor(Color.RED);
                }
                //在点击处显示tooltip
                mPaintTooltips.setColor(Color.RED);
                chart.getToolTip().setCurrentXY(x, y);
                chart.getToolTip().addToolTip(Double.toString(xValue) + "号", mPaintTooltips);
                chart.getToolTip().addToolTip(
                        " 售出:" + Double.toString(yValue) + "瓶酒", mPaintTooltips);
                if (null != xDataChatInterface) {
                    xDataChatInterface.click(Integer.parseInt(Double.toString(xValue)), Integer.parseInt(Double.toString(yValue)));
                }
                chart.getToolTip().getBackgroundPaint().setAlpha(100);
                this.invalidate();
                break;
            }
            i++;
        }
    }

    private XDataChatInterface xDataChatInterface;

    public void setxDataChatInterface(XDataChatInterface xDataChatInterface) {
        this.xDataChatInterface = xDataChatInterface;
    }

    public interface XDataChatInterface {
        void click(int date, int number);
    }
}
