package ir.kitgroup.salein.Fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import com.orm.query.Select;
import org.jetbrains.annotations.NotNull;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import es.dmoral.toasty.Toasty;
import ir.kitgroup.salein.Connect.MyViewModel;
import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.DataBase.Company;
import ir.kitgroup.salein.models.Config;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.classes.Util;
import ir.kitgroup.salein.databinding.FragmentConfirmCodeBinding;

@AndroidEntryPoint

public class ConfirmCodeFragment extends Fragment {

    //region  Parameter
    @Inject
    Config config;

    @Inject
    SharedPreferences sharedPreferences;


    private MyViewModel myViewModel;
    private Company company;
    private FragmentConfirmCodeBinding binding;
    private int code;
    private String mobileNumber = "";
    //endregion Parameter


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentConfirmCodeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        company = Select.from(Company.class).first();

        //region Get Bundle And Set Data
        Bundle bundle = getArguments();
        code = bundle.getInt("code");
        mobileNumber = bundle.getString("mobileNumber");
        //endregion Get Bundle And Set Data

        binding.tvMessage.setText(getString(R.string.send_code_part1) + " " + mobileNumber + " " + getString(R.string.send_code_part2));

        //region Configuration Text Size
        int fontSize;
        if (Util.screenSize >= 7) {
            binding.tvMessage.setTextSize(18);
            fontSize = 14;
        } else
            fontSize = 12;
        binding.tvEnterCode.setTextSize(fontSize);
        binding.btnLogin.setTextSize(fontSize);
        //endregion Configuration Text Size

        //region Set Icon And Title
        binding.imageLogo.setImageResource(config.imageIcon);
        //endregion Set Icon And Title

