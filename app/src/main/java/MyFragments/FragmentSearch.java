package MyFragments;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.qian.test.AdapterBand;
import com.example.qian.test.CallBackInterface;
import com.example.qian.test.Elder;
import com.example.qian.test.MyApplication;
import com.example.qian.test.R;
import com.example.qian.test.SubActivity;
import com.xiaomi.opensdk.pdc.OpenPdcSyncServerAdapter;
import com.xiaomi.opensdk.pdc.OpenProfileManager;
import com.xiaomi.opensdk.pdc.UserProfile;
import com.xiaomi.opensdk.pdc.exception.AuthenticationException;
import com.xiaomi.opensdk.pdc.exception.RetriableException;
import com.xiaomi.opensdk.pdc.exception.UnretriableException;
import com.zhaoxiaodan.miband.ActionCallback;
import com.zhaoxiaodan.miband.MiBand;
import com.zhaoxiaodan.miband.listeners.HeartRateNotifyListener;
import com.zhaoxiaodan.miband.listeners.NotifyListener;
import com.zhaoxiaodan.miband.model.BatteryInfo;
import com.zhaoxiaodan.miband.model.LedColor;
import com.zhaoxiaodan.miband.model.UserInfo;
import com.zhaoxiaodan.miband.model.VibrationMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import DB.MyBlueToothDevice;
import DB.MyDBOpenHelper;
import DB.RssiCompare;
import cn.bong.android.sdk.BongManager;
import cn.bong.android.sdk.config.Environment;
import cn.bong.android.sdk.model.http.auth.AuthError;
import cn.bong.android.sdk.model.http.auth.AuthInfo;
import cn.bong.android.sdk.model.http.auth.AuthUiListener;
import network.VolleySingleton;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSearch extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SQLiteDatabase db;
    private MyDBOpenHelper myDBHelper;
    private static final int MIBAND_CONNECT = 1;
    private static final int CONNECTED = 2;
    private static final int SHOW_BATTERY=3;
    private static final int SHOW_INFO=4;
    private static final int NOTIFY_DISCONNECTION=5;

    private static final int _HEART_RESULT =7;

    public static final String WEATHER_URL="http://api.openweathermap.org/data/2.5/forecast/daily?";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String ACCESS_TOKEN =
            "V2_yVt8cILMTaCl7zk0xTiPCDhTQa1TZgA1NHC3fFBI-kEHYZqvoJ78k2PcVsJW2maCR3h643E0krOsjEhwNM3fmFI_iCBRGMP43LS-d8aRVIHz4kHlmgWSCo0RbkYQsqqqZOo4Jx5MKpY8-U5fEqj1OA";
    private static final String CLIENT_ID = "2882303761517338017";
    private OpenProfileManager mProfileManager;
    private Handler mMainThreadHanler;
    private Handler mSubThreadHanler;
    private HandlerThread mHandlerThread;
    private int mRetryTime;

    private CardView mElderInfo;
    private TextView mElderName;
    private TextView mElderPhone;
    private TextView  mElderId;
    private EditText mBirthday;
    private RadioButton mMale;
    private RadioButton mFemale;
    private Button mSetButton;
    private Button mGetButton;
    private FloatingActionButton findButton;
    private TextView mResult;
    private ProgressDialog alert = null;
    private ProgressDialog pd1=null;
    private ProgressDialog.Builder builder = null;
    private AlertDialog alert1=null;
    private AlertDialog.Builder builder1=null;
    private RequestQueue requestQueue;
    private VolleySingleton volleySingleton;
    private NotificationManager notificationManager=null;
    private Notification notification=null;
    private Elder elder;
    private isLoadDataListener loadLisneter;
    private BatteryInfoDisplay infoDisplay;
    private BluetoothDevice device;
    private MiBand miBand;
    private RecyclerView bandList;
    private AdapterBand adapterBand;
    private List<BluetoothDevice> device_conn;
    private List<MyBlueToothDevice> bluetoothDevices;
    private BluetoothDevice miband_selected;
    private BatteryInfo batteryInfo;
    private MyThread thread;
    private int mHeartRate;
    private int flag;
    private int heartThreshold;
    private MyBlueToothDevice myBlueToothDevice;
    private CallBackInterface callBack;

    private Thread mThread;
    public FragmentSearch() {
        // Required empty public constructor
    }
    public static String getRequestUrl(String cityName){
        return WEATHER_URL+"q="+cityName+"&cnt="+1+"&appid="+ MyApplication.API_KEY;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSearch.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSearch newInstance(String param1, String param2) {
        FragmentSearch fragment = new FragmentSearch();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("info",elder);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState!=null){
            elder= (Elder) savedInstanceState.getSerializable("info");

        }
    }

    public String getInfoFromDB(String id){
        Cursor cursor = db.rawQuery("SELECT * FROM elder_band WHERE band_addr = ?",
                new String[]{id});
        String elderName;
        if (cursor.moveToFirst()) {

                heartThreshold = cursor.getInt(cursor.getColumnIndex("heartbeat"));
                elderName = cursor.getString(cursor.getColumnIndex("elder_name"));
            flag= 1;
        }
        else {
            elderName = "未知";
            flag = 0;
        }
        cursor.close();
        return elderName;
    }

    ScanCallback scanCallback = new ScanCallback()
    {
        @Override
        public void onScanResult(int callbackType, ScanResult result)
        {
            device = result.getDevice();

            if(device_conn.size()==0){
                device_conn.add(device);
                myBlueToothDevice = new MyBlueToothDevice(device,device.getName(),device.getAddress(),result.getRssi(),getInfoFromDB(device.getAddress()));
                //bluetoothDevices.add("设备名称："+device.getName()+" | "+"地址："+device.getAddress()+" | "+"信息:" + getInfoFromDB(device.getAddress())+" | 信号强度:" + result.getRssi());
                bluetoothDevices.add(myBlueToothDevice);
            }else{
                for(int i=0;i<device_conn.size();i++){

                    if(device.getAddress().equals(device_conn.get(i).getAddress())){
                        break;
                    }else{
                        if(i==device_conn.size()-1){
                            device_conn.add(device);
                            myBlueToothDevice = new MyBlueToothDevice(device,device.getName(),device.getAddress(),result.getRssi(),getInfoFromDB(device.getAddress()));
                            //bluetoothDevices.add("设备名称："+device.getName()+" | "+"地址："+device.getAddress()+" | "+"信息:" + getInfoFromDB(device.getAddress())+" | 信号强度:" + result.getRssi());
                            bluetoothDevices.add(myBlueToothDevice);

                        }
                    }
                }
            }



            // �������չʾ

        }
    };



    //    class LooperThread extends Thread {
