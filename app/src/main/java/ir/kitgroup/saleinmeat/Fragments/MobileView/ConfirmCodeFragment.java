package ir.kitgroup.saleinmeat.Fragments.MobileView;


import android.annotation.SuppressLint;
import android.os.Bundle;

import android.os.NetworkOnMainThreadException;
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


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;


import java.lang.reflect.Type;
import java.util.Objects;


import ir.kitgroup.saleinmeat.Activities.Classes.LauncherActivity;
import ir.kitgroup.saleinmeat.classes.App;
import ir.kitgroup.saleinmeat.DataBase.Account;
import ir.kitgroup.saleinmeat.DataBase.User;

import ir.kitgroup.saleinmeat.models.ModelAccount;
import ir.kitgroup.saleinmeat.models.ModelLog;
import ir.kitgroup.saleinmeat.R;
import ir.kitgroup.saleinmeat.Util.Util;
import ir.kitgroup.saleinmeat.databinding.FragmentConfirmCodeBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ConfirmCodeFragment extends Fragment {

    //region  Parameter
    private FragmentConfirmCodeBinding binding;
    private User user;
    private int imageLogo;

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


        user = Select.from(User.class).first();

        //region Get Bundle And Set Data
        Bundle bundle = getArguments();
        assert bundle != null;
        String mobile = bundle.getString("mobile");
        int code = bundle.getInt("code");
        binding.tvMessage.setText(getString(R.string.send_code_part1) + " " + mobile + " " + getString(R.string.send_code_part2));
        //endregion Get Bundle And Set Data


        //region Configuration Text Size
        int fontSize;
        if (LauncherActivity.screenInches >= 7) {
            binding.tvMessage.setTextSize(18);
            fontSize = 14;
        } else
            fontSize = 12;
        binding.tvEnterCode.setTextSize(fontSize);
        binding.btnLogin.setTextSize(fontSize);
        //endregion Configuration Text Size


        //region Set Icon And Title
        switch (LauncherActivity.name) {
            case "ir.kitgroup.salein":
                imageLogo = R.drawable.salein;
                break;

            case "ir.kitgroup.saleintop":
                imageLogo = R.drawable.top;
                break;


            case "ir.kitgroup.saleinmeat":
                imageLogo = R.drawable.goosht;
                break;

            case "ir.kitgroup.saleinnoon":
                imageLogo = R.drawable.noon;
                break;
        }

        if (App.mode == 2) {
            binding.ivLogo.setImageResource(imageLogo);
        }

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
                //return;
            }
            getInquiryAccount(user.userName, user.passWord, mobile);
        });
        //endregion Action BtnLogin


        //region Action ivBackFragment
        binding.ivBackFragment.setOnClickListener(v -> getFragmentManager().popBackStack());
        //endregion Action ivBackFragment


    }



    //region Method
    private void getInquiryAccount(String userName, String passWord, String mobile) {

        try {
            Call<String> call = App.api.getInquiryAccount(userName, passWord, mobile, "", "", 1,1);
            binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
            binding.btnLogin.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelAccount>() {
                    }.getType();
                    ModelAccount iDs;
                    try {
                        iDs = gson.fromJson(response.body(), typeIDs);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "مدل دریافت شده از مشتریان نامعتبر است.", Toast.LENGTH_SHORT).show();
                        binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
                        binding.btnLogin.setEnabled(true);
                        binding.progressBar.setVisibility(View.GONE);
                        return;
                    }


                    assert iDs != null;
                    if (iDs.getAccountList() == null) {
                        Type typeIDs0 = new TypeToken<ModelLog>() {
                        }.getType();
                        ModelLog iDs0 = gson.fromJson(response.body(), typeIDs0);

                        if (iDs0.getLogs() != null) {
                            String description = iDs0.getLogs().get(0).getDescription();
                            Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                        }

                    } else {

                        //region Account Is Register
                        if (iDs.getAccountList().size() > 0) {
                            Account.deleteAll(Account.class);
                            Account.saveInTx(iDs.getAccountList());
                            getFragmentManager().popBackStack();


                            //region Show All Company
                            if (LauncherActivity.name.equals("ir.kitgroup.salein")) {
                                FragmentTransaction replaceFragment = getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, new StoriesFragment(), "StoriesFragment").addToBackStack("StoriesF");
                                replaceFragment.commit();
                            }
                            //endregion Show All Company


                            //region Go To MainOrderFragment Because Account Is Register
                            else {
                                Bundle bundle = new Bundle();
                                bundle.putString("Ord_TYPE", "");
                                bundle.putString("Tbl_GUID", "");
                                bundle.putString("Inv_GUID", "");
                                MainOrderMobileFragment mainOrderMobileFragment = new MainOrderMobileFragment();
                                mainOrderMobileFragment.setArguments(bundle);
                                FragmentTransaction replaceFragment = Objects.requireNonNull(getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment"));
                                replaceFragment.commit();
                            }
                            //endregion Go To MainOrderFragment Because Account Is Register


                        }
                        //endregion Account Is Register


                        //region Account Is Not Register
                        else {
                            getFragmentManager().popBackStack();
                            Bundle bundle = new Bundle();
                            bundle.putString("mobile", mobile);
                            MapFragment mapFragment = new MapFragment();
                            mapFragment.setArguments(bundle);
                            FragmentTransaction addFragment = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().add(R.id.frame_main, mapFragment, "MapFragment");
                            addFragment.commit();
                        }
                        //endregion Account Is Not Register


                        binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
                        binding.btnLogin.setEnabled(true);
                        binding.progressBar.setVisibility(View.GONE);

                    }


                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    Toast.makeText(getContext(), "خطای تایم اوت در دریافت اطلاعات مشتریان." + t.toString(), Toast.LENGTH_SHORT).show();
                    binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
                    binding.btnLogin.setEnabled(true);
                    binding.progressBar.setVisibility(View.GONE);

                }
            });


        } catch (NetworkOnMainThreadException ex) {
            Toast.makeText(getContext(), "خطا در دریافت اطلاعات مشتریان." + ex.toString(), Toast.LENGTH_SHORT).show();
            binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.btnLogin.setEnabled(true);
            binding.progressBar.setVisibility(View.GONE);
        }


    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setEditBackground(int drawable, EditText view) {
        view.setBackground(getResources().getDrawable(drawable));
    }
    //endregion Method


}
