package ir.kitgroup.salein.Connect;

import android.content.SharedPreferences;
import android.view.View;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.collections4.CollectionUtils;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import ir.kitgroup.salein.DataBase.Account;
import ir.kitgroup.salein.DataBase.Company;
import ir.kitgroup.salein.DataBase.InvoiceDetail;
import ir.kitgroup.salein.DataBase.Unit;
import ir.kitgroup.salein.R;
import ir.kitgroup.salein.models.Description;
import ir.kitgroup.salein.models.Invoice;
import ir.kitgroup.salein.models.Message;
import ir.kitgroup.salein.models.ModelAccount;
import ir.kitgroup.salein.models.ModelCompany;
import ir.kitgroup.salein.models.ModelDesc;
import ir.kitgroup.salein.models.ModelInvoice;
import ir.kitgroup.salein.models.ModelLog;
import ir.kitgroup.salein.models.ModelProduct;
import ir.kitgroup.salein.models.ModelProductLevel1;
import ir.kitgroup.salein.models.ModelProductLevel2;
import ir.kitgroup.salein.models.ModelSetting;
import ir.kitgroup.salein.models.ModelUnit;
import ir.kitgroup.salein.models.PaymentRecieptDetail;
import ir.kitgroup.salein.models.Product;
import ir.kitgroup.salein.models.ProductLevel1;
import ir.kitgroup.salein.models.ProductLevel2;
import ir.kitgroup.salein.models.Setting;

@HiltViewModel
public class MyViewModel extends ViewModel {
    private MyRepository myRepository;
    private SharedPreferences sharedPreferences;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    private MutableLiveData<String> resultSendSms = new MutableLiveData<>();
    private MutableLiveData<List<Company>> resultCompanies = new MutableLiveData<>();
    private MutableLiveData<List<Account>> resultInquiryAccount = new MutableLiveData<>();
    private MutableLiveData<List<Account>> resultSearchAccount = new MutableLiveData<>();
    private MutableLiveData<List<ProductLevel1>> resultProductLevel1 = new MutableLiveData<>();
    private MutableLiveData<List<ProductLevel2>> resultProductLevel2 = new MutableLiveData<>();
    private MutableLiveData<List<Product>> resultDiscountProduct = new MutableLiveData<>();
    private MutableLiveData<List<Product>> resultVipProduct = new MutableLiveData<>();
    private MutableLiveData<List<Product>> resultListProduct = new MutableLiveData<>();
    private MutableLiveData<List<Product>> resultProduct = new MutableLiveData<>();
    private MutableLiveData<List<Product>> resultSearchProduct = new MutableLiveData<>();
    private MutableLiveData<List<Description>> resultDescription = new MutableLiveData<>();
    private MutableLiveData<List<Setting>> resultSetting = new MutableLiveData<>();
    private MutableLiveData<List<Setting>> resultSettingPrice = new MutableLiveData<>();
    private MutableLiveData<List<Invoice>> resultAllInvoice = new MutableLiveData<>();
    private MutableLiveData<ModelLog> resultSendFeedBack = new MutableLiveData<>();

    private MutableLiveData<ModelInvoice> resultInvoice = new MutableLiveData<>();
    private MutableLiveData<ModelLog> resultLog = new MutableLiveData<>();
    private MutableLiveData<Integer> resultMaxSale = new MutableLiveData<>();
    private MutableLiveData<Boolean> resultAddAccount = new MutableLiveData<>();
    private MutableLiveData<Message> eMessage = new MutableLiveData<>();