//        public Handler mHandler;
//
//        public void run() {
//            Looper.prepare();
//            mHandler = new Handler() {
//                public void handleMessage(Message msg) {
//                    switch (msg.what) {
//                        case MIBAND_CONNECT:
//                            miBand.connect(miband_selected, new ActionCallback() {
//
//                                @Override
//                                public void onSuccess(Object data) {
//                                    Toast.makeText(getActivity(), "connect success", Toast.LENGTH_SHORT).show();
//                                    miBand.startVibration(VibrationMode.VIBRATION_WITH_LED);
//                                }
//
//                                @Override
//                                public void onFail(int errorCode, String msg) {
//                                    Toast.makeText(getActivity(), "connect failed", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                            break;
//                        case 2:
//                            Toast.makeText(getActivity(), "未找到设备", Toast.LENGTH_SHORT).show();
//                            break;
//                    }
//                }
//            };
//            Looper.loop();
//        }
//    }
class MyThread extends Thread {
    private Handler handler;

    public void run() {

        Message msg = new Message();
        msg.what=MIBAND_CONNECT;
        mHandler.sendEmptyMessage(msg.what);
        //执行数据操作，不涉及到UI
        Looper.prepare();
        handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                if(msg.what==CONNECTED){
                    Toast.makeText(getActivity(),"Connected",Toast.LENGTH_SHORT).show();
                   // mHandler.sendEmptyMessage(SHOW_INFO);
                   // mHandler.sendEmptyMessage(NOTIFY_DISCONNECTION);
                }else if(msg.what==MIBAND_CONNECT){
                    mHandler.sendEmptyMessage(MIBAND_CONNECT);
                }
                else if(msg.what==SHOW_BATTERY) {
                    Toast.makeText(getActivity(), batteryInfo.toString(), Toast.LENGTH_SHORT).show();


                }
                else if(msg.what==SHOW_INFO){
                   mHandler.sendEmptyMessage(SHOW_INFO);
                } else if(msg.what== NOTIFY_DISCONNECTION){

                            Toast.makeText(getActivity(),"连接断开，请重试！",Toast.LENGTH_SHORT).show();

                }else if(msg.what== _HEART_RESULT){
                    Toast.makeText(getActivity(),"heart rate: "+mHeartRate,Toast.LENGTH_SHORT).show();
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
                case MIBAND_CONNECT:
                    miBand.connect(miband_selected, new ActionCallback() {

                        @Override
                        public void onSuccess(Object data) {
//                            Toast.makeText(getActivity(), "connect success", Toast.LENGTH_SHORT).show();
                            //miBand.startVibration(VibrationMode.VIBRATION_WITH_LED);
                            if (loadLisneter != null) {
                                loadLisneter.loadComplete();
                            }
                        }

                        @Override
                        public void onFail(int errorCode, String msg) {
                            Toast.makeText(getActivity(), "connect failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case 2:
                    Toast.makeText(getActivity(), "未找到设备", Toast.LENGTH_SHORT).show();
                    break;
                case SHOW_INFO:
                    miBand.getBatteryInfo(new ActionCallback() {

                        @Override
                        public void onSuccess(Object data)
                        {
                            batteryInfo = (BatteryInfo)data;
                            //Toast.makeText(getActivity(), batteryInfo.toString(), Toast.LENGTH_SHORT).show();
                            if(infoDisplay!=null){
                                infoDisplay.showBattaryInfo();
                            }

                            //cycles:4,level:44,status:unknow,last:2015-04-15 03:37:55
                        }

                        @Override
                        public void onFail(int errorCode, String msg)
                        {
                            Toast.makeText(getActivity(), "readRssi fail", Toast.LENGTH_SHORT).show();
                            //Log.d(TAG, "readRssi fail");
                        }
                    });

            }
            return false;
        }
    });



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
        myDBHelper = new MyDBOpenHelper(getActivity(), "my.db", null, 1);
        db = myDBHelper.getWritableDatabase();
        //myDBHelper.onUpgrade(db,1,2);
        volleySingleton= VolleySingleton.getsInstance();
        requestQueue=volleySingleton.getRequestQueue();
        notificationManager= (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

         miBand=new MiBand(getActivity());
        device_conn=new ArrayList<BluetoothDevice>();
        bluetoothDevices = new ArrayList<MyBlueToothDevice>();
//        IntentFilter filter = new IntentFilter(SubActivity.action);
//        getActivity().registerReceiver(broadcastReceiver, filter);

    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            int i = intent.getIntExtra("newHeartThreshold",100);
            heartThreshold= i;
        }
    };

        @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==2){
            int i = data.getIntExtra("newHeartThreshold",100);
            heartThreshold= i;
        }

    }
