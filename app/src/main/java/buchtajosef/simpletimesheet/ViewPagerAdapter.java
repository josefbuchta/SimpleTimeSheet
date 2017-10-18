package buchtajosef.simpletimesheet;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;

import java.util.List;


class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public static SparseArray<mFragment> fragments = new SparseArray<>();
    private CharSequence Titles[];
    private int NumbOfTabs;

    ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabs) {
        super(fm);
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabs;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public mFragment getItem(int position) {
        mFragment tab;
        switch (position) {
            case 1: tab = new TabDay();
                break;
            case 2: tab = new TabMonth();
                break;
            case 3: tab = new TabTools();
                break;
            default: tab = new TabClock();
                break;
        }
        return tab;
    }

    // This method return the titles for the Tabs in the Tab Strip
    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip
    @Override
    public int getCount() {
        return NumbOfTabs;
    }

    void refreshFragment(int position) {
        mFragment f = fragments.get(position);
        if (f != null) {
            f.fillView();
        }
    }
}
