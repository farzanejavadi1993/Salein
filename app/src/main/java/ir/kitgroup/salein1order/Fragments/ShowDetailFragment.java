package ir.kitgroup.salein1order.Fragments;

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

import com.orm.query.Select;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import ir.kitgroup.salein1order.DataBase.Product;
import ir.kitgroup.salein1order.MainActivity;
import ir.kitgroup.salein1order.R;

public class ShowDetailFragment  extends Fragment {

    private int fontSize=0;
    private View view;
    private ImageView ivProduct;
    private TextView tvDescriptionProduct;
    private TextView tvDescriptionUser;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (MainActivity.screenInches >= 7) {
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
        Product prd = Select.from(Product.class).where("I ='" + Id + "'").first();

        if (prd != null) {

   /*         File file = new File(Environment.getExternalStoragePublicDirectory("SaleIn") +
                    "/Images/",
                    prd.I.toUpperCase() + ".jpg");*/

            String yourFilePath = getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) +"/"+"SaleIn"+"/"+ prd.I.toUpperCase() + ".jpg";
            File file = new File(yourFilePath);

            if (file.exists()) {

                Bitmap image = null;

                try {

                    image = BitmapFactory.decodeStream(new FileInputStream(file));
                } catch (FileNotFoundException e) {

                    e.printStackTrace();
                }

                ivProduct.setImageBitmap(image);
                tvDescriptionProduct.setText(prd.DES);
            }
        }
        return view;

    }
}
