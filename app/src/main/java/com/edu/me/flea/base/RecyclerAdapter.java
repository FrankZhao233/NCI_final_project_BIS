package com.edu.me.flea.base;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

import com.edu.me.flea.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class RecyclerAdapter<Data> extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder<Data>>
    implements View.OnClickListener,View.OnLongClickListener{

    private List<Data> mDataList;
    private AdapterListener<Data> adapterListener;

    public RecyclerAdapter() {
        this(null);
    }

    public RecyclerAdapter(AdapterListener<Data> adapterListener) {
        this(new ArrayList<>(),adapterListener);
    }

    public RecyclerAdapter(List<Data> mDataList, AdapterListener<Data> adapterListener) {
        this.mDataList = mDataList;
        this.adapterListener = adapterListener;
    }

    @Override
    public ViewHolder<Data> onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View root = inflater.inflate(viewType,parent,false);
        ViewHolder<Data> viewHolder = onCreateViewHolder(root,viewType);

        root.setTag(R.id.recycler_view_holder,viewHolder);
        root.setOnClickListener(this);
        root.setOnLongClickListener(this);
        return viewHolder;
    }

    public abstract ViewHolder<Data> onCreateViewHolder(View root, int viewType);

    @Override
    public void onBindViewHolder(ViewHolder<Data> holder, int position) {

        Data data = mDataList.get(position);
        holder.bind(data);
    }

    @Override
    public int getItemViewType(int position) {
        Data data = mDataList.get(position);
        return getItemLayout(data,position);
    }


    public abstract int getItemLayout(Data data,int position);

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public void onClick(View v) {
        ViewHolder<Data> holder = (ViewHolder<Data>) v.getTag(R.id.recycler_view_holder);
        if(holder != null){
            if(adapterListener == null)
                return;
            int pos = holder.getAdapterPosition();
            adapterListener.onItemClick(holder,mDataList.get(pos));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        ViewHolder<Data> holder = (ViewHolder<Data>) v.getTag(R.id.recycler_view_holder);
        if(holder != null){
            if(adapterListener != null){
                int pos = holder.getAdapterPosition();
                adapterListener.onItemLongClick(holder,mDataList.get(pos));
                return true;
            }
        }
        return false;
    }

    public List<Data> getItems() {
        return mDataList;
    }

    public void add(Data data){
        mDataList.add(data);
        notifyItemInserted(mDataList.size() -1 );
    }


    public void addAllData(Collection<Data> datas){
        int start = mDataList.size();
        mDataList.addAll(datas);
        Log.d("flea","RecyclerAdapter  addAllData mDataList==>"+mDataList);

        notifyItemRangeChanged(start,datas.size());
    }


    public void addAllData(Data... datas){
        int start = mDataList.size();
        mDataList.addAll(Arrays.asList(datas));
        notifyItemRangeChanged(start,mDataList.size()-1);
    }


    public void remove(){
        mDataList.clear();
        notifyDataSetChanged();
    }


    public void replace(Collection<Data> datas){
        mDataList.clear();
        mDataList.addAll(datas);
        notifyDataSetChanged();
    }

    public void setAdapterListener(AdapterListener<Data> listener){
        this.adapterListener = listener;
    }

    public interface AdapterListener<Data>{
        void onItemClick(ViewHolder<Data> holder, Data data);
        void onItemLongClick(ViewHolder<Data> holder, Data data);
    }

    public static abstract class ViewHolder<Data> extends RecyclerView.ViewHolder{
        protected Data mData;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(Data data){
            mData = data;
            onBind(data);
        }

        protected abstract void onBind(Data data);
    }

    public abstract static class AdapterListenerImpl<Data> implements AdapterListener<Data>{
        @Override
        public void onItemClick(ViewHolder<Data> holder,Data data) {

        }

        @Override
        public void onItemLongClick(ViewHolder<Data> holder,Data data) {

        }
    }
}