    @Inject
    public MyViewModel(MyRepository myRepository, SharedPreferences sharedPreferences) {
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
                        .subscribe(jsonElement -> {
                            resultSendSms.setValue("");
                        }, throwable -> {
                            eMessage.setValue(new Message(-1, "خطا در ارسال پیامک", ""));
                        }));
    }

    public MutableLiveData<String> getResultSmsLogin() {
        return resultSendSms;
    }


    public void getCompany(String parentAccountId) {
        compositeDisposable.add(
                myRepository.getCompany(parentAccountId)
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
                                    resultCompanies.setValue(iDs.getCompany());

                                } else {
                                    eMessage.setValue(new Message(-1, "خطا در ارتباط با سرور ، دوباره سعی کنید.", ""));
                                }
                            } catch (Exception e) {
                            }

                        }, throwable -> {
                            eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات شرکت", ""));
                        }));
    }

    public MutableLiveData<List<Company>> getResultCompany() {
        return resultCompanies;
    }


    public void getInquiryAccount(String user, String passWord, String mobile) {

        compositeDisposable.add(
                myRepository.getInquiryAccount(user, passWord, mobile)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {
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
                                        }
                                        eMessage.setValue(new Message(-1, description, ""));
                                        compositeDisposable.dispose();
                                    }
                                } else {
                                    sharedPreferences.edit().putBoolean("disableAccount", false).apply();
                                    resultInquiryAccount.setValue(iDs.getAccountList());
                                }
                            } catch (Exception e) {
                                eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات مشتری.", ""));
                            }


                        }, throwable -> {
                            resultCompanies.setValue(null);
                            eMessage.setValue(new Message(-1, "خطا در دریافت اطلاعات شرکت", ""));

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
                            resultCompanies.setValue(null);
                            eMessage.setValue(new Message(-1, "خطا در ثبت اطلاعات مشتری", ""));

                        }));
    }

    public MutableLiveData<Boolean> getResultAddAccount() {
        return resultAddAccount;
    }


    public void getAccountSearch(String user, String passWord, String word) {
        compositeDisposable.clear();
        compositeDisposable.add(
                myRepository.getAccountSearch(user, passWord, word)
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

                                assert iDs != null;
                                if (iDs.getAccountList() != null) {
                                    resultSearchAccount.setValue(iDs.getAccountList());
                                } else {
                                    eMessage.setValue(new Message(-2, "هیچ مشتری پیدا نشد.", ""));
                                }

                            } catch (Exception e) {
                                eMessage.setValue(new Message(-2, "خطا در جستجوی مشتری", ""));

                            }

                        }, throwable -> {
                            eMessage.setValue(new Message(-2, "خطا در جستجوی مشتری", ""));

                        }));
    }

    public MutableLiveData<List<Account>> getResultSearchAccount() {
        return resultSearchAccount;
    }


    public void getProductLevel1(String user, String passWord) {

        compositeDisposable.add(
                myRepository.getProductLevel1(user, passWord)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {
                            Gson gson = new Gson();
                            Type typeModelProductLevel1 = new TypeToken<ModelProductLevel1>() {
                            }.getType();


                            try {
                                ModelProductLevel1 iDs = gson.fromJson(jsonElement, typeModelProductLevel1);
                                if (iDs == null) {
                                    eMessage.setValue(new Message(-1, "لیست دریافت شده از گروه کالاها نا معتبر می باشد", ""));

                                }
                                resultProductLevel1.setValue(iDs.getProductLevel1());
                            } catch (Exception e) {
                                eMessage.setValue(new Message(-1, "خطا در دریافت گروه کالاها", ""));

                            }

                        }, throwable -> {
                            eMessage.setValue(new Message(-1, "فروشگاه تعطیل است.", ""));

                        }));
    }

    public MutableLiveData<List<ProductLevel1>> getResultProductLevel1() {
        return resultProductLevel1;
    }


    public void getProductLevel2(String user, String passWord, String prd1Id) {

        compositeDisposable.add(
                myRepository.getProductLevel2(user, passWord, prd1Id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {
                            Gson gson = new Gson();
                            Type typeModelProduct2 = new TypeToken<ModelProductLevel2>() {
                            }.getType();


                            try {
                                ModelProductLevel2 iDs = gson.fromJson(jsonElement, typeModelProduct2);
                                if (iDs == null) {
                                    eMessage.setValue(new Message(-1, "لیست دریافت شده از زیر گروه کالاها نا معتبر می باشد", ""));

                                }

                                resultProductLevel2.setValue(iDs.getProductLevel2());
                            } catch (Exception e) {
                                eMessage.setValue(new Message(-1, "لیست دریافت شده از زیرگروه گروه کالاها نا معتبر می باشد", ""));

                            }

                        }, throwable -> {
                            eMessage.setValue(new Message(-1, "", ""));
                        }));
    }

    public MutableLiveData<List<ProductLevel2>> getResultProductLevel2() {
        return resultProductLevel2;
    }


    public void getListProduct(String user, String passWord, String prd2) {

        compositeDisposable.add(
                myRepository.getListProduct(user, passWord, prd2)
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
                                    eMessage.setValue(new Message(-1, "خطا در دریافت لیست کالاها", ""));

                                }

                                CollectionUtils.filter(iDs.getProductList(), i -> i.getPrice(sharedPreferences) > 0.0 && i.getSts());
                                resultListProduct.setValue(iDs.getProductList());

                            } catch (Exception e) {
                                eMessage.setValue(new Message(-1, "خطا در دریافت لیست کالاها", ""));

                            }


                        }, throwable -> {
                            eMessage.setValue(new Message(-1, "خطا در دریافت کالاها", ""));
                        }));
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
                                    eMessage.setValue(new Message(-1, "خطا در دریافت لیست کالاها", ""));

                                }

                                CollectionUtils.filter(iDs.getProductList(), i -> i.getPrice(sharedPreferences) > 0.0 && i.getSts());
                                resultProduct.setValue(iDs.getProductList());

                            } catch (Exception e) {
                                eMessage.setValue(new Message(-1, "خطا در دریافت لیست کالاها", ""));

                            }


                        }, throwable -> {
                            eMessage.setValue(new Message(-1, "خطا در دریافت کالاها", ""));
                        }));
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
                                    eMessage.setValue(new Message(-1, "خطا در دریافت لیست کالاهای تخفیف دار", ""));

                                }
                                sharedPreferences.edit().putBoolean("discount", true).apply();
                                resultDiscountProduct.setValue(iDs.getProductList());

                            } catch (Exception e) {
                                eMessage.setValue(new Message(-1, "خطا در دریافت لیست کالاهای تخفیف دار", ""));

                            }

                        }, throwable -> {
                            eMessage.setValue(new Message(-1, "خطا در دریافت کالاهای تخفیف دار", ""));
                        }));
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
                        .subscribe(jsonElement -> {
                            Gson gson = new Gson();
                            Type typeModelProduct = new TypeToken<ModelProduct>() {
                            }.getType();
                            ModelProduct iDs;

                            try {
                                iDs = gson.fromJson(jsonElement, typeModelProduct);
                                if (iDs == null) {
                                    eMessage.setValue(new Message(-1, "لیست دریافت شده از کالاها نا معتبر می باشد ، دوباره تلاش کنید.", ""));

                                }

                                sharedPreferences.edit().putBoolean("vip", true).apply();
                                resultVipProduct.setValue(iDs.getProductList());

                            } catch (Exception e) {

                                eMessage.setValue(new Message(-1, "مدل دریافت شده از کالاها نا معتبر است ، دوباره تلاش کنید.", ""));

                            }


                        }, throwable -> {
                            eMessage.setValue(new Message(-1, "خطا در دریافت کالاهای تخفیف دار", ""));
                        }));
    }

    public MutableLiveData<List<Product>> getResultVipProduct() {
        return resultVipProduct;
    }

    public void getUnit(String user, String passWord) {
        compositeDisposable.add(
                myRepository.getUnit(user, passWord)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {
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


                        }, throwable -> {
                            eMessage.setValue(new Message(-1, "خطا در دریافت توضیحات", ""));
                        }));
    }

    public MutableLiveData<List<Description>> getResultDescription() {
        return resultDescription;
    }


    public void getSetting(String user, String passWord) {

        compositeDisposable.add(
                myRepository.getSetting(user, passWord)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {

                            Gson gson = new Gson();
                            Type typeIDs = new TypeToken<ModelSetting>() {
                            }.getType();

                            try {
                                ModelSetting iDs = gson.fromJson(jsonElement, typeIDs);
                                if (iDs != null)
                                    resultSetting.setValue(iDs.getSettings());

                            } catch (Exception ignored) {
                                eMessage.setValue(new Message(-1, "خطا در دریافت تنظیمات", ""));
                            }
                        }, throwable -> {
                            eMessage.setValue(new Message(-1, "خطا در دریافت تنظیمات", ""));
                        }));
    }

    public MutableLiveData<List<Setting>> getResultSetting() {
        return resultSetting;
    }

    public void getSettingPrice(String user, String passWord) {

        compositeDisposable.add(
                myRepository.getSetting(user, passWord)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> {
                        })
                        .subscribe(jsonElement -> {

                            Gson gson = new Gson();
                            Type typeIDs = new TypeToken<ModelSetting>() {
                            }.getType();

                            try {
                                ModelSetting iDs = gson.fromJson(jsonElement, typeIDs);
                                if (iDs != null)
                                    resultSettingPrice.setValue(iDs.getSettings());
                            } catch (Exception ignored) {
                                eMessage.setValue(new Message(-1, "خطا در دریافت مبلغ کالا", ""));
                            }
                        }, throwable -> {
                            eMessage.setValue(new Message(-1, "خطا در دریافت مبلغ کالا", ""));
                        }));
    }

    public MutableLiveData<List<Setting>> getResultSettingPrice() {
        return resultSettingPrice;
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
                        }, throwable -> {
                            eMessage.setValue(new Message(-1, "خطا در دریافت سفارش", ""));
                        }));
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
                        .subscribe(jsonElement -> {

                            Gson gson = new Gson();
                            Type typeIDs = new TypeToken<ModelLog>() {
                            }.getType();
                            ModelLog iDs = gson.fromJson(jsonElement, typeIDs);

                            if (iDs != null) {
                                resultLog.setValue(iDs);
                            } else {
                                eMessage.setValue(new Message(-1, "خطا در حذف سفارش رخ داده است", ""));

                            }


                        }, throwable -> {
                            eMessage.setValue(new Message(-1, "خطا در حذف سفارش", ""));
                        }));
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
                                resultMaxSale.setValue(remain);
                            } catch (Exception e) {
                                ModelLog iDs = gson.fromJson(jsonElement, typeIDs);
                                resultLog.setValue(iDs);
                                return;
                            }

                        }, throwable -> {
                            eMessage.setValue(new Message(-1, "خطا در دریافت مانده کالا", ""));
                        }));
    }

    public MutableLiveData<Integer> getResultMaxSale() {
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
                                    eMessage.setValue(new Message(-1, "دریافت آخرین اطلاعات ناموفق", ""));
                                }
                            } catch (Exception ignored) {
                                eMessage.setValue(new Message(-1, "دریافت آخرین اطلاعات ناموفق", ""));

                            }
                        }, throwable -> {
                            eMessage.setValue(new Message(-1, "دریافت آخرین اطلاعات ناموفق", ""));
                        }));
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
                                eMessage.setValue(new Message(-1, "خطا در ارسال پیام", ""));


                        }, throwable -> {
                            eMessage.setValue(new Message(-1, "خطا در ارسال پیام", ""));
                        }));
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
                                    eMessage.setValue(new Message(-1, "لیست دریافت شده از کالاها نا معتبر می باشد ، دوباره تلاش کنید.", ""));
                                }
                                sharedPreferences.edit().putBoolean("vip", true).apply();
                                resultSearchProduct.setValue(iDs.getProductList());

                            } catch (Exception e) {
                                eMessage.setValue(new Message(-1, "مدل دریافت شده از کالاها نا معتبر است ، دوباره تلاش کنید.", ""));
                            }
                        }, throwable -> {
                            eMessage.setValue(new Message(-1, "خطا در دریافت کالاهای تخفیف دار", ""));
                        }));
    }

    public MutableLiveData<List<Product>> getResultSearchProduct() {
        return resultSearchProduct;
    }

    public MutableLiveData<Message> getResultMessage() {
        return eMessage;
    }
}
