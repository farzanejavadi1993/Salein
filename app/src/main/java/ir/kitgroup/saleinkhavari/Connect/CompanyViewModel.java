package ir.kitgroup.saleinkhavari.Connect;

import android.content.SharedPreferences;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orm.query.Select;

import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Type;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.saleinkhavari.DataBase.Account;

import ir.kitgroup.saleinkhavari.DataBase.SaleinShop;

import ir.kitgroup.saleinkhavari.DataBase.Company;
import ir.kitgroup.saleinkhavari.DataBase.InvoiceDetail;

import ir.kitgroup.saleinkhavari.DataBase.Unit;

import ir.kitgroup.saleinkhavari.models.CustomTab;
import ir.kitgroup.saleinkhavari.models.Description;
import ir.kitgroup.saleinkhavari.models.Invoice;
import ir.kitgroup.saleinkhavari.models.Message;
import ir.kitgroup.saleinkhavari.models.ModelAccount;
import ir.kitgroup.saleinkhavari.models.ModelCustomTab;
import ir.kitgroup.saleinkhavari.models.ModelDesc;
import ir.kitgroup.saleinkhavari.models.ModelInvoice;
import ir.kitgroup.saleinkhavari.models.ModelLog;
import ir.kitgroup.saleinkhavari.models.ModelProduct;
import ir.kitgroup.saleinkhavari.models.ModelProductLevel1;
import ir.kitgroup.saleinkhavari.models.ModelProductLevel2;
import ir.kitgroup.saleinkhavari.models.ModelSetting;
import ir.kitgroup.saleinkhavari.models.ModelTypeOrder;
import ir.kitgroup.saleinkhavari.models.ModelUnit;
import ir.kitgroup.saleinkhavari.models.PaymentRecieptDetail;
import ir.kitgroup.saleinkhavari.models.Product;
import ir.kitgroup.saleinkhavari.models.ProductLevel1;
import ir.kitgroup.saleinkhavari.models.ProductLevel2;
import ir.kitgroup.saleinkhavari.models.Setting;
import ir.kitgroup.saleinkhavari.ui.payment.PaymentFragment;

@HiltViewModel
public class CompanyViewModel extends ViewModel {
    private final CompanyRepository myRepository;
    private final SharedPreferences sharedPreferences;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();


    private final MutableLiveData<String> resultSendSms = new MutableLiveData<>();
    private final MutableLiveData<List<Company>> resultAllCompany = new MutableLiveData<>();

    private final MutableLiveData<List<Account>> resultInquiryAccount = new MutableLiveData<>();
    private final MutableLiveData<List<ProductLevel1>> resultProductLevel1 = new MutableLiveData<>();
    private final MutableLiveData<List<ProductLevel2>> resultProductLevel2 = new MutableLiveData<>();
    private final MutableLiveData<List<CustomTab>> resultCustomTab = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> resultProductCustomTab = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> resultDiscountProduct = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> resultVipProduct = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> resultListProduct = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> resultProduct = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> resultSearchProduct = new MutableLiveData<>();
    private final MutableLiveData<List<Description>> resultDescription = new MutableLiveData<>();
    private final MutableLiveData<List<Setting>> resultSetting = new MutableLiveData<>();
    private final MutableLiveData<List<Invoice>> resultAllInvoice = new MutableLiveData<>();
    private final MutableLiveData<ModelLog> resultSendFeedBack = new MutableLiveData<>();
    private final MutableLiveData<ModelInvoice> resultInvoice = new MutableLiveData<>();
    private final MutableLiveData<ModelLog> resultLog = new MutableLiveData<>();
    private final MutableLiveData<InvoiceDetail> resultMaxSale = new MutableLiveData<>();
    private final MutableLiveData<Boolean> resultAddAccount = new MutableLiveData<>();
    private final MutableLiveData<Integer> resultTransportationCost = new MutableLiveData<>();

    private final MutableLiveData<ModelTypeOrder> resultTypeOrder = new MutableLiveData<>();
    private final MutableLiveData<Message> eMessage = new MutableLiveData<>();


    @Inject
    public CompanyViewModel(CompanyRepository myRepository, SharedPreferences sharedPreferences) {
        this.myRepository = myRepository;
        this.sharedPreferences = sharedPreferences;
    }

