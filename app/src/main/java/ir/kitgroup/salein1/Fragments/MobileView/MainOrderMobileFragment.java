package ir.kitgroup.salein1.Fragments.MobileView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import ir.kitgroup.salein1.Classes.App;
import ir.kitgroup.salein1.DataBase.Invoice;

import ir.kitgroup.salein1.Fragments.BuyListsFragment;
import ir.kitgroup.salein1.Fragments.ProfileFragment;
import ir.kitgroup.salein1.R;

import ir.kitgroup.salein1.databinding.FragmentMobileOrderMainBinding;

public class MainOrderMobileFragment extends Fragment {

    //region Parameter
    private FragmentMobileOrderMainBinding binding;
    //endregion Parameter
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMobileOrderMainBinding.inflate(getLayoutInflater());
        return binding.getRoot();


    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Bundle bnd = getArguments();
        assert bnd != null;
        String Ord_TYPE = bnd.getString("Ord_TYPE");
        String Tbl_GUID = bnd.getString("Tbl_GUID");


        Bundle bundle = new Bundle();
        bundle.putString("Tbl_GUID", Tbl_GUID);
        bundle.putString("Ord_TYPE", Ord_TYPE);
        bundle.putString("Inv_GUID", "");
        OrderFragmentMobile orderFragmentMobile = new OrderFragmentMobile();
        orderFragmentMobile.setArguments(bundle);
        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.frame_mobile, orderFragmentMobile, "OrderFragment").commit();




        List<Invoice> invoiceL = Select.from(Invoice.class).list();
        CollectionUtils.filter(invoiceL, l -> l.INV_EXTENDED_AMOUNT != null);
        int size = invoiceL.size();
        binding.lItemBuy.setBadgeText(String.valueOf(size));

        if (App.mode != 2) {
            binding.bottomNavigationViewLinear.setVisibility(View.GONE);
        }


        binding.bottomNavigationViewLinear.setNavigationChangeListener((view12, position) -> {
            if (view12.getId() == R.id.l_item_profile) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_mobile, new ProfileFragment(), "ProfileFragment").commit();

            } else if (view12.getId() == R.id.l_item_home) {
                Toast.makeText(getActivity(), "backstack any fragment", Toast.LENGTH_SHORT).show();


            } else if (view12.getId() == R.id.l_item_buy) {

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_mobile, new BuyListsFragment(), "BuyListFragment").commit();

            }

        });
    }
}
