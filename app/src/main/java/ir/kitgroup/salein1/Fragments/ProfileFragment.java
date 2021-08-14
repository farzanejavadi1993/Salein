package ir.kitgroup.salein1.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.orm.query.Select;

import java.util.List;

import ir.kitgroup.salein1.DataBase.Account;
import ir.kitgroup.salein1.DataBase.Invoice;
import ir.kitgroup.salein1.DataBase.Invoicedetail;
import ir.kitgroup.salein1.DataBase.OrderType;
import ir.kitgroup.salein1.DataBase.Product;
import ir.kitgroup.salein1.DataBase.ProductGroupLevel1;
import ir.kitgroup.salein1.DataBase.ProductGroupLevel2;
import ir.kitgroup.salein1.DataBase.Setting;
import ir.kitgroup.salein1.DataBase.Tables;
import ir.kitgroup.salein1.DataBase.User;
import ir.kitgroup.salein1.Fragments.Organization.LoginOrganizationFragment;
import ir.kitgroup.salein1.R;

public class ProfileFragment  extends Fragment {
    private MaterialButton btnLogOut;
    private static SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private TextView tvMobile;
    private TextView tvName;
    private TextView tvAddress;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile,container,false);
      btnLogOut=view.findViewById(R.id.btnLogOut);
      tvName=view.findViewById(R.id.tvName);
      tvMobile=view.findViewById(R.id.tvMobile);
      tvAddress=view.findViewById(R.id.tvAddress);



      Account account=Select.from(Account.class).first();
      if (account!=null) {
          tvName.setText(account.N);
          tvMobile.setText(account.M);

          if (account.ADR!=null && account.ADR.equals("")){
              tvAddress.setText(account.ADR);

          }else {
              tvAddress.setText("ناموجود");
          }
      }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPreferences.edit();


      btnLogOut.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              if (Account.count(Account.class) > 0)
                  Account.deleteAll(Account.class);

              if (Invoice.count(Invoice.class) > 0)
                  Invoice.deleteAll(Invoice.class);

              if (Invoicedetail.count(Invoicedetail.class) > 0)
                  Invoicedetail.deleteAll(Invoicedetail.class);

              if (OrderType.count(OrderType.class) > 0)
                  OrderType.deleteAll(OrderType.class);

              if (Product.count(Product.class) > 0)
                  Product.deleteAll(Product.class);


              if (Setting.count(Setting.class) > 0)
                  Setting.deleteAll(Setting.class);

              if (ProductGroupLevel1.count(ProductGroupLevel1.class) > 0)
                  ProductGroupLevel1.deleteAll(ProductGroupLevel1.class);

              if (ProductGroupLevel2.count(ProductGroupLevel2.class) > 0)
                  ProductGroupLevel2.deleteAll(ProductGroupLevel2.class);


              if (Tables.count(Tables.class) > 0)
                  Tables.deleteAll(Tables.class);

              if (!Select.from(User.class).list().get(0).CheckUser) {
                  if (User.count(User.class) > 0)
                      User.deleteAll(User.class);
              }


              if (Tables.count(Tables.class) > 0)
                  Tables.deleteAll(Tables.class);

              List<User> user = Select.from(User.class).list();
              if (user.size() > 0) {
                 // user.get(0).Exit = true;
                  user.get(0).save();
              }


              editor.putBoolean("firstSync", false);
              editor.putBoolean("firstSyncSetting", false);
              editor.apply();

              getFragmentManager().popBackStack();
              getFragmentManager().popBackStack();
              LoginOrganizationFragment userFragment = new LoginOrganizationFragment();
              getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, userFragment).addToBackStack("UserF").commit();

          }
      });
        return view;
    }
}

