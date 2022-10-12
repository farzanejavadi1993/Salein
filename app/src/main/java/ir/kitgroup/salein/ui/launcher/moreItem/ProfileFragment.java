package ir.kitgroup.salein.ui.launcher.moreItem;

import android.graphics.PorterDuff;
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


import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.databinding.FragmentProfileBinding;

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





        binding.ivBackFragment.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);



       Account user = Select.from(Account.class).first();
        if (user != null) {
            binding.txtName.setText(user.getN());
            binding.tvPhone.setText(user.getM());

            if (user.getAdr() != null && !user.getAdr().equals("")) {
                binding.cardAddress1.setVisibility(View.VISIBLE);
                binding.tvAddress1.setText(user.getAdr());

            }



            if (user.getAdr2() != null && !user.getAdr2().equals("")){
                binding.cardAddress2.setVisibility(View.VISIBLE);
                binding.tvAddress2.setText(user.getAdr2());
            }





          binding.cardAddress1.setOnClickListener(view1 -> {
              NavDirections action = ProfileFragmentDirections.actionGoToRegisterFragment("ProfileFragment",user.getM(),1);
              Navigation.findNavController(binding.getRoot()).navigate(action);
          });

            binding.cardAddress2.setOnClickListener(view1 -> {
                NavDirections action = ProfileFragmentDirections.actionGoToRegisterFragment("ProfileFragment",user.getM(),2);
                Navigation.findNavController(binding.getRoot()).navigate(action);
            });

            binding.ivBackFragment.setOnClickListener(v -> Navigation.findNavController(binding.getRoot()).popBackStack());
        }
    }
}
