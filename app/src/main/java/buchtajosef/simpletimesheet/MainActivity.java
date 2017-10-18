package buchtajosef.simpletimesheet;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.TextView;
import com.ibm.icu.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private MySQLiteHelper myTimeSheetDB;
    public static SelectedDate mSelectedDate;
    ViewPagerAdapter pagerAdapter;
    CharSequence Titles[];
    int NumberOfTabs = 4;
    Drawable[] icons = new Drawable[NumberOfTabs];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Titles = new CharSequence[] {getString(R.string.main_tab_1), getString(R.string.main_tab_2), getString(R.string.main_tab_3), getString(R.string.main_tab_4)};
        myTimeSheetDB = new MySQLiteHelper(this);
        mSelectedDate = new SelectedDate();

        icons = new Drawable[]{
                ResourcesCompat.getDrawable(getResources(), android.R.drawable.ic_menu_recent_history, null),
                ResourcesCompat.getDrawable(getResources(), android.R.drawable.ic_menu_today, null),
                ResourcesCompat.getDrawable(getResources(), android.R.drawable.ic_menu_month, null),
                ResourcesCompat.getDrawable(getResources(), android.R.drawable.ic_menu_manage, null)
        };
        //Adding toolbar in our activity
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, NumberOfTabs);

        // Assigning ViewPager View and setting the adapter
        ViewPager viewPager;
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        assert viewPager != null;
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
                assert tabLayout != null;
                TabLayout.Tab t;
                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    t = tabLayout.getTabAt(i);
                    assert t != null;
                    View v = t.getCustomView();
                    assert v != null;
                    TextView title = (TextView) v.findViewById(R.id.tab_view_title);
                    if (i == position) {
                        title.setText(pagerAdapter.getPageTitle(i));
                    } else {
                        title.setText("");
                    }
                }
                pagerAdapter.refreshFragment(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab t;
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            t = tabLayout.getTabAt(i);
            assert t != null;
            t.setCustomView(R.layout.tab_view);
            View v = t.getCustomView();
            assert v != null;
            ImageView icon = (ImageView) v.findViewById(R.id.tab_view_icon);
            TextView title = (TextView) v.findViewById(R.id.tab_view_title);
            icon.setImageDrawable(icons[i]);
            if (i == 0) {
                title.setText(pagerAdapter.getPageTitle(i));
            }
        }
        checkPermissions();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissionToAdd = new ArrayList<>();
            String[] neededPermissions = {
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
            };
            for (String permission : neededPermissions) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionToAdd.add(permission);
                }
            }
            if (!permissionToAdd.isEmpty()) {
                ActivityCompat.requestPermissions(this, permissionToAdd.toArray(new String[0]), 1);
            }
        }
    }

    public class SelectedDate {
        private DaysData selectedDay;
        private DaysData todaysData;
        private ArrayList<DaysData> selectedMonth = new ArrayList<>();
        private ArrayList<DaysData> prevMonth = new ArrayList<>();
        private ArrayList<DaysData> nextMonth = new ArrayList<>();
        private String monthName = "";
        private String monthNameStandalone = "";
        private String dayName = "";
        private String todaysMonthName = "";
        private String todaysMonthNameStandalone = "";
        private String todaysDayName = "";
        private int monthCout, monthCoutWeekends, daysWorked, weekendsWorked;
        DateFormatSymbols dfs;

        SelectedDate () {
            dfs = new DateFormatSymbols();
            Calendar c = Calendar.getInstance();

            String[] dayNames = dfs.getShortWeekdays();
            for (int i = 1; i < dayNames.length; i++) {
                dayNames[i] = dayNames[i].substring(0,1).toUpperCase() + dayNames[i].substring(1);
            }
            todaysDayName = dayNames[c.get(Calendar.DAY_OF_WEEK)];

            String[] monthNames = dfs.getMonths();
            for (int i = 0; i < monthNames.length; i++) {
                monthNames[i] = monthNames[i].substring(0,1).toUpperCase() + monthNames[i].substring(1);
            }
            todaysMonthName = monthNames[c.get(Calendar.MONTH)];

            monthNames = dfs.getMonths(DateFormatSymbols.STANDALONE, DateFormatSymbols.WIDE);
            for (int i = 0; i < monthNames.length; i++) {
                monthNames[i] = monthNames[i].substring(0,1).toUpperCase() + monthNames[i].substring(1);
            }
            todaysMonthNameStandalone = monthNames[c.get(Calendar.MONTH)];

            Cursor resultSet = myTimeSheetDB.getDayData(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            if (resultSet != null && resultSet.moveToFirst()) {
                todaysData = new DaysData(getApplicationContext(), resultSet);
            } else {
                todaysData = new DaysData(getApplicationContext(), null);
                todaysData.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            }
            setToday();
        }

        private void setSelectedDay (Calendar c) {
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            Cursor resultSet = myTimeSheetDB.getDayData(year, month, day);
            if (resultSet != null && resultSet.moveToFirst()) {
                selectedDay = new DaysData(getApplicationContext(), resultSet);
            } else {
                selectedDay = new DaysData(getApplicationContext(), null);
                selectedDay.set(year, month, day);
            }
            setNames(c);
        }

        private void setSelectedMonth () {
            Cursor resultSet = myTimeSheetDB.getMonthData(selectedDay.get(DaysData.YEAR), selectedDay.get(DaysData.MONTH));
            selectedMonth.clear();
            if (resultSet != null && resultSet.moveToFirst()) {
                do {
                    selectedMonth.add(new DaysData(getApplicationContext(), resultSet));
                } while (resultSet.moveToNext());
                resultSet.close();
            }
            setupMonthHours();
            Calendar c = Calendar.getInstance();
            c.set(selectedDay.get(DaysData.YEAR), selectedDay.get(DaysData.MONTH),selectedDay.get(DaysData.DAY));
            int maxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int i = 1; i<= maxDay; i++) {
                if ( i > selectedMonth.size() || selectedMonth.get(i-1).get(DaysData.DAY) != i ) {
                    DaysData dd = new DaysData(getApplicationContext(), null);
                    dd.set(selectedDay.get(Calendar.YEAR), selectedDay.get(Calendar.MONTH), i);
                    selectedMonth.add(i-1,dd);
                }
            }
            setOtherMonthsData();
        }

        private void setOtherMonthsData () {
            Calendar c = Calendar.getInstance();
            Calendar cPrev = Calendar.getInstance();
            Calendar cNext = Calendar.getInstance();
            c.set(selectedDay.get(DaysData.YEAR), selectedDay.get(DaysData.MONTH), selectedDay.get(DaysData.DAY));
            int month = c.get(Calendar.MONTH);
            switch (month) {
                case Calendar.JANUARY:
                    cPrev.set(c.get(Calendar.YEAR) - 1, Calendar.DECEMBER, 1);
                    cNext.set(c.get(Calendar.YEAR), Calendar.FEBRUARY, 1);
                    break;
                case Calendar.DECEMBER:
                    cPrev.set(c.get(Calendar.YEAR), Calendar.NOVEMBER, 1);
                    cNext.set(c.get(Calendar.YEAR) + 1, Calendar.JANUARY, 1);
                    break;
                default:
                    cPrev.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH) - 1, 1);
                    cNext.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, 1);
                    break;
            }

            prevMonth.clear();
            Cursor resultSet = myTimeSheetDB.getMonthData(cPrev.get(Calendar.YEAR), cPrev.get(Calendar.MONTH));
            if (resultSet != null && resultSet.moveToFirst()) {
                do {
                    prevMonth.add(new DaysData(getApplicationContext(), resultSet));
                } while (resultSet.moveToNext());
            }
            int maxDay = cPrev.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int i = 1; i<= maxDay; i++) {
                if ( i > prevMonth.size() || prevMonth.get(i-1).get(DaysData.DAY) != i ) {
                    DaysData dd = new DaysData(getApplicationContext(), null);
                    dd.set(cPrev.get(Calendar.YEAR), cPrev.get(Calendar.MONTH), i);
                    prevMonth.add(i-1,dd);
                }
            }

            nextMonth.clear();
            resultSet = myTimeSheetDB.getMonthData(cNext.get(Calendar.YEAR), cNext.get(Calendar.MONTH));
            if (resultSet != null && resultSet.moveToFirst()) {
                do {
                    nextMonth.add(new DaysData(getApplicationContext(), resultSet));
                } while (resultSet.moveToNext());
                resultSet.close();
            }
            maxDay = cNext.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int i = 1; i<= maxDay; i++) {
                if ( i > nextMonth.size() || nextMonth.get(i-1).get(DaysData.DAY) != i ) {
                    DaysData dd = new DaysData(getApplicationContext(), null);
                    dd.set(cNext.get(Calendar.YEAR), cNext.get(Calendar.MONTH), i);
                    nextMonth.add(i-1,dd);
                }
            }
        }

        private void setNames (Calendar c) {
            String[] dayNames = dfs.getShortWeekdays();
            for (int i = 1; i < dayNames.length; i++) {
                dayNames[i] = dayNames[i].substring(0,1).toUpperCase() + dayNames[i].substring(1);
            }
            dayName = dayNames[c.get(Calendar.DAY_OF_WEEK)];

            String[] monthNames = dfs.getMonths(DateFormatSymbols.STANDALONE, DateFormatSymbols.WIDE);
            for (int i = 0; i < monthNames.length; i++) {
                monthNames[i] = monthNames[i].substring(0,1).toUpperCase() + monthNames[i].substring(1);
            }
            monthNameStandalone = monthNames[c.get(Calendar.MONTH)];

            monthNames = dfs.getMonths();
            for (int i = 0; i < monthNames.length; i++) {
                monthNames[i] = monthNames[i].substring(0,1).toUpperCase() + monthNames[i].substring(1);
            }
            monthName = monthNames[c.get(Calendar.MONTH)];
        }

        void setPrevDay () {
            int day = selectedDay.get(DaysData.DAY);
            Calendar c = Calendar.getInstance();
            c.set(selectedDay.get(DaysData.YEAR), selectedDay.get(DaysData.MONTH),selectedDay.get(DaysData.DAY));

            if (day == c.getActualMinimum(Calendar.DAY_OF_MONTH)) {
                int month = c.get(Calendar.MONTH);
                if (month == Calendar.JANUARY) {
                    c.set(c.get(Calendar.YEAR) - 1, Calendar.DECEMBER, 31);
                } else {
                    c.set(Calendar.MONTH , (month-1));
                    c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
                }
                setSelectedDay(c);
                setSelectedMonth();
            } else {
                c.set(Calendar.DAY_OF_MONTH, day - 1);
                selectedDay = selectedMonth.get(day - 2);
                setNames(c);
            }
        }

        void setNextDay () {
            int day = selectedDay.get(DaysData.DAY);
            Calendar c = Calendar.getInstance();
            c.set(selectedDay.get(DaysData.YEAR), selectedDay.get(DaysData.MONTH),selectedDay.get(DaysData.DAY));

            if (day == c.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                int month = c.get(Calendar.MONTH);
                if (month == Calendar.DECEMBER) {
                    c.set(c.get(Calendar.YEAR) + 1, Calendar.JANUARY, 1);
                } else {
                    c.set(Calendar.MONTH , (month + 1));
                    c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
                }
                setSelectedDay(c);
                setSelectedMonth();
            } else {
                c.set(Calendar.DAY_OF_MONTH, day + 1);
                selectedDay = selectedMonth.get(day);
                setNames(c);
            }
        }

        void setPrevMonth () {
            Calendar c = Calendar.getInstance();
            c.set(selectedDay.get(DaysData.YEAR), selectedDay.get(DaysData.MONTH),selectedDay.get(DaysData.DAY));
            int month = c.get(Calendar.MONTH);
            if (month == Calendar.JANUARY) {
                c.set(c.get(Calendar.YEAR) - 1, Calendar.DECEMBER, 1);
            } else {
                c.set(Calendar.MONTH , (month-1));
                c.set(Calendar.DAY_OF_MONTH, 1);
            }
            setSelectedDay(c);
            setSelectedMonth();
        }

        void setNextMonth () {
            Calendar c = Calendar.getInstance();
            c.set(selectedDay.get(DaysData.YEAR), selectedDay.get(DaysData.MONTH),selectedDay.get(DaysData.DAY));
            int month = c.get(Calendar.MONTH);
            if (month == Calendar.DECEMBER) {
                c.set(c.get(Calendar.YEAR) + 1, Calendar.JANUARY, 1);
            } else {
                c.set(Calendar.MONTH , (month + 1));
                c.set(Calendar.DAY_OF_MONTH, 1);
            }
            setSelectedDay(c);
            setSelectedMonth();
        }

        void setToday () {
            Calendar c = Calendar.getInstance();
            setSelectedDay(c);
            setSelectedMonth();
        }

        void setDayAsSelected (DaysData dd) {
            if (selectedDay.get(DaysData.YEAR) == dd.get(DaysData.YEAR) && selectedDay.get(DaysData.MONTH) == dd.get(DaysData.MONTH)) {
                for (int i = 0; i < selectedMonth.size(); i++) {
                    if (selectedMonth.get(i).get(DaysData.DAY) == dd.get(DaysData.DAY)) {
                        selectedDay = selectedMonth.get(i);
                        i = selectedMonth.size();
                    }
                }
            } else {
                Calendar c = Calendar.getInstance();
                c.set(dd.get(DaysData.YEAR), dd.get(DaysData.MONTH), dd.get(DaysData.DAY));
                setSelectedDay(c);
                setSelectedMonth();
            }
        }

        String getMonthName () {
            return monthName;
        }

        String getMonthNameStandalone () {
            return monthNameStandalone;
        }

        String getDaysName () {
            return dayName;
        }

        String getTodaysMonthName () {
            return todaysMonthName;
        }

        public String getTodaysMonthNameStandalone () {
            return  todaysMonthNameStandalone;
        }

        String getTodaysDayName () {
            return todaysDayName;
        }

        DaysData getDayData () {
            return selectedDay;
        }

        DaysData getTodaysData () {
            return todaysData;
        }

        ArrayList<DaysData> getMonthData () {
            return selectedMonth;
        }

        ArrayList<DaysData> getPrevMonthData () {
            return prevMonth;
        }

        ArrayList<DaysData> getNextMonthData () {
            return nextMonth;
        }

        void reloadData () {
            Calendar c = Calendar.getInstance();
            Cursor resultSet = myTimeSheetDB.getDayData(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            if (resultSet != null && resultSet.moveToFirst()) {
                todaysData = new DaysData(getApplicationContext(), resultSet);
            } else {
                todaysData = new DaysData(getApplicationContext(), null);
                todaysData.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            }

            c.set(selectedDay.get(DaysData.YEAR), selectedDay.get(DaysData.MONTH), selectedDay.get(DaysData.DAY));
            setSelectedDay(c);
            setSelectedMonth();
        }

        private void setupMonthHours () {
            monthCout = 0;
            monthCoutWeekends = 0;
            daysWorked = 0;
            weekendsWorked = 0;
            Calendar c = Calendar.getInstance();
            c.set(selectedDay.get(DaysData.YEAR), selectedDay.get(DaysData.MONTH), selectedDay.get(DaysData.DAY));
            int dayOfWeek;
            int count;
            for (DaysData dd : selectedMonth) {
                c.set(Calendar.DAY_OF_MONTH, dd.get(DaysData.DAY));
                dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

                int h = dd.get(DaysData.HOUR_OUT) - dd.get(DaysData.HOUR_IN);
                int m = dd.get(DaysData.MINUTE_OUT) - dd.get(DaysData.MINUTE_IN);
                if (dd.hasMoreTimes()) {
                    ArrayList<DaysData> ddList = dd.getAdditionalTimes();
                    for (int i = 0; i < ddList.size(); i++) {
                        h += ddList.get(i).get(DaysData.HOUR_OUT) - ddList.get(i).get(DaysData.HOUR_IN);
                        m += ddList.get(i).get(DaysData.MINUTE_OUT) - ddList.get(i).get(DaysData.MINUTE_IN);
                    }
                }
                count = ((h * 60) + m);

                switch (dayOfWeek) {
                    case Calendar.SATURDAY:
                        monthCoutWeekends += count;
                        weekendsWorked ++;
                        break;
                    case Calendar.SUNDAY:
                        monthCoutWeekends += count;
                        weekendsWorked ++;
                        break;
                    default:
                        monthCout += count;
                        daysWorked ++;
                        break;
                }
            }
        }

        int getMonthCout () {
            return monthCout;
        }

        int getMonthCoutWeekends () {
            return monthCoutWeekends;
        }

        int getMonthCoutTotal () {
            return monthCout+monthCoutWeekends;
        }

        int getDaysWorked () {
            return daysWorked;
        }

        int getDaysWorkedWeekends () {
            return weekendsWorked;
        }

        int getDaysWorkedTotal () {
            return daysWorked+weekendsWorked;
        }
    }
}