        //region TextWatcher Code
        binding.edtV1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 1) {
                    binding.edtV2.requestFocus();
                }
                String code = binding.edtV1.getText().toString() +
                        binding.edtV2.getText().toString() +
                        binding.edtV3.getText().toString() +
                        binding.edtV4.getText().toString() +
                        binding.edtV5.getText().toString();
                if (code.length() == 5) {
                    binding.btnLogin.setEnabled(true);
                    binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));

                } else {
                    binding.btnLogin.setEnabled(false);
                    binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
                }

            }
        });

        binding.edtV2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 1) {
                    binding.edtV3.requestFocus();
                } else {

                    binding.edtV1.requestFocus();

                }
                String code = binding.edtV1.getText().toString() +
                        binding.edtV2.getText().toString() +
                        binding.edtV3.getText().toString() +
                        binding.edtV4.getText().toString() +
                        binding.edtV5.getText().toString();
                if (code.length() == 5) {
                    binding.btnLogin.setEnabled(true);
                    binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));

                } else {
                    binding.btnLogin.setEnabled(false);
                    binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
                }

            }
        });
        binding.edtV3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 1) {
                    binding.edtV4.requestFocus();
                } else {
                    binding.edtV2.requestFocus();
                }
                String code = binding.edtV1.getText().toString() +
                        binding.edtV2.getText().toString() +
                        binding.edtV3.getText().toString() +
                        binding.edtV4.getText().toString() +
                        binding.edtV5.getText().toString();
                if (code.length() == 5) {
                    binding.btnLogin.setEnabled(true);
                    binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));

                } else {
                    binding.btnLogin.setEnabled(false);
                    binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
                }

            }
        });
        binding.edtV4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 1) {
                    binding.edtV5.requestFocus();
                } else {
                    binding.edtV3.requestFocus();
                }
                String code = binding.edtV1.getText().toString() +
                        binding.edtV2.getText().toString() +
                        binding.edtV3.getText().toString() +
                        binding.edtV4.getText().toString() +
                        binding.edtV5.getText().toString();
                if (code.length() == 5) {
                    binding.btnLogin.setEnabled(true);
                    binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));

                } else {
                    binding.btnLogin.setEnabled(false);
                    binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
                }

            }
        });
        binding.edtV5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 1) {
                    Util.hideKeyBoard(getActivity(), binding.edtV5);
                } else {
                    binding.edtV4.requestFocus();
                }
                String code = binding.edtV1.getText().toString() +
                        binding.edtV2.getText().toString() +
                        binding.edtV3.getText().toString() +
                        binding.edtV4.getText().toString() +
                        binding.edtV5.getText().toString();
                if (code.length() == 5) {
                    binding.btnLogin.setEnabled(true);
                    binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));

                } else {
                    binding.btnLogin.setEnabled(false);
                    binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
                }
            }
        });

        binding.edtV1.setOnFocusChangeListener((view1, b) -> {
            if (b) {
                setEditBackground(R.drawable.rounded_edittext_blue, (EditText) view1);
            } else {
                setEditBackground(R.drawable.rounded_edittext_gray, (EditText) view1);
            }
        });
        binding.edtV2.setOnFocusChangeListener((view1, b) -> {
            if (b) {
                setEditBackground(R.drawable.rounded_edittext_blue, (EditText) view1);
            } else {
                setEditBackground(R.drawable.rounded_edittext_gray, (EditText) view1);
            }
        });
        binding.edtV3.setOnFocusChangeListener((view1, b) -> {
            if (b) {
                setEditBackground(R.drawable.rounded_edittext_blue, (EditText) view1);
            } else {
                setEditBackground(R.drawable.rounded_edittext_gray, (EditText) view1);
            }
        });
        binding.edtV4.setOnFocusChangeListener((view1, b) -> {
            if (b) {
                setEditBackground(R.drawable.rounded_edittext_blue, (EditText) view1);
            } else {
                setEditBackground(R.drawable.rounded_edittext_gray, (EditText) view1);
            }
        });
        binding.edtV5.setOnFocusChangeListener((view1, b) -> {
            if (b) {
                setEditBackground(R.drawable.rounded_edittext_blue, (EditText) view1);

            } else {
                setEditBackground(R.drawable.rounded_edittext_gray, (EditText) view1);
            }
        });

        //endregion TextWatcher Code

        //region Action BtnLogin
        binding.btnLogin.setOnClickListener(v -> {
            String codeInput =
                    binding.edtV1.getText().toString() +
                            binding.edtV2.getText().toString() +
                            binding.edtV3.getText().toString() +
                            binding.edtV4.getText().toString() +
                            binding.edtV5.getText().toString();
            if (Integer.parseInt(codeInput) != code) {
                Toast.makeText(getActivity(), "کد وارد شده صحیح نمی باشد", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
            binding.btnLogin.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);
            myViewModel.getInquiryAccount(company.USER,company.PASS,mobileNumber);
        });
        //endregion Action BtnLogin

        //region Action ivBackFragment
        binding.ivBackFragment.setOnClickListener(v -> getFragmentManager().popBackStack());
        //endregion Action ivBackFragment

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        myViewModel.getResultMessage().observe(getViewLifecycleOwner(), result -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.btnLogin.setEnabled(true);
            if (result == null) return;

            Toasty.warning(requireActivity(), result.getName(), Toast.LENGTH_SHORT, true).show();
            myViewModel.getResultMessage().setValue(null);

        });


        myViewModel.getResultInquiryAccount().observe(getViewLifecycleOwner(), result -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.btnLogin.setEnabled(true);
            if (result == null)
                return;
            sharedPreferences.edit().putBoolean("disableAccount", false).apply();
            getActivity().getSupportFragmentManager().popBackStack();

            //region Account Is Register
            if (result.size() > 0) {
                Account.deleteAll(Account.class);
                Account.saveInTx(result);

                FragmentTransaction addFragment;
                //region Show All Company
                if (company.INSK_ID.equals("ir.kitgroup.salein")) {
                    addFragment = getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new StoriesFragment(), "StoriesFragment");
                }
                //endregion Show All Company


                //region Go To MainOrderFragment Because Account Is Register
                else {
                    Bundle bundleMain = new Bundle();
                    bundleMain.putString("Inv_GUID", "");
                    bundleMain.putString("Tbl_GUID", "");
                    bundleMain.putString("Ord_TYPE", "");
                    MainOrderFragment mainOrderFragment = new MainOrderFragment();
                    mainOrderFragment.setArguments(bundleMain);
                    addFragment = getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mainOrderFragment, "MainOrderFragment");
                }
                //endregion Go To MainOrderFragment Because Account Is Register
                addFragment.commit();
            }
            //endregion Account Is Register


            //region Account Is Not Register
            else {
                Bundle bundleMap = new Bundle();
                bundleMap.putString("mobileNumber",mobileNumber );
                bundleMap.putString("edit_address", "");
                bundleMap.putString("type", "");
                MapFragment mapFragment = new MapFragment();
                mapFragment.setArguments(bundleMap);
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mapFragment, "MapFragment").commit();
            }
            //endregion Account Is Not Register


        });

    }


    //region Method

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setEditBackground(int drawable, EditText view) {
        view.setBackground(getResources().getDrawable(drawable));
    }

    //endregion Method


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
