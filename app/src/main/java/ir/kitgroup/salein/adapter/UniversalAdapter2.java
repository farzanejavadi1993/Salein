package ir.kitgroup.salein.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UniversalAdapter2 extends RecyclerView.Adapter<UniversalAdapter2.MyHolder> {

    private final Object mLock = new Object();
    private ArrayList<?> mOriginalValues;
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

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<?> list) {
        this.list = list;

        try {
           notifyDataSetChanged();
        }catch (Exception ignored){}

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

    @SuppressLint("NotifyDataSetChanged")
    public void clearList() {

        list.clear();

        try {
           notifyDataSetChanged();
        }catch (Exception ignored){}
    }

    @SuppressLint("NotifyDataSetChanged")
    public void update(List<?> objects) {
        list.addAll(objects);
        try {
        notifyDataSetChanged();
        }catch (Exception ignored){}


    }


    @Override
    public int getItemCount() {
        int size=0;
        if (list!=null )
            size=list.size();
        return size;
    }


    public class MyHolder extends RecyclerView.ViewHolder {
        ViewDataBinding binding;

        public MyHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void setOnTouchListener(OnItemTouchListener touchListener) {

        this.touchListener = touchListener;
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

    public interface OnItemTouchListener {
        void onTouch(View view, int position, MotionEvent motionEvent);
    }


    public interface OnItemBindListener {
        void onBind(ViewDataBinding binding, int position);

    }


}
