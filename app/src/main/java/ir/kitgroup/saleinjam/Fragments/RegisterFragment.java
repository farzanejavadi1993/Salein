package ir.kitgroup.saleinjam.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;
import org.jetbrains.annotations.NotNull;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.saleinjam.Connect.API;
import ir.kitgroup.saleinjam.DataBase.Account;
import ir.kitgroup.saleinjam.classes.ConfigRetrofit;
import ir.kitgroup.saleinjam.classes.Util;
import ir.kitgroup.saleinjam.DataBase.Company;
import ir.kitgroup.saleinjam.models.ModelLog;
import ir.kitgroup.saleinjam.R;
import ir.kitgroup.saleinjam.databinding.FragmentRegisterBinding;
import ir.kitgroup.saleinjam.models.ModelSetting;
import ir.kitgroup.saleinjam.models.Setting;


public class RegisterFragment extends Fragment {
    private FragmentRegisterBinding binding;
    private Company company;
    private API api;
    private CompositeDisposable compositeDisposable;
    private final List<Account> accountsList = new ArrayList<>();
    private int gender = 0;
    private boolean ACCSTP = true;
    //endregion Parameter
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        company = Select.from(Company.class).first();
        api = ConfigRetrofit.getRetrofit("http://" + company.IP1 + "/api/REST/", false, 30).create(API.class);
        compositeDisposable = new CompositeDisposable();

        Bundle bundle = getArguments();
        String mobileNumber = bundle.getString("mobileNumber");
        String address2 = bundle.getString("address2");
        double latitude = bundle.getDouble("lat");
        double longitude = bundle.getDouble("lng");

        binding.edtAddressCustomerComplete.setText(address2);
        binding.edtNumberPhoneCustomer.setText(mobileNumber);

        //region Configuration Text Size
        int fontSize;
        if (Util.screenSize >= 7) {
            fontSize = 14;
            binding.textView2.setTextSize(15);
        } else
            fontSize = 12;

        binding.tvNameCustomer.setTextSize(fontSize);
        binding.edtFLNameCustomer.setTextSize(fontSize);
        binding.tvPhoneNumberCustomer.setTextSize(fontSize);
        binding.edtNumberPhoneCustomer.setTextSize(fontSize);
        binding.tvAddressCustomer.setTextSize(fontSize);
        binding.greentv.setTextSize(fontSize);
        binding.edtAddressCustomerComplete.setTextSize(fontSize);
        binding.tvPlaqueCustomer.setTextSize(fontSize);
        binding.edtPlaqueCustomer.setTextSize(fontSize);
        binding.radioMan.setTextSize(fontSize);
        binding.radioWoman.setTextSize(fontSize);
        binding.btnRegisterInformation.setTextSize(fontSize);
        //endregion Configuration Text Size

