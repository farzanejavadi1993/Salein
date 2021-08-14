/*
package ir.kitgroup.salein1.Fragments;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ir.kitgroup.salein1.Adapters.AccountAdapter;
import ir.kitgroup.salein1.Adapters.TypeOrderAdapter;
import ir.kitgroup.salein1.Classes.App;
import ir.kitgroup.salein1.Classes.CustomProgress;
import ir.kitgroup.salein1.DataBase.Account;
import ir.kitgroup.salein1.DataBase.OrderType;
import ir.kitgroup.salein1.DataBase.Setting;
import ir.kitgroup.salein1.DataBase.User;
import ir.kitgroup.salein1.MainActivity;
import ir.kitgroup.salein1.Models.ModelAccount;
import ir.kitgroup.salein1.Models.ModelLog;
import ir.kitgroup.salein1.Models.ModelTypeOrder;
import ir.kitgroup.salein1.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchAccountFragment extends Fragment {


    //region Variable DialogAddAccount

    private Dialog dialogAddAccount;
    private EditText edtNameUser;
    private EditText edtAddressUser;
    private EditText edtMobileUser;
    private RadioButton radioMan;
    private RadioButton radioWoman;
    private MaterialButton btnRegisterAccount;
    private ImageView imgCloseAddDialog;
    //endregion Variable DialogAddAccount


    private User user;
    private String userName;
    private String passWord;
    private CustomProgress customProgress;
    private ArrayList<Account> accountList = new ArrayList<>();
    private ArrayList<OrderType> orderTypeList = new ArrayList<>();

    private TextView txtErrorAccount;
    private RecyclerView recyclerAccount;
    private AccountAdapter accountAdapter;
    private TypeOrderAdapter typeOrderAdapter;
    private RecyclerView recyclerTypeOrder;
    private RelativeLayout btnSync;
    private RelativeLayout btnAddAccount;
    private ImageView ivAddAccount;
    private int fontSize = 0;
    private View view;
    private TextWatcher textWatcher;
    private EditText edtSearchAccount;

    private String TGID;
    private String GuidAccount = "";
    private String nameCustomer = "";
    private Integer CodeTypeOrder = 0;
    private MaterialButton btnOrder;
    private int gender;
    private GridLayoutManager gridLayoutManager;


    private List<Account> accountsList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_search_account_fragment, container, false);
        if (MainActivity.screenInches >= 7) {
            fontSize = 13;
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            fontSize = 12;
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }


        Bundle bundle=getArguments();
        TGID =bundle.getString("TGID");

        //region Cast DialogAddAcount
        dialogAddAccount = new Dialog(getActivity());
        dialogAddAccount.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddAccount.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAddAccount.setContentView(R.layout.dialog_add_account);
        dialogAddAccount.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dialogAddAccount.setCancelable(false);


        imgCloseAddDialog = dialogAddAccount.findViewById(R.id.iv_close_add_dialog);
        edtNameUser = dialogAddAccount.findViewById(R.id.edt_name_account);
        edtNameUser.setTextSize(fontSize);
        edtMobileUser = dialogAddAccount.findViewById(R.id.edt_mobile_account);
        edtMobileUser.setTextSize(fontSize);
        edtAddressUser = dialogAddAccount.findViewById(R.id.edt_address_account);
        edtAddressUser.setTextSize(fontSize);
        radioMan = dialogAddAccount.findViewById(R.id.radioMan);
        radioMan.setTextSize(fontSize);
        radioWoman = dialogAddAccount.findViewById(R.id.radioWoman);
        radioWoman.setTextSize(fontSize);
        btnRegisterAccount = dialogAddAccount.findViewById(R.id.btn_register_account);
        btnRegisterAccount.setTextSize(fontSize);
        radioMan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gender = 0;
                }
            }
        });
        radioWoman.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gender = 1;
                }
            }
        });

        imgCloseAddDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddAccount.dismiss();
            }
        });


        btnRegisterAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtNameUser.getText().toString().equals("") || edtMobileUser.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "لطفا فیلد نام مشتری و شماره موبایل مشتری را پر کنید.", Toast.LENGTH_SHORT).show();
                } else if (!edtMobileUser.getText().toString().equals("1") && (edtMobileUser.getText().toString().length() < 11 || edtMobileUser.getText().toString().length() > 11)) {
                    Toast.makeText(getActivity(), "شماره موبایل صحیح نمی باشد.", Toast.LENGTH_SHORT).show();
                } else {
                    accountsList.clear();
                    Account account = new Account();
                    account.I = UUID.randomUUID().toString();
                    account.N = edtNameUser.getText().toString();
                    account.M = edtMobileUser.getText().toString();
                    account.ADR = edtAddressUser.getText().toString();
                    account.S = String.valueOf(gender);
                    accountsList.add(account);
                    addAccount(userName, passWord, accountsList);

                }
            }
        });
        //endregion Cast DialogAddAcount


        userName = Select.from(User.class).list().get(0).userName;
        passWord = Select.from(User.class).list().get(0).passWord;

        customProgress = CustomProgress.getInstance();
        //  txtErrorAccount = view.findViewById(R.id.tv_error);
        edtSearchAccount = view.findViewById(R.id.edt_search_account);
        edtSearchAccount.setTextSize(fontSize);

        btnSync = view.findViewById(R.id.btn_sync_account);
        btnAddAccount = view.findViewById(R.id.btn_add_account);
        ivAddAccount = view.findViewById(R.id.iv_add_account);
        final int newColor = getActivity().getResources().getColor(R.color.black);
        ivAddAccount.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);
        btnOrder = view.findViewById(R.id.btn_order);
        btnOrder.setTextSize(fontSize);
        edtSearchAccount.setTextSize(fontSize);
        edtSearchAccount.setTextSize(fontSize);
        // txtErrorAccount.setTextSize(fontSize);


        recyclerAccount = view.findViewById(R.id.recyclerView_name_customer);
        accountAdapter = new AccountAdapter(getActivity(), accountList);
        recyclerAccount.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerAccount.setAdapter(accountAdapter);
        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearchAccount.removeTextChangedListener(textWatcher);
                edtSearchAccount.setText("");
                GuidAccount = "";
                edtSearchAccount.addTextChangedListener(textWatcher);
                accountAdapter.nullArray();
                getAccount(userName, passWord);
            }
        });

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                GuidAccount = "";
                nameCustomer = "";
                if (s.toString().isEmpty()) {
                    recyclerAccount.setVisibility(View.GONE);
              edtSearchAccount.setHint("جستجوی مشتری(موبایل/نام)");
                } else {
                    recyclerAccount.setVisibility(View.VISIBLE);
                    Search(LauncherFragment.toEnglishNumber(s.toString()));
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        edtSearchAccount.addTextChangedListener(textWatcher);


        accountAdapter.setOnClickItemListener((GUID, name) -> {

            GuidAccount = GUID;
            nameCustomer = name;
            edtSearchAccount.removeTextChangedListener(textWatcher);
            edtSearchAccount.setText(name);
            recyclerAccount.setVisibility(View.GONE);
            edtSearchAccount.addTextChangedListener(textWatcher);


        });


*/
/*
        List<Setting> setting = Select.from(Setting.class).list();
        if (setting .size()>0)
            GuidAccount = setting.get(0).DEFAULT_CUSTOMER;
*//*



      */
