package com.example.qian.test;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 2016/2/24.
 */
public class AdapterBand extends RecyclerView.Adapter<AdapterBand.ViewHolderBoxOffice> implements View.OnClickListener , View.OnLongClickListener,GestureDetector.OnGestureListener {    //adapte fragment with the layoutfile and the data

    private List<String> listW=new ArrayList<>();
    private LayoutInflater layoutInflater;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;


    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {

            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }



    @Override
    public boolean onLongClick(View v) {
        if (mOnItemClickListener != null) {

            mOnItemClickListener.onItemLongClick(v,(int)v.getTag());
        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    //define interface
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , int data);
        void onItemLongClick(View view,int data);
    }
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    public AdapterBand(Context context){
        layoutInflater=LayoutInflater.from(context);
    }

    public void setBandList(List<String> list){
        this.listW=list;
        notifyItemRangeChanged(0,listW.size());
    }

    static class ViewHolderBoxOffice extends RecyclerView.ViewHolder{
        private TextView bandAddr;



        public ViewHolderBoxOffice(View itemView) {

            super(itemView);
            bandAddr= (TextView) itemView.findViewById(R.id.bandAddress);

        }
    }

    @Override
    public ViewHolderBoxOffice onCreateViewHolder(ViewGroup parent, int viewType) {   //inflate the layout file
        View view=layoutInflater.inflate(R.layout.cardview_test,parent,false);
        ViewHolderBoxOffice viewHolder=new ViewHolderBoxOffice(view);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolderBoxOffice holder, int position) {
        String current=listW.get(position);
        holder.bandAddr.setText(current);

        holder.itemView.setTag(position);
    }



    @Override
    public int getItemCount() {
        return listW.size();
    }
}
