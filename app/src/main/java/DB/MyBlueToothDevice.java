package DB;

import android.bluetooth.BluetoothDevice;

/**
 * Created by 重书 on 2016/6/1.
 */
public class MyBlueToothDevice  {
    private BluetoothDevice bluetoothDevice;
    private String name;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    private String info;

    public MyBlueToothDevice(BluetoothDevice b ,String name,String addr,int rssi,String info){
        this.bluetoothDevice=b;
        this.name = name;
        this.address = addr;
        this.rssi= rssi;
        this.info= info;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    private String address;
    private int rssi;


}
