package ir.kitgroup.salein1.Activities.Classes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orm.query.Select;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import ir.kitgroup.salein1.DataBase.Product;
import ir.kitgroup.salein1.MainActivity;
import ir.kitgroup.salein1.R;

public class ShowDetailActivity extends AppCompatActivity {

    private ImageView ivProduct;
    private TextView tvDescriptionProduct;
    private TextView tvDescriptionUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (MainActivity.screenInches >= 7) {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setContentView(R.layout.activity_show_detail_fragment);
        } else {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.activity_detail);
        }

        Bundle bundle = getIntent().getExtras();
        String Id = bundle.getString("Id");

        ivProduct = findViewById(R.id.iv_product);
        tvDescriptionProduct = findViewById(R.id.tv_description_product);
        tvDescriptionUser = findViewById(R.id.tv_description_user);
        Product prd = Select.from(Product.class).where("I ='" + Id + "'").first();

        if (prd != null) {

   /*         File file = new File(Environment.getExternalStoragePublicDirectory("SaleIn") +
                    "/Images/",
                    prd.I.toUpperCase() + ".jpg");*/

            String yourFilePath = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) +"/"+"SaleIn"+"/"+ prd.I.toUpperCase() + ".jpg";
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
    }
}