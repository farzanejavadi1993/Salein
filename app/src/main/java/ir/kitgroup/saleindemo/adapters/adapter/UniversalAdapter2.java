package ir.kitgroup.saleindemo.adapters.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by ali ahmadian on 9/2/2019.
 */

public class UniversalAdapter2 extends RecyclerView.Adapter<UniversalAdapter2.MyHolder> {
    int viewID;
    List list;
    int variableID;
    OnItemClickListener onItemClickListener;
    OnItemBindListener onItemBindListener;
    OnItemTouchListener touchListener;

    public UniversalAdapter2(int viewID, List<?> list, int variableID) {
        this.viewID = viewID;
        this.list = list;
        this.variableID = variableID;
    }

    public void setList(List<?> list) {
        this.list = list;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, viewID, parent,
                false);
        return new MyHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.binding.setVariable(variableID, list.get(position));
        holder.itemView.setOnClickListener(v -> {
            try {
                onItemClickListener.onClick(holder.binding, position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        try {
            onItemBindListener.onBind(holder.binding, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void clearList() {

        list.clear();
        notifyDataSetChanged();
    }

    public void update(List<?> objects) {
        list.addAll(objects);
        notifyDataSetChanged();

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ViewDataBinding binding;

        public MyHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void setOnTouchListener(OnItemTouchListener touchListener){

        this.touchListener=touchListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemBindListener(OnItemBindListener onItemBindListener) {
        this.onItemBindListener = onItemBindListener;
    }

    public interface OnItemClickListener {
        void onClick(ViewDataBinding binding, int position);

    }

    public interface OnItemTouchListener{
        void onTouch(View view, int position, MotionEvent motionEvent);
    }



    public interface OnItemBindListener {
        void onBind(ViewDataBinding binding, int position);

    }



}
