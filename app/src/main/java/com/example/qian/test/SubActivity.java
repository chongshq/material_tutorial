package com.example.qian.test;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.QuartEase;
import com.zhaoxiaodan.miband.MiBand;

import DB.MyDBOpenHelper;

public class SubActivity extends ActionBarActivity {
    private SQLiteDatabase db;
    private MyDBOpenHelper myDBHelper;
    private StringBuilder stringBuilder;
    private LineChartView chartView;
    private String[] labels={"one","two","three"};
    private String band_addr;
    private MiBand miBand;
    private LineSet dataset;
    private float[] values={77,80,70,75};
    private String[] mDate;
    private float[] mHeartBeat;
    private FloatingActionButton heartBeatTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        mDate=new String[4];
        mHeartBeat=new float[4];

        MyApplication myApp = (MyApplication)getApplicationContext();
        band_addr=myApp.getBand_addr();
        miBand=myApp.getMiBand();

        myDBHelper = new MyDBOpenHelper(this, "my.db", null, 1);
        db = myDBHelper.getWritableDatabase();
        for(int i=0;i<4;i++){
            addData(band_addr,(int)values[i]);
        }

        getData(band_addr);
        Toolbar toolbar= (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        chartView= (LineChartView) findViewById(R.id.linechart);
        heartBeatTest= (FloatingActionButton) findViewById(R.id.beatbtn);
        heartBeatTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // float[] n = new float[]{80,70,70,73};
                chartView.dismiss(new Animation(2000));
                dataset.addPoint(new Point("current",80));

               // chartView.updateValues(0, n);
                chartView.removeAllViews();
               chartView.addData(dataset);
               chartView.show();
               //chartView.notifyDataUpdate();
               //  chartView.show(new Animation(2000));
                //chartView.notifyDataUpdate();

            }
        });
        //chartView.setClickablePointRadius(radius);

        dataset = new LineSet(mDate, mHeartBeat);
        Animation anim = new Animation(2000);
        anim.setAlpha(20);
        //anim.setEasing(QuartEase.);

        //dataset.addPoint(new Point("four", 75));
        dataset.setDotsColor(getResources().getColor(R.color.chartradius));
        dataset.setDotsRadius(getResources().getDimension(R.dimen.chart_radius));
        dataset.setDotsStrokeThickness(getResources().getDimension(R.dimen.chart_stroke));

        dataset.setDotsStrokeColor(getResources().getColor(R.color.chartbroke));
        dataset.setThickness(getResources().getDimension(R.dimen.chart_stroke));
        dataset.setColor(getResources().getColor(R.color.chartbroke));
        chartView.setAxisBorderValues(50, 100, 10);

       // dataset.setFill(getResources().getColor(R.color.colorPrimary));
        chartView.addData(dataset);
        chartView.show(anim);

        //dataset.addPoint(string, float)
    }

    private void addData(String id, int beat){

        db.execSQL("INSERT INTO band(band_addr,date,heartbeat) values(?,?,?)",
                new String[]{id,"daytime",beat+""});
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
                mDate[i]=date;
                mHeartBeat[i]=heartbeat;
                i++;
            } while (cursor.moveToNext() && i<4);
        }
        cursor.close();
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
