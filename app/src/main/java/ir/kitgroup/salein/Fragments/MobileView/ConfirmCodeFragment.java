package ir.kitgroup.salein.Fragments.MobileView;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.os.CountDownTimer;
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
import java.util.Random;


import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.salein.Activities.Classes.LauncherActivity;
import ir.kitgroup.salein.classes.App;
import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.DataBase.User;

import ir.kitgroup.salein.models.Company;
import ir.kitgroup.salein.models.ModelAccount;
import ir.kitgroup.salein.models.ModelLog;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.Util.Util;
import ir.kitgroup.salein.databinding.FragmentConfirmCodeBinding;

@AndroidEntryPoint

public class ConfirmCodeFragment extends Fragment {

    @Inject
    Double ScreenSize;


    @Inject
    Company company;
    //region  Parameter
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private FragmentConfirmCodeBinding binding;
    private User user;
    private int imageLogo;
    private int code;
    private CountDownTimer countDownTimer;
    private long timeInLeftMillisSecond=60000;
    boolean endTime=false;


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
         code = bundle.getInt("code");
        binding.tvMessage.setText(getString(R.string.send_code_part1) + " " + mobile + " " + getString(R.string.send_code_part2));
        //endregion Get Bundle And Set Data


        //region Configuration Text Size
        int fontSize;
        if (ScreenSize  >= 7) {
            binding.tvMessage.setTextSize(18);
            fontSize = 14;
        } else
            fontSize = 12;
        binding.tvEnterCode.setTextSize(fontSize);
        binding.btnLogin.setTextSize(fontSize);
        //endregion Configuration Text Size


        //region Set Icon And Title
        switch (company.name) {
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

        if (company.mode  == 2) {
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
                else {

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
                }
                else {

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
                }
                else {

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
                }else {

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
            getInquiryAccount1(user.userName, user.passWord, mobile);
        });
        //endregion Action BtnLogin


        //region Action ivBackFragment
        binding.ivBackFragment.setOnClickListener(v -> getFragmentManager().popBackStack());
        //endregion Action ivBackFragment



        //startTimer();
        binding.resendCode.setOnClickListener(v -> {
            code= new Random(System.nanoTime()).nextInt(89000) + 10000;
           // login(mobile, String.valueOf(code));
        });
    }



    //region Method







//    private void login(String mobile, String message) {
//
//
//        try {
//
//            binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
//            binding.btnLogin.setEnabled(false);
//            binding.progressBar.setVisibility(View.VISIBLE);
//
//
//            Call<String> call = App.api.getSmsLogin(user.userName, user.passWord, message, mobile,2);
//            call.enqueue(new Callback<String>() {
//                @Override
//                public void onResponse(Call<String> call, Response<String> response) {
//
//                    binding.progressBar.setVisibility(View.GONE);
//                    binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
//                    binding.btnLogin.setEnabled(true);
//
//                }
//
//
//                @Override
//                public void onFailure(Call<String> call, Throwable t) {
//                    Toast.makeText(getActivity(), "خطا در ارسال پیامک", Toast.LENGTH_SHORT).show();
//                    binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
//                    binding.btnLogin.setEnabled(true);
//                    binding.progressBar.setVisibility(View.GONE);
//
//
//                }
//            });
//
//
//        } catch (NetworkOnMainThreadException ex) {
//            Toast.makeText(getActivity(), "خطا در ارسال پیامک", Toast.LENGTH_SHORT).show();
//            binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
//            binding.btnLogin.setEnabled(true);
//            binding.progressBar.setVisibility(View.GONE);
//
//
//        }
//
//
//    }



    @SuppressLint("UseCompatLoadingForDrawables")
    private void setEditBackground(int drawable, EditText view) {
        view.setBackground(getResources().getDrawable(drawable));
    }


    private void startTimer(){
        countDownTimer=new CountDownTimer(timeInLeftMillisSecond,1000) {
            @Override
            public void onTick(long l) {

                timeInLeftMillisSecond=l;
                updateTime();
            }

            @Override
            public void onFinish() {

            }
        }.start();

    }


    private void stopTimer(){
        countDownTimer.cancel();
    }

    private void updateTime(){

        int minute=(int) timeInLeftMillisSecond/6000;
        int second=(int) timeInLeftMillisSecond%6000/1000;
        String text=""+minute;
        text +=":";
        if (second<10)
            text +="0";

        text +=second;
        binding.resendCode.setText(" ارسال مجدد کد تا "+text);
    }
    //endregion Method


    @Override
    public void onDestroy() {
        super.onDestroy();
      //  stopTimer();
    }



    @SuppressLint("SetTextI18n")
    private void getInquiryAccount1(String userName, String passWord, String mobile) {

        if (!isNetworkAvailable(getActivity())){

            Toast.makeText(getActivity(), "خطا در اتصال به اینترنت", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
            binding.btnLogin.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);
            compositeDisposable.add(
                    App.api.getInquiryAccount1(userName, passWord, mobile, "", "", 1, 1)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                Gson gson = new Gson();
                                Type typeIDs = new TypeToken<ModelAccount>() {
                                }.getType();
                                ModelAccount iDs;
                                try {
                                    iDs = gson.fromJson(jsonElement, typeIDs);
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
                                    ModelLog iDs0 = gson.fromJson(jsonElement, typeIDs0);

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
                                        if (company.name.equals("ir.kitgroup.salein")) {
                                            FragmentTransaction replaceFragment = getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, new StoriesFragment(), "StoriesFragment");
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
                                            FragmentTransaction replaceFragment = requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_main, mainOrderMobileFragment, "MainOrderMobileFragment");
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
                                        FragmentTransaction addFragment = requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, mapFragment, "MapFragment");
                                        addFragment.commit();
                                    }
                                    //endregion Account Is Not Register


                                    binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
                                    binding.btnLogin.setEnabled(true);
                                    binding.progressBar.setVisibility(View.GONE);

                                }



                            }, throwable -> {

                                Toast.makeText(getContext(), "خطای تایم اوت در دریافت اطلاعات مشتریان." , Toast.LENGTH_SHORT).show();
                                binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
                                binding.btnLogin.setEnabled(true);
                                binding.progressBar.setVisibility(View.GONE);


                            })
            );
        } catch (Exception e) {
            Toast.makeText(getContext(), "خطا در دریافت اطلاعات مشتریان." , Toast.LENGTH_SHORT).show();
            binding.btnLogin.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.btnLogin.setEnabled(true);
            binding.progressBar.setVisibility(View.GONE);

        }

    }


    private   boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)  activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
