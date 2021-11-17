package ir.kitgroup.salein.Fragments;

import android.annotation.SuppressLint;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import ir.kitgroup.salein.classes.Util;
import ir.kitgroup.salein.databinding.FragmentRulesBinding;
import ir.kitgroup.salein.models.Company;

@AndroidEntryPoint
public class RulesFragment extends Fragment {

    @Inject
    Company company;

    private FragmentRulesBinding binding;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentRulesBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        int fontSize;
        if (Util.screenSize>=7)
            fontSize=12;
        else
            fontSize=14;


        binding.tvTitle.setTextSize(fontSize);
        binding.tvRule.setTextSize(fontSize);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.tvRule.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);

        }
        if (company.namePackage.equals("ir.kitgroup.saleinmeat")) {


            binding.tvRule.setText("۱_ تعاریف واصطلاحات\n" +
                    "۱.۱ " +
                    company.nameCompany +
                    ": سیستم نرم افزاری فروش بصورت حضوری و آنلاین است که به منظور" +
                    company.services +
                    "\n" +
                    "۲.۱اپلیکیشن: نرم افزاروبرنامه متعلق به مجموعه " +
                    company.nameCompany +
                    " است که به منظور استفاده از خدمات سوپر گوشت دنیوی طراحی وارائه شده است .\n" +
                    "۳.۱کاربر:شخصی حقیقی یاحقوقی که بصورت اینترنتی به اپلیکیشن " +
                    company.nameCompany +
                    " متصل شده ودرخواست خدمات کرده وسفارش خودراثبت میکند.\n" +
                    "۴.۱حساب کاربری:حسابی است که اشخاص برای استفاده از خدمات" +
                    " اپلیکیشن " +
                    company.nameCompany +
                    " ایجاد میکند.\n" +
                    "۵.۱ اعتبار:مبلغی است که کاربران (مشتریان)درحساب کاربری خود دارندواز ان جهت استفاده از " +
                    "خدمات " +
                    company.nameCompany +
                    " وسفارش محصول استفاده میکنند\u200C.\n " +
                    "۲_ حساب کاربری\n" +
                    "۱.۲کاربران در اپلیکیشن " +
                    company.nameCompany +
                    " قوانین و مقررات " +
                    company.nameCompany +
                    " راپذیرفته و با آگاهی از آن استفاده میکنند.\n" +
                    "۲.۲ برای استفاده از خدمات " +
                    company.nameCompany +
                    " لازم است کاربران اعم از حقیقی و حقوقی یک حساب کاربری در اپلیکیشن ایجاد کنند.\n" +
                    "۳.۲حساب کاربری شامل نام ،شماره همراه ،ادرس و مکانیت کاربر می باشد.\n" +
                    "۴.۲مسئولیت همه فعالیت هایی که از طریق حساب کاربری انجام میشودو بعهده کاربر مربوطه می باشد.\n" +
                    "هزینه ها وپرداخت\n" +
                    "۱.۳ امکان استرداد هزینه های پرداختی به کاربر وجود ندارد.\n" +
                    "۲.۳ هزینه سفارش از طریق اپلیکیشن  وبه صورت سیستمی تعریف میشود وبدیهی است که پس از ثبت سفارش ، کاربر امکان اعتراض نسبت به هزینه را ندارد.\n" +
                    "۳.۳پرداخت هزینه سفارش فقط از طریق روش هایی که توسط اپلیکیشن " +
                    company.nameCompany  +
                    " مشخص میشود امکان پذیرمیباشد این روش ها شامل پرداخت از طریق کارت بانکی بصورت انلاین،پرداخت نقدی درمحل تحویل سفارش واز طریق اعتبار حساب می باشد.\n" +
                    "۴.۳کاربرمیپذیرد که لغو سفارش فقط تا قبل تایید شدن سفارش از طرف " +
                    company.nameCompany  +
                    " بصورت سیستمی امکان پذیراست ویا از طریق تماس تلفنی با شماره " +
                    company.numberPhone +
                    " مقدوراست.\n" +
                    "۵.۳کاربرمیتواندوضعیت سفارش خودرابعدازثبت سفارش در قسمت پروفایل کاربری قسمت پیگیری سفارشها مشاهده کند.\n" +
                    " ۴ مسئولیت های" +
                    company.nameCompany  +
                    "\n" +
                    "۱.۴ " +
                    company.nameCompany  +
                    " موظف است از روش هایی که برای پشتیبانی تعیین کرده در دسترس کاربران باشد ونسبت به رسیدگی شکایت ونارضایتی تلاش کند.\n" +
                    "۲.۴ تمام اطلاعات کاربربصورت محرمانه محافظت شده و دسترسی به ان توسط سایرین ممنوع می باشد.\n" +
                    "۳.۴ " +
                    company.nameCompany +
                    " موظف است سفارش کاربر را با استفاده از وسایل نقلیه موتوری در زمان مشخص شده دربسته بندی مخصوص تحویل کاربر دهد.\n" +
                    "۵ قطع خدمات\n" +
                    " درصورتیکه کاربر به هرنحوی از سیستم سوء استفاده کند" +
                    company.nameCompany +
                    " حق دارد حساب کاربری شخص را مسدود وادامه خدمات به کاربر مورد نظر راقطع کند.");
        }


    }
}