//    @Override
//    public void setHeartThresholdComplete(int threshold) {
//        int i = threshold;
//        heartThreshold= i;
//    }


    public void procedure(int data) {
        Toast.makeText(getActivity(), "你选择了" + bluetoothDevices.get(data).getName(), Toast.LENGTH_SHORT).show();
        miband_selected = device_conn.get(data);
        //mHandler.sendEmptyMessage(MIBAND_CONNECT);
        thread = new MyThread();
        thread.start();
//                thread.handler.sendEmptyMessage(MIBAND_CONNECT);

        Toast.makeText(getActivity(), "Connecting now ...", Toast.LENGTH_SHORT).show();
        setLoadDataComplete(new isLoadDataListener() {
            @Override
            public void loadComplete() {
                //miBand.setLedColor(LedColor.BLUE);
                // Toast.makeText(getActivity(),"Connected",Toast.LENGTH_SHORT).show();
                thread.handler.sendEmptyMessage(CONNECTED);
                miBand.startVibration(VibrationMode.VIBRATION_WITH_LED);
                miBand.pair(new ActionCallback() {
                    @Override
                    public void onSuccess(Object data) {
                        //changeStatus("pair succ");
                    }

                    @Override
                    public void onFail(int errorCode, String msg) {
                        //changeStatus("pair fail");
                    }
                });
                miBand.setDisconnectedListener(new NotifyListener() {
                    @Override
                    public void onNotify(byte[] data) {
                        thread.handler.sendEmptyMessage(NOTIFY_DISCONNECTION);
                    }
                });
                if (flag == 0) {
                    db.execSQL("INSERT INTO elder_band(band_addr,elder_name,band_name,heartbeat) values(?,?,?,?)",
                            new String[]{miband_selected.getAddress(), "我的手环", miband_selected.getName(), 0 + ""});
                    flag = 1;
                } else if (flag == 1) {

                }


                MyApplication myApplication = (MyApplication) getActivity().getApplicationContext();
                myApplication.setMiBand(miBand, getActivity());
                myApplication.setBand_addr(miband_selected.getAddress());
                Intent it = new Intent(getActivity(), SubActivity.class);
                Bundle b = new Bundle();
                b.putInt("heartThreshold", heartThreshold);
                it.putExtras(b);
                startActivityForResult(it, 2);
                //thread.handler.sendEmptyMessage(SHOW_BATTERY);

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_search, container, false);
        bandList= (RecyclerView) view.findViewById(R.id.bandList);
        bandList.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterBand=new AdapterBand(getActivity());
        bandList.setAdapter(adapterBand);
//        setCallBackListener(new CallBackInterface() {
//            @Override
//            public void setHeartThresholdComplete(int threshold) {
//                Toast.makeText(getActivity(),"回调",Toast.LENGTH_SHORT).show();
//            }
//        });
        adapterBand.setOnItemClickListener(new AdapterBand.OnRecyclerViewItemClickListener() {

            @Override
            public void onItemClick(View view, int data) {
                procedure(data);
            }

            @Override
            public void onItemLongClick(View view, int data) {

                thread.handler.sendEmptyMessage(SHOW_INFO);
                miBand.startHeartRateScan();
                setBattaryInfo(new BatteryInfoDisplay() {
                    @Override
                    public void showBattaryInfo() {
                        thread.handler.sendEmptyMessage(SHOW_BATTERY);
                    }
                });
            }
        });

        mElderId= (TextView) view.findViewById(R.id.elderid);
        mElderName= (TextView) view.findViewById(R.id.eldername);
        mElderPhone= (TextView) view.findViewById(R.id.elderphone);
        mElderInfo= (CardView) view.findViewById(R.id.elderInfo);
        mElderInfo.setVisibility(View.GONE);


        if (savedInstanceState != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
//            elder = (Elder) savedInstanceState.getSerializable("info");
//            mElderInfo.setVisibility(View.VISIBLE);
//            mElderId.setText(elder.getId());
//            mElderPhone.setText(elder.getPhone());
//            mElderName.setText(elder.getName());
//            mElderInfo.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//
//                    Intent it = new Intent(getActivity(), SubActivity.class);
//                    startActivity(it);
//                }
//            });
        }



        findButton= (FloatingActionButton) view.findViewById(R.id.findbtn);
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothAdapter blueadapter=BluetoothAdapter.getDefaultAdapter();
                if(blueadapter.isEnabled()){
                    AsynFind asynFind=new AsynFind();
                    asynFind.execute();
                }else {
                    Toast.makeText(getActivity(),"请开启蓝牙",Toast.LENGTH_SHORT).show();
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent,2);
                }


            }
        });

        return view;
    }





    class AsynFind extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            try {

                //MiBand.startScan(scanCallback);


                MiBand.startScan(scanCallback);
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            alert=ProgressDialog.show(getActivity(), "搜索手环", "正在查找附近设备,并尝试连接,请稍后...",false,true);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            alert.dismiss();
            MiBand.stopScan(scanCallback);
            if(device_conn.size()==0){
                //mHandler.sendEmptyMessage(2);
            }else {
                Collections.sort(bluetoothDevices,new RssiCompare());
//            Toast.makeText(getActivity(),"搜索完成",Toast.LENGTH_SHORT).show();
//            final String[] band = new String[]{"Shanghai", "Beijing", "手环3", "手环4", "手环5", "手环6", "手环7"};
                alert1 = null;
                builder1 = new AlertDialog.Builder(getActivity());
                //String[] array = (String[]) bluetoothDevices.toArray(new String[bluetoothDevices.size()]);
                List<String> array = new ArrayList<String>();
                for(int i=0 ;i<bluetoothDevices.size();i++){

                    array.add("设备名称："+bluetoothDevices.get(i).getName()+" | "+"地址："+bluetoothDevices.get(i).getAddress()+" | 信号强度:" +bluetoothDevices.get(i).getRssi()+" | "+"信息:" +bluetoothDevices.get(i).getInfo());
                }
                adapterBand.setBandList(array);

            }
            for(int i=0;i<bluetoothDevices.size();i++){
                if(Objects.equals(bluetoothDevices.get(i).getName(), "MI1S")){
                   // Toast.makeText(getActivity(),"find mi band",Toast.LENGTH_SHORT).show();
                    procedure(i);
                }
            }
        }
    }

    private void sendJsonRequest(String BandId) {
        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET,getRequestUrl(BandId)
                ,new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                //volleyError.setVisibility(View.GONE);
                Log.e("s",getRequestUrl("Shanghai"));
                elder=parseJSONResponse(response);
                Toast.makeText(getActivity(),elder.getName(),Toast.LENGTH_SHORT).show();
//                mElderInfo.setVisibility(View.VISIBLE);
//                mElderId.setText(elder.getId());
//                mElderPhone.setText(elder.getPhone());
//                mElderName.setText(elder.getName());
                //adapterBoxOffice.setWeatherList(listWeather);
                //Weather._alert(getActivity(), response.toString());
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

               // volleyError.setVisibility(View.VISIBLE);
                if(error instanceof TimeoutError || error instanceof NoConnectionError){
                    //volleyError.setText("network error");
                }else if(error instanceof NetworkError){
                    //volleyError.setText("network error");
                }

            }
        });
        requestQueue.add(request);
    }


    private Elder parseJSONResponse(JSONObject response){
        Elder _elder = new Elder();
        if(response==null){
            return null;
        }
        try {
            StringBuilder data=new StringBuilder();
            JSONObject objectWeather = response.getJSONObject("city");
            String cityName=objectWeather.getString("name");
            data.append(cityName + ":\n");
            JSONArray arrayList =response.getJSONArray("list");

            for(int i=0;i<arrayList.length();i++){
                JSONObject current=arrayList.getJSONObject(i);
                JSONObject temperature= current.getJSONObject("temp");
                JSONArray weather= current.getJSONArray("weather");
                String temp_avg=temperature.getString("day");
                String temp_min=temperature.getString("min");
                String temp_max=temperature.getString("max");
                JSONObject weather_current=weather.getJSONObject(0);
                String weather_info=weather_current.getString("main");
                String pressure=current.getString("pressure");
                data.append(temp_avg + "\n" + temp_min + "\n" + temp_max + "\n" + weather_info + "\n" + pressure + "\n");

                _elder.setId(weather_info);
                _elder.setName(temp_avg);
                _elder.setPhone(pressure);
                _elder._alert(getActivity(), _elder.toString());
                //listWeather.add(_weather);
            }




        } catch (JSONException e) {
            e.printStackTrace();
        }
        return _elder;
    }


    class sendJsonRequestInAsyn extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (loadLisneter != null) {
                loadLisneter.loadComplete();
            }


            else {
                Toast.makeText(getActivity(),"please retry",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Void doInBackground(String... params) {
            sendJsonRequest(params[0]);
            if(elder!=null){

            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {

            pd1 = new ProgressDialog(getActivity());
            //依次设置标题,内容,是否用取消按钮关闭,是否显示进度
            pd1.setTitle("加载老人信息");
            pd1.setMessage("数据加载中,请稍后...");
            pd1.setCancelable(true);

            pd1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd1.setIndeterminate(true);
            //调用show()方法将ProgressDialog显示出来
            pd1.show();
            //Toast.makeText(getActivity(),"Loading",Toast.LENGTH_SHORT).show();
        }
    }
    private void prepare() {
        mProfileManager = new OpenProfileManager(new OpenPdcSyncServerAdapter.DefaultEnvironment(getActivity()),
                CLIENT_ID, ACCESS_TOKEN);
        // 初始化sdk(接入api时需要AppID和AppSecret，且请注意AppSecret等信息的保密工作，防止被盗用)
        BongManager.initialize(getActivity(), "1419735044202", "", "558860f5ba4546ddb31eafeee11dc8f4");
        // 开启 调试模式，打印日志（默认关闭）
        BongManager.setDebuged(true);
        //设置 环境（默认线上）：Daily（测试）,  PreDeploy（预发，线上数据）, Product（线上）;
        BongManager.setEnvironment(Environment.Daily);
        // 设置触摸Yes键时震动
        BongManager.setTouchVibrate(true);
        BongManager.bongAuth(getActivity(), "demo", new AuthUiListener() {
            @Override
            public void onError(AuthError error) {
                Toast.makeText(getActivity(),"error",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSucess(AuthInfo result) {
                Toast.makeText(getActivity(),"success",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
            }
        });
    }

    private void initView() {

    }

    private interface isLoadDataListener {
        public void loadComplete();
    }

    private interface BatteryInfoDisplay {
        public void showBattaryInfo();
    }




    public void setLoadDataComplete(isLoadDataListener dataComplete) {
        this.loadLisneter = dataComplete;
    }

    public void setBattaryInfo(BatteryInfoDisplay infoDisplay) {
        this.infoDisplay = infoDisplay;
    }



}
