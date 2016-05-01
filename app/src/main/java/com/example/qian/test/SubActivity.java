package com.example.qian.test;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
//import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.zhaoxiaodan.miband.MiBand;
import com.zhaoxiaodan.miband.listeners.HeartRateNotifyListener;
import com.zhaoxiaodan.miband.listeners.NotifyListener;
import com.zhaoxiaodan.miband.model.UserInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import DB.MyDBOpenHelper;


public class SubActivity extends ActionBarActivity {
    private static final int MIBAND_DETECT = 1;
    private static final int HEART_RESULT = 2;
    private SQLiteDatabase db;
    private MyDBOpenHelper myDBHelper;
    private StringBuilder stringBuilder;
    private String daytime;
    private LineChart chartView;
    private LineChart newchartView;
    private String[] mDate_string;
    private String band_addr;
    private MiBand miBand;
    private LineSet dataset;
    private Button setInfobtn;
    private Button setListenerbtn;

    private ArrayList<String> mDate;
    private ArrayList<Float> mHeartBeat;
    private float[] mHeartBeat_float;
    private FloatingActionButton heartBeatTest;
    private int heartbeat_result;
    private int initialState ;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        initialState = 0;
        mDate=new ArrayList<String>();
        mHeartBeat=new ArrayList<Float>();
//        setInfobtn = (Button) findViewById(R.id.setInfo);
//        setListenerbtn = (Button) findViewById(R.id.setListenter);

        MyApplication myApp = (MyApplication)getApplicationContext();
        band_addr=myApp.getBand_addr();
        miBand=myApp.getMiBand();


        myDBHelper = new MyDBOpenHelper(this, "my.db", null, 1);
        db = myDBHelper.getWritableDatabase();
//        for(int i=0;i<4;i++){
//            addData(band_addr,(int)values[i]);
//        }

        getData(band_addr);
        Toolbar toolbar= (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        chartView= (LineChart) findViewById(R.id.linechart);
        heartBeatTest= (FloatingActionButton) findViewById(R.id.beatbtn);
        setbtn();

        toFloat();


        initNewChart(chartView);

        setNewChart(chartView);
    }

