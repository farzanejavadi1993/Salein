package ir.kitgroup.saleindemo.ui.launcher.homeItem;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.orm.query.Select;

import java.util.UUID;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.saleindemo.DataBase.Company;
import ir.kitgroup.saleindemo.DataBase.InvoiceDetail;
import ir.kitgroup.saleindemo.R;
import ir.kitgroup.saleindemo.databinding.FragmentMainBinding;
import ir.kitgroup.saleindemo.ui.launcher.searchItem.SearchProductFragment;

@AndroidEntryPoint
public class MainFragment extends Fragment {

    @Inject
    SharedPreferences sharedPreferences;
    private FragmentMainBinding binding;
    private Company company;
    MainFragment mainFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint({"NonConstantResourceId", "CommitTransaction"})
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainFragment = this;

        String Inv_GUID = MainFragmentArgs.fromBundle(getArguments()).getInvGUID();

        company = Select.from(Company.class).first();

        //region Create Order

        if (Inv_GUID.equals("")) {
            String name = company.getInskId().split("ir.kitgroup.")[1];
            Inv_GUID = sharedPreferences.getString(name, "");

            if (Inv_GUID.equals("")) {
                Inv_GUID = UUID.randomUUID().toString();
                sharedPreferences.edit().putString(name, Inv_GUID).apply();
            }
        }
        sharedPreferences.edit().putString("Inv_GUID", Inv_GUID).apply();//Save GUID Order Form To Use In App


        binding.navView.setSelectedItemId(R.id.homee);
        binding.navView.getOrCreateBadge(R.id.orders).clearNumber();
        binding.navView.getOrCreateBadge(R.id.orders).setBackgroundColor(getResources().getColor(R.color.red_table));


        binding.navView.setOnNavigationItemSelectedListener(item -> {


            switch (item.getItemId()) {

                case R.id.homee:

                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_launcher1, new HomeFragment(mainFragment)).commit();
                    return true;


                case R.id.search:

                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_launcher1, new SearchProductFragment(mainFragment)).commit();
                    return true;


                case R.id.orders:
                    NavDirections action = MainFragmentDirections.actionGotoInvoiceFragment("", "2", false);
                    Navigation.findNavController(binding.getRoot()).navigate(action);
                    return true;

                case R.id.profile:
                    Navigation.findNavController(binding.getRoot()).navigate(R.id.actionGoToMoreFragment);
                    return true;
            }
            return false;
        });

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_launcher1, new HomeFragment(mainFragment)).commit();


    }


    public void clearNumber() {
        binding.navView.getOrCreateBadge(R.id.orders).clearNumber();
    }

    public void setNumber(int count) {
        binding.navView.getOrCreateBadge(R.id.orders).setNumber(count);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
