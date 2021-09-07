package ir.kitgroup.ordersaleinapplication.Fragments.MobileView;

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

import ir.kitgroup.ordersaleinapplication.DataBase.Account;
import ir.kitgroup.ordersaleinapplication.DataBase.Invoice;
import ir.kitgroup.ordersaleinapplication.DataBase.InvoiceDetail;
import ir.kitgroup.ordersaleinapplication.DataBase.OrderType;
import ir.kitgroup.ordersaleinapplication.DataBase.Product;
import ir.kitgroup.ordersaleinapplication.DataBase.ProductGroupLevel1;
import ir.kitgroup.ordersaleinapplication.DataBase.ProductGroupLevel2;
import ir.kitgroup.ordersaleinapplication.DataBase.Setting;
import ir.kitgroup.ordersaleinapplication.DataBase.Tables;
import ir.kitgroup.ordersaleinapplication.DataBase.User;

import ir.kitgroup.ordersaleinapplication.R;

import ir.kitgroup.ordersaleinapplication.classes.App;
import ir.kitgroup.ordersaleinapplication.databinding.FragmentSettingBinding;

public class SettingFragment extends Fragment {

    //region Parameter
    private FragmentSettingBinding binding;
    //endregion Parameter

    private  SharedPreferences sharedPreferences;


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {


        binding = FragmentSettingBinding.inflate(getLayoutInflater());
        return binding.getRoot();


    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);






        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());



        binding.btnLogOut.setOnClickListener(v -> {
            if (Account.count(Account.class) > 0)
                Account.deleteAll(Account.class);

            if (Invoice.count(Invoice.class) > 0)
                Invoice.deleteAll(Invoice.class);

            if (InvoiceDetail.count(InvoiceDetail.class) > 0)
                InvoiceDetail.deleteAll(InvoiceDetail.class);

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
                    if(App.mode==1);
                    User.deleteAll(User.class);
            }


            if (Tables.count(Tables.class) > 0)
                Tables.deleteAll(Tables.class);




            sharedPreferences.edit().putBoolean("firstSync", false).apply();
            sharedPreferences.edit().putBoolean("firstSyncSetting", false).apply();


            getFragmentManager().popBackStack();
            getFragmentManager().popBackStack();
            LoginClientFragment userFragment = new LoginClientFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, userFragment).commit();

        });




    binding.lProfile.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, new ProfileFragment(),"ProfileFragment").addToBackStack("ProfileF").commit());

        binding.btnOrderList.setOnClickListener(v -> getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, new OrderListFragment(), "OrderListFragment").addToBackStack("OrderListFMobile").commit());
    }
}