        //region Action RadioButton
        binding.radioMan.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                gender = 0;
            }
        });
        binding.radioWoman.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                gender = 1;
            }
        });
        //endregion Action RadioButton

        //region Action btnRegisterInformation
        binding.btnRegisterInformation.setOnClickListener(v -> {
            if (binding.edtFLNameCustomer.getText().toString().isEmpty() ||
                            binding.edtNumberPhoneCustomer.getText().toString().isEmpty() ||
                            binding.edtAddressCustomerComplete.getText().toString().isEmpty() ||
                            binding.edtPlaqueCustomer.getText().toString().isEmpty()
            ) {
                Toast.makeText(getActivity(), "لطفا تمام فیلد ها را پر کنید", Toast.LENGTH_SHORT).show();
                return;
            }
            Account account = new Account();
            account.I = UUID.randomUUID().toString();
            account.N = binding.edtFLNameCustomer.getText().toString();
            account.M = binding.edtNumberPhoneCustomer.getText().toString();
            account.PSW = binding.edtNumberPhoneCustomer.getText().toString();
            account.ADR = binding.edtAddressCustomerComplete.getText().toString() + " پلاک " + binding.edtPlaqueCustomer.getText().toString();
            account.LAT = String.valueOf(latitude);
            account.LNG = String.valueOf(longitude);
            account.S = String.valueOf(gender);
            account.PC = binding.edtCodeIntroduction.getText().toString();
            account.STAPP = ACCSTP;
            accountsList.clear();
            accountsList.add(account);
            getSetting();
        });
        //endregion Action btnRegisterInformation
    }
    //region Method
    private static class JsonObjectAccount {
        public List<Account> Account;
    }
    private void addAccount(String userName, String pass, List<Account> accounts) {
        if (!isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), "خطا در اتصال به اینترنت", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.bottom_background_inActive_color));
            binding.btnRegisterInformation.setEnabled(false);
            binding.progressBar.setVisibility(View.VISIBLE);

            JsonObjectAccount jsonObjectAcc = new JsonObjectAccount();
            jsonObjectAcc.Account = accounts;

            Gson gson = new Gson();
            Type typeJsonObject = new TypeToken<JsonObjectAccount>() {
            }.getType();
            compositeDisposable.add(
                    api.addAccount(userName, pass, gson.toJson(jsonObjectAcc, typeJsonObject), "")
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                Gson gson1 = new Gson();
                                Type typeIDs = new TypeToken<ModelLog>() {
                                }.getType();
                                ModelLog iDs = gson1.fromJson(jsonElement, typeIDs);

                                assert iDs != null;
                                int message = iDs.getLogs().get(0).getMessage();
                                String description = iDs.getLogs().get(0).getDescription();
                                Toast.makeText(getActivity(), description, Toast.LENGTH_SHORT).show();
                                if (message == 1) {

                                    Account.deleteAll(Account.class);
                                    Account.saveInTx(accountsList);
                                    accountsList.clear();

                                    getActivity().getSupportFragmentManager().popBackStack();
                                    //region Show All Company
                                    if (company.INSK_ID.equals("ir.kitgroup.salein")) {
                                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, new StoriesFragment(), "StoriesFragment").commit();
                                    }
                                    //endregion Show All Company

                                    //region Go To MainOrderFragment Because Account Is Register
                                    else {
                                        Bundle bundleMainOrder = new Bundle();
                                        bundleMainOrder.putString("Inv_GUID", "");
                                        bundleMainOrder.putString("Tbl_GUID", "");
                                        bundleMainOrder.putString("Ord_TYPE", "");

                                        MainOrderFragment mainOrderFragment = new MainOrderFragment();
                                        mainOrderFragment.setArguments(bundleMainOrder);
                                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame_launcher, mainOrderFragment, "MainOrderFragment").commit();
                                    }
                                    //endregion Go To MainOrderFragment Because Account Is Register
                                }
                                binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
                                binding.btnRegisterInformation.setEnabled(true);
                                binding.progressBar.setVisibility(View.GONE);

                            }, throwable -> {
                                Toast.makeText(getContext(), "خطای تایم اوت در ثبت مشتری", Toast.LENGTH_SHORT).show();
                                binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
                                binding.btnRegisterInformation.setEnabled(true);
                                binding.progressBar.setVisibility(View.GONE);
                            })
            );
        } catch (Exception e) {
            Toast.makeText(getContext(), "خطا در ثبت مشتری", Toast.LENGTH_SHORT).show();
            binding.btnRegisterInformation.setBackgroundColor(getResources().getColor(R.color.purple_700));
            binding.btnRegisterInformation.setEnabled(true);
            binding.progressBar.setVisibility(View.GONE);
        }
    }
    //endregion Method
    private boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        compositeDisposable.dispose();
        binding = null;
    }
    @Override
    public void onStop() {
        super.onStop();
        compositeDisposable.clear();
    }
    private void getSetting() {
        try {
            compositeDisposable.add(
                    api.getSetting1(company.USER, company.PASS)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(disposable -> {
                            })
                            .subscribe(jsonElement -> {
                                Gson gson = new Gson();
                                Type typeIDs = new TypeToken<ModelSetting>() {
                                }.getType();

                                ModelSetting iDs;
                                try {
                                    iDs = gson.fromJson(jsonElement, typeIDs);
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "خطا در ارتباط باسرور.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (iDs == null) {
                                    Toast.makeText(getContext(), "خطا در ارتباط باسرور.", Toast.LENGTH_SHORT).show();
                                } else {
                                    List<Setting> settingsList = new ArrayList<>(iDs.getSettings());
                                    if (settingsList.size() > 0) {
                                        String accStp = settingsList.get(0).ACC_STATUS_APP;

                                        if (accStp.equals("0")){
                                            ACCSTP=false;
                                        }else {
                                            ACCSTP=true;
                                        }
                                        accountsList.get(0).STAPP = ACCSTP;
                                        addAccount(company.USER, company.PASS, accountsList);
                                    }
                                }
                            }, throwable -> {}));
        } catch (Exception ignored) {}
    }}
