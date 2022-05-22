package ir.kitgroup.saleindemo.adapters.adapter;

import android.annotation.SuppressLint;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import ir.kitgroup.saleindemo.R;


/**
 * Created by ali ahmadian on 9/2/2019.
 */

public class BindingAdapter {

    @androidx.databinding.BindingAdapter("setImageView")
    public static void setImageView(ImageView imageView, String src) {

        if (imageView != null) {


           Picasso.get()
                   .load("http://api.kitgroup.ir/GetCompanyImage?id=" +
                           src + "&width=120&height=120")
                   .error(R.drawable.loading)
                   .placeholder(R.drawable.loading)
                   .into(imageView);

        }

    }

    @SuppressLint("SetTextI18n")
    @androidx.databinding.BindingAdapter("textPrice")
    public static void textPrice(TextView textView, String price) {

        if (textView != null) {

            DecimalFormat formatter=new DecimalFormat("#,###,###,###") ;
            textView.setText(formatter.format(Float.parseFloat(String.valueOf(price)))+" ریال ");

        }

    }




}
