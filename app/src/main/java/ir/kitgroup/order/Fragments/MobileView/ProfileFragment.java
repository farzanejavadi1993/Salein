package ir.kitgroup.order.Fragments.MobileView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;

import ir.kitgroup.order.DataBase.Account;
import ir.kitgroup.order.Fragments.Client.Register.RegisterFragment;
import ir.kitgroup.order.R;
import ir.kitgroup.order.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    private String type;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(getLayoutInflater());

        Bundle bundle = getArguments();

        try {
            type = bundle.getString("edit");
        } catch (Exception ignore) {

        }


        binding.tvAddress1.setText("");
        binding.tvAddress2.setText("");
        binding.tvMobile.setText("");
        binding.tvName.setText("");

        Account account = Select.from(Account.class).first();

        if (account != null) {
            binding.tvName.setText(account.N);
            binding.tvMobile.setText(account.M);

            if (account.ADR != null && !account.ADR.equals("")) {
                binding.tvAddress1.setText(account.ADR);

            } else {
                binding.tvAddress1.setText("ناموجود");
            }


            if (account.ADR1 != null && !account.ADR1.equals("")) {
                binding.tvAddress2.setText(account.ADR1);

            } else {
                binding.tvAddress2.setText("ناموجود");
            }


            binding.lAdr1.setOnClickListener(v -> {
                Bundle bundle1=new Bundle();
                bundle1.putString("edit_address","2");
                bundle1.putString("mobile","1");
                RegisterFragment registerFragment=new RegisterFragment();
                registerFragment.setArguments(bundle1);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, registerFragment).addToBackStack("RegisterF").commit();
            });

            binding.lAdr2.setOnClickListener(v -> {
                Bundle bundle1=new Bundle();
                bundle1.putString("edit_address","2");
                bundle1.putString("mobile","2");
                RegisterFragment registerFragment=new RegisterFragment();
                registerFragment.setArguments(bundle1);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, registerFragment).addToBackStack("RegisterF").commit();

            });

            binding.ivBackFragment.setOnClickListener(v -> getFragmentManager().popBackStack());

            binding.btnRegisterInformation.setOnClickListener(
                    v -> {
                        if (type != null) {
                            account.M = binding.tvMobile.getText().toString();
                            account.N = binding.tvName.getText().toString();
                            if (!binding.tvAddress1.getText().toString().equals("ناموجود"))
                                account.ADR = binding.tvAddress1.getText().toString();
                            if (!binding.tvAddress2.getText().toString().equals("ناموجود"))
                                account.ADR1 = binding.tvAddress2.getText().toString();
                            account.update();
                            //ارسال به سرور

                            assert getFragmentManager() != null;
                            getFragmentManager().popBackStack();
                            Fragment frg = getActivity().getSupportFragmentManager().findFragmentByTag("PaymentFragment");
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            assert frg != null;
                            ft.detach(frg);
                            ft.attach(frg);
                            ft.commit();

                        } else {
                            Toast.makeText(getActivity(), "در حال حاضر در دسترس نمی باشد.", Toast.LENGTH_SHORT).show();
                        }

                    });

        }
        return binding.getRoot();
    }

}
