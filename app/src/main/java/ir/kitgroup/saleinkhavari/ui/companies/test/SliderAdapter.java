package ir.kitgroup.saleinkhavari.ui.companies.test;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

import ir.kitgroup.saleinkhavari.R;
import ir.kitgroup.saleinkhavari.classes.Util;
import ir.kitgroup.saleinkhavari.models.Advertise;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder> {


    public interface ClickItem{
        void onClick1(Advertise advertise, int position);
    }
    private ClickItem clickItem;

    public void setOnClickListener(ClickItem clickItem){
        this.clickItem=clickItem;
    }


    private final List<Advertise> list;
    private final Context context;

    public SliderAdapter(Activity context, ArrayList<Advertise> list) {
        this.list = list;
        this.context=context;

    }


    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        return new SliderAdapterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout, null));
    }


    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, final int position) {
        final String id = list.get(position).getI();

        Glide.with(context)
                .load(Util.Main_Url_IMAGE + "/GetAdvertisementImage?id=" +
                        id + "&width=" + 600 + "&height=" + 400)

                .centerInside()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(viewHolder.ivSpecial);


        viewHolder.itemView.setOnClickListener(view -> clickItem.onClick1(list.get(position) ,position));
    }

    @Override
    public int getCount() {
        int size=0;
        if (list !=null)
            size= list.size();
        return size;
    }

    static class SliderAdapterViewHolder extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView ivSpecial;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            ivSpecial = itemView.findViewById(R.id.iv_special);
            this.itemView = itemView;
        }
    }
}