package ir.kitgroup.salein1.Fragments.MobileView;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import ir.kitgroup.salein1.Adapters.OrderListAdapter;

import ir.kitgroup.salein1.DataBase.Invoice;

import ir.kitgroup.salein1.R;
import ir.kitgroup.salein1.databinding.FragmentOrderListBinding;



public class OrderListFragment extends Fragment {

    //region Parameter
    private FragmentOrderListBinding binding;
    //endregion Parameter

    public OrderListAdapter orderListAdapter;
    private final ArrayList<Invoice> list = new ArrayList<>();


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentOrderListBinding.inflate(getLayoutInflater());
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        list.clear();
        List<Invoice> lists=Select.from(Invoice.class).list();

        CollectionUtils.filter(lists,l->l.INV_EXTENDED_AMOUNT!=null);
        list.addAll(lists);
        orderListAdapter = new OrderListAdapter(getContext(), list,2);
        binding.recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recycler.setAdapter(orderListAdapter);
        binding.recycler.setHasFixedSize(false);
        orderListAdapter.setOnClickItemListener((type, status, Inv_GUID) -> {
            Invoice invoice=Select.from(Invoice.class).where("INVUID ='"+Inv_GUID+"'").first();

            Bundle bundle = new Bundle();
            if (!invoice.SendStatus)
            bundle.putString("status", "0");
            bundle.putString("type", "1");
            bundle.putString("Inv_GUID", Inv_GUID);
            bundle.putString("Tbl_GUID", "");
            bundle.putString("Ord_TYPE",String.valueOf(invoice.INV_TYPE_ORDER));

            InVoiceDetailMobileFragment inVoiceDetailFragmentMobile = new InVoiceDetailMobileFragment();
            inVoiceDetailFragmentMobile.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, inVoiceDetailFragmentMobile, "InVoiceDetailFragmentMobile").addToBackStack("InVoiceDetailFMobile").commit();
        });



    }
}
