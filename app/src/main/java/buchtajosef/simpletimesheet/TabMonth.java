package buchtajosef.simpletimesheet;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ibm.icu.text.DateFormatSymbols;

import java.util.ArrayList;
import java.util.Calendar;


public class TabMonth extends mFragment {

    private GridView calendarGrid;
    private ImageButton deleteButton, nextButton, prevButton, homeButton;
    private TextView dayInfo, mo, tu, we, th, fr, sa, su, time, timeWeek, timeTotal, days, daysWeek, daysTotal;
    private ArrayList<DaysData> monthData = new ArrayList<>();

    public TabMonth() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab_month,container,false);
        setBackground(v);
        myTimeSheetDB =  new MySQLiteHelper(getActivity());

        ViewPagerAdapter.fragments.put(2,this);

        calendarGrid = (GridView) v.findViewById(R.id.calendar_grid);
        deleteButton = (ImageButton) v.findViewById(R.id.calendar_deleteButton);
        nextButton = (ImageButton) v.findViewById(R.id.calendar_nextButton);
        prevButton = (ImageButton) v.findViewById(R.id.calendar_prevButton);
        homeButton = (ImageButton) v.findViewById(R.id.calendar_homeButton);
        time = (TextView) v.findViewById(R.id.calendar_summary_time);
        timeWeek = (TextView) v.findViewById(R.id.calendar_summary_time_weekends);
        timeTotal = (TextView) v.findViewById(R.id.calendar_summary_time_total);
        days = (TextView) v.findViewById(R.id.calendar_summary_days);
        daysWeek = (TextView) v.findViewById(R.id.calendar_summary_days_weekends);
        daysTotal = (TextView) v.findViewById(R.id.calendar_summary_days_total);
        dayInfo = (TextView) v.findViewById(R.id.calendar_info);
        mo = (TextView) v.findViewById(R.id.calendar_header_mo);
        tu = (TextView) v.findViewById(R.id.calendar_header_tu);
        we = (TextView) v.findViewById(R.id.calendar_header_we);
        th = (TextView) v.findViewById(R.id.calendar_header_th);
        fr = (TextView) v.findViewById(R.id.calendar_header_fr);
        sa = (TextView) v.findViewById(R.id.calendar_header_sa);
        su = (TextView) v.findViewById(R.id.calendar_header_su);

        setupDayNames();
        setupButtons();
        return v;
    }

    public void fillView () {
        try {
            String info = MainActivity.mSelectedDate.getMonthNameStandalone() + " " +
                    MainActivity.mSelectedDate.getDayData().getString(DaysData.YEAR);
            dayInfo.setText(info);

            monthData.clear();
            for (DaysData day : MainActivity.mSelectedDate.getMonthData()) {
                monthData.add(day);
            }
            fillInEmptyDays();

            MyGridAdapter adapter = new MyGridAdapter(monthData);
            calendarGrid.setAdapter(adapter);
            calendarGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MainActivity.mSelectedDate.setDayAsSelected(monthData.get(position));
                    fillView();
                    TabDay.savedState = false;
                }
            });

            int countTotal = MainActivity.mSelectedDate.getMonthCoutTotal();
            int count = MainActivity.mSelectedDate.getMonthCout();
            int countWeekends = MainActivity.mSelectedDate.getMonthCoutWeekends();
            int countDaysTotal = MainActivity.mSelectedDate.getDaysWorkedTotal();
            int countDays = MainActivity.mSelectedDate.getDaysWorked();
            int countDaysWeekend = MainActivity.mSelectedDate.getDaysWorkedWeekends();

            time.setText(count/60 + "h " + count % 60 + "m");
            timeWeek.setText(countWeekends / 60 + "h " + countWeekends % 60 + "m");
            timeTotal.setText(countTotal / 60 + "h " + countTotal % 60 + "m");
            days.setText(String.valueOf(countDays));
            daysWeek.setText(String.valueOf(countDaysWeekend));
            daysTotal.setText(String.valueOf(countDaysTotal));

            if (countTotal > 0) {
                deleteButton.setVisibility(View.VISIBLE);
            } else {
                deleteButton.setVisibility(View.INVISIBLE);
            }

        } catch (NullPointerException e) {
            Log.e("FillViewMonth", e.getMessage());
        }
    }

    private void setupDayNames () {
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] dayNames = dfs.getShortWeekdays();
        for (int i = 1; i < dayNames.length; i++) {
            dayNames[i] = dayNames[i].substring(0,1).toUpperCase() + dayNames[i].substring(1);
        }
        mo.setText(dayNames[2]);
        tu.setText(dayNames[3]);
        we.setText(dayNames[4]);
        th.setText(dayNames[5]);
        fr.setText(dayNames[6]);
        sa.setText(dayNames[7]);
        su.setText(dayNames[1]);
    }

    private void fillInEmptyDays () {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(MainActivity.mSelectedDate.getDayData().get(DaysData.YEAR), MainActivity.mSelectedDate.getDayData().get(DaysData.MONTH), 1);
        int dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
        ArrayList<DaysData> data = MainActivity.mSelectedDate.getPrevMonthData();
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                break;
            case Calendar.SUNDAY:
                for (int i = 1; i < 7; i ++) {
                    monthData.add(0, data.get(data.size() - i));
                }
                break;
            default:
                for (int i = 1; i < dayOfWeek-1; i ++) {
                    monthData.add(0, data.get(data.size() - i));
                }
                break;
        }

        mCalendar.set(Calendar.DAY_OF_MONTH, mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK);
        data = MainActivity.mSelectedDate.getNextMonthData();
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                break;
            default:
                for (int i = 0; i <= 7-dayOfWeek; i ++) {
                    monthData.add(data.get(i));
                }
                break;
        }
    }

    private class MyGridAdapter extends BaseAdapter {
        private final ArrayList<DaysData> data;

        MyGridAdapter (ArrayList<DaysData> d){
            data = d;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View cellView;

            if (convertView == null) {
                cellView = inflater.inflate(R.layout.calendar_day_item, new LinearLayout(getContext()), false);
                TextView dayNumber = (TextView) cellView.findViewById(R.id.calendar_item_dayNumber);
                TextView dayAmount = (TextView) cellView.findViewById(R.id.calendar_item_dayAmount);

                dayNumber.setText(data.get(position).getString(DaysData.DAY));
                if (data.get(position).get(DaysData.ROWID) == 0 ) {
                    dayAmount.setText("");
                } else {
                    int h = data.get(position).get(DaysData.HOUR_OUT) - data.get(position).get(DaysData.HOUR_IN);
                    int m = data.get(position).get(DaysData.MINUTE_OUT) - data.get(position).get(DaysData.MINUTE_IN);
                    if (data.get(position).hasMoreTimes()) {
                        ArrayList<DaysData> ddList = data.get(position).getAdditionalTimes();
                        for (int i = 0; i < ddList.size(); i++) {
                            h += ddList.get(i).get(DaysData.HOUR_OUT) - ddList.get(i).get(DaysData.HOUR_IN);
                            m += ddList.get(i).get(DaysData.MINUTE_OUT) - ddList.get(i).get(DaysData.MINUTE_IN);
                        }
                    }
                    int count = ((h * 60) + m);
                    if (count > 0) {
                        String text = count / 60 + ":" + count % 60 + "h";
                        dayAmount.setText(text);
                    } else {
                        dayAmount.setText(data.get(position).getString(DaysData.DESCRIPTION));
                    }
                }
                Calendar c = Calendar.getInstance();
                c.set(data.get(position).get(DaysData.YEAR), data.get(position).get(DaysData.MONTH), data.get(position).get(DaysData.DAY));
                int dow = c.get(Calendar.DAY_OF_WEEK);
                if (dow == Calendar.SUNDAY || dow == Calendar.SATURDAY) {
                    cellView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorCalendarWeekend));
                }

                c = Calendar.getInstance();
                int d = c.get(Calendar.DAY_OF_MONTH);
                int m = c.get(Calendar.MONTH);
                int y = c.get(Calendar.YEAR);
                if (y == data.get(position).get(DaysData.YEAR) && m == data.get(position).get(DaysData.MONTH) && d == data.get(position).get(DaysData.DAY)) {
                    cellView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorCalendarToday));
                }

                if (MainActivity.mSelectedDate.getDayData().get(DaysData.MONTH) == data.get(position).get(DaysData.MONTH) &&
                        MainActivity.mSelectedDate.getDayData().get(DaysData.DAY) == data.get(position).get(DaysData.DAY)) {
                    cellView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorCalendarSelectedDay));
                }

                int month = MainActivity.mSelectedDate.getDayData().get(Calendar.MONTH);
                if (data.get(position).get(DaysData.MONTH) != month) {
                    dayNumber.setTextColor(Color.LTGRAY);
                    cellView.setBackgroundColor(Color.TRANSPARENT);
                }
            } else {
                cellView = convertView;
            }

            return cellView;
        }
    }

    private void setupButtons () {
        assert deleteButton != null;
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickDelete();
            }
        });
        assert nextButton != null;
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickNextMonth();
            }
        });
        assert prevButton != null;
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickPrevMonth();
            }
        });
        assert homeButton != null;
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickHomeButton();
            }
        });
    }

    private void onClickNextMonth () {
        MainActivity.mSelectedDate.setNextMonth();
        fillView();
    }

    private void onClickPrevMonth () {
        MainActivity.mSelectedDate.setPrevMonth();
        fillView();
    }

    public void onClickHomeButton () {
        MainActivity.mSelectedDate.setToday();
        fillView();
    }

    private void onClickDelete () {
        AlertDialog.Builder deleteConfirm = new AlertDialog.Builder(this.getContext());
        deleteConfirm.setMessage(getString(R.string.dialog_message_delete_month));
        deleteConfirm.setTitle(getString(R.string.dialog_title_delete));
        deleteConfirm.setNegativeButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        deleteConfirm.setPositiveButton(getString(R.string.dialog_button_delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myTimeSheetDB.deleteMonth(MainActivity.mSelectedDate.getDayData().get(DaysData.YEAR), MainActivity.mSelectedDate.getDayData().get(DaysData.MONTH));
                MainActivity.mSelectedDate.reloadData();
                fillView();
            }
        });
        deleteConfirm.show();
    }
}
