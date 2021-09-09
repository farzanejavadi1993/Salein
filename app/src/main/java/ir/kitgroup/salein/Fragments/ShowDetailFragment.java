package ir.kitgroup.salein.Fragments;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import ir.kitgroup.salein.Activities.Classes.LauncherActivity;
import ir.kitgroup.salein.DataBase.Product;

import ir.kitgroup.salein.R;
import ir.kitgroup.salein.Util.Util;

public class ShowDetailFragment  extends Fragment {

    private int fontSize=0;
    private View view;
    private ImageView ivProduct;
    private TextView tvDescriptionProduct;
    private TextView tvDescriptionUser;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (LauncherActivity.screenInches >= 7) {
            fontSize = 13;
            view = inflater.inflate(R.layout.activity_show_detail_fragment, container, false);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        }
        else {
            fontSize = 12;
            view = inflater.inflate(R.layout.activity_detail, container, false);
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        Bundle bundle = getArguments();
        String Id = bundle.getString("Id");

        ivProduct =view. findViewById(R.id.iv_product);
        tvDescriptionProduct =view. findViewById(R.id.tv_description_product);
        tvDescriptionUser = view.findViewById(R.id.tv_description_user);

        ArrayList<Product> arrayList=new ArrayList<>();
        arrayList.addAll(Util.AllProduct);
        CollectionUtils.filter(arrayList, a->a.I.equals(Id));


        if (arrayList.size()>0) {

   /*         File file = new File(Environment.getExternalStoragePublicDirectory("SaleIn") +
                    "/Images/",
                    prd.I.toUpperCase() + ".jpg");*/

            String yourFilePath = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) +"/"+"SaleIn"+"/"+ arrayList.get(0).I.toUpperCase() + ".jpg";
            File file = new File(yourFilePath);

            if (file.exists()) {

                Bitmap image = null;

                try {

                    image = BitmapFactory.decodeStream(new FileInputStream(file));
                } catch (FileNotFoundException e) {

                    e.printStackTrace();
                }

                ivProduct.setImageBitmap(image);
                tvDescriptionProduct.setText(arrayList.get(0).DES);
            }
        }
        return view;

    }
}
