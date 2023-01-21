package ir.kitgroup.salein.Connect;

import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.DataBase.Company;
import ir.kitgroup.salein.classes.NetWorkHelper1;
import ir.kitgroup.salein.classes.Util;
import ir.kitgroup.salein.models.Advertise;
import ir.kitgroup.salein.models.AppDetail;
import ir.kitgroup.salein.models.Log;
import ir.kitgroup.salein.models.Message;
import ir.kitgroup.salein.models.ModelCompany;


@HiltViewModel
public class MainViewModel extends ViewModel {

    private final MainRepository mainRepository;
    private final SharedPreferences sharedPreferences;
    private NetWorkHelper1 networkHelper;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final MutableLiveData<List<Company>> resultAllCompany = new MutableLiveData<>();


    private final MutableLiveData<List<Company>> resultCompany = new MutableLiveData<>();
    private final MutableLiveData<List<Log>> resultAddAccountToServer = new MutableLiveData<>();
    private final MutableLiveData<List<AppDetail>> resultApp = new MutableLiveData<>();
    private final MutableLiveData<List<Account>> resultCustomerFromServer = new MutableLiveData<>();
    private final MutableLiveData<List<Advertise>> resultCompanyAdvertise = new MutableLiveData<>();

    private final MutableLiveData<Message> eMessage = new MutableLiveData<>();


    @Inject
    public MainViewModel(MainRepository mainRepository, SharedPreferences sharedPreferences, NetWorkHelper1 networkHelper) {
        this.mainRepository = mainRepository;
        this.sharedPreferences = sharedPreferences;
        this.networkHelper = networkHelper;
    }

    public void addAccountToServer(Util.JsonObjectAccount accounts) {

        Gson gson = new Gson();
        Type typeAccount = new TypeToken<Util.JsonObjectAccount>() {
        }.getType();

        compositeDisposable.add(
                mainRepository.addAccountToServer(gson.toJson(accounts, typeAccount))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(
                                resultAddAccountToServer::setValue,
                                throwable ->
                                        eMessage.setValue(new Message(111, "", "خطا در ثبت اطلاعات مشتری"))));
    }

    public MutableLiveData<List<Log>> getResultAddAccountToServer() {
        return resultAddAccountToServer;
    }


    public void getApp(String id) {


        compositeDisposable.add(
                mainRepository.getApp(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(
                                resultApp::setValue,
                                throwable -> {

                                    if (!networkHelper.isNetworkConnected1())
                                        eMessage.setValue(new Message(3, "", "خطا در اتصال اینترنت"));
                                    else
                                        eMessage.setValue(new Message(1, "", "خطا درارتباط با سرور "));

                                }
                        ));


    }

    public MutableLiveData<List<AppDetail>> getResultGetApp() {
        return resultApp;
    }


    public void getCustomerFromServer(String mobile) {
        compositeDisposable.clear();
        compositeDisposable.add(
                mainRepository.getCustomerFromServer(mobile)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(
                                resultCustomerFromServer::setValue,
                                throwable ->
                                        eMessage.setValue(new Message(110, "", "خطا در دریافت اطلاعات کاربر"))));
    }

    public MutableLiveData<List<Account>> getResultCustomerFromServer() {
        return resultCustomerFromServer;
    }


    public void getCompany(String id) {
        compositeDisposable.add(
                mainRepository.getCompany(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(
                                resultCompany::setValue
                                , throwable ->
                                        eMessage.setValue(new Message(-1, "", "خطا در دریافت اطلاعات شرکت"))
                        )
        );
    }

    public MutableLiveData<List<Company>> getResultCompany() {
        return resultCompany;
    }

    public MutableLiveData<Message> getResultMessage() {
        return eMessage;
    }


    public void getAllCompany(String parentAccountId, int page) {
        compositeDisposable.add(
                mainRepository.getAllCompany(parentAccountId, page)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {
                            Gson gson = new Gson();
                            Type typeIDs = new TypeToken<ModelCompany>() {
                            }.getType();
                            try {
                                ModelCompany iDs = gson.fromJson(jsonElement, typeIDs);
                                if (iDs != null && iDs.getCompany() != null) {
                                    resultAllCompany.setValue(iDs.getCompany());

                                } else {
                                    eMessage.setValue(new Message(-1, "خطا در ارتباط با سرور ، دوباره سعی کنید.", ""));
                                }
                            } catch (Exception ignored) {
                            }

                        }, throwable ->
                                eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات شرکت", ""))));
    }

    public MutableLiveData<List<Company>> getResultAllCompany() {
        return resultAllCompany;
    }

    public void clearData() {
        compositeDisposable.clear();
    }


    public void getAdvsByCompanyId(List<String> companiesId, int page, String appId, String customerId) {
      compositeDisposable.clear();
        compositeDisposable.add(
                mainRepository.getAdvsByCompanyId(companiesId, page, appId, customerId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(
                                jsonElement -> resultCompanyAdvertise.setValue(jsonElement)
                                , throwable ->
                                {
                                    if (!networkHelper.isNetworkConnected1())
                                        eMessage.setValue(new Message(1, "", "خطا در اتصال اینترنت"));
                                    else
                                        eMessage.setValue(new Message(1, "", "خطا در دریافت آگهی "));
                                }

                        )
        );
    }

    public MutableLiveData<List<Advertise>> getResultCompanyAdvertisement() {
        return resultCompanyAdvertise;
    }
}