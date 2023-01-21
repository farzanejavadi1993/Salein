package ir.kitgroup.salein.ui.companies;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.salein.databinding.FragmentCompanyBinding;

@AndroidEntryPoint
public class CompanyFragment extends Fragment {

    @Inject
    Typeface typeface;

    public FragmentCompanyBinding binding;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding = FragmentCompanyBinding.inflate(getLayoutInflater());

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

       AllCompanyFragment companyFragment = new AllCompanyFragment();
       companyFragment.setCompanyFragment(this);



        PagerAdapter adapter = new PagerAdapter(getChildFragmentManager());
        adapter.addFragment(new BannerFragment(), "آگهی ها");
        adapter.addFragment(new MyCompanyFragment(), "انتخاب های من");
        adapter.addFragment(companyFragment, "همه");


        binding.viewpager.setAdapter(adapter);
        binding.tabLayout.setupWithViewPager(binding.viewpager);

        TabLayout.Tab tab = binding.tabLayout.getTabAt(2);
        tab.select();
        setCustomFontToTabLayout();

    }

    public void setCustomFontToTabLayout() {
        ViewGroup vg = (ViewGroup) binding.tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(typeface);
                    ((TextView) tabViewChild).setTextSize(12);
                }
            }
        }
    }

    //region Method
    private static class PagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> myFragments = new ArrayList<>();
        private final List<String> myFragmentTitles = new ArrayList<>();

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            myFragments.add(fragment);
            myFragmentTitles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return myFragments.get(position);
        }

        @Override
        public int getCount() {
            return myFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return myFragmentTitles.get(position);
        }
    }
    //endregion Method
}
