package ir.kitgroup.salein.adapter;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orm.query.Select;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.salein.DataBase.Company;
import ir.kitgroup.salein.R;


public class BindingAdapter {



    @androidx.databinding.BindingAdapter("setImageProduct")
    public static void setImageProduct(ImageView imageView, String id) {
        if (imageView != null) {
            String ip = Select.from(Company.class).first().getIp1();
            Glide.with(imageView.getContext())
                    .load("http://" + ip + "/GetImage?productId=" + id + "&width=200&height=200")
                    .error(R.drawable.nopic)
                    .placeholder(R.drawable.nopic)
                    .centerInside()
                    .fitCenter()
                    .into(imageView);
        }
    }




}
