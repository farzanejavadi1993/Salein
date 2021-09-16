package ir.kitgroup.saleintop.Fragments.MobileView;


import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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


import ir.kitgroup.saleintop.Activities.Classes.LauncherActivity;
import ir.kitgroup.saleintop.classes.App;
import ir.kitgroup.saleintop.DataBase.Account;
import ir.kitgroup.saleintop.DataBase.User;

import ir.kitgroup.saleintop.models.ModelAccount;
import ir.kitgroup.saleintop.models.ModelLog;
import ir.kitgroup.saleintop.R;
import ir.kitgroup.saleintop.Util.Util;
import ir.kitgroup.saleintop.databinding.FragmentConfirmCodeBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ConfirmCodeFragment extends Fragment {

    //region  Parameter
    private FragmentConfirmCodeBinding binding;
    private User user;
    private int fontSize=0;
    private String messageWelcome="";
    private int imageLogo;

    //endregion Parameter

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentConfirmCodeBinding.inflate(getLayoutInflater());

        if (LauncherActivity.screenInches>=7) {
            binding.tvMessage.setTextSize(18);
            fontSize = 14;
        }
        else
            fontSize=12;


        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);

            switch (pInfo.packageName){
                case "ir.kitgroup.salein":
                    imageLogo=R.drawable.salein;
                    break;

                case "ir.kitgroup.saleintop":
                    imageLogo=R.drawable.top;
                    break;


                case "ir.kitgroup.saleinmeat":
                    imageLogo=R.drawable.goosht;
                    break;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return binding.getRoot();

    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Bundle bundle = getArguments();
        assert bundle != null;
        String mobile = bundle.getString("mobile");
        int code = bundle.getInt("code");



        binding.tvEnterCode.setTextSize(fontSize);
        binding.btnLogin.setTextSize(fontSize);

        user = Select.from(User.class).first();


        binding.ivBackFragment.setOnClickListener(v -> getFragmentManager().popBackStack());
        binding.tvMessage.setText(getString(R.string.send_code_part1) + " " + mobile + " " + getString(R.string.send_code_part2));
        if (App.mode == 2) {

            binding.ivLogo.setImageResource(imageLogo);
        }



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
                binding.btnLogin.setEnabled(true);
                binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
            } else {
                binding.btnLogin.setEnabled(false);
                binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
                setEditBackground(R.drawable.rounded_edittext_gray, (EditText) view1);
            }


        });


        binding.btnLogin.setOnClickListener(v -> {
         String codeInput=
                 binding.edtV1.getText().toString()+
                 binding.edtV2.getText().toString()+
                 binding.edtV3.getText().toString()+
                 binding.edtV4.getText().toString()+
                 binding.edtV5.getText().toString();
         if (Integer.parseInt(codeInput)!=code){
             Toast.makeText(getActivity(), "کد وارد شده صحیح نمی باشد", Toast.LENGTH_SHORT).show();
             //return;
         }
                    getInquiryAccount(user.userName, user.passWord, mobile);
                }
        );


    }


    private void getInquiryAccount(String userName, String passWord, String mobile) {

        try {
            Call<String> call = App.api.getInquiryAccount(userName, passWord, mobile, "", "", 1);
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

                        //user is register
                        if (iDs.getAccountList().size() > 0) {
                            Account.deleteAll(Account.class);
                            Account.saveInTx(iDs.getAccountList());
                            getFragmentManager().popBackStack();
                            Bundle bundle = new Bundle();
                            bundle.putString("Ord_TYPE", "");
                            bundle.putString("Tbl_GUID", "");
                            bundle.putString("Inv_GUID", "");
//                            if (LauncherActivity.screenInches>=7){
//                                OrderFragment orderFragment = new OrderFragment();
//                                orderFragment.setArguments(bundle);
//                                FragmentTransaction replaceFragment = Objects.requireNonNull(getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, orderFragment, "MainOrderMobileFragment"));
//                                replaceFragment.commit();
//                            }else {
                                MainOrderMobileFragment mainOrderMobileFragment = new MainOrderMobileFragment();
                                mainOrderMobileFragment.setArguments(bundle);
                                FragmentTransaction replaceFragment = Objects.requireNonNull(getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment"));
                                replaceFragment.commit();
                           // }


                        }

                        //user not register
                        else {
                            getFragmentManager().popBackStack();
                            Bundle bundle = new Bundle();
                            bundle.putString("mobile", mobile);
                            MapFragment mapFragment = new MapFragment();
                            mapFragment.setArguments(bundle);
                            FragmentTransaction addFragment = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().add(R.id.frame_main, mapFragment, "MapFragment");
                            addFragment.commit();
                        }

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

    private void setEditBackground(int drawable, EditText view) {
        view.setBackground(getResources().getDrawable(drawable));
    }

}