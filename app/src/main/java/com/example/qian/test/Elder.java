package com.example.qian.test;

import android.content.Context;
import android.widget.Toast;

import java.io.Serializable;

/**
 * Created by 重书 on 2016/3/27.
 */
public class Elder implements Serializable {
    private String id;
    private String name;
    private String phone;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "city:"+id+",weather:"+name+",temperature:"+phone;
    }

    public static void _alert(Context context, String s) {
        Toast.makeText(context, s + "", Toast.LENGTH_SHORT).show();
    }
}
