package ir.kitgroup.salein1.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import ir.kitgroup.salein1.Adapters.TableListAdapter;
import ir.kitgroup.salein1.DataBase.Invoice;
import ir.kitgroup.salein1.R;



public class BuyListsFragment extends Fragment {

    private RecyclerView tableRecyclerview;
    public  TableListAdapter tableAdapter;
    private ArrayList<Invoice> tableList = new ArrayList<>();


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_list_buys,container,false);

        tableList.clear();
        List<Invoice> list=Select.from(Invoice.class).list();

        CollectionUtils.filter(list,l->l.INV_EXTENDED_AMOUNT!=null);
        tableList.addAll(list);
        tableAdapter = new TableListAdapter(getContext(), null,tableList,2);
        tableRecyclerview=view.findViewById(R.id.recycler_table);
        tableRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        tableRecyclerview.setAdapter(tableAdapter);
        tableRecyclerview.setHasFixedSize(false);
        tableAdapter.setOnClickItemListener(new TableListAdapter.ClickItem() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onRowClick(String Name, boolean Reserve, String Guid) {

                Bundle bundle = new Bundle();
                bundle.putString("type", "1");
                bundle.putString("FGUID", Guid);
                bundle.putString("TGID", "");
                bundle.putString("ACCNM", "");
                bundle.putString("ACCGID", "");
                bundle.putString("OTYPE", "");

                InvoiceDetail invoiceDetailFragment = new InvoiceDetail();
                invoiceDetailFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, invoiceDetailFragment, "InvoiceDetailFragment").addToBackStack("InvoiceDetailF").commit();
            }
        });


        return view;
    }
}
