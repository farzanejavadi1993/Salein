package ir.kitgroup.saleindemo.ui.launcher.homeItem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import ir.kitgroup.saleindemo.R;
import ir.kitgroup.saleindemo.databinding.FragmentMainBinding;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentMainBinding.inflate(getLayoutInflater());
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.navView.getMenu().getItem(3).setEnabled(false);
        binding.navView.setSelectedItemId(R.id.homee);
        binding.navView.getOrCreateBadge(R.id.orders).clearNumber();
        binding.navView.getOrCreateBadge(R.id.orders).setBackgroundColor(getResources().getColor(R.color.red_table));
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_launcher1, new HomeFragment()).commit();
        binding.navView.setOnNavigationItemSelectedListener(item -> {


            switch (item.getItemId()) {

                case R.id.homee:
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_launcher1, new HomeFragment()).commit();
                    return true;


                case R.id.search:

                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_launcher1, new HomeFragment()).commit();
                    return true;


                case R.id.orders:
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_launcher1, new HomeFragment()).commit();
                    return true;

                case R.id.profile:
                    Navigation.findNavController(binding.getRoot()).navigate(R.id.actionGoToMoreFragment);
                    return true;
            }
            return false;
        });
    }


}
