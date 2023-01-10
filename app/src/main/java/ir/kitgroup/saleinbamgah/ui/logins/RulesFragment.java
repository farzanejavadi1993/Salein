package ir.kitgroup.saleinbamgah.ui.logins;

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

import com.orm.query.Select;

import org.jetbrains.annotations.NotNull;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.saleinbamgah.DataBase.Company;
import ir.kitgroup.saleinbamgah.databinding.FragmentRulesBinding;

@AndroidEntryPoint
public class RulesFragment extends Fragment {

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

        Company company = Select.from(Company.class).first();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.tvRule.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }
        if (company != null)
            binding.tvRule.setText("۱_ تعاریف واصطلاحات\n" +
                    "۱.۱ " +
                    company.getN() +
                    ": سیستم نرم افزاری فروش بصورت حضوری و آنلاین است که به منظور ارائه " +
                    company.getDesc() +
                    " در " +
                    company.getCity() +

                    " فعالیت میکند." +
                    "\n" +
                    "۲.۱اپلیکیشن: نرم افزار و برنامه متعلق به مجموعه " +
                    company.getN() +
                    " است که به منظور استفاده از خدمات " +
                    company.getN() +
                    " طراحی وارائه شده است .\n" +
                    "۳.۱کاربر:شخصی حقیقی یاحقوقی که بصورت اینترنتی به اپلیکیشن " +
                    company.getN() +
                    " متصل شده و درخواست خدمات کرده و سفارش خود را ثبت میکند.\n" +
                    "۴.۱حساب کاربری:حسابی است که اشخاص برای استفاده از خدمات" +
                    " اپلیکیشن " +
                    company.getN() +
                    " ایجاد میکند.\n" +
                    "۵.۱ اعتبار:مبلغی است که کاربران (مشتریان)درحساب کاربری خود دارند و از ان جهت استفاده از " +
                    "خدمات " +
                    company.getN() +
                    " وسفارش محصول استفاده میکنند\u200C.\n " +
                    "۲_ حساب کاربری\n" +
                    "۱.۲کاربران در اپلیکیشن " +
                    company.getN() +
                    " قوانین و مقررات " +
                    company.getN() +
                    " را پذیرفته و با آگاهی از آن استفاده میکنند.\n" +
                    "۲.۲ برای استفاده از خدمات " +
                    company.getN() +
                    " لازم است کاربران اعم از حقیقی و حقوقی یک حساب کاربری در اپلیکیشن ایجاد کنند.\n" +
                    "۳.۲حساب کاربری شامل نام ،شماره همراه ،ادرس و مکانیت کاربر می باشد.\n" +
                    "۴.۲مسئولیت همه فعالیت هایی که از طریق حساب کاربری انجام میشود و بعهده کاربر مربوطه می باشد.\n" +
                    "هزینه ها وپرداخت\n" +
                    "۱.۳ امکان استرداد هزینه های پرداختی به کاربر وجود ندارد.\n" +
                    "۲.۳ هزینه سفارش از طریق اپلیکیشن  وبه صورت سیستمی تعریف میشود وبدیهی است که پس از ثبت سفارش ، کاربر امکان اعتراض نسبت به هزینه را ندارد.\n" +
                    "۳.۳پرداخت هزینه سفارش فقط از طریق روش هایی که توسط اپلیکیشن " +
                    company.getN() +
                    " مشخص میشود امکان پذیر می باشد این روش ها شامل پرداخت از طریق کارت بانکی بصورت انلاین ، پرداخت نقدی درمحل تحویل سفارش واز طریق اعتبار حساب می باشد.\n" +
                    "۴.۳کاربر می پذیرد که لغو سفارش فقط تا قبل تایید شدن سفارش از طرف " +
                    company.getN() +
                    " بصورت سیستمی امکان پذیراست ویا از طریق تماس تلفنی با شماره " +
                    company.getT1() +
                    " مقدوراست.\n" +
                    "۵.۳کاربر می تواند وضعیت سفارش خود را بعدازثبت سفارش در قسمت پروفایل کاربری قسمت پیگیری سفارشها مشاهده کند.\n" +
                    " ۴ مسئولیت های " +
                    company.getN() +
                    "\n" +
                    "۱.۴ " +
                    company.getN() +
                    " موظف است از روش هایی که برای پشتیبانی تعیین کرده در دسترس کاربران باشد ونسبت به رسیدگی شکایت و نارضایتی تلاش کند.\n" +
                    "۲.۴ تمام اطلاعات کاربر بصورت محرمانه محافظت شده و دسترسی به ان توسط سایرین ممنوع می باشد.\n" +
                    "۳.۴ " +
                    company.getN() +
                    " موظف است سفارش کاربر را با استفاده از وسایل نقلیه موتوری در زمان مشخص شده دربسته بندی مخصوص تحویل کاربر دهد.\n" +
                    "۵ قطع خدمات\n" +
                    " درصورتیکه کاربر به هرنحوی از سیستم سوء استفاده کند" +
                    company.getN() +
                    " حق دارد حساب کاربری شخص را مسدود وادامه خدمات به کاربر مورد نظر راقطع کند.");
    }


}
