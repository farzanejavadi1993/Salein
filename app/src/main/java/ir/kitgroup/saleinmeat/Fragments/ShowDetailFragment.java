package ir.kitgroup.saleinmeat.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.orm.query.Select;
import com.squareup.picasso.Picasso;

import ir.kitgroup.saleinmeat.Activities.Classes.LauncherActivity;


import ir.kitgroup.saleinmeat.DataBase.User;
import ir.kitgroup.saleinmeat.R;
import ir.kitgroup.saleinmeat.databinding.ActivityDetailBinding;

public class ShowDetailFragment  extends Fragment {




    private int placeHolderImage=R.drawable.saleinorder_icon;
    private ActivityDetailBinding binding;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        binding=ActivityDetailBinding.inflate(getLayoutInflater());
        Bundle bundle = getArguments();
        String Id = bundle.getString("Id");




        try {
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
        }catch (Exception e){

        }



        String ip = Select.from(User.class).first().ipLocal;

        Picasso.get()
                .load("http://" + ip + "/GetImage?productId=" + Id
                        )
                .error(placeHolderImage)
                .resize(300,300)
                .centerCrop()
                .placeholder(R.drawable.loading)
                .into(binding.ivProduct);

        return binding.getRoot();

    }
}