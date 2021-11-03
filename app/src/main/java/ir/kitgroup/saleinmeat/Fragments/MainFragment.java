package ir.kitgroup.saleinmeat.Fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.saleinmeat.DataBase.InvoiceDetail;
import ir.kitgroup.saleinmeat.R;
import ir.kitgroup.saleinmeat.databinding.FragmentMainBinding;
import ir.kitgroup.saleinmeat.models.Company;

@AndroidEntryPoint
public class MainFragment extends Fragment {

    @Inject
    Company company;


    @Inject
    SharedPreferences sharedPreferences;
    private FragmentMainBinding binding;
    private int counter;


    private String Tbl_GUID;
    private String Tbl_NAME;
    private String Ord_TYPE;
    private String Inv_GUID;
    private String Inv_GUID_ORG;
    private String Acc_NAME = "";
    private String Acc_GUID = "";
    private Boolean Seen = false;

    private String Transport_GUID = "";
    private MainOrderMobileFragment mainOrderMobileFragment;
    private Bundle bundle1;


    @SuppressLint("NonConstantResourceId")
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(getLayoutInflater());


        //region Get Bundle
        Bundle bnd = getArguments();
        assert bnd != null;

        Tbl_GUID = bnd.getString("Tbl_GUID");
        Tbl_NAME = bnd.getString("Tbl_NAME");
        Ord_TYPE = bnd.getString("Ord_TYPE");
        Inv_GUID = bnd.getString("Inv_GUID");
        Inv_GUID_ORG = bnd.getString("Inv_GUID");
        Acc_NAME = bnd.getString("Acc_NAME");
        Acc_GUID = bnd.getString("Acc_GUID");
        Seen = bnd.getBoolean("Seen");
        boolean EDIT = bnd.getBoolean("EDIT");

        //endregion Get Bundle







        Transport_GUID = sharedPreferences.getString("Transport_GUID", "");


        if (company.mode == 1)
            binding.bottomNavigationViewLinear.setVisibility(View.GONE);
        else {

            binding.bottomNavigationViewLinear.setSelectedItemId(R.id.homee);

            binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).clearNumber();

            binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).setBackgroundColor(getActivity().getResources().getColor(R.color.red_table));
            binding.bottomNavigationViewLinear.setOnNavigationItemSelectedListener(item -> {

                binding.bottomNavigationViewLinear.getMenu().getItem(0).setEnabled(false);

                switch (item.getItemId()) {
                    case R.id.homee:

                        mainOrderMobileFragment.setArguments(bundle1);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_launcher_main, mainOrderMobileFragment, "MainOrderMobileFragment").commit();
                        return true;


                    case R.id.orders:
                        Bundle bundle = new Bundle();
                        bundle.putString("type", "2");//go to InVoiceDetailMobileFragment for register order first time
                        bundle.putString("Inv_GUID", Inv_GUID);
                        bundle.putBoolean("Seen", Seen);
                        bundle.putString("Tbl_GUID", Tbl_GUID);
                        bundle.putString("Tbl_NAME", Tbl_NAME);
                        bundle.putString("Ord_TYPE", Ord_TYPE);
                        bundle.putString("Acc_Name", Acc_NAME);
                        bundle.putString("Acc_GUID", Acc_GUID);
                        bundle.putBoolean("EDIT", !Inv_GUID_ORG.equals("") && company.mode == 2);

                        InVoiceDetailMobileFragment inVoiceDetailFragmentMobile = new InVoiceDetailMobileFragment();
                        inVoiceDetailFragmentMobile.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, inVoiceDetailFragmentMobile, "InVoiceDetailFragmentMobile").addToBackStack("InVoiceDetailFMobile").commit();

                        return true;


                    case R.id.profile:
                        binding.bottomNavigationViewLinear.getMenu().getItem(0).setEnabled(true);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_launcher_main, new SettingFragment(), "SettingFragment").commit();
                        return true;
                }


                return false;
            });
      }


        
        
        
        

        //region Create Order
        if (Inv_GUID.equals("")) {
            String name;

                //Client
                name = company.namePackage.split("ir.kitgroup.")[1];
                Inv_GUID = sharedPreferences.getString(name, "");
                if (Inv_GUID.equals("") && !name.equals("saleinOrder")) {
                    Inv_GUID = UUID.randomUUID().toString();
                    sharedPreferences.edit().putString(name, Inv_GUID).apply();
                }else {
                    Inv_GUID = UUID.randomUUID().toString();
                }





            List<InvoiceDetail> invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
            if (invDetails.size() > 0) {
                CollectionUtils.filter(invDetails, i -> !i.PRD_UID.equals(Transport_GUID));
                counter = invDetails.size();
            }


            if (counter == 0)
                binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).clearNumber();
            else
                binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).setNumber(counter);

        } else {
            List<InvoiceDetail> invDetails = Select.from(InvoiceDetail.class).where("INVUID ='" + Inv_GUID + "'").list();
            if (invDetails.size() > 0) {
                if (invDetails.size() > 0) {
                    CollectionUtils.filter(invDetails, i -> !i.PRD_UID.equals(Transport_GUID));
                    counter = invDetails.size();
                }
                counter = invDetails.size();
            }

            if (counter == 0)
                binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).clearNumber();
            else
                binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).setNumber(counter);


            binding.bottomNavigationViewLinear.getMenu().getItem(0).setVisible(false);
        }


        //endregion Create Order


        bundle1 = new Bundle();
        bundle1.putString("Tbl_GUID", Tbl_GUID);
        bundle1.putString("Tbl_NAME", Tbl_NAME);
        bundle1.putString("Ord_TYPE", Ord_TYPE);
        bundle1.putString("Inv_GUID", Inv_GUID);
        bundle1.putString("Acc_GUID", Acc_GUID);
        bundle1.putString("Acc_NAME", Acc_NAME);
        bundle1.putBoolean("EDIT", EDIT);

        mainOrderMobileFragment = new MainOrderMobileFragment();
        mainOrderMobileFragment.setArguments(bundle1);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_launcher_main, mainOrderMobileFragment, "MainOrderMobileFragment").commit();
        return binding.getRoot();

    }


    public void setCounterOrder(int count) {
        counter=count;
        binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).setNumber(count);
    }


    public void setClearCounterOrder() {
        binding.bottomNavigationViewLinear.getOrCreateBadge(R.id.orders).clearNumber();
    }



}
