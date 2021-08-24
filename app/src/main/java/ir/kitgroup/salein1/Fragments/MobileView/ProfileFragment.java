package ir.kitgroup.salein1.Fragments.MobileView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;

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
import ir.kitgroup.salein1.Fragments.Client.LoginClient.LoginClientFragment;

import ir.kitgroup.salein1.R;

import ir.kitgroup.salein1.databinding.FragmentProfileBinding;

public class ProfileFragment  extends Fragment {

    //region Parameter
    private FragmentProfileBinding binding;
    //endregion Parameter

    private  SharedPreferences sharedPreferences;


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {


        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();


    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        Account account=Select.from(Account.class).first();
        if (account!=null) {
            binding.tvName.setText(account.N);
            binding.tvMobile.setText(account.M);

            if (account.ADR!=null && account.ADR.equals("")){
                binding.tvAddress.setText(account.ADR);

            }else {
                binding.tvAddress.setText("ناموجود");
            }
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());



        binding.btnLogOut.setOnClickListener(v -> {
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


            sharedPreferences.edit().putBoolean("firstSync", false).apply();
            sharedPreferences.edit().putBoolean("firstSyncSetting", false).apply();


            getFragmentManager().popBackStack();
            getFragmentManager().popBackStack();
            LoginClientFragment userFragment = new LoginClientFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, userFragment).commit();

        });



        binding.btnOrderList.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, new OrderListFragment(), "OrderListFragment").addToBackStack("OrderListFMobile").commit());
    }
}

