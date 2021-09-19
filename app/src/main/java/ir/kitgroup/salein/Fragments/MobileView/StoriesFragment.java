package ir.kitgroup.salein.Fragments.MobileView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import ir.kitgroup.salein.Activities.Classes.LauncherActivity;


import ir.kitgroup.salein.databinding.FragmentStoriesBinding;

public class StoriesFragment extends Fragment {

    private FragmentStoriesBinding binding;


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
       binding=FragmentStoriesBinding.inflate(getLayoutInflater());
       return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        PagerAdapter pagerAdapter = new PagerAdapter(getChildFragmentManager(), getActivity());
        pagerAdapter.addFragment(new CompanyFragment(), "فروشگاه ها");
        pagerAdapter.addFragment(new MyCompanyFragment(), "فروشگاه های من");


        binding.viewPager.setAdapter(pagerAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);
        binding.viewPager.setCurrentItem(1);
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
                    ((TextView) tabViewChild).setTypeface(LauncherActivity.iranSansBold);
                    ((TextView) tabViewChild).setTextSize(12);
                }
            }
        }
    }

    private class PagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> myFragments = new ArrayList<>();
        private final List<String> myFragmentTitles = new ArrayList<>();
        private Context context;
        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        public void addFragment(Fragment fragment, String title) {
            myFragments.add(fragment);
            myFragmentTitles.add(title);
        }

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

    public void setMyCompany(){
        binding.viewPager.setCurrentItem(1);
        binding.tabLayout.getTabAt(1).select();
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }
}
