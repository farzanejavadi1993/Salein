package ir.kitgroup.saleindemo.ui.launcher.moreItem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.orm.query.Select;
import org.jetbrains.annotations.NotNull;

import ir.kitgroup.saleindemo.DataBase.Users;
import ir.kitgroup.saleindemo.R;
import ir.kitgroup.saleindemo.databinding.FragmentProfileBinding;
import ir.kitgroup.saleindemo.ui.map.MapFragment;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);







        Users user = Select.from(Users.class).first();
        if (user != null) {
            binding.txtName.setText(user.N);
            binding.tvMobile.setText(user.M);

            if (user.ADR != null && !user.ADR.equals(""))
                binding.txtAddress1.setText(user.ADR);

            else
                binding.txtAddress1.setText("ناموجود");


            if (user.ADR2 != null && !user.ADR2.equals(""))
                binding.txtAddress2.setText(user.ADR2);
          else
                binding.txtAddress2.setText("ناموجود");




          binding.txtAddress1.setOnClickListener(view1 -> {
              NavDirections action = ProfileFragmentDirections.actionGoToRegisterFragment("ProfileFragment",user.M,1);
              Navigation.findNavController(binding.getRoot()).navigate(action);
          });

            binding.txtAddress2.setOnClickListener(view1 -> {
                NavDirections action = ProfileFragmentDirections.actionGoToRegisterFragment("ProfileFragment",user.M,2);
                Navigation.findNavController(binding.getRoot()).navigate(action);
            });

            binding.ivBackFragment.setOnClickListener(v -> Navigation.findNavController(binding.getRoot()).popBackStack());
        }
    }
}