/*  recyclerTypeOrder = view.findViewById(R.id.recycler_type_order);
        typeOrderAdapter = new TypeOrderAdapter(getActivity(), orderTypeList);*//*



//        if (MainActivity.screenInches >= 7) {
//            gridLayoutManager = new GridLayoutManager(getActivity(), 5) {
//                @Override
//                protected boolean isLayoutRTL() {
//                    return true;
//                }
//            };
//        } else {
//            gridLayoutManager = new GridLayoutManager(getActivity(), 3) {
//                @Override
//                protected boolean isLayoutRTL() {
//                    return true;
//                }
//            };
//        }


      */
/*  recyclerTypeOrder.setLayoutManager(gridLayoutManager);
        recyclerTypeOrder.setAdapter(typeOrderAdapter);
        typeOrderAdapter.setOnClickItemListener(new TypeOrderAdapter.ClickItem() {
            @Override
            public void onRowClick(Integer code, Integer ty) {

                CodeTypeOrder = code;

            }
        });*//*



*/
/*        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            *//*
*/
/*    OrderType ordT = Select.from(OrderType.class).where("c ='" + CodeTypeOrder + "'").first();
                if (CodeTypeOrder == 0) {
                    Toast.makeText(getActivity(), "نوع سفارش را تعیین کنید.", Toast.LENGTH_SHORT).show();
                } else if (GuidAccount.equals("")) {
                    Toast.makeText(getActivity(), "مشتری مورد نظر خود را انتخاب کنید.", Toast.LENGTH_SHORT).show();
                } else if (TGID.equals("")
                        && ordT != null
                        && (ordT.getTy() == 1)

                ) {
                    Toast.makeText(getActivity(), "شماره میز را وارد کنید.", Toast.LENGTH_SHORT).show();
                } else {

                    getFragmentManager().popBackStack();
                    Bundle bundle = new Bundle();
                    bundle.putString("TGID", TGID);
                    bundle.putString("accGuid", GuidAccount);
                    bundle.putString("accName", nameCustomer);
                    bundle.putString("orderType", String.valueOf(CodeTypeOrder));
                    bundle.putString("factorGuid", "");
                    OrderFragment orderFragment = new OrderFragment();
                    orderFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_main, orderFragment, "OrderFragment").addToBackStack("OrderF").commit();

                }*//*
*/
/*

               // getFragmentManager().popBackStack();

            }
        });*//*


        btnAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtNameUser.setText("");
                edtAddressUser.setText("");
                edtMobileUser.setText("");
                dialogAddAccount.show();
            }
        });


        getAccount(userName, passWord);
        return view;
    }



    void getAccount(String user, String pass) {

        try {

            customProgress.showProgress(getActivity(), "در حال بارگزاری مشتریان...", false);
            Call<String> call = App.api.getAccount("saleinkit_api", user, pass);

            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelAccount>() {
                    }.getType();
                    ModelAccount iDs = gson.fromJson(response.body(), typeIDs);
                    if (iDs.getAccountList() == null) {
                        Type typeIDs0 = new TypeToken<ModelLog>() {
                        }.getType();
                        ModelLog iDs0 = gson.fromJson(response.body(), typeIDs0);

                        if (iDs0.getLogs() != null) {

                            String description = iDs0.getLogs().get(0).getDescription();
                            Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getActivity(), "خطایی رخ داده است.", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        accountList.clear();

                        accountList.addAll(iDs.getAccountList());
                        LauncherFragment.AllAcount.clear();
                        LauncherFragment.AllAcount.addAll(accountList);
                        if (GuidAccount != null && !GuidAccount.equals("")) {
                            ArrayList<Account> result = new ArrayList<>();
                            result.addAll(accountList);
                            CollectionUtils.filter(result, r -> r.I.equals(GuidAccount));
                            if (result.size() > 0) {

                                edtSearchAccount.removeTextChangedListener(textWatcher);
                                edtSearchAccount.setText("مشتری پیش فرض "+"("+result.get(0).N+")");
                                edtSearchAccount.addTextChangedListener(textWatcher);
                            } else {
                                GuidAccount = "";
                            }
                        }

                        accountAdapter.notifyDataSetChanged();
                        recyclerAccount.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "بارگیری موفق", Toast.LENGTH_SHORT).show();

                    }

                    customProgress.hideProgress();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_SHORT).show();
                    customProgress.hideProgress();
                }
            });


        } catch (NetworkOnMainThreadException EX) {

            Toast.makeText(getActivity(), EX.toString(), Toast.LENGTH_SHORT).show();
            customProgress.hideProgress();
        }

    }

    public void Search(String search) {


        if (recyclerAccount.getAdapter() != null) {
            ((Filterable) recyclerAccount.getAdapter()).getFilter().filter(search);
        }

    }


    private class JsonObject {

        public List<Account> Account;

    }

    private void addAccount(String userName, String pass, List<Account> accounts) {


        try {
            JsonObject jsonObject = new JsonObject();
            jsonObject.Account = accounts;


            Gson gson = new Gson();
            Type typeJsonObject = new TypeToken<JsonObject>() {
            }.getType();

            Call<String> call = App.api.addAccount(userName, pass, gson.toJson(jsonObject, typeJsonObject));
            customProgress.showProgress(getContext(), "در حال ثبت مشتری در سرور...", false);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    Gson gson = new Gson();
                    Type typeIDs = new TypeToken<ModelLog>() {
                    }.getType();
                    ModelLog iDs = gson.fromJson(response.body(), typeIDs);

                    int message = iDs.getLogs().get(0).getMessage();
                    String description = iDs.getLogs().get(0).getDescription();
                    if (message == 1) {
                        Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                        dialogAddAccount.dismiss();
                        customProgress.hideProgress();
                        getAccount(userName, pass);
                    } else if (message == 2) {
                        customProgress.hideProgress();
                        Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                    } else if (message == 3) {
                        customProgress.hideProgress();
                        Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                    }


                }


                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(getContext(), "خطا در دریافت اطلاعات" + t.toString(), Toast.LENGTH_SHORT).show();
                    customProgress.hideProgress();

                }
            });


        } catch (NetworkOnMainThreadException ex) {

            Toast.makeText(getContext(), "خطا در دریافت اطلاعات" + ex.toString(), Toast.LENGTH_SHORT).show();
            customProgress.hideProgress();
        }


    }
}*/