    private void setbtn() {

//        setInfobtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                UserInfo userInfo = new UserInfo(20111111, 1, 32, 180, 55, "胖梁", 0);
//               miBand.setUserInfo(userInfo);
//            }
//        });
//
//        setListenerbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                miBand.setHeartRateScanListener(new HeartRateNotifyListener()
//                {
//                    @Override
//                    public void onNotify(int heartRate)
//                    {
//                        //Toast.makeText(SubActivity.this,heartRate,Toast.LENGTH_SHORT).show();
//                        heartbeat_result = heartRate;
//                        addEntry();
//                    }
//                });
//            }
//        });
        heartBeatTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(initialState==0){
                    UserInfo userInfo = new UserInfo(20111111, 1, 32, 180, 55, "胖梁", 0);
                    miBand.setUserInfo(userInfo);
                    alert = null;
                    builder = new AlertDialog.Builder(SubActivity.this);
                    alert = builder.setTitle("提示：")
                            .setMessage("开启后台定期心跳检测？")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Toast.makeText(mContext, "你点击了取消按钮~", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    miBand.setHeartRateScanListener(new HeartRateNotifyListener()
                                    {
                                        @Override
                                        public void onNotify(int heartRate)
                                        {
                                            //Toast.makeText(SubActivity.this,heartRate,Toast.LENGTH_SHORT).show();
                                            heartbeat_result = heartRate;
                                            addEntry();
                                        }
                                    });
                                    Toast.makeText(SubActivity.this, "设置成功，可开始检测", Toast.LENGTH_SHORT).show();
                                    initialState= 1;
                                }
                            }).create();             //创建AlertDialog对象
                    alert.show();                    //显示对话框
                }
                else if (initialState==1){
                    Intent i = new Intent(SubActivity.this,HeartBeatService.class);
                    Log.d("BackService","dd");
                    startService(i);

                    Toast.makeText(SubActivity.this, "正在检测心率...", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    class MyThread extends Thread {
        private Handler handler;

        public void run() {

            Message msg = new Message();
            msg.what = MIBAND_DETECT;
            mHandler.sendEmptyMessage(msg.what);
            //执行数据操作，不涉及到UI
            Looper.prepare();
            handler = new Handler() {
                @Override
                public void handleMessage(android.os.Message msg) {
                    if (msg.what == HEART_RESULT) {
                        Toast.makeText(SubActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    }
                }
            };
            Looper.loop();
        }
    }
    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MIBAND_DETECT:


                    //heartbeat_result = 87;
                    //addData(band_addr,heartbeat_result);  //心跳调用接口获得

                    miBand.startHeartRateScan();

                break;
            }
            return false;
        }
    });

    private void setNewChart(LineChart mChart) {

        LineData data = new LineData();

        // 数据显示的颜色
        data.setValueTextColor(Color.WHITE);

        // 先增加一个空的数据，随后往里面动态添加
        mChart.setData(data);

        // 图表的注解(只有当数据集存在时候才生效)
        Legend l = mChart.getLegend();

        // 可以修改图表注解部分的位置
        // l.setPosition(LegendPosition.LEFT_OF_CHART);

        // 线性，也可是圆
        l.setForm(Legend.LegendForm.LINE);

        // 颜色
        l.setTextColor(Color.WHITE);

        // x坐标轴
        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);

        // 几个x坐标轴之间才绘制？
        xl.setSpaceBetweenLabels(5);

        // 如果false，那么x坐标轴将不可见
        xl.setEnabled(true);

        // 将X坐标轴放置在底部，默认是在顶部。
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);

        // 图表左边的y坐标轴线
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);

        // 最大值
        leftAxis.setAxisMaxValue(150);

        // 最小值
        leftAxis.setAxisMinValue(50);

        // 不一定要从0开始
        leftAxis.setStartAtZero(false);

        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        // 不显示图表的右边y坐标轴线
        rightAxis.setEnabled(false);
        data = chartView.getData();

        LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);

        // 如果该统计折线图还没有数据集，则创建一条出来，如果有则跳过此处代码。
        if (set == null) {
            set = createLineDataSet();
            data.addDataSet(set);
        }


        for(int i=0;i<mHeartBeat.size();i++){
            data.addXValue(mDate.get(i));

            Entry entry = new Entry(mHeartBeat.get(i), set.getEntryCount());

            data.addEntry(entry, 0);
        }


        chartView.notifyDataSetChanged();

        // 当前统计图表中最多在x轴坐标线上显示的总量
        chartView.setVisibleXRangeMaximum(5);

        // y坐标轴线最大值
        // mChart.setVisibleYRange(30, AxisDependency.LEFT);

        // 将坐标移动到最新
        // 此代码将刷新图表的绘图
        chartView.moveViewToX(data.getXValCount() - 5);

    }
    void addEntry() {

        LineData data = chartView.getData();

        LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);

        // 如果该统计折线图还没有数据集，则创建一条出来，如果有则跳过此处代码。
        if (set == null) {
            set = createLineDataSet();
            data.addDataSet(set);
        }

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        daytime=sdf.format(new java.util.Date());
        data.addXValue(daytime);

        // 生成随机测试数
        //float f = (float) ((Math.random()) * 20 + 50);
        float f = heartbeat_result;

        // set.getEntryCount()获得的是所有统计图表上的数据点总量，
        // 如从0开始一样的数组下标，那么不必多次一举的加1
        Entry entry = new Entry(f, set.getEntryCount());

        // 往linedata里面添加点。注意：addentry的第二个参数即代表折线的下标索引。
        // 因为本例只有一个统计折线，那么就是第一个，其下标为0.
        // 如果同一张统计图表中存在若干条统计折线，那么必须分清是针对哪一条（依据下标索引）统计折线添加。
        data.addEntry(entry, 0);
        addData(band_addr,(int)f);

        // 像ListView那样的通知数据更新
        chartView.notifyDataSetChanged();

        // 当前统计图表中最多在x轴坐标线上显示的总量
        chartView.setVisibleXRangeMaximum(5);

        // y坐标轴线最大值
        // mChart.setVisibleYRange(30, AxisDependency.LEFT);

        // 将坐标移动到最新
        // 此代码将刷新图表的绘图
        chartView.moveViewToX(data.getXValCount() - 5);

        // mChart.moveViewTo(data.getXValCount()-7, 55f,
        // AxisDependency.LEFT);
    }


    LineDataSet createLineDataSet() {

        LineDataSet set = new LineDataSet(null, "实时心跳数据追踪");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        // 折线的颜色
        set.setColor(getResources().getColor(R.color.chartradius));

        set.setCircleColor(Color.WHITE);
        set.setLineWidth(10f);
        set.setCircleSize(5f);
        set.setFillAlpha(128);
        set.setFillColor(getResources().getColor(R.color.chartradius));
        set.setHighLightColor(Color.GREEN);
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(10f);
        set.setDrawValues(true);
        return set;
    }


    private void toFloat() {
        if(mHeartBeat.size()>4){
            mHeartBeat_float = new float[4];
            mDate_string=new String[4];
            int j=3;
            for(int i=mHeartBeat.size()-1;i>mHeartBeat.size()-5;i--){
                mHeartBeat_float[j]=mHeartBeat.get(i);
                mDate_string[j]=mDate.get(i);
                j--;
            }
        }else {
            mHeartBeat_float=new float[mHeartBeat.size()];
            mDate_string=new String[mHeartBeat.size()];
            for(int i=0;i<mHeartBeat.size();i++){
                mHeartBeat_float[i]=mHeartBeat.get(i);
                mDate_string[i]=mDate.get(i);
            }
        }

    }

    private void initChart(LineChart chart){
        dataset.setDotsColor(getResources().getColor(R.color.chartradius));
        dataset.setDotsRadius(getResources().getDimension(R.dimen.chart_radius));
        dataset.setDotsStrokeThickness(getResources().getDimension(R.dimen.chart_stroke));

        dataset.setDotsStrokeColor(getResources().getColor(R.color.chartbroke));
        dataset.setThickness(getResources().getDimension(R.dimen.chart_stroke));
        dataset.setColor(getResources().getColor(R.color.chartbroke));
        //chart.setAxisBorderValues(50, 100, 10);

    }

    private void initNewChart(LineChart chart){
        chart.setDescription("Ocare @ HeartBeat");
        chart.setNoDataTextDescription("暂时尚无数据");

        chart.setTouchEnabled(true);

        // 可拖曳
        chart.setDragEnabled(true);

        // 可缩放
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);

        chart.setPinchZoom(true);

        // 设置图表的背景颜色
        chart.setBackgroundColor(Color.LTGRAY);
    }

    private void addData(String id, int beat){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        daytime=sdf.format(new java.util.Date());

        db.execSQL("INSERT INTO band(band_addr,date,heartbeat) values(?,?,?)",
                new String[]{id,daytime,beat+""});
    }

    private void getData(String id) {
        int i=0;
        Cursor cursor = db.rawQuery("SELECT * FROM band WHERE band_addr = ?",
                new String[]{id});
        if (cursor.moveToFirst()) {
            do {
                int heartbeat = cursor.getInt(cursor.getColumnIndex("heartbeat"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                //stringBuilder.append("id：" + pid + "：" + name + "\n");
                mDate.add(date);
                mHeartBeat.add(Float.valueOf(heartbeat));

            } while (cursor.moveToNext() );
        }
        cursor.close();
    }

    class detectHeartBeat extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            chartView.removeAllViews();
//            chartView.reset();
//
//            dataset.addPoint(new Point(daytime,heartbeat_result));
//            initChart(newchartView);
//            newchartView.addData(dataset);
//
//            newchartView.show(new Animation(2000));
        }

        @Override
        protected Void doInBackground(Void... params) {
            UserInfo userInfo = new UserInfo(20111111, 1, 32, 180, 55, "张君义", 0);
            miBand.setUserInfo(userInfo);

            heartbeat_result = 87;
            addData(band_addr,heartbeat_result);  //心跳调用接口获得

            miBand.setHeartRateScanListener(new HeartRateNotifyListener()
            {
                @Override
                public void onNotify(int heartRate)
                {
                    Toast.makeText(SubActivity.this,"heart rate: "+heartRate,Toast.LENGTH_SHORT).show();
                }
            }); miBand.startHeartRateScan();
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_sub, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id =item.getItemId();
        if(id==R.id.action_settings){
            return true;
        }
        if(id==android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return  super.onOptionsItemSelected(item);
    }

}
