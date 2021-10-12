package ir.kitgroup.salein.Fragments;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.orm.query.Select;
import com.squareup.picasso.Picasso;

import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import ir.kitgroup.salein.Activities.Classes.LauncherActivity;


import ir.kitgroup.salein.DataBase.User;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.Util.Util;
import ir.kitgroup.salein.databinding.ActivityDetailBinding;
import ir.kitgroup.salein.models.Product;

public class ShowDetailFragment  extends Fragment {


    private View view;

    private int placeHolderImage;
    private ActivityDetailBinding binding;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        Bundle bundle = getArguments();
        String Id = bundle.getString("Id");




        switch (LauncherActivity.name) {
            case "ir.kitgroup.salein":

                placeHolderImage = R.drawable.salein;
                break;

            case "ir.kitgroup.saleintop":
                placeHolderImage = R.drawable.top_png;


                break;


            case "ir.kitgroup.saleinmeat":

                placeHolderImage = R.drawable.meat_png;

                break;


            case "ir.kitgroup.saleinnoon":


                placeHolderImage = R.drawable.noon;

                break;
        }


        String ip = Select.from(User.class).first().ipLocal;

        Picasso.get()
                .load("http://" + ip + "/GetImage?productId=" + Id
                        )
                .error(placeHolderImage)
                .placeholder(R.drawable.loading)
                .into(binding.ivProduct);

        return view;

    }
}