    public void getSmsLogin(String user, String passWord, String message, String mobile) {
        compositeDisposable.clear();
        compositeDisposable.add(
                myRepository.getSmsLogin(user, passWord, message, mobile)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {

                        })
                        .subscribe(jsonElement ->
                                        resultSendSms.setValue(""),
                                throwable ->
                                        eMessage.setValue(new Message(-1, "خطا در ارسال پیامک", ""))));
    }

    public MutableLiveData<String> getResultSmsLogin() {
        return resultSendSms;
    }

    public void getInquiryAccount(String user, String passWord, String mobile) {

        List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).list();
        compositeDisposable.add(
                myRepository.getInquiryAccount(user, passWord, mobile)
                        .subscribeOn(Schedulers.io())

                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {
                            InvoiceDetail.saveInTx(invoiceDetails);
                            Gson gson = new Gson();
                            Type typeIDs = new TypeToken<ModelAccount>() {
                            }.getType();

                            try {
                                ModelAccount iDs = gson.fromJson(jsonElement, typeIDs);
                                assert iDs != null;
                                if (iDs.getAccountList() == null) {
                                    Type typeIDs0 = new TypeToken<ModelLog>() {
                                    }.getType();
                                    ModelLog iDs0 = gson.fromJson(jsonElement, typeIDs0);
                                    if (iDs0.getLogs() != null) {
                                        String description = iDs0.getLogs().get(0).getDescription();
                                        int message = iDs0.getLogs().get(0).getMessage();
                                        if (message == 3) {
                                            sharedPreferences.edit().putBoolean("disableAccount", true).apply();
                                            eMessage.setValue(new Message(-1, description, ""));
                                        }
                                        sharedPreferences.edit().putBoolean("disableAccount", false).apply();

                                        if (!Select.from(SaleinShop.class).first().isPublicApp())
                                            compositeDisposable.dispose();
                                    }
                                } else {
                                    sharedPreferences.edit().putBoolean("disableAccount", false).apply();
                                    resultInquiryAccount.setValue(iDs.getAccountList());
                                }
                            } catch (Exception ignored) {
                                eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات مشتری.", ""));
                            }


                        }, throwable -> {
                            resultAllCompany.setValue(null);
                            eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات مشتری.", ""));

                        })
        );


    }

    public MutableLiveData<List<Account>> getResultInquiryAccount() {
        return resultInquiryAccount;
    }


    public void addAccount(String user, String passWord, List<Account> accounts) {
        compositeDisposable.clear();
        compositeDisposable.add(
                myRepository.addAccount(user, passWord, accounts)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {
                            Gson gson = new Gson();
                            Type typeIDs = new TypeToken<ModelLog>() {
                            }.getType();
                            ModelLog iDs = gson.fromJson(jsonElement, typeIDs);

                            assert iDs != null;
                            int message = iDs.getLogs().get(0).getMessage();
                            String description = iDs.getLogs().get(0).getDescription();


                            if (message == 1) {
                                resultAddAccount.setValue(true);
                            } else
                                eMessage.setValue(new Message(-1, description, ""));


                        }, throwable -> {
                            resultAllCompany.setValue(null);
                            eMessage.setValue(new Message(-1, "خطا در ثبت اطلاعات مشتری", ""));

                        }));
    }
    public MutableLiveData<Boolean> getResultAddAccount() {
        return resultAddAccount;
    }



    public void getTransportationCost(double latCustomer,double longCustomer,double latCompany,double longCompany,double invoicePrice) {
        compositeDisposable.add(
                myRepository.getTransportationCost(latCustomer, longCustomer,latCompany,longCompany,invoicePrice)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {
                            resultTransportationCost.setValue(jsonElement);
                            }, throwable -> {

                            eMessage.setValue(new Message(-11, "خطا در دریافت هزینه ارسال", ""));

                        }));
    }

    public MutableLiveData<Integer> getResultTransportationCost() {
        return resultTransportationCost;
    }



    public void getProductLevel1(String user, String passWord) {
        List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).list();
        compositeDisposable.add(
                myRepository.getProductLevel1(user, passWord)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {
                            InvoiceDetail.saveInTx(invoiceDetails);
                            Gson gson = new Gson();
                            Type typeModelProductLevel1 = new TypeToken<ModelProductLevel1>() {
                            }.getType();


                            try {
                                ModelProductLevel1 iDs = gson.fromJson(jsonElement, typeModelProductLevel1);
                                if (iDs == null) {
                                    eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""));

                                }
                                resultProductLevel1.setValue(iDs.getProductLevel1());
                            } catch (Exception ignored) {
                                eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""));

                            }

                        }, throwable -> eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""))));
    }

    public MutableLiveData<List<ProductLevel1>> getResultProductLevel1() {
        return resultProductLevel1;
    }


    public void getProductLevel2(String user, String passWord, String prd1Id) {
        List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).list();
        compositeDisposable.add(
                myRepository.getProductLevel2(user, passWord, prd1Id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {
                            InvoiceDetail.saveInTx(invoiceDetails);
                            Gson gson = new Gson();
                            Type typeModelProduct2 = new TypeToken<ModelProductLevel2>() {
                            }.getType();


                            try {
                                ModelProductLevel2 iDs = gson.fromJson(jsonElement, typeModelProduct2);
                                if (iDs == null) {
                                    eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.د", ""));

                                }

                                resultProductLevel2.setValue(iDs.getProductLevel2());
                            } catch (Exception ignored) {
                                eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""));

                            }

                        }, throwable -> eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""))));
    }

    public MutableLiveData<List<ProductLevel2>> getResultProductLevel2() {
        return resultProductLevel2;
    }


    public void getCustomTab(String user, String passWord) {

        compositeDisposable.add(
                myRepository.getCustomTab(user, passWord)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {
                            Gson gson = new Gson();
                            Type typeIDs = new TypeToken<ModelCustomTab>() {
                            }.getType();

                            try {
                                ModelCustomTab iDs = gson.fromJson(jsonElement, typeIDs);
                                if (iDs != null)
                                    resultCustomTab.setValue(iDs.getCustomTab());

                            } catch (Exception ignored) {
                                eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""));
                            }

                        }, throwable ->
                                eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", "")))
        );
    }

    public MutableLiveData<List<CustomTab>> geResultCustomTab() {
        return resultCustomTab;
    }


    public void getProductCustomTab(String user, String passWord, int key) {

        compositeDisposable.add(
                myRepository.getProductCustomSync(user, passWord, key)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {
                            Gson gson = new Gson();
                            Type typeIDs = new TypeToken<ModelProduct>() {
                            }.getType();

                            try {
                                ModelProduct iDs = gson.fromJson(jsonElement, typeIDs);
                                if (iDs != null)
                                    resultProductCustomTab.setValue(iDs.getProductList());

                            } catch (Exception ignored) {
                                eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""));
                            }

                        }, throwable ->
                                eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", "")))
        );
    }

    public MutableLiveData<List<Product>> getResultProductCustomTab() {
        return resultProductCustomTab;
    }

    public void getListProduct(String user, String passWord, String prd2) {
        compositeDisposable.clear();
        compositeDisposable.add(
                myRepository.getListProduct(user, passWord, prd2)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(
                                this::accept,
                                throwable ->
                                        eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""))));
    }

    public MutableLiveData<List<Product>> getResultListProduct() {
        return resultListProduct;
    }


    public void getProduct(String user, String passWord, String prdId) {

        compositeDisposable.add(
                myRepository.getProduct(user, passWord, prdId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {

                            Gson gson = new Gson();
                            Type typeModelProduct = new TypeToken<ModelProduct>() {
                            }.getType();


                            try {
                                ModelProduct iDs = gson.fromJson(jsonElement, typeModelProduct);
                                if (iDs == null) {
                                    eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""));

                                }

                                CollectionUtils.filter(iDs.getProductList(), i -> i.getPrice(sharedPreferences) > 0.0 && i.getSts());
                                resultProduct.setValue(iDs.getProductList());

                            } catch (Exception ignored) {
                                eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""));

                            }


                        }, throwable -> eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""))));
    }

    public MutableLiveData<List<Product>> getResultProduct() {
        return resultProduct;
    }


    public void getDiscountProduct(String user, String passWord) {
        compositeDisposable.clear();
        compositeDisposable.add(
                myRepository.getDiscountPercent(user, passWord)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {
                            Gson gson = new Gson();
                            Type typeModelProduct = new TypeToken<ModelProduct>() {
                            }.getType();

                            try {
                                ModelProduct iDs = gson.fromJson(jsonElement, typeModelProduct);
                                if (iDs == null) {
                                    eMessage.setValue(new Message(-4, "خطا در دریافت اطلاعات.", ""));

                                }
                                sharedPreferences.edit().putBoolean("discount", true).apply();
                                resultDiscountProduct.setValue(iDs.getProductList());

                            } catch (Exception ignored) {
                                eMessage.setValue(new Message(-4, "خطا در دریافت اطلاعات.", ""));

                            }

                        }, throwable -> eMessage.setValue(new Message(-4, "خطا در دریافت اطلاعات.", ""))));
    }

    public MutableLiveData<List<Product>> getResultDiscountProduct() {
        return resultDiscountProduct;
    }


    public void getVipProduct(String user, String passWord, String accountId) {
        compositeDisposable.clear();
        compositeDisposable.add(
                myRepository.getProductVipSync(user, passWord, accountId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(
                                this::accept2,
                                throwable ->
                                        eMessage.setValue(new Message(-4, "خطا در دریافت اطلاعات.", ""))));
    }

    public MutableLiveData<List<Product>> getResultVipProduct() {
        return resultVipProduct;
    }


    public void getUnit(String user, String passWord) {
        List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).list();
        compositeDisposable.add(
                myRepository.getUnit(user, passWord)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {
                            InvoiceDetail.saveInTx(invoiceDetails);
                            Gson gson = new Gson();
                            Type typeIDs = new TypeToken<ModelUnit>() {
                            }.getType();

                            try {
                                ModelUnit iDs = gson.fromJson(jsonElement, typeIDs);
                                if (iDs != null && iDs.getUnit().size() > 0) {
                                    Unit.deleteAll(Unit.class);
                                    Unit.saveInTx(iDs.getUnit());
                                }
                            } catch (Exception ignore) {
                            }
                        }, throwable -> {

                        }));
    }


    public void getDescription(String user, String passWord, String id) {
        compositeDisposable.clear();
        compositeDisposable.add(
                myRepository.getDescription(user, passWord, id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {

                            Gson gson = new Gson();
                            Type typeIDs = new TypeToken<ModelDesc>() {
                            }.getType();

                            try {
                                ModelDesc iDs = gson.fromJson(jsonElement, typeIDs);
                                if (iDs != null)
                                    resultDescription.setValue(iDs.getDescriptions());

                            } catch (Exception ignored) {
                                eMessage.setValue(new Message(-1, "خطا در دریافت توضیحات", ""));
                            }


                        }, throwable -> eMessage.setValue(new Message(-1, "خطا در دریافت توضیحات", ""))));
    }

    public MutableLiveData<List<Description>> getResultDescription() {
        return resultDescription;
    }


    public void getSetting(String user, String passWord) {
        List<InvoiceDetail> invoiceDetails = Select.from(InvoiceDetail.class).list();
        compositeDisposable.add(
                myRepository.getSetting(user, passWord)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {

                            InvoiceDetail.saveInTx(invoiceDetails);
                            Gson gson = new Gson();
                            Type typeIDs = new TypeToken<ModelSetting>() {
                            }.getType();

                            try {
                                ModelSetting iDs = gson.fromJson(jsonElement, typeIDs);
                                if (iDs != null)
                                    resultSetting.setValue(iDs.getSettings());

                            } catch (Exception ignored) {
                                eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""));
                            }
                        }, throwable -> eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""))));
    }

    public MutableLiveData<List<Setting>> getResultSetting() {
        return resultSetting;
    }





    public void getInvoice(String user, String passWord, String Inv_GUID) {

        compositeDisposable.add(
                myRepository.getInvoice(user, passWord, Inv_GUID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {

                            Gson gson = new Gson();
                            Type typeIDs = new TypeToken<ModelInvoice>() {
                            }.getType();

                            try {
                                ModelInvoice iDs = gson.fromJson(jsonElement, typeIDs);
                                if (iDs != null) {
                                    resultInvoice.setValue(iDs);

                                } else {
                                    eMessage.setValue(new Message(-1, "دریافت سفارش ناموفق", ""));

                                }
                            } catch (Exception ignored) {
                                eMessage.setValue(new Message(-1, "دریافت سفارش ناموفق", ""));

                            }
                        }, throwable -> eMessage.setValue(new Message(-1, "خطا در دریافت سفارش", ""))));
    }

    public MutableLiveData<ModelInvoice> getResultInvoice() {
        return resultInvoice;
    }


    public void getDeleteInvoice(String user, String passWord, String Inv_GUID) {

        compositeDisposable.add(
                myRepository.deleteInvoice(user, passWord, Inv_GUID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(this::accept3, throwable -> eMessage.setValue(new Message(-1, "خطا در حذف سفارش", ""))));
    }

    public MutableLiveData<ModelLog> getResultLog() {
        return resultLog;
    }


    public void getMaxSale(String user, String passWord, String Prd_prd) {

        compositeDisposable.add(
                myRepository.getMaxSales(user, passWord, Prd_prd)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {
                            Gson gson = new Gson();
                            Type typeIDs = new TypeToken<ModelLog>() {
                            }.getType();
                            int remain;
                            try {
                                remain = Integer.parseInt(jsonElement);
                                InvoiceDetail invoiceDetail=new InvoiceDetail();
                                invoiceDetail.setFakeAmount(remain);
                                invoiceDetail.setPRD_UID(Prd_prd);
                                resultMaxSale.setValue(invoiceDetail);
                            } catch (Exception ignored) {
                                ModelLog iDs = gson.fromJson(jsonElement, typeIDs);
                                resultLog.setValue(iDs);
                            }

                        }, throwable ->
                                eMessage.setValue(new Message(-1, "خطا در دریافت مانده کالا", ""))));
    }

    public MutableLiveData<InvoiceDetail> getResultMaxSale() {
        return resultMaxSale;
    }



    public void getAllInvoice(String user, String passWord, String accId, String date) {

        compositeDisposable.add(
                myRepository.getAllInvoice(user, passWord, accId, date)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {

                            Gson gson = new Gson();
                            Type typeIDs = new TypeToken<ModelInvoice>() {
                            }.getType();

                            try {
                                ModelInvoice iDs = gson.fromJson(jsonElement, typeIDs);
                                if (iDs != null) {
                                    resultAllInvoice.setValue(iDs.getInvoice());
                                } else {
                                    eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""));
                                }
                            } catch (Exception ignored) {
                                eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""));

                            }
                        }, throwable -> eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""))));
    }

    public MutableLiveData<List<Invoice>> getResultAllInvoice() {
        return resultAllInvoice;
    }


    public void sendFeedBack(String user, String passWord, List<Invoice> invoice, List<InvoiceDetail> invoiceDetail, List<PaymentRecieptDetail> clsPaymentRecieptDetail) {

        compositeDisposable.add(
                myRepository.sendFeedBack(user, passWord, invoice, invoiceDetail, clsPaymentRecieptDetail)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {


                            Gson gson1 = new Gson();
                            Type typeIDs = new TypeToken<ModelLog>() {
                            }.getType();
                            ModelLog iDs = gson1.fromJson(jsonElement, typeIDs);
                            if (iDs != null)
                                resultSendFeedBack.setValue(iDs);
                            else
                                eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""));


                        }, throwable -> eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""))));
    }

    public MutableLiveData<ModelLog> getResultFeedBack() {
        return resultSendFeedBack;
    }


    public void getSearchProduct(String user, String passWord, String word) {
        compositeDisposable.clear();
        compositeDisposable.add(
                myRepository.getSearchProduct(user, passWord, word)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {
                            Gson gson = new Gson();
                            Type typeModelProduct = new TypeToken<ModelProduct>() {
                            }.getType();
                            ModelProduct iDs;
                            try {
                                iDs = gson.fromJson(jsonElement, typeModelProduct);
                                if (iDs == null) {
                                    eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""));
                                }
//                                sharedPreferences.edit().putBoolean("vip", false).apply();
//                                sharedPreferences.edit().putBoolean("discount", false).apply();
                                resultSearchProduct.setValue(iDs.getProductList());

                            } catch (Exception ignored) {
                                eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""));
                            }
                        }, throwable -> eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""))));
    }

    public MutableLiveData<List<Product>> getResultSearchProduct() {
        return resultSearchProduct;
    }


    public void sendOrder(String user, String passWord, List<Invoice> invoice, List<InvoiceDetail> invoiceDetail, List<PaymentRecieptDetail> clsPaymentRecieptDetail, String numberPos) {

        PaymentFragment.JsonObject jsonObject = new PaymentFragment.JsonObject();
        jsonObject.Invoice = invoice;
        jsonObject.InvoiceDetail = invoiceDetail;
        jsonObject.PaymentRecieptDetail = clsPaymentRecieptDetail;

        Gson gson1 = new Gson();
        Type typeJsonObject = new TypeToken<PaymentFragment.JsonObject>() {
        }.getType();


        compositeDisposable.add(
                myRepository.sendOrder(user, passWord, gson1.toJson(jsonObject, typeJsonObject), numberPos)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {
                            Gson gson = new Gson();
                            Type typeIDs = new TypeToken<ModelLog>() {
                            }.getType();

                            try {
                                ModelLog iDs = gson.fromJson(jsonElement, typeIDs);

                                resultLog.setValue(iDs);

                            } catch (Exception ignored) {
                                eMessage.setValue(new Message(-1, "خطا در ارسال سفارش", ""));
                            }
                        }, throwable -> eMessage.setValue(new Message(-1, "خطا در ارسال سفارش", ""))));
    }

    public MutableLiveData<ModelLog> getResultSendOrder() {
        return resultLog;
    }


    public void getTypeOrder(String user, String passWord) {

        compositeDisposable.add(
                myRepository.getTypeOrder(user, passWord)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {
                            Gson gson = new Gson();
                            Type typeIDs = new TypeToken<ModelTypeOrder>() {
                            }.getType();


                            try {
                                ModelTypeOrder iDs = gson.fromJson(jsonElement, typeIDs);
                                resultTypeOrder.setValue(iDs);
                            } catch (Exception e) {
                                eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""));
                            }
                        }, throwable -> eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""))));
    }

    public MutableLiveData<ModelTypeOrder> getResultTypeOrder() {
        return resultTypeOrder;
    }


    public void updateAccount(String user, String passWord, List<Account> accounts) {
       /* Util.JsonObjectAccount jsonObject1 = new Util.JsonObjectAccount();
        jsonObject1.Account = accounts;


        Gson gson1 = new Gson();
        Type typeJsonObject = new TypeToken<Util.JsonObjectAccount>() {
        }.getType();

        gson1.toJson(jsonObject1, typeJsonObject);*/

        compositeDisposable.clear();
        compositeDisposable.add(
                myRepository.updateAccount(user, passWord, accounts)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {
                            Gson gson = new Gson();
                            Type typeIDs = new TypeToken<ModelLog>() {
                            }.getType();
                            ModelLog iDs = gson.fromJson(jsonElement, typeIDs);
                            resultLog.setValue(iDs);
                        }, throwable -> eMessage.setValue(new Message(-1, "خطا در ویرایش اطلاعات", ""))));
    }

    public MutableLiveData<ModelLog> getResultUpdateAccount() {
        return resultLog;
    }


    public MutableLiveData<Message> getResultMessage() {
        return eMessage;
    }

    private void accept(String jsonElement) {

        Gson gson = new Gson();
        Type typeModelProduct = new TypeToken<ModelProduct>() {
        }.getType();


        try {
            ModelProduct iDs = gson.fromJson(jsonElement, typeModelProduct);
            if (iDs == null) {
                eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""));

            }

            CollectionUtils.filter(iDs.getProductList(), i -> i.getPrice(sharedPreferences) > 0.0 && i.getSts());
            resultListProduct.setValue(iDs.getProductList());

        } catch (Exception ignored) {
            eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""));

        }


    }

    private void accept2(String jsonElement) {
        Gson gson = new Gson();
        Type typeModelProduct = new TypeToken<ModelProduct>() {
        }.getType();
        ModelProduct iDs;

        try {
            iDs = gson.fromJson(jsonElement, typeModelProduct);
            if (iDs == null) {
                eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات.", ""));

            }

            sharedPreferences.edit().putBoolean("vip", true).apply();
            resultVipProduct.setValue(iDs.getProductList());

        } catch (Exception ignored) {
            resultVipProduct.setValue(new ArrayList<>());
        }


    }

    private void accept3(String jsonElement) {

        Gson gson = new Gson();
        Type typeIDs = new TypeToken<ModelLog>() {
        }.getType();
        ModelLog iDs = gson.fromJson(jsonElement, typeIDs);

        if (iDs != null) {
            resultLog.setValue(iDs);
        } else {
            eMessage.setValue(new Message(-1, "خطا در حذف سفارش ", ""));

        }


    }



}
